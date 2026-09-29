# Blocco A — riesecuzione della Procedura B a 7 strategie

Classificazione identica a quella dello strumento: un mutante è *obsoleto* se nessuna strategia lo supera, *robusto* se le superano tutte, *fragile* altrimenti. La fragilità di una strategia si conta solo sui mutanti fragili.


## CineLib linguistica

File letti: batches-llm-catalog.csv, batches-llm-moviedetail.csv, batches-llm-movieform.csv, batches-llm-reviews.csv, batches-llm-stats.csv

**Complessivo** — 807 mutanti (robusti 492, fragili 199, obsoleti 23, non compilati 93)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 807 | 576 | 115 | 23 | 93 | 16.1 |
| Relative | 807 | 651 | 40 | 23 | 93 | 5.6 |
| Robula | 807 | 632 | 59 | 23 | 93 | 8.3 |
| Robula+ | 807 | 669 | 22 | 23 | 93 | 3.1 |
| Selenium | 807 | 637 | 54 | 23 | 93 | 7.6 |
| Katalon | 807 | 650 | 41 | 23 | 93 | 5.7 |
| Hook-Based | 807 | 690 | 1 | 23 | 93 | 0.1 |

### Per ruolo del nodo mutato

Il ruolo è la posizione del nodo mutato rispetto al bersaglio del test. Sul bersaglio l'attributo di riferimento è preservato per costruzione: uno zero di fragilità in quella riga è una proprietà della progettazione dell'esperimento, non una misura di robustezza. Sui ruoli di contesto la misura è invece informativa.

**α bersaglio** — 339 mutanti (robusti 200, fragili 89, obsoleti 17, non compilati 33)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 339 | 255 | 34 | 17 | 33 | 11.1 |
| Relative | 339 | 266 | 23 | 17 | 33 | 7.5 |
| Robula | 339 | 252 | 37 | 17 | 33 | 12.1 |
| Robula+ | 339 | 277 | 12 | 17 | 33 | 3.9 |
| Selenium | 339 | 255 | 34 | 17 | 33 | 11.1 |
| Katalon | 339 | 261 | 28 | 17 | 33 | 9.2 |
| Hook-Based | 339 | 288 | 1 | 17 | 33 | 0.3 |

**β padre** — 191 mutanti (robusti 99, fragili 66, obsoleti 2, non compilati 24)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 191 | 114 | 51 | 2 | 24 | 30.5 |
| Relative | 191 | 154 | 11 | 2 | 24 | 6.6 |
| Robula | 191 | 157 | 8 | 2 | 24 | 4.8 |
| Robula+ | 191 | 157 | 8 | 2 | 24 | 4.8 |
| Selenium | 191 | 150 | 15 | 2 | 24 | 9.0 |
| Katalon | 191 | 165 | 0 | 2 | 24 | 0.0 |
| Hook-Based | 191 | 165 | 0 | 2 | 24 | 0.0 |

**γ antenato** — 54 mutanti (robusti 20, fragili 16, obsoleti 0, non compilati 18)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 54 | 23 | 13 | 0 | 18 | 36.1 |
| Relative | 54 | 32 | 4 | 0 | 18 | 11.1 |
| Robula | 54 | 32 | 4 | 0 | 18 | 11.1 |
| Robula+ | 54 | 34 | 2 | 0 | 18 | 5.6 |
| Selenium | 54 | 34 | 2 | 0 | 18 | 5.6 |
| Katalon | 54 | 34 | 2 | 0 | 18 | 5.6 |
| Hook-Based | 54 | 36 | 0 | 0 | 18 | 0.0 |

