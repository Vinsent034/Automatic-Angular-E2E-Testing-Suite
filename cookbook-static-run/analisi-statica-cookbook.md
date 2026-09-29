# Blocco A — riesecuzione della Procedura B a 7 strategie

Classificazione identica a quella dello strumento: un mutante è *obsoleto* se nessuna strategia lo supera, *robusto* se le superano tutte, *fragile* altrimenti. La fragilità di una strategia si conta solo sui mutanti fragili.


## CookBook statica

File letti: batches-s1s2.csv, batches-s3.csv, batches-s4.csv, batches-s5.csv, batches-s6.csv

**Complessivo** — 1503 mutanti (robusti 769, fragili 343, obsoleti 131, non compilati 260)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 1844 | 1031 | 348 | 131 | 334 | 23.0 |
| Relative | 1844 | 1335 | 44 | 131 | 334 | 2.9 |
| Robula | 1844 | 1320 | 59 | 131 | 334 | 3.9 |
| Robula+ | 1844 | 1340 | 39 | 131 | 334 | 2.6 |
| Selenium | 1844 | 1336 | 43 | 131 | 334 | 2.8 |
| Katalon | 1844 | 1217 | 162 | 131 | 334 | 10.7 |
| Hook-Based | 1844 | 1351 | 28 | 131 | 334 | 1.9 |

### Per ruolo del nodo mutato

Il ruolo è la posizione del nodo mutato rispetto al bersaglio del test. Sul bersaglio l'attributo di riferimento è preservato per costruzione: uno zero di fragilità in quella riga è una proprietà della progettazione dell'esperimento, non una misura di robustezza. Sui ruoli di contesto la misura è invece informativa.

**α bersaglio** — 422 mutanti (robusti 195, fragili 76, obsoleti 57, non compilati 94)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 524 | 285 | 59 | 57 | 123 | 14.7 |
| Relative | 524 | 309 | 35 | 57 | 123 | 8.7 |
| Robula | 524 | 307 | 37 | 57 | 123 | 9.2 |
| Robula+ | 524 | 316 | 28 | 57 | 123 | 7.0 |
| Selenium | 524 | 321 | 23 | 57 | 123 | 5.7 |
| Katalon | 524 | 294 | 50 | 57 | 123 | 12.5 |
| Hook-Based | 524 | 325 | 19 | 57 | 123 | 4.7 |

**β padre** — 296 mutanti (robusti 144, fragili 76, obsoleti 25, non compilati 51)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 367 | 192 | 82 | 25 | 68 | 27.4 |
| Relative | 367 | 272 | 2 | 25 | 68 | 0.7 |
| Robula | 367 | 266 | 8 | 25 | 68 | 2.7 |
| Robula+ | 367 | 270 | 4 | 25 | 68 | 1.3 |
| Selenium | 367 | 268 | 6 | 25 | 68 | 2.0 |
| Katalon | 367 | 250 | 24 | 25 | 68 | 8.0 |
| Hook-Based | 367 | 272 | 2 | 25 | 68 | 0.7 |

**γ antenato** — 311 mutanti (robusti 113, fragili 113, obsoleti 25, non compilati 60)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 382 | 147 | 133 | 25 | 77 | 43.6 |
| Relative | 382 | 273 | 7 | 25 | 77 | 2.3 |
| Robula | 382 | 266 | 14 | 25 | 77 | 4.6 |
| Robula+ | 382 | 273 | 7 | 25 | 77 | 2.3 |
| Selenium | 382 | 266 | 14 | 25 | 77 | 4.6 |
| Katalon | 382 | 240 | 40 | 25 | 77 | 13.1 |
| Hook-Based | 382 | 273 | 7 | 25 | 77 | 2.3 |

**δ fratello** — 420 mutanti (robusti 291, fragili 65, obsoleti 19, non compilati 45)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 517 | 381 | 61 | 19 | 56 | 13.2 |
| Relative | 517 | 442 | 0 | 19 | 56 | 0.0 |
| Robula | 517 | 442 | 0 | 19 | 56 | 0.0 |
| Robula+ | 517 | 442 | 0 | 19 | 56 | 0.0 |
| Selenium | 517 | 442 | 0 | 19 | 56 | 0.0 |
| Katalon | 517 | 396 | 46 | 19 | 56 | 10.0 |
| Hook-Based | 517 | 442 | 0 | 19 | 56 | 0.0 |

**ε componente** — 54 mutanti (robusti 26, fragili 13, obsoleti 5, non compilati 10)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 54 | 26 | 13 | 5 | 10 | 29.5 |
| Relative | 54 | 39 | 0 | 5 | 10 | 0.0 |
| Robula | 54 | 39 | 0 | 5 | 10 | 0.0 |
| Robula+ | 54 | 39 | 0 | 5 | 10 | 0.0 |
| Selenium | 54 | 39 | 0 | 5 | 10 | 0.0 |
| Katalon | 54 | 37 | 2 | 5 | 10 | 4.5 |
| Hook-Based | 54 | 39 | 0 | 5 | 10 | 0.0 |

### Hook-Based per operatore

| Operatore | Mutanti | Successi | Fragilità | Obsolescenza | Non compilati |
|---|---:|---:|---:|---:|---:|
| a | 73 | 51 | 0 | 4 | 36 |
| b | 73 | 80 | 4 | 7 | 0 |
| c | 12 | 16 | 0 | 0 | 0 |
| d | 91 | 96 | 2 | 9 | 2 |
| e | 92 | 92 | 2 | 11 | 6 |
| f | 111 | 128 | 0 | 7 | 0 |
| g | 191 | 110 | 6 | 17 | 106 |
| h | 215 | 105 | 6 | 24 | 128 |
| i | 215 | 232 | 8 | 23 | 0 |
| j | 215 | 218 | 0 | 11 | 34 |
| k | 215 | 223 | 0 | 18 | 22 |
