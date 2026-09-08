# STATIC (2840, 5 gruppi) — 2840 mutanti — ['batches-s1s2.csv', 'batches-s3.csv', 'batches-s4.csv', 'batches-s5.csv', 'batches-s6.csv']

## RQ1 — Validità (validi/tot) per operatore × ruolo

| Op | a(alp) | b(bet) | g(gam) | d(del) | TOT |
|--|--|--|--|--|--|
| a | 22/72 | 13/22 | 2/14 | 18/58 | 55/166 |
| b | 28/72 | 11/21 | 2/13 | 27/60 | 68/166 |
| c | 0/9 | – | – | 0/9 | 0/18 |
| d | 41/92 | 2/5 | 2/5 | 35/78 | 80/180 |
| e | 44/91 | 1/6 | 0/5 | 35/79 | 80/181 |
| f | 41/96 | 30/73 | 4/16 | 43/98 | 118/283 |
| g | 19/59 | 14/57 | 8/32 | 17/49 | 58/197 |
| h | 62/152 | 56/151 | 53/124 | 59/139 | 230/566 |
| i | 78/152 | 26/63 | 11/23 | 53/123 | 168/361 |
| j | 67/152 | 28/63 | 11/23 | 54/123 | 160/361 |
| k | 70/152 | 26/63 | 12/23 | 55/123 | 163/361 |
| **TOT** | 472/1099 | 207/524 | 105/278 | 396/939 | **1180/2840** |

**Validità totale: 1180/2840 = 41.5%**  | **escluso h: 950/2274 = 41.8%**

## RQ1 — Cause di non-validità (not-compiled) per operatore

| Op | Non validi/tot | Nota |
|--|--|--|
| a | 111/166 | cambia valore binding |
| b | 98/166 | rompe template Angular |
| c | 18/18 | rinomina attributo/binding |
| d | 100/180 | rompe template Angular |
| e | 101/181 | rompe template Angular |
| f | 165/283 | riordino: raro NG parser |
| g | 139/197 | spostamento: raro |
| h | 336/566 | cross-template: binding non risolve nel componente destinazione |
| i | 193/361 | rompe template Angular |
| j | 201/361 | cambio tag |
| k | 198/361 | rompe template Angular |
| **TOT** | **1660/2840** | |

## RQ2 — Robustezza per strategia (riassuntiva)

| Strategia | Test | Success | Fragility | Obsolescence | NotCompiled | Success% |
|--|--|--|--|--|--|--|
| Absolute | 3645 | 701 | 175 | 367 | 2402 | 56.4% |
| Relative | 3645 | 850 | 26 | 367 | 2402 | 68.4% |
| Robula | 3645 | 837 | 39 | 367 | 2402 | 67.3% |
| Robula+ | 3645 | 849 | 27 | 367 | 2402 | 68.3% |
| Selenium | 3645 | 840 | 36 | 367 | 2402 | 67.6% |
| Katalon | 3645 | 836 | 40 | 367 | 2402 | 67.3% |

## RQ2 — Dettaglio robustezza (passed/validi) — strategia × operatore

| Strategia | a | b | c | d | e | f | g | h | i | j | k |
|--|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 44/59 | 54/70 | – | 57/82 | 61/82 | 67/125 | 14/68 | 103/239 | 89/181 | 84/166 | 128/171 |
| Relative | 42/59 | 53/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 122/239 | 136/181 | 119/166 | 141/171 |
| Robula | 38/59 | 50/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 121/239 | 134/181 | 116/166 | 141/171 |
| Robula+ | 38/59 | 51/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 121/239 | 134/181 | 127/166 | 141/171 |
| Selenium | 46/59 | 56/70 | – | 58/82 | 61/82 | 85/125 | 16/68 | 120/239 | 130/181 | 129/166 | 139/171 |
| Katalon | 44/59 | 55/70 | – | 54/82 | 57/82 | 97/125 | 15/68 | 117/239 | 133/181 | 123/166 | 141/171 |
