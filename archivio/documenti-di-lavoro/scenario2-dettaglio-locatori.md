# Scenario 2 — Movie Detail (CineLib) — locatori 6 strategie

Pagina di riferimento: `http://localhost:4300/movie/3` (film "Quiet Harbor", id 3; cast a 3 righe).
DOM renderizzato salvato in `custom-locators/src/main/resources/cinelib-movie-detail.html`.
Data autorale: 2026-07-09.

3 elementi target (stesso pattern dello scenario 1: elemento + figlio, su istanza a indice 2 per il cast):
- **detail-title** → `<h1 x-test-detail-title>` ("Quiet Harbor")
- **cast-row** (2ª riga) → `<div x-test-cast-row>` (Sam Whitfield)
- **cast-name** (2ª riga) → `<span x-test-cast-name>` ("Sam Whitfield")

## detail-title
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/header[1]/div[2]/h1[1]` |
| Relative (SelectorsHub) | `//h1[normalize-space()='Quiet Harbor']` |
| Selenium IDE | `css=.detail-title` |
| Katalon | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Q'])[1]/following::h1[1]` |
| Robula | `//h1` |
| Robula+ | `//h1` |

## cast-row (2ª riga)
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[2]/div[1]/app-cast-row[2]/div[1]` |
| Relative (SelectorsHub) | `//body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[2]/div[1]/app-cast-row[2]/div[1]` |
| Selenium IDE | `css=app-cast-row:nth-child(2) > .cast-row` |
| Katalon | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Edie'])[1]/following::div[1]` |
| Robula | `//app-cast-row[2]/*` |
| Robula+ | `//*[2]/*[@class='cast-row']` |

## cast-name (2ª riga)
| Strategia | Locatore |
|---|---|
| Absolute | `/html[1]/body[1]/app-root[1]/main[1]/app-movie-detail[1]/article[1]/section[2]/div[1]/app-cast-row[2]/div[1]/span[2]` |
| Relative (SelectorsHub) | `//span[normalize-space()='Sam Whitfield']` |
| Selenium IDE | `css=app-cast-row:nth-child(2) .cast-name` |
| Katalon | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Edie'])[1]/following::span[2]` |
| Robula | `//app-cast-row[2]/*/span[@class='cast-name']` |
| Robula+ | `//*[contains(text(),'Sam Whitfield')]` |

## Note
- Robula/Robula+ generati col modulo `custom-locators` (entry aggiunte in `Robula.main()` e `RobulaPlus.main()`,
  input = XPath assoluto + `cinelib-movie-detail.html`). Restituiscono un valore solo se univoco → tutti validi.
- Il Relative di `cast-row` è finito posizionale (quasi = Absolute) perché la classe `cast-row` è condivisa
  dalle 3 righe e il `<div>` non ha attributo/testo univoco proprio (gli univoci stanno sugli `<span>` figli).
- Prossimo passo: scrivere il file di test Java dello scenario, copiarlo in `cinelib-only/`, mettere a PENDING
  i 173 mutanti di movie-detail + cast-row e lanciare il mutation-tester.
