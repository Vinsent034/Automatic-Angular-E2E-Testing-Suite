import { test, expect } from '@playwright/test';

/**
 * Reviews on the detail page.
 * Locator strategy in this file: FORM CONTROLS via label/role plus
 * data-attribute assertions — exercises a nested reactive form and a dynamic list.
 */
test.describe('Reviews', () => {
  test('seed movie shows its reviews and average', async ({ page }) => {
    await page.goto('/movie/3'); // Quiet Harbor: 3 reviews, avg 4.7
    await expect(page.locator('[x-test-review-item]')).toHaveCount(3);
    await expect(page.locator('[x-test-reviews-average]')).toHaveText('★ 4.7');
  });

  test('adding a review appends it to the list and recomputes the average', async ({ page }) => {
    await page.goto('/movie/4'); // Iron Valley: no reviews in the seed
    await expect(page.locator('[x-test-reviews-empty]')).toBeVisible();

    await page.locator('[x-test-review-field-author]').fill('Vince');
    await page.locator('[x-test-review-field-rating]').selectOption('5');
    await page.locator('[x-test-review-field-comment]').fill('Great call-back to the classics.');
    await page.locator('[x-test-review-submit]').click();

    await expect(page.locator('[x-test-review-item]')).toHaveCount(1);
    await expect(page.locator('[x-test-review-author]')).toHaveText('Vince');
    await expect(page.locator('[x-test-reviews-average]')).toHaveText('★ 5');
  });

  test('submitting an empty review shows validation errors', async ({ page }) => {
    await page.goto('/movie/4');
    await page.locator('[x-test-review-submit]').click();
    await expect(page.locator('[x-test-review-error-author]')).toBeVisible();
    await expect(page.locator('[x-test-review-error-comment]')).toBeVisible();
    await expect(page.locator('[x-test-review-item]')).toHaveCount(0);
  });

  test('removing a review drops it from the list', async ({ page }) => {
    await page.goto('/movie/3');
    await page.locator('[x-test-review-remove]').first().click();
    await expect(page.locator('[x-test-review-item]')).toHaveCount(2);
  });
});
