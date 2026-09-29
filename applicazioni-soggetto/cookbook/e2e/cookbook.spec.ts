import { expect, test } from '@playwright/test';

/**
 * CookBook reference scenarios. One describe block per scenario, so the number
 * of test cases per scenario is visible at a glance (see SCENARIOS-cookbook.md).
 *
 * State is in-memory only and seeded deterministically: a fresh page load IS
 * the seed state, so no storage cleanup is needed. Locators mix the x-test
 * ground truth with data-*, role, text and CSS class, as in CineLib and FlowBoard.
 *
 * Seed: 8 recipes (main 3, starter 2, side 1, dessert 2), 6 vegetarian,
 * 3 favorites (#1, #3, #6); shopping list with 3 lines of recipe #2, 1 bought.
 */

test.describe('S1 - recipe list: search', () => {
  test.beforeEach(async ({ page }) => { await page.goto('/'); });

  test('search "risotto" shows exactly one card, recipe #2', async ({ page }) => {
    await expect(page.locator('[x-test-result-count]')).toHaveText('8');
    await page.locator('[x-test-search]').fill('risotto');
    await expect(page.locator('.recipe-card')).toHaveCount(1);
    await expect(page.locator('[x-test-card="2"]')).toBeVisible();
    await expect(page.locator('[x-test-card-title="2"]')).toHaveText('Mushroom Risotto');
  });

  test('search by tag "oven" shows recipes #6 and #8', async ({ page }) => {
    await page.locator('[x-test-search]').fill('oven');
    await expect(page.locator('[x-test-result-count]')).toHaveText('2');
    await expect(page.locator('[x-test-card="6"]')).toBeVisible();
    await expect(page.locator('[x-test-card="8"]')).toBeVisible();
  });

  test('a search with no match shows the empty state', async ({ page }) => {
    await page.locator('[x-test-search]').fill('sushi');
    await expect(page.locator('.recipe-card')).toHaveCount(0);
    await expect(page.locator('[x-test-empty]')).toHaveText('No recipes match your filters.');
  });
});

test.describe('S2 - recipe list: filters, sort and favorites', () => {
  test.beforeEach(async ({ page }) => { await page.goto('/'); });

  test('vegetarian only leaves 6 recipes', async ({ page }) => {
    await page.locator('[x-test-veg-only]').check();
    await expect(page.locator('[x-test-result-count]')).toHaveText('6');
    await expect(page.locator('.recipe-card')).toHaveCount(6);
  });

  test('vegetarian desserts are only Tiramisu', async ({ page }) => {
    await page.locator('[x-test-veg-only]').check();
    await page.locator('[x-test-course-filter]').selectOption('dessert');
    await expect(page.locator('[x-test-result-count]')).toHaveText('1');
    await expect(page.locator('[x-test-card-title="4"]')).toHaveText('Tiramisu');
  });

  test('sorting by rating puts Tiramisu first', async ({ page }) => {
    await page.locator('[x-test-sort]').selectOption('rating');
    await expect(page.locator('.card-title').first()).toHaveText('Tiramisu');
  });

  test('toggling a favorite updates aria-pressed and the toast', async ({ page }) => {
    const fav = page.locator('[x-test-card-fav="2"]');
    await expect(fav).toHaveAttribute('aria-pressed', 'false');
    await fav.click();
    await expect(fav).toHaveAttribute('aria-pressed', 'true');
    await expect(page.locator('[x-test-toast-message]')).toHaveText('Added "Mushroom Risotto" to favorites');
  });
});

test.describe('S3 - recipe detail and servings', () => {
  test('detail of #1 shows title, author and six ingredients', async ({ page }) => {
    await page.goto('/recipe/1');
    await expect(page.locator('[x-test-detail-title]')).toHaveText('Tomato Pasta');
    await expect(page.locator('[x-test-detail-author]')).toHaveText('Giulia');
    await expect(page.locator('app-ingredient-row')).toHaveCount(6);
  });

  test('changing servings from 2 to 4 doubles the quantities', async ({ page }) => {
    await page.goto('/recipe/1');
    await expect(page.locator('[x-test-ing-qty="1"]')).toHaveText('200');
    await page.locator('[x-test-servings]').fill('4');
    await page.locator('[x-test-servings]').press('Tab');
    await expect(page.locator('[x-test-ing-qty="1"]')).toHaveText('400');
    await expect(page.locator('[x-test-ing-name="1"]')).toHaveText('Spaghetti');
  });

  test('nutrition panel shows 520 kcal and 16 g of protein', async ({ page }) => {
    await page.goto('/recipe/1');
    await expect(page.locator('[x-test-kcal]')).toHaveText('520');
    await expect(page.locator('[x-test-protein]')).toHaveText('16');
  });

  test('an unknown id shows the not-found state', async ({ page }) => {
    await page.goto('/recipe/99');
    await expect(page.locator('[x-test-not-found]')).toHaveText('Recipe not found');
  });
});

