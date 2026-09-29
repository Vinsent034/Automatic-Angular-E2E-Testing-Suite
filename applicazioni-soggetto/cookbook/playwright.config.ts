import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright config for CookBook.
 *
 * Like CineLib, the e2e suite exists to study how different *locator
 * strategies* survive mutations of the application markup. State is fully
 * in-memory and seeded deterministically, so every page load starts from the
 * exact same board: no login, no storage cleanup, no seeding step needed.
 *
 * CookBook runs on port 4500 so it can coexist with CineLib (4300) and
 * FlowBoard (4400).
 */
export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env['CI'],
  retries: 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'http://localhost:4500',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure'
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } }
  ],
  webServer: {
    command: 'npm start -- --port 4500',
    url: 'http://localhost:4500',
    reuseExistingServer: !process.env['CI'],
    timeout: 120_000
  }
});
