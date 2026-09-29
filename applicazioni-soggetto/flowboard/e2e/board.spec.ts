import { expect, test } from '@playwright/test';

/**
 * FlowBoard e2e suite, organised by the six reference scenarios (S1-S6) used by
 * the robustness campaigns. The nine original tests (July 2026) are kept verbatim;
 * fifteen were added in September 2026 (FlowBoard v2) to bring the suite in line
 * with CineLib (29) and CookBook (23): 24 tests, 4 per scenario.
 *
 * State is in-memory only, so a fresh page load IS the seed state — no
 * localStorage cleanup is required. Locator styles are mixed on purpose
 * (x-test ground truth, data-*, role, text, CSS class) as in CineLib.
 *
 * Seed: Backlog #1 #2 #5 #10 · In Progress #3 #4 · Review #6 #7 · Done #8 #9;
 * high #1 #3 #7 · medium #2 #4 #6 #8 · low #5 #9 #10;
 * Anna #1 #4 #9 · Luca #2 #8 #10 · Marco #3 #7 · Sara #5 #6.
 */

test.describe('S1 - search and filters', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('search "login" shows exactly one card, titled "Design login"', async ({ page }) => {
    // pre: 10 cards on the board
    await expect(page.locator('[x-test-total-badge]')).toHaveText('10');

    await page.locator('[x-test-search]').fill('login');

    // post: one visible card, and it is "Design login"
    await expect(page.locator('.card')).toHaveCount(1);
    await expect(page.locator('[x-test-filtered-count]')).toHaveText('1');
    await expect(page.locator('[x-test-card-title="3"]')).toHaveText('Design login');
  });

  test('priority filter "High" leaves only cards with data-priority="high"', async ({ page }) => {
    // pre: total badge = 10
    await expect(page.locator('[x-test-total-badge]')).toHaveText('10');

    await page.locator('[x-test-filter-priority]').selectOption('high');

    // post: only high-priority cards remain (seed has 3: #1, #3, #7)
    await expect(page.locator('.card')).toHaveCount(3);
    await expect(page.locator('.card[data-priority="high"]')).toHaveCount(3);
    await expect(page.locator('.card[data-priority="low"], .card[data-priority="medium"]')).toHaveCount(0);
  });

  test('assignee filter "Sara" leaves cards #5 and #6', async ({ page }) => {
    await page.locator('[x-test-filter-assignee]').selectOption('Sara');

    await expect(page.locator('[x-test-filtered-count]')).toHaveText('2');
    await expect(page.locator('[x-test-card="5"]')).toBeVisible();
    await expect(page.locator('[x-test-card="6"]')).toBeVisible();
    await expect(page.locator('.card')).toHaveCount(2);
  });

  test('a search with no match empties every column', async ({ page }) => {
    await page.locator('[x-test-search]').fill('zzz');

    await expect(page.locator('[x-test-filtered-count]')).toHaveText('0');
    await expect(page.locator('.card')).toHaveCount(0);
    await expect(page.locator('[x-test-empty-message]')).toHaveCount(4);
    await expect(page.locator('[x-test-empty-message]').first()).toHaveText('No cards here');
  });
});

test.describe('S2 - columns and moves', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  test('moving card #1 to In Progress updates both column counts', async ({ page }) => {
    // pre: card #1 sits in Backlog; Backlog has 4 cards, In Progress 2
    await expect(page.locator('[x-test-card="1"]')).toHaveAttribute('data-column', 'backlog');
    await expect(page.locator('[x-test-column-count="backlog"]')).toHaveText('4');
    await expect(page.locator('[x-test-column-count="inprogress"]')).toHaveText('2');

    await page.locator('[x-test-card-move="1"]').selectOption('inprogress');

    // post: Backlog -1, In Progress +1, card lives in the new column
    await expect(page.locator('[x-test-column-count="backlog"]')).toHaveText('3');
    await expect(page.locator('[x-test-column-count="inprogress"]')).toHaveText('3');
    await expect(page.locator('section[data-column="inprogress"] [x-test-card="1"]')).toBeVisible();
  });

  test('the seed columns are titled and counted 4 / 2 / 2 / 2', async ({ page }) => {
    await expect(page.locator('[x-test-column-title="backlog"]')).toHaveText('Backlog');
    await expect(page.locator('[x-test-column-title="done"]')).toHaveText('Done');
    await expect(page.locator('[x-test-column-count="backlog"]')).toHaveText('4');
    await expect(page.locator('[x-test-column-count="inprogress"]')).toHaveText('2');
    await expect(page.locator('[x-test-column-count="review"]')).toHaveText('2');
    await expect(page.locator('[x-test-column-count="done"]')).toHaveText('2');
  });

  test('moving card #8 back to Backlog updates counts and the toast', async ({ page }) => {
    await page.locator('[x-test-card-move="8"]').selectOption('backlog');

    await expect(page.locator('[x-test-column-count="done"]')).toHaveText('1');
    await expect(page.locator('[x-test-column-count="backlog"]')).toHaveText('5');
    await expect(page.locator('[x-test-toast]')).toHaveText('Card #8 moved to Backlog');
  });

  test('deleting card #5 drops the total to 9 and removes the card', async ({ page }) => {
    // pre: card #5 exists
    await expect(page.locator('[x-test-card="5"]')).toBeVisible();

    await page.locator('[x-test-card-delete="5"]').click();

    // post: total badge = 9, card gone, toast confirms
    await expect(page.locator('[x-test-total-badge]')).toHaveText('9');
    await expect(page.locator('[x-test-card="5"]')).toHaveCount(0);
    await expect(page.locator('[x-test-toast]')).toHaveText('Card #5 deleted');
  });
});

