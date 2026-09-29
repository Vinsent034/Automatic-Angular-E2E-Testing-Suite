import { Injectable, computed, signal } from '@angular/core';
import {
  Course, CourseFilter, CourseStat, Recipe, RecipeDraft, ShoppingGroup, ShoppingItem, SortKey
} from './models';
import { SEED_RECIPES, SEED_SHOPPING } from './seed';

const COURSES: readonly Course[] = ['starter', 'main', 'side', 'dessert'];

/**
 * Single global signal store. Every component injects it as `store`, so any
 * template binding written as `store.something()` compiles in ANY component
 * template: an element that reads state through `store` stays valid wherever
 * it is moved, which keeps the "move an element between templates" mutation
 * operator (h) compilable.
 *
 * Deterministic by construction: state starts from the seed, lives only in
 * memory, and no method uses timers, dates or randomness.
 */
@Injectable({ providedIn: 'root' })
export class CookStore {
  private readonly recipesSig = signal<readonly Recipe[]>(SEED_RECIPES);
  readonly recipes = this.recipesSig.asReadonly();

  private readonly shoppingSig = signal<readonly ShoppingItem[]>(SEED_SHOPPING);
  readonly shopping = this.shoppingSig.asReadonly();

  /** Toolbar of the recipe list. */
  readonly search = signal('');
  readonly courseFilter = signal<CourseFilter>('all');
  readonly vegetarianOnly = signal(false);
  readonly sortKey = signal<SortKey>('title');

  /** Recipe shown on the detail page, and servings chosen there (null = recipe default). */
  readonly selectedId = signal<number | null>(null);
  readonly servings = signal<number | null>(null);

  /** Last action message, shown by the toast bar until replaced or dismissed. */
  readonly toast = signal('Recipes loaded from seed');

  private nextRecipeId = SEED_RECIPES.length + 1;
  private nextItemId = SEED_SHOPPING.length + 1;

  // ------------------------------------------------------------------
  // Derived state
  // ------------------------------------------------------------------

  readonly totalRecipes = computed(() => this.recipes().length);
  readonly vegetarianCount = computed(() => this.recipes().filter(r => r.vegetarian).length);
  readonly favoriteCount = computed(() => this.recipes().filter(r => r.favorite).length);
  readonly averageMinutes = computed(() => {
    const list = this.recipes();
    if (list.length === 0) return 0;
    const total = list.reduce((sum, r) => sum + r.prepMinutes + r.cookMinutes, 0);
    return Math.round(total / list.length);
  });

  /** Recipes passing search (title or tag), course and vegetarian filters, sorted. */
  readonly filtered = computed(() => {
    const text = this.search().trim().toLowerCase();
    const course = this.courseFilter();
    const vegOnly = this.vegetarianOnly();
    const list = this.recipes().filter(r =>
      (text === '' || r.title.toLowerCase().includes(text) || r.tags.some(t => t.includes(text))) &&
      (course === 'all' || r.course === course) &&
      (!vegOnly || r.vegetarian));
    const key = this.sortKey();
    return [...list].sort((a, b) => {
      if (key === 'time') return (a.prepMinutes + a.cookMinutes) - (b.prepMinutes + b.cookMinutes) || a.id - b.id;
      if (key === 'rating') return b.rating - a.rating || a.id - b.id;
      return a.title.localeCompare(b.title);
    });
  });

  readonly filteredCount = computed(() => this.filtered().length);

  readonly selected = computed<Recipe | null>(
    () => this.recipes().find(r => r.id === this.selectedId()) ?? null);

  readonly servingsShown = computed(() => this.servings() ?? this.selected()?.servings ?? 0);

  /** Factor applied to ingredient quantities on the detail page. */
  readonly scaleFactor = computed(() => {
    const recipe = this.selected();
    return recipe === null ? 1 : this.servingsShown() / recipe.servings;
  });

  readonly shoppingOpenCount = computed(() => this.shopping().filter(i => !i.done).length);
  readonly shoppingDoneCount = computed(() => this.shopping().filter(i => i.done).length);

  /** Shopping lines grouped by recipe, in order of first appearance. */
  readonly shoppingGroups = computed<readonly ShoppingGroup[]>(() => {
    const groups: { recipeId: number; title: string; items: ShoppingItem[] }[] = [];
    for (const item of this.shopping()) {
      let group = groups.find(g => g.recipeId === item.recipeId);
      if (group === undefined) {
        const recipe = this.recipes().find(r => r.id === item.recipeId);
        group = { recipeId: item.recipeId, title: recipe?.title ?? 'Removed recipe', items: [] };
        groups.push(group);
      }
      group.items.push(item);
    }
    return groups;
  });