**δ fratello** — 223 mutanti (robusti 173, fragili 28, obsoleti 4, non compilati 18)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 223 | 184 | 17 | 4 | 18 | 8.3 |
| Relative | 223 | 199 | 2 | 4 | 18 | 1.0 |
| Robula | 223 | 191 | 10 | 4 | 18 | 4.9 |
| Robula+ | 223 | 201 | 0 | 4 | 18 | 0.0 |
| Selenium | 223 | 198 | 3 | 4 | 18 | 1.5 |
| Katalon | 223 | 190 | 11 | 4 | 18 | 5.4 |
| Hook-Based | 223 | 201 | 0 | 4 | 18 | 0.0 |

### Hook-Based per operatore

| Operatore | Mutanti | Successi | Fragilità | Obsolescenza | Non compilati |
|---|---:|---:|---:|---:|---:|
| a | 133 | 132 | 0 | 0 | 1 |
| b | 80 | 80 | 0 | 0 | 0 |
| c | 119 | 111 | 0 | 0 | 8 |
| d | 59 | 49 | 1 | 9 | 0 |
| e | 49 | 39 | 0 | 10 | 0 |
| f | 10 | 10 | 0 | 0 | 0 |
| g | 12 | 11 | 0 | 1 | 0 |
| h | 104 | 22 | 0 | 3 | 79 |
| i | 20 | 20 | 0 | 0 | 0 |
| j | 120 | 115 | 0 | 0 | 5 |
| k | 101 | 101 | 0 | 0 | 0 |


## CineLib statica

File letti: batches-catalog.csv, batches-moviedetail.csv, batches-movieform.csv, batches-reviews.csv, batches-stats.csv

**Complessivo** — 1142 mutanti (robusti 476, fragili 316, obsoleti 124, non compilati 226)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 1142 | 568 | 224 | 124 | 226 | 24.5 |
| Relative | 1142 | 754 | 38 | 124 | 226 | 4.1 |
| Robula | 1142 | 738 | 54 | 124 | 226 | 5.9 |
| Robula+ | 1142 | 774 | 18 | 124 | 226 | 2.0 |
| Selenium | 1142 | 764 | 28 | 124 | 226 | 3.1 |
| Katalon | 1142 | 652 | 140 | 124 | 226 | 15.3 |
| Hook-Based | 1142 | 781 | 11 | 124 | 226 | 1.2 |

### Per ruolo del nodo mutato

Il ruolo è la posizione del nodo mutato rispetto al bersaglio del test. Sul bersaglio l'attributo di riferimento è preservato per costruzione: uno zero di fragilità in quella riga è una proprietà della progettazione dell'esperimento, non una misura di robustezza. Sui ruoli di contesto la misura è invece informativa.

**α bersaglio** — 363 mutanti (robusti 179, fragili 81, obsoleti 47, non compilati 56)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 363 | 224 | 36 | 47 | 56 | 11.7 |
| Relative | 363 | 241 | 19 | 47 | 56 | 6.2 |
| Robula | 363 | 234 | 26 | 47 | 56 | 8.5 |
| Robula+ | 363 | 252 | 8 | 47 | 56 | 2.6 |
| Selenium | 363 | 251 | 9 | 47 | 56 | 2.9 |
| Katalon | 363 | 202 | 58 | 47 | 56 | 18.9 |
| Hook-Based | 363 | 255 | 5 | 47 | 56 | 1.6 |

**β padre** — 264 mutanti (robusti 71, fragili 104, obsoleti 21, non compilati 68)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 264 | 86 | 89 | 21 | 68 | 45.4 |
| Relative | 264 | 165 | 10 | 21 | 68 | 5.1 |
| Robula | 264 | 159 | 16 | 21 | 68 | 8.2 |
| Robula+ | 264 | 169 | 6 | 21 | 68 | 3.1 |
| Selenium | 264 | 165 | 10 | 21 | 68 | 5.1 |
| Katalon | 264 | 149 | 26 | 21 | 68 | 13.3 |
| Hook-Based | 264 | 171 | 4 | 21 | 68 | 2.0 |