test.describe('S3 - card form', () => {
  test('adding a card from /card/new brings the total to 11', async ({ page }) => {
    await page.goto('/');
    // pre: empty form on /card/new
    await page.getByRole('button', { name: 'New card' }).click();
    await expect(page).toHaveURL(/\/card\/new$/);
    await expect(page.locator('[x-test-form-title]')).toHaveValue('');

    await page.locator('[x-test-form-title]').fill('Try the new board');
    await page.locator('[x-test-form-description]').fill('Added by the e2e suite.');
    await page.locator('[x-test-form-priority]').selectOption('high');
    await page.locator('[x-test-form-assignee]').fill('Vince');
    await page.locator('[x-test-form-column]').selectOption('review');
    await page.locator('[x-test-form-tags]').fill('e2e, demo');
    await page.locator('[x-test-form-submit]').click();

    // post: total badge = 11 and the card sits in the chosen column
    await expect(page.locator('[x-test-total-badge]')).toHaveText('11');
    await expect(
      page.locator('section[data-column="review"] .card-title', { hasText: 'Try the new board' })
    ).toBeVisible();
  });

  test('the new-card form starts with the default values', async ({ page }) => {
    await page.goto('/card/new');

    await expect(page.locator('[x-test-form-title-new]')).toHaveText('New card');
    await expect(page.locator('[x-test-form-title]')).toHaveValue('');
    await expect(page.locator('[x-test-form-priority]')).toHaveValue('medium');
    await expect(page.locator('[x-test-form-column]')).toHaveValue('backlog');
    await expect(page.locator('[x-test-form-submit]')).toHaveText('Save card');
  });

  test('saving with an empty title keeps the form open and adds nothing', async ({ page }) => {
    await page.goto('/card/new');

    await page.locator('[x-test-form-submit]').click();

    await expect(page).toHaveURL(/\/card\/new$/);
    await expect(page.locator('[x-test-total-badge]')).toHaveText('10');
  });

  test('editing card #2 prefills the form and saves the new priority', async ({ page }) => {
    await page.goto('/card/2/edit');

    await expect(page.locator('[x-test-form-title-edit]')).toHaveText('Edit card');
    await expect(page.locator('[x-test-form-title]')).toHaveValue('Write project brief');
    await expect(page.locator('[x-test-form-assignee]')).toHaveValue('Luca');

    await page.locator('[x-test-form-priority]').selectOption('high');
    await page.locator('[x-test-form-submit]').click();

    await expect(page).toHaveURL(/\/$/);
    await expect(page.locator('[x-test-card-priority="2"]')).toHaveText('high');
    await expect(page.locator('[x-test-toast]')).toHaveText('Card #2 updated');
  });
});

test.describe('S4 - card detail', () => {
  test('opening card #3 shows "Design login" in the detail page', async ({ page }) => {
    await page.goto('/');
    // pre: board loaded from seed
    await page.locator('[x-test-card-open="3"]').click();

    // post: detail is store-bound to card #3
    await expect(page).toHaveURL(/\/card\/3$/);
    await expect(page.locator('[x-test-detail-title]')).toHaveText('Design login');
    await expect(page.locator('[x-test-detail-assignee]')).toHaveText('Marco');
  });

  test('the detail of card #1 lists priority, column and tags', async ({ page }) => {
    await page.goto('/card/1');

    await expect(page.locator('[x-test-detail-id]')).toHaveText('#1');
    await expect(page.locator('[x-test-detail-priority]')).toHaveText('high');
    await expect(page.locator('[x-test-detail-column]')).toHaveText('backlog');
    await expect(page.locator('[x-test-detail-tags] .tag')).toHaveText(['infra', 'setup']);
  });

  test('deleting card #4 from its detail page returns to the board', async ({ page }) => {
    await page.goto('/card/4');

    await page.locator('[x-test-detail-delete]').click();

    await expect(page).toHaveURL(/\/$/);
    await expect(page.locator('[x-test-total-badge]')).toHaveText('9');
    await expect(page.locator('[x-test-card="4"]')).toHaveCount(0);
    await expect(page.locator('[x-test-toast]')).toHaveText('Card #4 deleted');
  });

  test('an unknown card id shows the not-found state', async ({ page }) => {
    await page.goto('/card/99');

    await expect(page.locator('[x-test-detail-not-found-message]')).toHaveText('Card not found');
    await expect(page.locator('[x-test-detail-card]')).toHaveCount(0);
  });
});

