import { Component, OnInit, inject, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CookStore } from '../cook-store';
import { Course, Difficulty, Ingredient, RecipeDraft, Step } from '../models';
import { PanelComponent } from '../panel/panel.component';

@Component({
  selector: 'app-recipe-form',
  imports: [FormsModule, RouterLink, PanelComponent],
  templateUrl: './recipe-form.component.html'
})
export class RecipeFormComponent implements OnInit {
  readonly store = inject(CookStore);
  private readonly router = inject(Router);

  /** Route param on /recipe/:id/edit; undefined on /recipe/new. */
  readonly id = input<string>();

  readonly editing = signal(false);
  readonly error = signal('');

  title = '';
  course: Course = 'main';
  difficulty: Difficulty = 'easy';
  servings = 2;
  prepMinutes = 10;
  cookMinutes = 20;
  vegetarian = true;
  author = '';
  tagsText = '';
  ingredientsText = '';
  stepsText = '';

  ngOnInit(): void {
    const idParam = this.id();
    if (idParam === undefined) return;
    const recipe = this.store.recipes().find(r => r.id === Number(idParam));
    if (recipe === undefined) return;
    this.editing.set(true);
    this.title = recipe.title;
    this.course = recipe.course;
    this.difficulty = recipe.difficulty;
    this.servings = recipe.servings;
    this.prepMinutes = recipe.prepMinutes;
    this.cookMinutes = recipe.cookMinutes;
    this.vegetarian = recipe.vegetarian;
    this.author = recipe.author;
    this.tagsText = recipe.tags.join(', ');
    this.ingredientsText = recipe.ingredients.map(i => `${i.quantity} ${i.unit} ${i.name}`).join('\n');
    this.stepsText = recipe.steps.map(s => s.text).join('\n');
  }

  submit(): void {
    if (this.title.trim() === '') {
      this.error.set('Title is required');
      return;
    }
    this.error.set('');
    const draft: RecipeDraft = {
      title: this.title.trim(),
      course: this.course,
      difficulty: this.difficulty,
      servings: Math.max(1, Number(this.servings) || 1),
      prepMinutes: Math.max(0, Number(this.prepMinutes) || 0),
      cookMinutes: Math.max(0, Number(this.cookMinutes) || 0),
      vegetarian: this.vegetarian,
      author: this.author.trim() || 'Anonymous',
      tags: this.tagsText.split(',').map(t => t.trim()).filter(t => t !== ''),
      ingredients: this.parseIngredients(),
      steps: this.parseSteps()
    };
    if (this.editing()) {
      const id = Number(this.id());
      this.store.updateRecipe(id, draft);
      this.router.navigate(['/recipe', id]);
    } else {
      const id = this.store.addRecipe(draft);
      this.router.navigate(['/recipe', id]);
    }
  }

  /** One ingredient per line: "quantity unit name", e.g. "200 g Spaghetti". */
  private parseIngredients(): Ingredient[] {
    return this.ingredientsText.split('\n').map(l => l.trim()).filter(l => l !== '')
      .map((line, index) => {
        const [qty, unit, ...name] = line.split(/\s+/);
        return {
          id: index + 1, name: name.join(' ') || unit || qty,
          quantity: Number(qty) || 1, unit: name.length > 0 ? unit : 'piece', optional: false
        };
      });
  }

  private parseSteps(): Step[] {
    return this.stepsText.split('\n').map(l => l.trim()).filter(l => l !== '')
      .map((text, index) => ({ n: index + 1, text, minutes: 0 }));
  }
}