**γ antenato** — 227 mutanti (robusti 55, fragili 83, obsoleti 33, non compilati 56)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 227 | 64 | 74 | 33 | 56 | 43.3 |
| Relative | 227 | 130 | 8 | 33 | 56 | 4.7 |
| Robula | 227 | 130 | 8 | 33 | 56 | 4.7 |
| Robula+ | 227 | 134 | 4 | 33 | 56 | 2.3 |
| Selenium | 227 | 130 | 8 | 33 | 56 | 4.7 |
| Katalon | 227 | 118 | 20 | 33 | 56 | 11.7 |
| Hook-Based | 227 | 136 | 2 | 33 | 56 | 1.2 |

**δ fratello** — 288 mutanti (robusti 171, fragili 48, obsoleti 23, non compilati 46)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 288 | 194 | 25 | 23 | 46 | 10.3 |
| Relative | 288 | 218 | 1 | 23 | 46 | 0.4 |
| Robula | 288 | 215 | 4 | 23 | 46 | 1.7 |
| Robula+ | 288 | 219 | 0 | 23 | 46 | 0.0 |
| Selenium | 288 | 218 | 1 | 23 | 46 | 0.4 |
| Katalon | 288 | 183 | 36 | 23 | 46 | 14.9 |
| Hook-Based | 288 | 219 | 0 | 23 | 46 | 0.0 |

### Hook-Based per operatore

| Operatore | Mutanti | Successi | Fragilità | Obsolescenza | Non compilati |
|---|---:|---:|---:|---:|---:|
| a | 54 | 21 | 0 | 2 | 31 |
| b | 54 | 52 | 0 | 2 | 0 |
| c | 4 | 4 | 0 | 0 | 0 |
| d | 75 | 61 | 0 | 14 | 0 |
| e | 76 | 44 | 0 | 14 | 18 |
| f | 118 | 101 | 0 | 15 | 2 |
| g | 115 | 23 | 0 | 12 | 80 |
| h | 163 | 45 | 2 | 27 | 89 |
| i | 161 | 131 | 9 | 21 | 0 |
| j | 161 | 145 | 0 | 10 | 6 |
| k | 161 | 154 | 0 | 7 | 0 |


## FlowBoard linguistica

File letti: batches-s1s2.csv, batches-s3.csv, batches-s4.csv, batches-s5.csv, batches-s6.csv

**Complessivo** — 1225 mutanti (robusti 891, fragili 250, obsoleti 23, non compilati 61)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 1543 | 1241 | 190 | 23 | 89 | 13.1 |
| Relative | 1543 | 1397 | 34 | 23 | 89 | 2.3 |
| Robula | 1543 | 1386 | 45 | 23 | 89 | 3.1 |
| Robula+ | 1543 | 1407 | 24 | 23 | 89 | 1.7 |
| Selenium | 1543 | 1394 | 37 | 23 | 89 | 2.5 |
| Katalon | 1543 | 1355 | 76 | 23 | 89 | 5.2 |
| Hook-Based | 1543 | 1430 | 1 | 23 | 89 | 0.1 |

### Per ruolo del nodo mutato

Il ruolo è la posizione del nodo mutato rispetto al bersaglio del test. Sul bersaglio l'attributo di riferimento è preservato per costruzione: uno zero di fragilità in quella riga è una proprietà della progettazione dell'esperimento, non una misura di robustezza. Sui ruoli di contesto la misura è invece informativa.

**α bersaglio** — 886 mutanti (robusti 692, fragili 154, obsoleti 12, non compilati 28)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 1101 | 946 | 100 | 12 | 43 | 9.5 |
| Relative | 1101 | 1013 | 33 | 12 | 43 | 3.1 |
| Robula | 1101 | 1004 | 42 | 12 | 43 | 4.0 |
| Robula+ | 1101 | 1025 | 21 | 12 | 43 | 2.0 |
| Selenium | 1101 | 1025 | 21 | 12 | 43 | 2.0 |
| Katalon | 1101 | 1001 | 45 | 12 | 43 | 4.3 |
| Hook-Based | 1101 | 1046 | 0 | 12 | 43 | 0.0 |

