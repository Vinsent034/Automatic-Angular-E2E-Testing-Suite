import { Recipe, ShoppingItem } from './models';

/**
 * Deterministic seed: fixed ids, titles and numbers. The app always boots from
 * this exact state and "Reset" restores it. Nothing here may come from
 * Date.now(), Math.random() or storage.
 *
 * Courses: main 3 (#1, #2, #6) - starter 2 (#3, #5) - dessert 2 (#4, #7) - side 1 (#8)
 * Vegetarian: 6 (#1, #2, #3, #4, #5, #8) - Favorites: 3 (#1, #3, #6)
 */
export const SEED_RECIPES: readonly Recipe[] = [
  {
    id: 1, title: 'Tomato Pasta', course: 'main', difficulty: 'easy', servings: 2,
    prepMinutes: 10, cookMinutes: 15, vegetarian: true, favorite: true, rating: 4.6, author: 'Giulia',
    tags: ['pasta', 'quick'],
    ingredients: [
      { id: 1, name: 'Spaghetti', quantity: 200, unit: 'g', optional: false },
      { id: 2, name: 'Tomato passata', quantity: 300, unit: 'ml', optional: false },
      { id: 3, name: 'Garlic', quantity: 1, unit: 'clove', optional: false },
      { id: 4, name: 'Basil', quantity: 6, unit: 'leaves', optional: false },
      { id: 5, name: 'Olive oil', quantity: 2, unit: 'tbsp', optional: false },
      { id: 6, name: 'Parmesan', quantity: 30, unit: 'g', optional: true }
    ],
    steps: [
      { n: 1, text: 'Boil the spaghetti in salted water.', minutes: 10 },
      { n: 2, text: 'Warm the oil with the garlic, then add the passata.', minutes: 12 },
      { n: 3, text: 'Toss the pasta with the sauce and the basil.', minutes: 3 }
    ],
    nutrition: { kcal: 520, protein: 16, carbs: 88, fat: 12 }
  },
  {
    id: 2, title: 'Mushroom Risotto', course: 'main', difficulty: 'medium', servings: 4,
    prepMinutes: 15, cookMinutes: 25, vegetarian: true, favorite: false, rating: 4.4, author: 'Marco',
    tags: ['rice', 'comfort'],
    ingredients: [
      { id: 1, name: 'Arborio rice', quantity: 320, unit: 'g', optional: false },
      { id: 2, name: 'Mushrooms', quantity: 400, unit: 'g', optional: false },
      { id: 3, name: 'Onion', quantity: 1, unit: 'piece', optional: false },
      { id: 4, name: 'Vegetable stock', quantity: 1, unit: 'l', optional: false },
      { id: 5, name: 'Butter', quantity: 40, unit: 'g', optional: false },
      { id: 6, name: 'Parmesan', quantity: 50, unit: 'g', optional: true }
    ],
    steps: [
      { n: 1, text: 'Soften the onion in half of the butter.', minutes: 6 },
      { n: 2, text: 'Toast the rice, then add the stock a ladle at a time.', minutes: 18 },
      { n: 3, text: 'Stir in the mushrooms and finish with butter.', minutes: 6 }
    ],
    nutrition: { kcal: 610, protein: 15, carbs: 92, fat: 19 }
  },
  {
    id: 3, title: 'Greek Salad', course: 'starter', difficulty: 'easy', servings: 2,
    prepMinutes: 15, cookMinutes: 0, vegetarian: true, favorite: true, rating: 4.2, author: 'Sara',
    tags: ['salad', 'fresh'],
    ingredients: [
      { id: 1, name: 'Tomatoes', quantity: 3, unit: 'piece', optional: false },
      { id: 2, name: 'Cucumber', quantity: 1, unit: 'piece', optional: false },
      { id: 3, name: 'Feta', quantity: 150, unit: 'g', optional: false },
      { id: 4, name: 'Black olives', quantity: 60, unit: 'g', optional: false },
      { id: 5, name: 'Red onion', quantity: 0.5, unit: 'piece', optional: false },
      { id: 6, name: 'Oregano', quantity: 1, unit: 'tsp', optional: true }
    ],
    steps: [
      { n: 1, text: 'Cut tomatoes, cucumber and onion into chunks.', minutes: 10 },
      { n: 2, text: 'Top with feta and olives, season and serve.', minutes: 5 }
    ],
    nutrition: { kcal: 330, protein: 11, carbs: 12, fat: 27 }
  },
  {
    id: 4, title: 'Tiramisu', course: 'dessert', difficulty: 'medium', servings: 6,
    prepMinutes: 30, cookMinutes: 0, vegetarian: true, favorite: false, rating: 4.8, author: 'Anna',
    tags: ['coffee', 'no-bake'],
    ingredients: [
      { id: 1, name: 'Mascarpone', quantity: 500, unit: 'g', optional: false },
      { id: 2, name: 'Eggs', quantity: 4, unit: 'piece', optional: false },
      { id: 3, name: 'Sugar', quantity: 100, unit: 'g', optional: false },
      { id: 4, name: 'Ladyfingers', quantity: 300, unit: 'g', optional: false },
      { id: 5, name: 'Espresso', quantity: 300, unit: 'ml', optional: false },
      { id: 6, name: 'Cocoa powder', quantity: 2, unit: 'tbsp', optional: false }
    ],
    steps: [
      { n: 1, text: 'Whisk the yolks with the sugar, fold in the mascarpone.', minutes: 10 },
      { n: 2, text: 'Dip the ladyfingers in espresso and layer with the cream.', minutes: 15 },
      { n: 3, text: 'Dust with cocoa and chill before serving.', minutes: 5 }
    ],
    nutrition: { kcal: 540, protein: 10, carbs: 48, fat: 34 }
  },
  {
    id: 5, title: 'Minestrone', course: 'starter', difficulty: 'easy', servings: 4,
    prepMinutes: 20, cookMinutes: 40, vegetarian: true, favorite: false, rating: 4.0, author: 'Luca',
    tags: ['soup', 'vegetables'],
    ingredients: [
      { id: 1, name: 'Carrots', quantity: 2, unit: 'piece', optional: false },
      { id: 2, name: 'Celery', quantity: 2, unit: 'stalk', optional: false },
      { id: 3, name: 'Zucchini', quantity: 1, unit: 'piece', optional: false },
      { id: 4, name: 'Cannellini beans', quantity: 400, unit: 'g', optional: false },
      { id: 5, name: 'Small pasta', quantity: 100, unit: 'g', optional: true }
    ],
    steps: [
      { n: 1, text: 'Dice all the vegetables.', minutes: 15 },
      { n: 2, text: 'Simmer the vegetables with the beans in water.', minutes: 35 },
      { n: 3, text: 'Add the pasta and cook until tender.', minutes: 10 }
    ],
    nutrition: { kcal: 290, protein: 12, carbs: 45, fat: 6 }
  },
  {
    id: 6, title: 'Lemon Chicken', course: 'main', difficulty: 'medium', servings: 4,
    prepMinutes: 10, cookMinutes: 35, vegetarian: false, favorite: true, rating: 4.5, author: 'Marco',
    tags: ['chicken', 'oven'],
    ingredients: [
      { id: 1, name: 'Chicken thighs', quantity: 8, unit: 'piece', optional: false },
      { id: 2, name: 'Lemons', quantity: 2, unit: 'piece', optional: false },
      { id: 3, name: 'Rosemary', quantity: 2, unit: 'sprig', optional: false },
      { id: 4, name: 'Olive oil', quantity: 3, unit: 'tbsp', optional: false }
    ],
    steps: [
      { n: 1, text: 'Marinate the chicken with lemon, oil and rosemary.', minutes: 10 },
      { n: 2, text: 'Roast until golden.', minutes: 35 }
    ],
    nutrition: { kcal: 480, protein: 42, carbs: 4, fat: 32 }
  },
  {
    id: 7, title: 'Panna Cotta', course: 'dessert', difficulty: 'easy', servings: 4,
    prepMinutes: 15, cookMinutes: 5, vegetarian: false, favorite: false, rating: 4.3, author: 'Giulia',
    tags: ['cream', 'make-ahead'],
    ingredients: [
      { id: 1, name: 'Cream', quantity: 500, unit: 'ml', optional: false },
      { id: 2, name: 'Sugar', quantity: 80, unit: 'g', optional: false },
      { id: 3, name: 'Gelatin', quantity: 8, unit: 'g', optional: false },
      { id: 4, name: 'Vanilla pod', quantity: 1, unit: 'piece', optional: true }
    ],
    steps: [
      { n: 1, text: 'Warm the cream with the sugar and vanilla.', minutes: 5 },
      { n: 2, text: 'Dissolve the gelatin, pour into moulds and chill.', minutes: 15 }
    ],
    nutrition: { kcal: 420, protein: 4, carbs: 26, fat: 33 }
  },
  {
    id: 8, title: 'Roast Potatoes', course: 'side', difficulty: 'easy', servings: 4,
    prepMinutes: 10, cookMinutes: 45, vegetarian: true, favorite: false, rating: 4.1, author: 'Luca',
    tags: ['potatoes', 'oven'],
    ingredients: [
      { id: 1, name: 'Potatoes', quantity: 1, unit: 'kg', optional: false },
      { id: 2, name: 'Olive oil', quantity: 4, unit: 'tbsp', optional: false },
      { id: 3, name: 'Garlic', quantity: 3, unit: 'clove', optional: false },
      { id: 4, name: 'Thyme', quantity: 1, unit: 'tsp', optional: true }
    ],
    steps: [
      { n: 1, text: 'Cut the potatoes and toss them with oil and garlic.', minutes: 10 },
      { n: 2, text: 'Roast, turning once, until crisp.', minutes: 45 }
    ],
    nutrition: { kcal: 310, protein: 6, carbs: 48, fat: 11 }
  }
];

/** Three lines from recipe #2, one already bought. */
export const SEED_SHOPPING: readonly ShoppingItem[] = [
  { id: 1, recipeId: 2, name: 'Arborio rice', quantity: 320, unit: 'g', done: false },
  { id: 2, recipeId: 2, name: 'Mushrooms', quantity: 400, unit: 'g', done: true },
  { id: 3, recipeId: 2, name: 'Vegetable stock', quantity: 1, unit: 'l', done: false }
];
