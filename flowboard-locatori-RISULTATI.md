# FlowBoard — locatori RQ2 (baseline robustezza)

**Totale: 108 locatori** = 18 elementi (3 × 6 scenari) × 6 strategie.
Strategie: Absolute · Relative · Robula · Robula+ · Selenium · Katalon.
Automatici (io): Absolute/Robula/Robula+ = 54. Manuali (con Vince + estensioni): Relative/Selenium/Katalon = 54.

Regola (come CineLib): scartare qualsiasi locatore basato su `x-test-*` (ground-truth, mai usato dagli strumenti); il modulo Robula/Robula+ ha già l'esclusione `x-test*` applicata.

App su http://localhost:4400. Ogni scenario si cattura nel suo stato DOM (pre/post-condizione dello scenario).

---

## I 18 elementi target

| # | Scenario | DOM | Elemento | x-test (ground truth) |
|---|---|---|---|---|
| 1 | S1 | `/` (dopo ricerca "login") | search input | `x-test-search` |
| 2 | S1 | `/` (1 card) | card #3 | `x-test-card="3"` (`.card`) |
| 3 | S1 | `/` (1 card) | card title | `x-test-card-title="3"` |
| 4 | S2 | `/` (seed) | column-count Backlog | `x-test-column-count="backlog"` |
| 5 | S2 | `/` (seed) | move select card #1 | `x-test-card-move="1"` |
| 6 | S2 | `/` (seed) | colonna In Progress | `x-test-column="inprogress"` |
| 7 | S3 | `/card/new` | title input | `x-test-form-title` |
| 8 | S3 | `/card/new` | priority select | `x-test-form-priority` |
| 9 | S3 | `/card/new` | submit | `x-test-form-submit` |
| 10 | S4 | `/card/3` | detail title | `x-test-detail-title` |
| 11 | S4 | `/card/3` | detail assignee | `x-test-detail-assignee` |
| 12 | S4 | `/card/3` | delete button | `x-test-detail-delete` |
| 13 | S5 | `/stats` | stat total | `x-test-stat-total` |
| 14 | S5 | `/stats` | stat backlog | `x-test-stat-backlog` |
| 15 | S5 | `/stats` | tabella riga #3 | `x-test-stat-row="3"` |
| 16 | S6 | `/` (seed) | nav Stats | `x-test-nav-stats` |
| 17 | S6 | `/` (seed) | total badge | `x-test-total-badge` |
| 18 | S6 | `/` (seed) | footer version | `x-test-footer-version` |

---

## BINARIO AUTOMATICO — COMPLETO (54/54) ✅

Absolute (JS), Robula e Robula+ (modulo `custom-locators`, runner `org.unina.flowboard.FlowBoardRunner`).
Dump DOM per scenario in `custom-locators/src/main/resources/flowboard-*.html`; elenco in `flowboard-elements.tsv`.
**Verificato: 0 locatori basati su `x-test`** (esclusione già attiva in `Transformations`).

