# CineLib — Robustezza locatori: risultati completi (807/807 mutanti LLM)

Mutation-tester (Java 25), app su localhost:4300. 5 scenari, 6 strategie di locatori.
Obsolescence e Not Compiled sono costanti per strategia entro ogni scenario (dipendono dalla mutazione, non dal locatore).
CSV grezzi: `output/tests/cinelib-<scenario>-batches.csv`.

## Scenario 1 — Catalog search (catalog + movie-card) — 306 mutanti
(misurato in sessione precedente) — Obsolescence 8/306, Not Compiled 62/306 (≈ operatore h)

| Strategia | Success | Fragility |
|---|---|---|
| Robula+  | 230/306 | 6/306  |
| Selenium | 228/306 | 8/306  |
| Katalon  | 226/306 | 10/306 |
| Robula   | 226/306 | 10/306 |
| Relative | 224/306 | 12/306 |
| Absolute | 194/306 | 42/306 |

## Scenario 2 — Movie Detail (movie-detail + cast-row) — 173 mutanti
Obsolescence 7/173, Not Compiled 15/173

| Strategia | Success | Fragility |
|---|---|---|
| Relative  | 142/173 | 9/173  |
| Robula+   | 140/173 | 11/173 |
| Katalon   | 139/173 | 12/173 |
| Robula    | 134/173 | 17/173 |
| Selenium  | 133/173 | 18/173 |
| Absolute  | 128/173 | 23/173 |

## Scenario 3 — Movie Form (movie-form) — 113 mutanti
Obsolescence 3/113, Not Compiled 1/113

| Strategia | Success | Fragility |
|---|---|---|
| Robula+   | 109/113 | 0/113  |
| Selenium  | 107/113 | 2/113  |
| Relative  | 106/113 | 3/113  |
| Katalon   | 104/113 | 5/113  |
| Robula    | 103/113 | 6/113  |
| Absolute  | 92/113  | 17/113 |

## Scenario 4 — Reviews (reviews) — 118 mutanti
Obsolescence 3/118, Not Compiled 14/118

| Strategia | Success | Fragility |
|---|---|---|
| Robula+   | 100/118 | 1/118  |
| Relative  | 96/118  | 5/118  |
| Katalon   | 95/118  | 6/118  |
| Selenium  | 92/118  | 9/118  |
| Robula    | 91/118  | 10/118 |
| Absolute  | 85/118  | 16/118 |

## Scenario 5 — Stats (stats) — 97 mutanti
Obsolescence 6/97, Not Compiled 1/97 (l'unico = 1 mutante andato in timeout di ricompilazione, ambientale)

| Strategia | Success | Fragility |
|---|---|---|
| Robula+   | 87/97 | 3/97  |
| Katalon   | 84/97 | 6/97  |
| Relative  | 81/97 | 9/97  |
| Robula    | 75/97 | 15/97 |
| Selenium  | 74/97 | 16/97 |
| Absolute  | 69/97 | 21/97 |

## Conclusione trasversale
- **Absolute** è il più fragile in TUTTI e 5 gli scenari (42, 23, 17, 16, 21 mutazioni fragili) → conferma netta della fragilità dei locatori posizionali.
- **Robula+** e **Relative** i più robusti (Robula+ vince in 4 scenari su 5, con 0 fragilità nel Form).
- Not Compiled basso ovunque tranne lo scenario 1 (20% = operatore h cross-template, atteso per costruzione).
- Copertura: **807/807 mutanti LLM testati (100%)**.
