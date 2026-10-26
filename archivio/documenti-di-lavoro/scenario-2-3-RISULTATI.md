# Risultati robustezza — Scenario 2 e 3 (CineLib) — run 2026-07-09

Mutation-tester (Java 25 + vintage-fix), app CineLib su localhost:4300.
Obsolescence e Not Compiled sono costanti per strategia (dipendono dalla mutazione, non dal locatore).

## Scenario 2 — Movie Detail (movie-detail + cast-row) — 173 mutanti
CSV: `output/tests/cinelib-moviedetail-{batches,stats}.csv` — durata run 1857 s.
Comune a tutte le strategie: **Obsolescence 7/173, Not Compiled 15/173** (8,7%).

| Strategia | Success | Fragility |
|---|---|---|
| Relative  | 142/173 | 9/173  |
| Robula+   | 140/173 | 11/173 |
| Katalon   | 139/173 | 12/173 |
| Robula    | 134/173 | 17/173 |
| Selenium  | 133/173 | 18/173 |
| Absolute  | 128/173 | 23/173 |

## Scenario 3 — Movie Form (movie-form) — 113 mutanti
CSV: `output/tests/cinelib-movieform-{batches,stats}.csv` — durata run 1163 s.
Comune a tutte le strategie: **Obsolescence 3/113, Not Compiled 1/113** (0,9%).

| Strategia | Success | Fragility |
|---|---|---|
| Robula+   | 109/113 | 0/113  |
| Selenium  | 107/113 | 2/113  |
| Relative  | 106/113 | 3/113  |
| Katalon   | 104/113 | 5/113  |
| Robula    | 103/113 | 6/113  |
| Absolute  | 92/113  | 17/113 |

## Lettura
- **Absolute** è sistematicamente il più fragile (23/173 e 17/113) → conferma la fragilità dei locatori posizionali.
- **Relative / Robula+** i più robusti in entrambi gli scenari.
- Not Compiled basso in entrambi (8,7% e 0,9%), sotto la soglia 20%.
