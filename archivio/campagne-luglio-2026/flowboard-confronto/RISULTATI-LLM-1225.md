# LLM (1225 mutanti) — FlowBoard — 1225 mutanti — ['batches-s1s2.csv', 'batches-s3.csv', 'batches-s4.csv', 'batches-s5.csv', 'batches-s6.csv']

## RQ1 — Validità (validi/tot) per operatore × ruolo

| Op | a(alp) | b(bet) | g(gam) | d(del) | TOT |
|--|--|--|--|--|--|
| a | 124/125 | 6/6 | – | 2/2 | 132/133 |
| b | 111/112 | 4/4 | – | 2/2 | 117/118 |
| c | 125/125 | 6/6 | – | 2/2 | 133/133 |
| d | 57/57 | – | – | 2/2 | 59/59 |
| e | 57/57 | – | – | 2/2 | 59/59 |
| f | 54/55 | 39/40 | 9/9 | 53/54 | 155/158 |
| g | 52/53 | 34/34 | 4/4 | 49/50 | 139/141 |
| h | 34/54 | 29/45 | 5/13 | 42/48 | 110/160 |
| j | 121/123 | 6/6 | – | 2/2 | 129/131 |
| k | 125/125 | 6/6 | – | 2/2 | 133/133 |
| **TOT** | 860/886 | 130/147 | 18/26 | 158/166 | **1166/1225** |

**Validità totale: 1166/1225 = 95.2%**  | **escluso h: 1056/1065 = 99.2%**

## RQ1 — Cause di non-validità (not-compiled) per operatore

| Op | Non validi/tot | Nota |
|--|--|--|
| a | 1/133 | cambia valore binding |
| b | 1/118 | rompe template Angular |
| f | 3/158 | riordino: raro NG parser |
| g | 2/141 | spostamento: raro |
| h | 50/160 | cross-template: binding non risolve nel componente destinazione |
| j | 2/131 | cambio tag |
| **TOT** | **59/1225** | |

## RQ2 — Robustezza per strategia (riassuntiva)

| Strategia | Test | Success | Fragility | Obsolescence | NotCompiled | Success% |
|--|--|--|--|--|--|--|
| Absolute | 1543 | 1244 | 188 | 25 | 86 | 85.4% |
| Relative | 1543 | 1400 | 32 | 25 | 86 | 96.1% |
| Robula | 1543 | 1388 | 44 | 25 | 86 | 95.3% |
| Robula+ | 1543 | 1410 | 22 | 25 | 86 | 96.8% |
| Selenium | 1543 | 1397 | 35 | 25 | 86 | 95.9% |
| Katalon | 1543 | 1356 | 76 | 25 | 86 | 93.1% |

## RQ2 — Dettaglio robustezza (passed/validi) — strategia × operatore

| Strategia | a | b | c | d | e | f | g | h | j | k |
|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 166/166 | 149/151 | 167/168 | 69/72 | 69/72 | 157/195 | 125/173 | 92/130 | 119/162 | 131/168 |
| Relative | 162/166 | 145/151 | 162/168 | 68/72 | 68/72 | 194/195 | 172/173 | 113/130 | 148/162 | 168/168 |
| Robula | 160/166 | 143/151 | 160/168 | 69/72 | 69/72 | 194/195 | 169/173 | 113/130 | 147/162 | 164/168 |
| Robula+ | 164/166 | 147/151 | 164/168 | 69/72 | 69/72 | 194/195 | 169/173 | 113/130 | 157/162 | 164/168 |
| Selenium | 166/166 | 143/151 | 160/168 | 69/72 | 69/72 | 189/195 | 166/173 | 111/130 | 161/162 | 163/168 |
| Katalon | 165/166 | 149/151 | 166/168 | 61/72 | 61/72 | 177/195 | 157/173 | 101/130 | 151/162 | 168/168 |