| Elemento | Absolute | Robula | Robula+ |
|---|---|---|---|
| S1_Search | `/html[1]/…/app-board[1]/section[1]/div[2]/div[1]/input[1]` | `//input` | `//input` |
| S1_Card | `/html[1]/…/app-card[1]/article[1]` | `//article` | `//article` |
| S1_CardTitle | `/html[1]/…/article[1]/a[1]/h4[1]` | `//h4` | `//h4` |
| S2_ColumnCount | `/html[1]/…/div[3]/section[1]/header[1]/span[1]` | `//span[@data-column='backlog']` | `//*[@data-column='backlog' and @class='badge column-count']` |
| S2_CardMove | `/html[1]/…/section[1]/app-column[1]/ul[1]/li[1]/app-card[1]/article[1]/footer[1]/label[1]/select[1]` | `//article[@data-card-id='1']/*/*/select` | `//*[@data-card-id='1']/*/*/select` |
| S2_ColumnInprogress | `/html[1]/…/section[1]/div[3]/section[2]` | `//section[@data-column='inprogress']` | `//*[@aria-label='In Progress column']` |
| S3_FormTitle | `/html[1]/…/form[1]/div[1]/input[1]` | `//input[@id='field-title']` | `//*[@id='field-title']` |
| S3_FormPriority | `/html[1]/…/form[1]/div[3]/div[1]/select[1]` | `//select[@id='field-priority']` | `//*[@id='field-priority']` |
| S3_FormSubmit | `/html[1]/…/form[1]/div[5]/button[1]` | `//button` | `//button` |
| S4_DetailTitle | `/html[1]/…/article[1]/header[1]/h3[1]` | `//h3` | `//h3` |
| S4_DetailAssignee | `/html[1]/…/article[1]/div[1]/div[2]/span[2]` | `//span[@class='detail-assignee']` | `//*[contains(text(),'Marco')]` |
| S4_DetailDelete | `/html[1]/…/article[1]/footer[1]/button[1]` | `//button` | `//button` |
| S5_StatTotal | `/html[1]/…/app-stats[1]/section[1]/div[2]/div[1]/span[2]` | `//div[@data-stat='total']/span[@class='stat-value']` | `//*[@data-stat='total']/*[contains(text(),'10')]` |
| S5_StatBacklog | `/html[1]/…/section[1]/div[2]/div[2]/span[2]` | `//div[@data-stat='backlog']/span[@class='stat-value']` | `//*[@data-stat='backlog']/*[contains(text(),'4')]` |
| S5_StatRow3 | `/html[1]/…/section[1]/div[3]/table[1]/tbody[1]/tr[3]` | `//tr[@data-card-id='3']` | `//*[@data-card-id='3']` |
| S6_NavStats | `/html[1]/…/app-header[1]/header[1]/div[1]/nav[1]/a[2]` | `//a[@routerlink='/stats']` | `//*[contains(text(),'Stats')]` |
| S6_TotalBadge | `/html[1]/…/app-header[1]/header[1]/div[1]/div[2]/span[2]` | `//span[@data-kind='total']` | `//*[@data-kind='total']` |
| S6_FooterVersion | `/html[1]/…/footer[1]/div[1]/span[5]` | `//span[@class='footer-version']` | `//*[contains(text(),'v1.0')]` |

(XPath assoluti completi in `flowboard-elements.tsv` / `abs_results.json`.)

## BINARIO MANUALE — 18/54 (Relative ✅ · Selenium ⏳ · Katalon ⏳)

### Relative (SelectorsHub, Chrome) — COMPLETO 18/18 ✅
Nessun locatore basato su `x-test` (regola rispettata).

| Elemento | Relative (SelectorsHub) |
|---|---|
| S1_Search | `//input[@id='search-input']` |
| S1_Card | `//article[@aria-label='Card Design login']` |
| S1_CardTitle | `//h4[normalize-space()='Design login']` |
| S2_ColumnCount | `//span[normalize-space()='4']` |
| S2_CardMove | `//article[@aria-label='Card Setup repository']//select[@class='input select move-select']` |
| S2_ColumnInprogress | `//section[@aria-label='In Progress column']` |
| S3_FormTitle | `//input[@id='field-title']` |
| S3_FormPriority | `//select[@id='field-priority']` |
| S3_FormSubmit | `//button[normalize-space()='Save card']` |
| S4_DetailTitle | `//h3[normalize-space()='Design login']` |
| S4_DetailAssignee | `//span[@class='detail-assignee']` |
| S4_DetailDelete | `//button[normalize-space()='Delete card']` |
| S5_StatTotal | `//span[@class='stat-value'][normalize-space()='10']` |
| S5_StatBacklog | `//div[@data-stat='backlog']//span[@class='stat-value'][normalize-space()='4']` |
| S5_StatRow3 | `//tr[@data-card-id='3']` (SelectorsHub SH Selector `.stats-row[data-card-id='3']`; Rel XPath auto era glitchato su `<tr>`, verificato 1 match) |
| S6_NavStats | `//a[normalize-space()='Stats']` |
| S6_TotalBadge | `//span[@class='badge badge-total']` |
| S6_FooterVersion | `//span[@class='footer-version']` |

### Katalon Recorder (Chrome) — COMPLETO 18/18 ✅ (tutti verificati vs app dal vivo)
Stile Katalon: ancora testuale univoca + asse `following::`/`ancestor::` quando manca un attributo diretto. Nessun `x-test`.

