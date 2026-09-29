import { defineConfig, devices } from '@playwright/test';

/**
 * Playwright config for CineLib.
 *
 * The e2e suite exists to study how different *locator strategies* survive
 * mutations of the application markup. Each spec deliberately mixes locator
 * styles (data-attribute, role, text, CSS class, XPath, nth-position) so the
 * same behaviour can be reached in several ways and compared.
 *
 * The web server is started automatically; no login or seeding step is needed
 * because the catalog ships with stable sample data.
 */
export default defineConfig({
  testDir: './e2e',
  fullyParallel: true,
  forbidOnly: !!process.env['CI'],
  retries: 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL: 'http://localhost:4200',
    trace: 'on-first-retry',
    screenshot: 'only-on-failure'
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } }
  ],
  webServer: {
    command: 'npm start -- --port 4200',
    url: 'http://localhost:4200',
    reuseExistingServer: !process.env['CI'],
    timeout: 120_000
  }
});
