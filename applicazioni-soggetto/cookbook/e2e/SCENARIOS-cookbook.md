# CookBook — scenari, casi di test e bersagli

Stessa pipeline di CineLib e FlowBoard: robustezza dei locatori sotto mutazione dei template.
Porta 4500. Stato solo in memoria: ogni caricamento di pagina riparte dai dati iniziali.

Dati iniziali: 8 ricette (principali 3, antipasti 2, contorni 1, dolci 2), 6 vegetariane,
3 preferite (#1, #3, #6); lista della spesa con 3 voci della ricetta #2, una già comprata.

---

## Casi di test per scenario (suite Playwright, `e2e/cookbook.spec.ts`)

| Scenario | Vista | Casi di test |
|---|---|---:|
| S1 — ricerca nell'elenco | `/` | 3 |
| S2 — filtri, ordinamento, preferiti | `/` | 4 |
| S3 — scheda della ricetta e porzioni | `/recipe/:id` | 4 |
| S4 — modulo di inserimento e modifica | `/recipe/new`, `/recipe/:id/edit` | 4 |
| S5 — lista della spesa | `/shopping` | 4 |
| S6 — statistiche e cornice | `/stats` | 4 |
| **Totale** | | **23** |

Esito sulla versione non mutata: **23 superati su 23** (21/09/2026).

---

## Elementi localizzati dai test di robustezza (7 strategie)

Come nelle altre due applicazioni, ogni scenario ha un caso di test Selenium che localizza tre
elementi e verifica il loro stato; il caso è replicato per ciascuna delle sette strategie
(Absolute, Relative, Robula, Robula+, Selenium, Katalon, Hook-Based): 6 × 7 = 42 classi.

| Scenario | Stato del DOM | Elemento 1 | Elemento 2 | Elemento 3 | Verifica |
|---|---|---|---|---|---|
| S1 | `/` dopo la ricerca «risotto» | campo di ricerca `x-test-search` | scheda `x-test-card="2"` | titolo `x-test-card-title="2"` | 1 scheda, «Mushroom Risotto» |
| S2 | `/` con «solo vegetariane» e portata «dolci» | casella `x-test-veg-only` | filtro `x-test-course-filter` | conteggio `x-test-result-count` | conteggio «1» |
| S3 | `/recipe/1` con 4 porzioni | titolo `x-test-detail-title` | porzioni `x-test-servings` | quantità `x-test-ing-qty="1"` | «Tomato Pasta», «400» |
| S4 | `/recipe/new` | titolo `x-test-f-title` | portata `x-test-f-course` | invio `x-test-f-submit` | pulsante «Create recipe» |
| S5 | `/shopping` | da comprare `x-test-open-count` | spunta `x-test-shop-check="1"` | pulizia `x-test-clear-done` | «2», poi «1» dopo la spunta |
| S6 | `/stats` | totale `x-test-stat-total` | principali `x-test-course-count="main"` | versione `x-test-footer-version` | «8», «3», «v1.0» |

Regola già in uso: nessun locatore basato su `x-test-*`, tranne Hook-Based.

---

## Gruppi per le campagne (componente mutato → scenari che lo collaudano)

| Gruppo | Componenti | Classi di test |
|---|---|---:|
| `s1s2` | `recipe-list`, `recipe-card` | 14 |
| `s3` | `recipe-detail`, `ingredient-row`, `step-item`, `panel` | 7 |
| `s4` | `recipe-form` | 7 |
| `s5` | `shopping-list` | 7 |
| `s6` | `stats`, `app`, `header`, `toast` | 7 |

---

## Bersagli di mutazione: 52

Ogni bersaglio rispetta le regole R1-R4 del brief (verificato con le funzioni dei generatori:
padre, antenato e fratello istanziabili per 52 bersagli su 52, nessun contenitore condiviso da più
di due bersagli, nessun `x-test-*` sui nodi di contesto). Sette bersagli stanno dentro
`<app-panel>` nello stesso template: avrebbero anche il quinto ruolo, che però lo strumento attuale
non istanzia per un difetto ereditato (vedi `BLOCCO-B-stato.md` § 2):
`x-test-servings`, `x-test-kcal`, `x-test-protein` (scheda), `x-test-f-ingredients`,
`x-test-f-steps` (modulo), `x-test-course-count`, `x-test-top-title` (statistiche).

| Template | Bersagli |
|---|---:|
| `app` | 2 |
| `header` | 3 |
| `toast` | 1 |
| `recipe-list` | 8 |
| `recipe-card` | 4 |
| `recipe-detail` | 8 |
| `ingredient-row` | 2 |
| `step-item` | 2 |
| `recipe-form` | 9 |
| `shopping-list` | 6 |
| `stats` | 7 |
| **Totale** | **52** |
