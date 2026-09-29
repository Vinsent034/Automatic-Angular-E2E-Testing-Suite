import { test, expect } from '@playwright/test';

/**
 * Add / edit form.
 * Locator strategy in this file: LABEL + ID association (getByLabel) — close to
 * how a user perceives the form, sensitive to label-text mutations.
 */
test.describe('Movie form', () => {
  test('creating a movie navigates to its new detail page', async ({ page }) => {
    await page.goto('/movie/new');
    await page.getByLabel('Title').fill('The Test Pattern');
    await page.getByLabel('Year').fill('2025');
    await page.getByLabel('Genre').selectOption('Sci-Fi');
    await page.getByLabel('Rating').fill('4.7');
    await page.getByLabel('Runtime (minutes)').fill('118');
    await page.getByLabel('Director').fill('A. Mutant');

    await page.locator('[x-test-form-submit]').click();

    await expect(page.locator('[x-test-detail-title]')).toHaveText('The Test Pattern');
    await expect(page.locator('[x-test-detail-meta]')).toContainText('Sci-Fi');
  });

  test('submitting an empty form surfaces required-field errors', async ({ page }) => {
    await page.goto('/movie/new');
    await page.locator('[x-test-form-submit]').click();
    await expect(page.locator('[x-test-error-title]')).toBeVisible();
    await expect(page.locator('[x-test-error-director]')).toBeVisible();
  });

  test('adding then removing a cast member updates the editor rows', async ({ page }) => {
    await page.goto('/movie/new');
    await page.locator('[x-test-cast-add]').click();
    await page.locator('[x-test-cast-add]').click();
    await expect(page.locator('[x-test-cast-edit-row]')).toHaveCount(2);
    await page.locator('[x-test-cast-remove]').first().click();
    await expect(page.locator('[x-test-cast-edit-row]')).toHaveCount(1);
  });

  test('editing an existing movie pre-fills and saves changes', async ({ page }) => {
    await page.goto('/movie/2/edit'); // Neon Nights
    await expect(page.getByLabel('Title')).toHaveValue('Neon Nights');

    await page.getByLabel('Title').fill('Neon Nights (Remastered)');
    await page.locator('[x-test-form-submit]').click();

    await expect(page.locator('[x-test-detail-title]')).toHaveText('Neon Nights (Remastered)');
  });
});
