export type Course = 'starter' | 'main' | 'side' | 'dessert';
export type Difficulty = 'easy' | 'medium' | 'hard';
export type CourseFilter = Course | 'all';
export type SortKey = 'title' | 'time' | 'rating';

export interface Ingredient {
  /** Unique within its recipe. */
  readonly id: number;
  readonly name: string;
  /** Quantity for the base number of servings of the recipe. */
  readonly quantity: number;
  readonly unit: string;
  readonly optional: boolean;
}

export interface Step {
  readonly n: number;
  readonly text: string;
  readonly minutes: number;
}

export interface Nutrition {
  /** Per serving. */
  readonly kcal: number;
  readonly protein: number;
  readonly carbs: number;
  readonly fat: number;
}

export interface Recipe {
  readonly id: number;
  readonly title: string;
  readonly course: Course;
  readonly difficulty: Difficulty;
  readonly servings: number;
  readonly prepMinutes: number;
  readonly cookMinutes: number;
  readonly vegetarian: boolean;
  readonly favorite: boolean;
  /** 0..5, one decimal. */
  readonly rating: number;
  readonly author: string;
  readonly tags: readonly string[];
  readonly ingredients: readonly Ingredient[];
  readonly steps: readonly Step[];
  readonly nutrition: Nutrition;
}

export interface ShoppingItem {
  readonly id: number;
  readonly recipeId: number;
  readonly name: string;
  readonly quantity: number;
  readonly unit: string;
  readonly done: boolean;
}

export interface ShoppingGroup {
  readonly recipeId: number;
  readonly title: string;
  readonly items: readonly ShoppingItem[];
}

export interface CourseStat {
  readonly course: Course;
  readonly count: number;
  readonly share: number;
}

/** Fields edited by the recipe form. */
export type RecipeDraft = Omit<Recipe, 'id' | 'favorite' | 'rating' | 'nutrition'>;
