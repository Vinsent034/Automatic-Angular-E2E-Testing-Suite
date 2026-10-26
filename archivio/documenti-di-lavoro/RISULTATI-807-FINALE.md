# Tabelle RQ1 + RQ2 — TUTTI gli 807 mutanti (CineLib)

**Mutanti: 807/807  |  validi: 714/807**

## RQ1 — Validità (valid/tot) — operatore × ruolo

| Op | α | β | γ | δ | TOT |
|--|--|--|--|--|--|
| a | 43/44 | 39/39 | 14/14 | 36/36 | 132/133 |
| b | 43/43 | 18/18 | 2/2 | 17/17 | 80/80 |
| c | 43/44 | 33/35 | 3/5 | 33/35 | 112/119 |
| d | 34/34 | – | – | 25/25 | 59/59 |
| e | 33/34 | – | – | 14/15 | 47/49 |
| f | 5/5 | 1/1 | 1/1 | 3/3 | 10/10 |
| g | 7/7 | 1/1 | 1/1 | 3/3 | 12/12 |
| h | 11/39 | 2/26 | 0/16 | 11/23 | 24/104 |
| i | – | 11/11 | 2/2 | 7/7 | 20/20 |
| j | 43/45 | 34/34 | 9/9 | 31/32 | 117/120 |
| k | 44/44 | 26/26 | 4/4 | 27/27 | 101/101 |
| **TOT** | 306/339 | 165/191 | 36/54 | 207/223 | **714/807** |

**Validità escludendo `h` (not-compilable per costruzione): 690/703 = 98.2%**

## RQ1 — Cause di non-validità
| Operatore | Non validi | Nota |
|--|--|--|
| a | 1 | rompe binding/template Angular |
| c | 7 | rompe binding/template Angular |
| e | 2 | rompe binding/template Angular |
| h | 80 | cross-template: not-compilable per costruzione |
| j | 3 | rompe binding/template Angular |
| **TOT** | **93/807** | |

## RQ2 — Dettaglio robustezza (passed/validi) — strategia × operatore

| Strategia | a | b | c | d | e | f | g | h | i | j | k |
|--|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 131/132 | 80/80 | 112/112 | 49/59 | 36/47 | 7/10 | 5/12 | 22/24 | 10/20 | 59/117 | 63/101 |
| Relative | 126/132 | 76/80 | 109/112 | 49/59 | 37/47 | 10/10 | 11/12 | 24/24 | 19/20 | 94/117 | 100/101 |
| Selenium | 109/132 | 71/80 | 97/112 | 49/59 | 37/47 | 10/10 | 11/12 | 24/24 | 20/20 | 116/117 | 96/101 |
| Katalon | 132/132 | 78/80 | 109/112 | 42/59 | 32/47 | 5/10 | 8/12 | 24/24 | 20/20 | 104/117 | 100/101 |
| Robula | 123/132 | 73/80 | 104/112 | 49/59 | 37/47 | 10/10 | 9/12 | 22/24 | 19/20 | 96/117 | 93/101 |
| Robula+ | 125/132 | 78/80 | 109/112 | 48/59 | 37/47 | 10/10 | 11/12 | 24/24 | 20/20 | 113/117 | 97/101 |
