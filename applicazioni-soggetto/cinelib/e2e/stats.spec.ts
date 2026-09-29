import { test, expect } from '@playwright/test';

/**
 * Statistics page.
 * Locator strategy in this file: CSS CLASS and NTH-POSITION — the most
 * mutation-fragile strategies, kept here to contrast with the data-attribute
 * specs when measuring locator survival rates.
 */
test.describe('Statistics', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/stats');
  });

  test('summary cards report the seed totals', async ({ page }) => {
    await expect(page.locator('[x-test-stat-total-value]')).toHaveText('8');
    await expect(page.locator('[x-test-stat-favorites-value]')).toHaveText('3');
    await expect(page.locator('[x-test-stat-reviews-value]')).toHaveText('10');
  });

  test('genre bars list each distinct genre', async ({ page }) => {
    await expect(page.locator('.genre-bar-row')).toHaveCount(6);
    // nth-position: the first (most frequent) genre bar.
    await expect(page.locator('.genre-bar-count').first()).toHaveText('2');
  });

  test('top-rated table is ordered by rating descending', async ({ page }) => {
    const rows = page.locator('[x-test-top-row]');
    await expect(rows).toHaveCount(3);
    await expect(page.locator('[x-test-top-title]').first()).toHaveText('Quiet Harbor');
  });

  test('a top-rated title links to its detail page', async ({ page }) => {
    await page.locator('[x-test-top-title]').first().click();
    await expect(page).toHaveURL(/\/movie\/3$/);
  });
});
