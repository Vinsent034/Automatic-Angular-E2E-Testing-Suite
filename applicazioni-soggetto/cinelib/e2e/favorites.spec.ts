import { test, expect } from '@playwright/test';

/**
 * Favorites feature.
 * Locator strategy in this file: ROLE + ACCESSIBLE NAME (getByRole) and
 * ARIA state ([aria-pressed]) — resilient to class/markup changes, sensitive
 * to label/role mutations.
 */
test.describe('Favorites', () => {
  test('seed catalog starts with three favorites', async ({ page }) => {
    await page.goto('/');
    await expect(page.getByRole('button', { name: /Favorites \(\d+\)/ })).toContainText('Favorites (3)');
  });

  test('toggling a favorite on a card updates aria-pressed and shows a toast', async ({ page }) => {
    await page.goto('/');
    // First card in default A–Z order is "Deep Signal" (already a favorite).
    const firstToggle = page.locator('[x-test-favorite-toggle]').first();
    await expect(firstToggle).toHaveAttribute('aria-pressed', 'true');

    await firstToggle.click();
    await expect(firstToggle).toHaveAttribute('aria-pressed', 'false');
    await expect(page.locator('[x-test-toast-message]')).toContainText('Removed');
  });

  test('favorites-only filter narrows the catalog', async ({ page }) => {
    await page.goto('/');
    await page.locator('[x-test-favorites-filter]').click();
    await expect(page.locator('[x-test-card]')).toHaveCount(3);
  });

  test('favorite toggled on the detail page is reflected in the label', async ({ page }) => {
    await page.goto('/movie/2'); // Neon Nights — not a favorite in the seed
    const button = page.locator('[x-test-detail-favorite]');
    await expect(button).toHaveText(/Add to favorites/);
    await button.click();
    await expect(button).toHaveText(/In favorites/);
  });
});