| Elemento | Katalon |
|---|---|
| S1_Search | `id=search-input` |
| S1_Card | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='In Progress'])[1]/following::article[1]` |
| S1_CardTitle | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='high'])[1]/following::h4[1]` |
| S2_ColumnCount | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Backlog'])[1]/following::span[1]` |
| S2_CardMove | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Move'])[1]/following::select[1]` |
| S2_ColumnInprogress | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Delete'])[4]/following::section[1]` |
| S3_FormTitle | `id=field-title` |
| S3_FormPriority | `id=field-priority` |
| S3_FormSubmit | `xpath=//button[@type='submit']` |
| S4_DetailTitle | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='#3'])[1]/following::h3[1]` |
| S4_DetailAssignee | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Assignee'])[1]/following::span[1]` |
| S4_DetailDelete | `xpath=//button[@type='button']` |
| S5_StatTotal | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Total cards'])[2]/following::span[1]` |
| S5_StatBacklog | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Backlog'])[1]/following::span[1]` |
| S5_StatRow3 | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='#3'])[1]/ancestor::tr[1]` *(recorder dava la cella `following::td[1]`; usato `ancestor::tr` per centrare la riga, verificato)* |
| S6_NavStats | `link=Stats` |
| S6_TotalBadge | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Total cards'])[1]/following::span[1]` |
| S6_FooterVersion | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='·'])[2]/following::span[1]` |

### Selenium IDE (Firefox) — COMPLETO 18/18 ✅ (tutti verificati)
Stile Selenium: preferisce `css=` (classi) e `nth-child` posizionali; `id=`/`linkText=` dove disponibili. Nessun `x-test`.

| Elemento | Selenium |
|---|---|
| S1_Search | `id=search-input` |
| S1_Card | `css=.card` |
| S1_CardTitle | `css=.card-title` |
| S2_ColumnCount | `css=.column:nth-child(1) > .column-header > .badge` |
| S2_CardMove | `css=.column:nth-child(1) .card-slot:nth-child(1) .input` |
| S2_ColumnInprogress | `css=.column:nth-child(2)` |
| S3_FormTitle | `id=field-title` |
| S3_FormPriority | `id=field-priority` |
| S3_FormSubmit | `css=.submit-btn` |
| S4_DetailTitle | `css=.detail-title` |
| S4_DetailAssignee | `css=.detail-assignee` |
| S4_DetailDelete | `css=.btn-danger` |
| S5_StatTotal | `css=.stat-card:nth-child(1) > .stat-value` |
| S5_StatBacklog | `css=.stat-card:nth-child(2) > .stat-value` |
| S5_StatRow3 | `css=.stats-row:nth-child(3)` *(recorder dava la cella `> .cell-title`; usata la riga)* |
| S6_NavStats | `linkText=Stats` |
| S6_TotalBadge | `css=.badge-total` |
| S6_FooterVersion | `css=.footer-version` |

---

## ✅ TUTTI I 108 LOCATORI COMPLETI (6 strategie × 18 elementi)
Automatici 54 (Absolute/Robula/Robula+) + Manuali 54 (Relative/Katalon/Selenium). **Zero locatori basati su `x-test`.**
Verifica: script Playwright risolve ogni locatore nello stato-scenario e controlla l'`x-test` atteso → 52/54 auto-OK + 2 (`id=search-input`, `link=Stats` di Katalon) confermati manualmente (flaky solo nel batch per timing di caricamento). Raw: `scratchpad/manual_locators.tsv`.

**Osservazione qualitativa (pre-mutazione):** Absolute usa percorsi posizionali profondi (fragili); Robula/Robula+ e Relative usano attributi/classi/testo; Katalon usa ancore-testuali + assi `following::`/`ancestor::`; Selenium preferisce classi + `nth-child`. Katalon fatica su contenitori/righe-tabella e le sue ancore `[n]` sono sensibili allo stato.

**PROSSIMO: mutation-tester** — eseguire i 108 locatori contro i 1225 mutanti per misurare robustezza (success/fragility/obsolescence/not-compiled) → tabelle RQ1/RQ2.
