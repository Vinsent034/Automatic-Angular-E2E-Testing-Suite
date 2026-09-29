# Cinelib

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 19.2.27.

## Development server

To start a local development server, run:

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`. The application will automatically reload whenever you modify any of the source files.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Building

To build the project run:

```bash
ng build
```

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

```bash
ng test
```

## End-to-end tests (Playwright) — locator / mutation study

The `e2e/` suite exists to study how different **locator strategies** survive
mutations of the application markup. No login or seeding step is required: the
catalog ships with stable sample data, and Playwright gives each test a fresh
browser context (empty `localStorage`), so every test starts from the same seed.

Run the suite (it starts `ng serve` automatically):

```bash
npm run e2e            # headless run
npm run e2e:ui         # interactive UI mode
npm run e2e:report     # open the last HTML report
```

### Locator strategies by file

Each spec deliberately favours one strategy so you can compare survival rates
after mutating the templates:

| File | Strategy | Mutation sensitivity |
| --- | --- | --- |
| `catalog.spec.ts` | data-attribute (`[x-test-*]`) | low — robust baseline |
| `favorites.spec.ts` | role + accessible name, `[aria-pressed]` | medium — label/role |
| `detail.spec.ts` | text content, one XPath example | high — wording/structure |
| `reviews.spec.ts` | form controls + data-attributes | medium |
| `form.spec.ts` | label/id association (`getByLabel`) | medium — label text |
| `stats.spec.ts` | CSS class + nth-position | high — most fragile |

Every interactive element in the app also carries an `x-test-*` attribute and is
reachable via class, text, ARIA role, hierarchy and `data-*` hooks
(`data-genre`, `data-rating`, `data-kind`), so the same target can be located
several ways and each variant mutated independently.

## Additional Resources

For more information on using the Angular CLI, including detailed command references, visit the [Angular CLI Overview and Command Reference](https://angular.dev/tools/cli) page.
