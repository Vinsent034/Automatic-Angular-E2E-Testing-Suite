# Scenario 5 — Stats (CineLib) — locatori 6 strategie

Pagina: `http://localhost:4300/stats`.
DOM per Robula: `custom-locators/src/main/resources/cinelib-stats.html`.
Baseline 2026-07-09: 18/18 locatori OK (browser reale).

3 elementi target:
- **stat-total-value** → `<span x-test-stat-total-value>` ("8", card Movies)
- **top-title** (1ª riga tabella Top rated) → `<a x-test-top-title>` ("Quiet Harbor") — link di navigazione
- **genre-count** (1ª barra genere) → `<span x-test-genre-count>` ("2", riga Adventure)

## stat-total-value
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/div[1]/div[1]/span[1]` |
| Relative (SelectorsHub) | `//span[normalize-space()='8']` |
| Selenium IDE | `css=.stat-card:nth-child(1) > .stat-value` |
| Katalon | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Statistics'])[2]/following::span[1]` |
| Robula | `//div[1]/span[@class='stat-value']` |
| Robula+ | `//span[contains(text(),'8')]` |

## top-title
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/section[2]/table[1]/tbody[1]/tr[1]/td[2]/a[1]` |
| Relative (SelectorsHub) | `//a[normalize-space()='Quiet Harbor']` |
| Selenium IDE | `linkText=Quiet Harbor` |
| Katalon | `link=Quiet Harbor` |
| Robula | `//a[@ng-reflect-router-link='/movie,3']` (nota: attributo dev-mode) |
| Robula+ | `//*[contains(text(),'Quiet Harbor')]` |

## genre-count
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-stats[1]/section[1]/section[1]/div[1]/div[1]/span[2]` |
| Relative (SelectorsHub) | `//div[@data-genre='Adventure']//span[@class='genre-bar-count'][normalize-space()='2']` |
| Selenium IDE | `css=.genre-bar-row:nth-child(1) > .genre-bar-count` |
| Katalon | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Adventure'])[1]/following::span[1]` |
| Robula | `//div[@data-genre='Adventure']/span[@class='genre-bar-count']` |
| Robula+ | `//*[@data-genre='Adventure']/*[contains(text(),'2')]` |

## Note
- `top-title` è un `<a routerLink>`: i test lo LOCALIZZANO soltanto (findElement), non lo cliccano → nessuna navigazione.
- Robula `top-title` usa `ng-reflect-router-link` (attributo presente solo in dev mode; il tester gira su ng serve, quindi valido).