test.describe('S4 - recipe form', () => {
  test('the new-recipe form starts empty with "Create recipe"', async ({ page }) => {
    await page.goto('/recipe/new');
    await expect(page.locator('[x-test-form-heading]')).toHaveText('New recipe');
    await expect(page.locator('[x-test-f-title]')).toHaveValue('');
    await expect(page.locator('[x-test-f-submit]')).toHaveText('Create recipe');
  });

  test('creating a recipe opens its detail page', async ({ page }) => {
    await page.goto('/recipe/new');
    await page.locator('[x-test-f-title]').fill('Pesto Gnocchi');
    await page.locator('[x-test-f-course]').selectOption('main');
    await page.locator('[x-test-f-ingredients]').fill('500 g Gnocchi\n3 tbsp Pesto');
    await page.locator('[x-test-f-submit]').click();
    await expect(page).toHaveURL(/\/recipe\/9$/);
    await expect(page.locator('[x-test-detail-title]')).toHaveText('Pesto Gnocchi');
  });

  test('an empty title shows the required-field error', async ({ page }) => {
    await page.goto('/recipe/new');
    await page.locator('[x-test-f-submit]').click();
    await expect(page.locator('.form-error')).toHaveText('Title is required');
    await expect(page).toHaveURL(/\/recipe\/new$/);
  });

  test('the edit form is pre-filled', async ({ page }) => {
    await page.goto('/recipe/3/edit');
    await expect(page.locator('[x-test-form-heading]')).toHaveText('Edit recipe');
    await expect(page.locator('[x-test-f-title]')).toHaveValue('Greek Salad');
    await expect(page.locator('[x-test-f-submit]')).toHaveText('Save changes');
  });
});

test.describe('S5 - shopping list', () => {
  test('the seed list has 2 lines to buy and 1 bought', async ({ page }) => {
    await page.goto('/shopping');
    await expect(page.locator('[x-test-open-count]')).toHaveText('2');
    await expect(page.locator('[x-test-done-count]')).toHaveText('1');
  });

  test('checking a line moves it to bought', async ({ page }) => {
    await page.goto('/shopping');
    await page.locator('[x-test-shop-check="1"]').check();
    await expect(page.locator('[x-test-open-count]')).toHaveText('1');
    await expect(page.locator('[x-test-done-count]')).toHaveText('2');
  });

  test('clearing bought items removes the checked lines', async ({ page }) => {
    await page.goto('/shopping');
    await page.locator('[x-test-clear-done]').click();
    await expect(page.locator('.shop-item')).toHaveCount(2);
    await expect(page.locator('[x-test-done-count]')).toHaveText('0');
  });

  test('adding recipe #1 from its page adds five lines', async ({ page }) => {
    await page.goto('/recipe/1');
    await page.locator('[x-test-add-shopping]').click();
    await expect(page.locator('[x-test-toast-message]')).toHaveText('Added 5 items from "Tomato Pasta"');
    await page.locator('[x-test-nav-shopping]').click();
    await expect(page.locator('[x-test-open-count]')).toHaveText('7');
  });
});

test.describe('S6 - statistics and application shell', () => {
  test.beforeEach(async ({ page }) => { await page.goto('/stats'); });

  test('summary cards report the seed totals', async ({ page }) => {
    await expect(page.locator('[x-test-stat-total]')).toHaveText('8');
    await expect(page.locator('[x-test-stat-veg]')).toHaveText('6');
    await expect(page.locator('[x-test-stat-time]')).toHaveText('36');
    await expect(page.locator('[x-test-stat-fav]')).toHaveText('3');
  });

  test('recipes by course: main 3, starter 2, side 1, dessert 2', async ({ page }) => {
    await expect(page.locator('[x-test-course-count="main"]')).toHaveText('3');
    await expect(page.locator('[x-test-course-count="starter"]')).toHaveText('2');
    await expect(page.locator('[x-test-course-count="side"]')).toHaveText('1');
    await expect(page.locator('[x-test-course-count="dessert"]')).toHaveText('2');
  });

  test('the best rated recipe is Tiramisu', async ({ page }) => {
    await expect(page.locator('.top-link').first()).toHaveText('Tiramisu');
    await expect(page.locator('[x-test-top-title="4"]')).toHaveAttribute('href', '/recipe/4');
  });

  test('footer version and navigation are in place', async ({ page }) => {
    await expect(page.locator('[x-test-footer-version]')).toHaveText('v1.0');
    await expect(page.locator('[x-test-brand-name]')).toHaveText('CookBook');
    await page.locator('[x-test-nav-stats]').click();
    await expect(page.locator('[x-test-stats-title]')).toHaveText('Statistics');
  });
});
