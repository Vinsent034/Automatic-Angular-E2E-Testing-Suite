# Scenario 3 — Movie Form (CineLib) — locatori 6 strategie

Pagina di riferimento: `http://localhost:4300/movie/new` (form "Add movie", vuoto/deterministico).
Data autorale: 2026-07-09.

3 elementi target (famiglia DOM del form: input / select / button + label associate):
- **field-title** → `<input id="title" x-test-field-title>` (campo testo)
- **field-genre** → `<select id="genre" x-test-field-genre>` (menu a tendina)
- **form-submit** → `<button type="submit" x-test-form-submit>` ("Create movie")

## field-title
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[1]/input[1]` |
| Relative (SelectorsHub) | `//input[@id='title']` |
| Selenium IDE | `id=title` |
| Katalon | `id=title` |
| Robula | `//input[@id='title']` |
| Robula+ | `//*[@id='title']` |

## field-genre
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[2]/div[2]/select[1]` |
| Relative (SelectorsHub) | `//select[@id='genre']` |
| Selenium IDE | `id=genre` |
| Katalon | `id=genre` |
| Robula | `//select` |
| Robula+ | `//select` |

## form-submit
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-form[1]/section[1]/form[1]/div[7]/button[1]` |
| Relative (SelectorsHub) | `//button[normalize-space()='Create movie']` |
| Selenium IDE | `css=.btn-primary` |
| Katalon | `xpath=//button[@type='submit']` |
| Robula | `//button[@type='submit']` |
| Robula+ | `//*[contains(text(),'Create movie')]` |

## Note
- Selenium catturato con Selenium IDE su Firefox (2026-07-09).
- Absolute preso dagli Abs XPath di SelectorsHub (2026-07-09).
- Robula / Robula+ generati col modulo `custom-locators`: DOM `/movie/new` dumpato in
  `cinelib-movie-form.html`, entry in `Robula.main()`/`RobulaPlus.main()`, eseguiti. Tutti univoci.
- Nota: `//select` (genre) è univoco perché sul form `/movie/new` c'è un solo `<select>`.