**β padre** — 147 mutanti (robusti 70, fragili 52, obsoleti 7, non compilati 18)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 197 | 115 | 50 | 7 | 25 | 29.1 |
| Relative | 197 | 165 | 0 | 7 | 25 | 0.0 |
| Robula | 197 | 163 | 2 | 7 | 25 | 1.2 |
| Robula+ | 197 | 163 | 2 | 7 | 25 | 1.2 |
| Selenium | 197 | 155 | 10 | 7 | 25 | 5.8 |
| Katalon | 197 | 162 | 3 | 7 | 25 | 1.7 |
| Hook-Based | 197 | 165 | 0 | 7 | 25 | 0.0 |

**γ antenato** — 26 mutanti (robusti 6, fragili 9, obsoleti 4, non compilati 7)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 34 | 7 | 13 | 4 | 10 | 54.2 |
| Relative | 34 | 19 | 1 | 4 | 10 | 4.2 |
| Robula | 34 | 19 | 1 | 4 | 10 | 4.2 |
| Robula+ | 34 | 19 | 1 | 4 | 10 | 4.2 |
| Selenium | 34 | 17 | 3 | 4 | 10 | 12.5 |
| Katalon | 34 | 17 | 3 | 4 | 10 | 12.5 |
| Hook-Based | 34 | 19 | 1 | 4 | 10 | 4.2 |

**δ fratello** — 166 mutanti (robusti 123, fragili 35, obsoleti 0, non compilati 8)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 211 | 173 | 27 | 0 | 11 | 13.5 |
| Relative | 211 | 200 | 0 | 0 | 11 | 0.0 |
| Robula | 211 | 200 | 0 | 0 | 11 | 0.0 |
| Robula+ | 211 | 200 | 0 | 0 | 11 | 0.0 |
| Selenium | 211 | 197 | 3 | 0 | 11 | 1.5 |
| Katalon | 211 | 175 | 25 | 0 | 11 | 12.5 |
| Hook-Based | 211 | 200 | 0 | 0 | 11 | 0.0 |

### Hook-Based per operatore

| Operatore | Mutanti | Successi | Fragilità | Obsolescenza | Non compilati |
|---|---:|---:|---:|---:|---:|
| a | 133 | 168 | 0 | 0 | 0 |
| b | 118 | 151 | 0 | 0 | 1 |
| c | 133 | 168 | 0 | 0 | 0 |
| d | 59 | 69 | 0 | 3 | 0 |
| e | 59 | 69 | 0 | 3 | 0 |
| f | 158 | 195 | 0 | 1 | 2 |
| g | 141 | 173 | 0 | 0 | 2 |
| h | 160 | 113 | 1 | 16 | 74 |
| j | 131 | 156 | 0 | 0 | 10 |
| k | 133 | 168 | 0 | 0 | 0 |


## FlowBoard statica

File letti: batches-s1s2.csv, batches-s3.csv, batches-s4.csv, batches-s5.csv, batches-s6.csv

**Complessivo** — 2840 mutanti (robusti 1376, fragili 588, obsoleti 618, non compilati 258)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 3645 | 1991 | 606 | 667 | 381 | 18.6 |
| Relative | 3645 | 2437 | 160 | 667 | 381 | 4.9 |
| Robula | 3645 | 2428 | 169 | 667 | 381 | 5.2 |
| Robula+ | 3645 | 2448 | 149 | 667 | 381 | 4.6 |
| Selenium | 3645 | 2444 | 153 | 667 | 381 | 4.7 |
| Katalon | 3645 | 2394 | 203 | 667 | 381 | 6.2 |
| Hook-Based | 3645 | 2490 | 107 | 667 | 381 | 3.3 |

### Per ruolo del nodo mutato

