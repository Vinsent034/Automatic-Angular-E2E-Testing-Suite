import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright config for FlowBoard.
 *
 * Like CineLib, the e2e suite exists to study how different *locator
 * strategies* survive mutations of the application markup. State is fully
 * in-memory and seeded deterministically, so every page load starts from the
 * exact same board: no login, no storage cleanup, no seeding step needed.
 *
 * FlowBoard runs on port 4400 so it can coexist with CineLib (4300) and
 * other demo apps.
 */
export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env['CI'],
  retries: 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'http://localhost:4400',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure'
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } }
  ],
  webServer: {
    command: 'npm start -- --port 4400',
    url: 'http://localhost:4400',
    reuseExistingServer: !process.env['CI'],
    timeout: 120_000
  }
});
