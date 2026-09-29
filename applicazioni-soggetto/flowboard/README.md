# FlowBoard

Kanban board in **Angular 19** (standalone components + signals): seconda applicazione di prova, dopo CineLib, per lo studio sulla robustezza delle strategie di locatori rispetto alle mutazioni del markup.

Non è un prodotto reale: è un banco di prova **deterministico** progettato perché **tutti gli 11 operatori di mutazione (a–k)** producano mutanti validi (compilabili) e misurabili — incluso l'operatore **h** (spostamento di un elemento tra template di componenti diversi), che su CineLib risultava not-compilable per costruzione.

## Avvio

```bash
npm install
npm start          # ng serve sulla porta 4400 (configurata in angular.json)
```

App su <http://localhost:4400> — nessun login, nessun backend, nessuna chiamata HTTP. Porta 4400 per non confliggere con CineLib (4300).

```bash
npm run e2e        # test Playwright (avvia da solo il server sulla 4400)
npm run e2e:ui     # Playwright in modalità UI
npm run e2e:report # apre l'ultimo report HTML
```

## Determinismo

- Seed **fisso e hard-coded** in [src/app/seed.ts](src/app/seed.ts): 10 card con id 1–10 distribuite su 4 colonne. Nessun valore casuale, nessuna data/ora dinamica (le date sono stringhe fisse nel testo).
- Stato **solo in memoria** (niente localStorage): ogni ricaricamento della pagina riparte esattamente dal seed, quindi i test non hanno bisogno di alcuna pulizia di storage.
- Il pulsante **Reset board** (`x-test-reset`) ripristina seed, filtri e selezione.
- Gli id delle nuove card sono `max(id) + 1` → deterministici data la sequenza di azioni.
- Nessuna animazione né timeout: anche il "toast" è una barra di stato persistente aggiornata in modo sincrono.

## Architettura: store globale a signal (la regola d'oro per h)

Tutto lo stato vive in [`BoardStore`](src/app/board-store.ts) (`providedIn: 'root'`), esposto in **ogni** componente come proprietà `readonly store`. Gli elementi bersaglio dei test sono:

- **statici** (nessun binding): titoli, etichette, banner, icone, messaggi `@empty`, footer; oppure
- **store-bound**: binding scritti come `store.xxx()` (es. `{{ store.totalCards() }}`, `{{ store.selectedCard()?.title }}`).

Poiché la proprietà si chiama `store` in tutti i componenti, un elemento store-bound spostato in **qualsiasi** altro template compila comunque → l'operatore **h** produce mutanti validi. Niente `@Input`/prop-drilling per gli elementi bersaglio.

Stato: `columns`, `cards`, `selectedId`, `searchText`, `priorityFilter`, `assigneeFilter`, `toast`.
Derivati (computed): `totalCards`, `selectedCard`, `filteredCards`, `filteredCount`, `backlogCount`, `inProgressCount`, `reviewCount`, `doneCount`, `highCount`, `mediumCount`, `lowCount`, `assignees`, `cardsByColumn`.
Azioni: `addCard`, `updateCard`, `removeCard`, `moveCard`, `select`, `setSearch`, `setPriorityFilter`, `setAssigneeFilter`, `reset`.

## Route

| Route | Componente | Contenuto |
|---|---|---|
| `/` | `app-board` | toolbar (ricerca, filtri, add, reset) + 4 colonne esplicite + card |
| `/card/new` | `app-card-form` | form nuova card (titolo, descrizione, priorità, assegnatario, colonna, tag) |
| `/card/:id` | `app-card-detail` | dettaglio interamente legato a `store.selectedCard()` |
| `/card/:id/edit` | `app-card-form` | stesso form, precompilato |
| `/stats` | `app-stats` | stat card store-bound + tabella di tutte le card |

Componenti aggiuntivi: `app-header` (barra persistente), `app-column` (solo lista card di una colonna), `app-card`, `app-toast`.

