import { test, expect } from '@playwright/test';

/**
 * Movie detail page.
 * Locator strategy in this file: TEXT-BASED (getByText / getByRole name) plus
 * one XPath example — fragile to copy/wording mutations, on purpose.
 */
test.describe('Movie detail', () => {
  test('navigating from a card opens the matching movie', async ({ page }) => {
    await page.goto('/');
    await page.getByRole('heading', { name: 'Quiet Harbor' }).click();
    await expect(page).toHaveURL(/\/movie\/3$/);
    await expect(page.locator('[x-test-detail-title]')).toHaveText('Quiet Harbor');
  });

  test('detail meta line carries year, genre, runtime and rating', async ({ page }) => {
    await page.goto('/movie/3');
    await expect(page.locator('[x-test-detail-meta]')).toHaveText('2022 · Drama · 127 min · ★ 4.8');
  });

  test('cast list renders every member', async ({ page }) => {
    await page.goto('/movie/3');
    await expect(page.locator('[x-test-cast-row]')).toHaveCount(3);
    // XPath example: second cast row's name cell.
    const secondName = page.locator('xpath=(//*[@x-test-cast-row])[2]//*[@x-test-cast-name]');
    await expect(secondName).toHaveText('Sam Whitfield');
  });

  test('delete shows a confirm modal and cancel keeps the movie', async ({ page }) => {
    await page.goto('/movie/3');
    await page.getByRole('button', { name: 'Delete' }).click();
    await expect(page.locator('[x-test-delete-modal]')).toBeVisible();
    await page.locator('[x-test-modal-cancel]').click();
    await expect(page.locator('[x-test-delete-modal]')).toBeHidden();
    await expect(page).toHaveURL(/\/movie\/3$/);
  });

  test('confirming delete removes the movie and returns to the catalog', async ({ page }) => {
    await page.goto('/movie/3');
    await page.getByRole('button', { name: 'Delete' }).click();
    await page.locator('[x-test-modal-confirm]').click();
    await expect(page).toHaveURL(/\/$|\/#?$/);
    await expect(page.locator('[x-test-card]')).toHaveCount(7);
  });

  test('unknown id shows the not-found state', async ({ page }) => {
    await page.goto('/movie/9999');
    await expect(page.locator('[x-test-not-found]')).toBeVisible();
  });
});
