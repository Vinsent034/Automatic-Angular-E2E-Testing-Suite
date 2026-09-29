import { test, expect } from '@playwright/test';

/**
 * Catalog page.
 * Locator strategy in this file: mostly DATA-ATTRIBUTE (`x-test-*`) — the most
 * robust baseline against markup mutations.
 */
test.describe('Catalog', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('shows the full sample catalog on load', async ({ page }) => {
    await expect(page.locator('[x-test-result-count]')).toHaveText('8 movies');
    await expect(page.locator('[x-test-card]')).toHaveCount(8);
  });

  test('search filters by title', async ({ page }) => {
    await page.locator('[x-test-search-input]').fill('quiet');
    await expect(page.locator('[x-test-card]')).toHaveCount(1);
    await expect(page.locator('[x-test-card-title]')).toHaveText('Quiet Harbor');
  });

  test('search filters by genre keyword', async ({ page }) => {
    await page.locator('[x-test-search-input]').fill('drama');
    await expect(page.locator('[x-test-card]')).toHaveCount(2);
  });

  test('empty search shows the empty state', async ({ page }) => {
    await page.locator('[x-test-search-input]').fill('zzzzz');
    await expect(page.locator('[x-test-empty]')).toBeVisible();
    await expect(page.locator('[x-test-card]')).toHaveCount(0);
  });

  test('genre dropdown narrows the grid', async ({ page }) => {
    await page.locator('[x-test-filter-genre]').selectOption('Adventure');
    await expect(page.locator('[x-test-card]')).toHaveCount(2);
  });

  test('sort by top rated puts the highest rating first', async ({ page }) => {
    await page.locator('[x-test-sort]').selectOption('rating');
    await expect(page.locator('[x-test-card-title]').first()).toHaveText('Quiet Harbor');
  });

  test('list view toggle changes the grid layout class', async ({ page }) => {
    await page.locator('[x-test-view-list]').click();
    await expect(page.locator('[x-test-grid]')).toHaveClass(/movie-grid-list/);
    await page.locator('[x-test-view-grid]').click();
    await expect(page.locator('[x-test-grid]')).toHaveClass(/movie-grid-grid/);
  });
});