Il ruolo è la posizione del nodo mutato rispetto al bersaglio del test. Sul bersaglio l'attributo di riferimento è preservato per costruzione: uno zero di fragilità in quella riga è una proprietà della progettazione dell'esperimento, non una misura di robustezza. Sui ruoli di contesto la misura è invece informativa.

**α bersaglio** — 1099 mutanti (robusti 567, fragili 216, obsoleti 253, non compilati 63)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 1405 | 831 | 207 | 275 | 92 | 15.8 |
| Relative | 1405 | 982 | 56 | 275 | 92 | 4.3 |
| Robula | 1405 | 978 | 60 | 275 | 92 | 4.6 |
| Robula+ | 1405 | 987 | 51 | 275 | 92 | 3.9 |
| Selenium | 1405 | 987 | 51 | 275 | 92 | 3.9 |
| Katalon | 1405 | 957 | 81 | 275 | 92 | 6.2 |
| Hook-Based | 1405 | 1003 | 35 | 275 | 92 | 2.7 |

**β padre** — 524 mutanti (robusti 191, fragili 154, obsoleti 108, non compilati 71)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 679 | 277 | 179 | 116 | 107 | 31.3 |
| Relative | 679 | 422 | 34 | 116 | 107 | 5.9 |
| Robula | 679 | 417 | 39 | 116 | 107 | 6.8 |
| Robula+ | 679 | 419 | 37 | 116 | 107 | 6.5 |
| Selenium | 679 | 413 | 43 | 116 | 107 | 7.5 |
| Katalon | 679 | 425 | 31 | 116 | 107 | 5.4 |
| Hook-Based | 679 | 430 | 26 | 116 | 107 | 4.5 |

**γ antenato** — 278 mutanti (robusti 68, fragili 61, obsoleti 87, non compilati 62)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 369 | 101 | 82 | 99 | 87 | 29.1 |
| Relative | 369 | 160 | 23 | 99 | 87 | 8.2 |
| Robula | 369 | 162 | 21 | 99 | 87 | 7.4 |
| Robula+ | 369 | 163 | 20 | 99 | 87 | 7.1 |
| Selenium | 369 | 162 | 21 | 99 | 87 | 7.4 |
| Katalon | 369 | 163 | 20 | 99 | 87 | 7.1 |
| Hook-Based | 369 | 165 | 18 | 99 | 87 | 6.4 |

**δ fratello** — 939 mutanti (robusti 550, fragili 157, obsoleti 170, non compilati 62)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 1192 | 782 | 138 | 177 | 95 | 12.6 |
| Relative | 1192 | 873 | 47 | 177 | 95 | 4.3 |
| Robula | 1192 | 871 | 49 | 177 | 95 | 4.5 |
| Robula+ | 1192 | 879 | 41 | 177 | 95 | 3.7 |
| Selenium | 1192 | 882 | 38 | 177 | 95 | 3.5 |
| Katalon | 1192 | 849 | 71 | 177 | 95 | 6.5 |
| Hook-Based | 1192 | 892 | 28 | 177 | 95 | 2.6 |

### Hook-Based per operatore

| Operatore | Mutanti | Successi | Fragilità | Obsolescenza | Non compilati |
|---|---:|---:|---:|---:|---:|
| a | 166 | 132 | 2 | 23 | 70 |
| b | 166 | 184 | 7 | 34 | 2 |
| c | 18 | 22 | 0 | 2 | 0 |
| d | 180 | 173 | 7 | 41 | 0 |
| e | 181 | 165 | 5 | 39 | 13 |
| f | 283 | 291 | 7 | 50 | 2 |
| g | 197 | 117 | 21 | 65 | 120 |
| h | 566 | 310 | 32 | 200 | 159 |
| i | 361 | 349 | 17 | 80 | 4 |
| j | 361 | 369 | 5 | 67 | 9 |
| k | 361 | 378 | 4 | 66 | 2 |