## Zone h-safe (elementi statici o store-bound, fuori dai `@for`)

- **Header**: titolo app statico (`x-test-app-title`), tagline, badge totale `store.totalCards()` (`x-test-total-badge`), link di nav, pulsante "New card".
- **Board**: titolo pagina, badge "Board demo" (`x-test-demo-badge`), hint banner, etichette della toolbar, badge "Matching" `store.filteredCount()` (`x-test-filtered-count`).
- **Colonne**: le 4 colonne sono **esplicite nel template del board** (non generate in `@for`): titoli statici (`x-test-column-title="…"`), conteggi `store.backlogCount()` ecc. (`x-test-column-count="…"`).
- **Card detail**: ogni campo legge `store.selectedCard()?.…` (id, titolo, descrizione, priorità, assegnatario, colonna) → h-safe.
- **Stats**: 8 stat card store-bound + intestazioni statiche di tabella.
- **Toast e footer**: barra di stato `store.toast()` con etichette statiche; footer completamente statico.
- Messaggi di stato vuoto (`@empty`): statici, quindi h-safe anche se vivono in `app-column`.

Le card dentro le colonne (in `@for`, legate alla variabile di ciclo o all'input `card`) restano bersaglio per gli operatori **a–g, i, j, k**, ma **non** per h.

## Ingredienti per gli 11 operatori di mutazione

| Op | Mutazione | Dove trovarla |
|---|---|---|
| a | Attribute Value Modification | `data-priority`, `data-column`, `data-kind`, `data-stat`, `data-tag`, `data-assignee`, `aria-label`, `class`, `placeholder`, `id` |
| b | Attribute Removal | gli stessi attributi non essenziali di sopra |
| c | Attribute Identifier Modification | attributi `data-*`/`aria-*` rinominabili senza toccare i binding Angular |
| d | Text Content Modification | titoli, etichette, badge, bottoni, celle di tabella |
| e | Text Content Removal | idem |
| f | Tag Movement (stesso contenitore) | toolbar (5 gruppi fratelli), header (brand/stats/nav), footer card (move/edit/delete), form-actions, stats-grid, detail-rows |
| g | Tag Movement (ovunque) | alberi annidati su più livelli (board → column → card → footer → …) |
| h | Tag Movement (tra template) | tutte le zone h-safe elencate sopra |
| i | Tag Removal (unwrap) | wrapper con figli: `.brand`, `.toolbar-group`, `.card-top`, `.detail-row`, `.stat-card`, `.page-heading` |
| j | Tag Type Modification | `div`/`section`/`article`/`aside`/`span` intercambiabili |
| k | Tag Insertion | qualsiasi elemento può ricevere un wrapper |

## Attributi `x-test-*` (ground truth — mai mutati)

Ogni elemento interattivo o bersaglio ha un attributo `x-test-*` univoco, e resta raggiungibile anche con **classe CSS, testo, ruolo ARIA, attributo `data-*` e posizione gerarchica** (per autorare tutte e 6 le strategie: Absolute, Relative, Selenium, Katalon, Robula, Robula+).

**Shell / header**: `x-test-app`, `x-test-header`, `x-test-brand`, `x-test-app-title`, `x-test-app-tagline`, `x-test-total-label`, `x-test-total-badge`, `x-test-nav`, `x-test-nav-board`, `x-test-nav-stats`, `x-test-new-card`, `x-test-footer`, `x-test-footer-brand`, `x-test-footer-note`, `x-test-footer-version`.

**Board / toolbar**: `x-test-board`, `x-test-board-title`, `x-test-demo-badge`, `x-test-hint-banner`, `x-test-toolbar`, `x-test-search-label`, `x-test-search`, `x-test-priority-label`, `x-test-filter-priority`, `x-test-assignee-label`, `x-test-filter-assignee`, `x-test-matching-label`, `x-test-filtered-count`, `x-test-add-card`, `x-test-reset`, `x-test-columns`.

**Colonne** (valore = id colonna): `x-test-column="backlog|inprogress|review|done"`, `x-test-column-title="…"`, `x-test-column-count="…"`, `x-test-card-list`, `x-test-empty-message`.

**Card** (valore = id card): `x-test-card="N"`, `x-test-card-id="N"`, `x-test-card-priority="N"`, `x-test-card-open="N"`, `x-test-card-title="N"`, `x-test-card-assignee="N"`, `x-test-card-tags="N"`, `x-test-card-move="N"`, `x-test-card-edit="N"`, `x-test-card-delete="N"`.

**Dettaglio**: `x-test-card-detail`, `x-test-detail-back`, `x-test-detail-card`, `x-test-detail-id`, `x-test-detail-title`, `x-test-detail-priority`, `x-test-detail-description`, `x-test-detail-assignee`, `x-test-detail-column`, `x-test-detail-tags`, `x-test-detail-edit`, `x-test-detail-delete`, `x-test-detail-not-found`.

**Form**: `x-test-card-form`, `x-test-form`, `x-test-form-title-new` / `x-test-form-title-edit` (heading), `x-test-form-title`, `x-test-form-description`, `x-test-form-priority`, `x-test-form-assignee`, `x-test-form-column`, `x-test-form-tags`, `x-test-form-submit`, `x-test-form-cancel`, `x-test-form-back`.

**Stats**: `x-test-stats`, `x-test-stat-total`, `x-test-stat-backlog`, `x-test-stat-inprogress`, `x-test-stat-review`, `x-test-stat-done`, `x-test-stat-high`, `x-test-stat-medium`, `x-test-stat-low` (ciascuno con `…-card` e `…-label`), `x-test-stats-table`, `x-test-stat-row="N"`.

**Toast**: `x-test-toast-bar`, `x-test-toast-label`, `x-test-toast`.

## Seed (10 card)

| # | Titolo | Priorità | Assegnatario | Colonna |
|---|---|---|---|---|
| 1 | Setup repository | high | Anna | Backlog |
| 2 | Write project brief | medium | Luca | Backlog |
| 3 | Design login | high | Marco | In Progress |
| 4 | Implement search | medium | Anna | In Progress |
| 5 | Prepare test data | low | Sara | Backlog |
| 6 | Fix header layout | medium | Sara | Review |
| 7 | Refactor store | high | Marco | Review |
| 8 | Setup CI pipeline | medium | Luca | Done |
| 9 | Write README | low | Anna | Done |
| 10 | Update dependencies | low | Luca | Backlog |

Conteggi: Backlog 4 · In Progress 2 · Review 2 · Done 2 — priorità: high 3 · medium 4 · low 3.

## Scenari E2E (pre/post condizioni)

Implementati in [e2e/board.spec.ts](e2e/board.spec.ts); ogni scenario parte dal seed (basta ricaricare la pagina).

| Scenario | Pre-condizione | Azione | Post-condizione |
|---|---|---|---|
| Ricerca card | 10 card nel board | digita `login` in `x-test-search` | 1 card visibile, titolo "Design login" |
| Filtro priorità | badge totale = 10 | seleziona "High" in `x-test-filter-priority` | 3 card, tutte con `data-priority="high"` |
| Sposta card | card #1 in Backlog (4/2) | `x-test-card-move="1"` → In Progress | Backlog 3, In Progress 3 |
| Aggiungi card | form vuoto su `/card/new` | compila e `x-test-form-submit` | badge totale = 11, card nella colonna scelta |
| Dettaglio | board dal seed | apri card #3 (`x-test-card-open="3"`) | `x-test-detail-title` = "Design login" |
| Elimina card | card #5 esistente | `x-test-card-delete="5"` | badge totale = 9, card assente |
| Reset | stato mutato (9 card) | `x-test-reset` | badge totale = 10, card #5 di nuovo presente |
