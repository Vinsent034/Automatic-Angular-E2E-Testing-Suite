# FlowBoard — scenari E2E e budget mutazioni

Stessa pipeline di CineLib (robustezza locatori sotto mutazione). Differenza chiave:
su FlowBoard l'operatore **h** (spostamento cross-template) compila → si testano **tutti e 11**
gli operatori (a–k), non 10.

Seed deterministico (10 card, mai da localStorage; reload = seed; `x-test-reset` ripristina):

| | valore |
|---|---|
| Totale | 10 |
| Colonne | Backlog 4 (#1,2,5,10) · In Progress 2 (#3,4) · Review 2 (#6,7) · Done 2 (#8,9) |
| Priorità | high 3 (#1,3,7) · medium 4 (#2,4,6,8) · low 3 (#5,9,10) |
| Assignee | Anna (3) · Luca (3) · Marco (2) · Sara (2) |

---

## I 6 scenari (coprono tutti gli 8 componenti)

### S1 — Board: ricerca + filtri
- **DOM:** `/` (header + board + column + card + toast)
- **Pre:** total badge = 10, matching = 10
- **Azioni:** digita "login" nel search → filtro priorità "High"
- **Post:** 1 card ("Design login") con search; 3 card `data-priority="high"` col filtro
- **Componenti mutati:** board, card, column, header

### S2 — Sposta card tra colonne (menu Move) — *ricco per h*
- **DOM:** `/`
- **Pre:** card #1 in Backlog; Backlog 4, In Progress 2
- **Azione:** `x-test-card-move="1"` → "In Progress"
- **Post:** Backlog 3, In Progress 3; #1 nella nuova colonna
- **Componenti mutati:** card (menu Move store-bound), column, board (badge conteggi)

### S3 — Crea card da `/card/new`
- **DOM:** `/card/new` (card-form)
- **Pre:** form vuoto, title = ""
- **Azione:** compila title/description/priority/assignee/column/tags → submit
- **Post:** total badge = 11; card nella colonna scelta
- **Componenti mutati:** card-form

### S4 — Dettaglio + edit + delete
- **DOM:** `/card/3` (card-detail) + `/card/5` delete
- **Pre:** apri #3 → detail store-bound
- **Post:** title "Design login", assignee "Marco"; delete #5 → total 9, toast
- **Componenti mutati:** card-detail, card, toast

### S5 — Pagina Stats *(NUOVO — 39 x-test)*
- **DOM:** `/stats` (stats)
- **Pre/Post:** stat-card totali (10) e per colonna (4/2/2/2) e per priorità (3/4/3);
  tabella "All cards" con 10 righe `x-test-stat-row`
- **Componenti mutati:** stats

### S6 — Shell: header / nav / footer / reset *(NUOVO)*
- **DOM:** `/` (app-shell + header + footer)
- **Azioni:** nav Board↔Stats; reset dopo una delete
- **Post:** footer brand/note/version; nav attiva; reset → total 10, toast "Board reset to seed"
- **Componenti mutati:** app, header, toast, board

---

## Budget mutazioni (leva = numero di target in `role-targets.json`)

Meccanismo: per ogni **target** (`x-test-*`) → 11 operatori × 5 ruoli (α/β/γ/δ/ε),
~metà applicabili. ε sempre 0 in per-file (come lo static). h ora pieno (8 template).

**Riferimento CineLib:** 45 target → 807 generati (703 per-file + 104 h, di cui solo 24 h validi).
Resa per-file ≈ 15,6 mutanti/target.

**FlowBoard — budget ~60 target** (di 152 `x-test` distinti disponibili):

| Scenario | Componente | target selezionati (≈) |
|---|---|---:|
| S1/S2 | board + card + column + header | 26 |
| S3 | card-form | 12 |
| S4 | card-detail | 11 |
| S5 | stats | 12 |
| S6 | app + footer + toast | 6 |
| **Totale** | | **~67** |

**Proiezione:** per-file ≈ 60 × 15,6 ≈ **~940** + h **pieno e più ricco** (8 template, compilabile)
≈ **250–350** → **≈ 1200–1290 mutanti**. Range obiettivo centrato.

Se il conteggio reale (dry-run generatore, `LLM_ROLE_DRYRUN=1`) risultasse basso, si alza
il numero di target (margine ampio: 152 disponibili).

---

## Locatori RQ2 — 3 elementi × scenario × 6 strategie
Absolute/Robula/Robula+ automatici (modulo `custom-locators`), Relative/Selenium/Katalon manuali.
Elementi rappresentativi per scenario da fissare in fase di cattura.

---

## FlowBoard v2 (22/09/2026)

- Template originali di luglio ripristinati (erano rimasti mutati: mancavano `x-test-demo-badge`,
  `x-test-toast-label`, `x-test-empty-message`); vedi `PIANO-REVISIONE.md` § 10.1.
- Aggiunto `<app-panel>` (contenitore con proiezione) in `card-detail`, `card-form` e `stats`:
  **30 bersagli su 152 hanno ora il quinto ruolo ε**; 4422 combinazioni applicabili (erano 4203),
  590 prompt per il modello.
- **Suite Playwright portata da 9 a 24 test**, 4 per scenario (S1-S6), tutti superati.
- Robustezza: stessi 18 elementi di luglio (3 di essi ora dentro un pannello: priorità del modulo,
  assegnatario del dettaglio, riga 3 della tabella). Locatori in `flowboard-v2/locatori/`.