test.describe('S5 - statistics', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  // S5 — Stats page reports the seed totals (per column and per priority).
  test('stats page reports the seed totals and lists every card', async ({ page }) => {
    await page.locator('[x-test-nav-stats]').click();
    await expect(page).toHaveURL(/\/stats$/);

    // post: summary cards match the deterministic seed
    await expect(page.locator('[x-test-stat-total]')).toHaveText('10');
    await expect(page.locator('[x-test-stat-backlog]')).toHaveText('4');
    await expect(page.locator('[x-test-stat-inprogress]')).toHaveText('2');
    await expect(page.locator('[x-test-stat-review]')).toHaveText('2');
    await expect(page.locator('[x-test-stat-done]')).toHaveText('2');
    await expect(page.locator('[x-test-stat-high]')).toHaveText('3');
    await expect(page.locator('[x-test-stat-medium]')).toHaveText('4');
    await expect(page.locator('[x-test-stat-low]')).toHaveText('3');

    // and the "All cards" table has one row per seed card
    await expect(page.locator('[x-test-stats-table] tbody .stats-row')).toHaveCount(10);
    await expect(page.locator('[x-test-stat-row="3"] .cell-title')).toHaveText('Design login');
  });

  test('stats follow a deletion made on the board', async ({ page }) => {
    await page.locator('[x-test-card-delete="1"]').click();
    await page.locator('[x-test-nav-stats]').click();

    await expect(page.locator('[x-test-stat-total]')).toHaveText('9');
    await expect(page.locator('[x-test-stat-backlog]')).toHaveText('3');
    await expect(page.locator('[x-test-stat-high]')).toHaveText('2');
    await expect(page.locator('[x-test-stat-row="1"]')).toHaveCount(0);
  });

  test('the table row of card #7 shows priority, assignee and column', async ({ page }) => {
    await page.locator('[x-test-nav-stats]').click();

    const row = page.locator('[x-test-stat-row="7"]');
    await expect(row.locator('.cell-title')).toHaveText('Refactor store');
    await expect(row.locator('.cell-priority')).toHaveText('high');
    await expect(row.locator('.cell-assignee')).toHaveText('Marco');
    await expect(row.locator('.cell-column')).toHaveText('review');
  });

  test('the cards table has its five column headers', async ({ page }) => {
    await page.locator('[x-test-nav-stats]').click();

    await expect(page.locator('[x-test-stats-table-title]')).toHaveText('All cards');
    await expect(page.locator('[x-test-stats-table] thead th')).toHaveText(['Id', 'Title', 'Priority', 'Assignee', 'Column']);
  });
});

test.describe('S6 - application shell', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/');
  });

  // S6 — App shell: nav between Board and Stats, footer chrome, reset from the board.
  test('shell exposes nav, footer and returns to the board from stats', async ({ page }) => {
    // pre: footer chrome is present on the shell
    await expect(page.locator('[x-test-footer-brand]')).toHaveText('FlowBoard');
    await expect(page.locator('[x-test-footer-version]')).toHaveText('v1.0');

    // nav to stats, then back to the board via the header link
    await page.locator('[x-test-nav-stats]').click();
    await expect(page).toHaveURL(/\/stats$/);
    await page.locator('[x-test-nav-board]').click();
    await expect(page).toHaveURL(/\/$/);

    // toast reflects the initial seed load and board title is shown
    await expect(page.locator('[x-test-board-title]')).toHaveText('Project board');
    await expect(page.locator('[x-test-toast]')).toHaveText('Board loaded from seed');
  });

  test('reset restores the seed after any mutation of the state', async ({ page }) => {
    await page.locator('[x-test-card-delete="5"]').click();
    await expect(page.locator('[x-test-total-badge]')).toHaveText('9');

    await page.locator('[x-test-reset]').click();

    await expect(page.locator('[x-test-total-badge]')).toHaveText('10');
    await expect(page.locator('[x-test-card="5"]')).toBeVisible();
    await expect(page.locator('[x-test-toast]')).toHaveText('Board reset to seed');
  });

  test('header and toast show brand, tagline, demo badge and last action', async ({ page }) => {
    await expect(page.locator('[x-test-app-title]')).toHaveText('FlowBoard');
    await expect(page.locator('[x-test-app-tagline]')).toHaveText('Kanban demo board');
    await expect(page.locator('[x-test-demo-badge]')).toHaveText('Board demo');
    await expect(page.locator('[x-test-toast-label]')).toHaveText('Last action:');
  });

  test('the header "New card" button opens the form and Cancel comes back', async ({ page }) => {
    await page.locator('[x-test-new-card]').click();
    await expect(page).toHaveURL(/\/card\/new$/);

    await page.locator('[x-test-form-cancel]').click();

    await expect(page).toHaveURL(/\/$/);
    await expect(page.locator('[x-test-total-badge]')).toHaveText('10');
  });
});
