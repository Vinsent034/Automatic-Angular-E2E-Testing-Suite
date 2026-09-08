# Scenario 4 — Reviews (CineLib) — locatori 6 strategie

Pagina: `http://localhost:4300/movie/3` (componente reviews, sezione "Reviews" in fondo).
DOM per Robula: `custom-locators/src/main/resources/cinelib-movie-detail.html` (contiene la sezione reviews).
Baseline 2026-07-09: 18/18 locatori OK (browser reale).

3 elementi target:
- **reviews-average** → `<span x-test-reviews-average>` ("★ 4.7")
- **review-author** (1ª recensione) → `<span x-test-review-author>` ("Dario")
- **review-submit** → `<button x-test-review-submit>` ("Post review")

## reviews-average
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/div[1]/span[1]` |
| Relative (SelectorsHub) | `//span[@class='reviews-average']` |
| Selenium IDE | `css=.reviews-average` |
| Katalon | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Reviews'])[1]/following::span[1]` |
| Robula | `//span[@class='reviews-average']` |
| Robula+ | `//*[contains(text(),'★ 4.7')]` |

## review-author
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/ul[1]/li[1]/div[1]/span[1]` |
| Relative (SelectorsHub) | `//span[normalize-space()='Dario']` |
| Selenium IDE | `css=.review-item:nth-child(1) .review-author` |
| Katalon | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='★ 4.7'])[1]/following::span[2]` |
| Robula | `//li[1]/*/span[@class='review-author']` |
| Robula+ | `//*[contains(text(),'Dario')]` |

## review-submit
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[3]/app-reviews[1]/div[1]/form[1]/div[3]/button[1]` |
| Relative (SelectorsHub) | `//button[normalize-space()='Post review']` |
| Selenium IDE | `css=.btn-primary` |
| Katalon | `xpath=//button[@type='submit']` |
| Robula | `//button[@type='submit']` |
| Robula+ | `//*[contains(text(),'Post review')]` |