  readonly courseStats = computed<readonly CourseStat[]>(() => {
    const total = this.totalRecipes();
    return COURSES.map(course => {
      const count = this.recipes().filter(r => r.course === course).length;
      return { course, count, share: total === 0 ? 0 : Math.round((count / total) * 100) };
    });
  });

  readonly topRated = computed(() =>
    [...this.recipes()].sort((a, b) => b.rating - a.rating || a.id - b.id).slice(0, 3));

  // ------------------------------------------------------------------
  // Actions
  // ------------------------------------------------------------------

  select(id: number | null): void {
    this.selectedId.set(id);
    this.servings.set(null);
  }

  setServings(value: number): void {
    if (!Number.isFinite(value)) return;
    this.servings.set(Math.min(24, Math.max(1, Math.round(value))));
  }

  toggleFavorite(id: number): void {
    const recipe = this.recipes().find(r => r.id === id);
    if (recipe === undefined) return;
    this.recipesSig.update(list => list.map(r => r.id === id ? { ...r, favorite: !r.favorite } : r));
    this.toast.set(recipe.favorite ? `Removed "${recipe.title}" from favorites` : `Added "${recipe.title}" to favorites`);
  }

  addRecipe(draft: RecipeDraft): number {
    const id = this.nextRecipeId++;
    const recipe: Recipe = {
      ...draft, id, favorite: false, rating: 0,
      nutrition: { kcal: 0, protein: 0, carbs: 0, fat: 0 }
    };
    this.recipesSig.update(list => [...list, recipe]);
    this.toast.set(`Created "${recipe.title}"`);
    return id;
  }

  updateRecipe(id: number, draft: RecipeDraft): void {
    this.recipesSig.update(list => list.map(r => r.id === id ? { ...r, ...draft } : r));
    this.toast.set(`Saved "${draft.title}"`);
  }

  removeRecipe(id: number): void {
    const recipe = this.recipes().find(r => r.id === id);
    if (recipe === undefined) return;
    this.recipesSig.update(list => list.filter(r => r.id !== id));
    this.shoppingSig.update(list => list.filter(i => i.recipeId !== id));
    if (this.selectedId() === id) this.select(null);
    this.toast.set(`Deleted "${recipe.title}"`);
  }

  /** Adds the non-optional ingredients of a recipe, scaled if it is the one on screen. */
  addToShopping(recipeId: number): void {
    const recipe = this.recipes().find(r => r.id === recipeId);
    if (recipe === undefined) return;
    const factor = this.selectedId() === recipeId ? this.scaleFactor() : 1;
    const present = new Set(this.shopping().filter(i => i.recipeId === recipeId).map(i => i.name));
    const added: ShoppingItem[] = recipe.ingredients
      .filter(ing => !ing.optional && !present.has(ing.name))
      .map(ing => ({
        id: this.nextItemId++, recipeId, name: ing.name,
        quantity: ing.quantity * factor, unit: ing.unit, done: false
      }));
    this.shoppingSig.update(list => [...list, ...added]);
    this.toast.set(added.length === 0
      ? `"${recipe.title}" is already on the shopping list`
      : `Added ${added.length} items from "${recipe.title}"`);
  }

  toggleShopping(itemId: number): void {
    this.shoppingSig.update(list => list.map(i => i.id === itemId ? { ...i, done: !i.done } : i));
  }

  clearDone(): void {
    const removed = this.shoppingDoneCount();
    this.shoppingSig.update(list => list.filter(i => !i.done));
    this.toast.set(`Removed ${removed} bought items`);
  }

  reset(): void {
    this.recipesSig.set(SEED_RECIPES);
    this.shoppingSig.set(SEED_SHOPPING);
    this.search.set('');
    this.courseFilter.set('all');
    this.vegetarianOnly.set(false);
    this.sortKey.set('title');
    this.select(null);
    this.nextRecipeId = SEED_RECIPES.length + 1;
    this.nextItemId = SEED_SHOPPING.length + 1;
    this.toast.set('Recipes reset to seed');
  }

  /** Quantities rounded to two decimals, without trailing zeros. */
  formatQty(value: number): string {
    return String(Math.round(value * 100) / 100);
  }
}
