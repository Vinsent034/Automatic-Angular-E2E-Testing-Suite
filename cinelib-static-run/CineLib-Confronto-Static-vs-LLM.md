# CineLib — Confronto mutazioni **Static** vs **LLM**

**Robustezza dei locatori sotto mutazione** — mutation testing su app Angular (CineLib).  
Scope: 5 scenari (catalog, movie-detail, movie-form, reviews, stats), 7 componenti, **45 target** identici per le due tecniche. 
Mutanti generati e testati: **Static = 1142**, **LLM = 807**.  
*(Le due tecniche generano un numero diverso di mutanti — lo Static, meccanico e senza dedup, molti di più; il confronto è quindi sui tassi di validità/robustezza, non sui conteggi assoluti. Ogni percentuale è riportata con la frazione da cui deriva.)*

Le due tecniche generano mutazioni con gli **stessi 11 operatori (a–k)** sugli **stessi 45 target**; la differenza è **come**: lo Static muta meccanicamente (ignaro di Angular), l'LLM autorando mutazioni valide che preservano binding/`@if`/`@for`.

---

## RQ1 — Capacità di generare mutanti VALIDI

### 1. Static — validi/tot per operatore × ruolo
| Op | α | β | γ | δ | TOT |
|--|--|--|--|--|--|
| a | 13/25 | 1/6 | 2/9 | 6/14 | 22/54 |
| b | 25/25 | 6/6 | 9/9 | 14/14 | 54/54 |
| c | 4/4 | – | – | – | 4/4 |
| d | 33/33 | 9/9 | 6/6 | 27/27 | 75/75 |
| e | 32/32 | 0/9 | 1/8 | 25/27 | 58/76 |
| f | 32/32 | 28/28 | 25/27 | 31/31 | 116/118 |
| g | 12/32 | 8/32 | 6/24 | 7/27 | 33/115 |
| h | 22/45 | 14/45 | 12/36 | 21/37 | 69/163 |
| i | 45/45 | 43/43 | 36/36 | 37/37 | 161/161 |
| j | 42/45 | 42/43 | 36/36 | 35/37 | 155/161 |
| k | 45/45 | 43/43 | 36/36 | 36/37 | 160/161 |
| **TOT** | 305/363 | 194/264 | 169/227 | 239/288 | **907/1142** |

**Validità Static: 907/1142 = 79.4%** (mutanti validi / mutanti totali)

### 2. LLM — validi/tot per operatore × ruolo
| Op | α | β | γ | δ | TOT |
|--|--|--|--|--|--|
| a | 43/44 | 38/39 | 14/14 | 36/36 | 131/133 |
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
| **TOT** | 306/339 | 164/191 | 36/54 | 207/223 | **713/807** |

**Validità LLM: 713/807 = 88.4%** (mutanti validi / mutanti totali)

### 3. Confronto validità per operatore (Static vs LLM)

| Op | Static valid% (validi/tot) | LLM valid% (validi/tot) | Δ (LLM−Static) |
|--|--|--|--|
| a | 40.7% (22/54) | 98.5% (131/133) | +57.8 |
| b | 100.0% (54/54) | 100.0% (80/80) | +0.0 |
| c | 100.0% (4/4) | 94.1% (112/119) | -5.9 |
| d | 100.0% (75/75) | 100.0% (59/59) | +0.0 |
| e | 76.3% (58/76) | 95.9% (47/49) | +19.6 |
| f | 98.3% (116/118) | 100.0% (10/10) | +1.7 |
| g | 28.7% (33/115) | 100.0% (12/12) | +71.3 |
| h | 42.3% (69/163) | 23.1% (24/104) | -19.3 |
| i | 100.0% (161/161) | 100.0% (20/20) | +0.0 |
| j | 96.3% (155/161) | 97.5% (117/120) | +1.2 |
| k | 99.4% (160/161) | 100.0% (101/101) | +0.6 |
| **TOT** | **79.4% (907/1142)** | **88.4% (713/807)** | **+8.9** |

> **Finding RQ1:** lo Static genera mutanti validi al **79.4% (907/1142)**, l'LLM al **88.4% (713/807)**. La tecnica LLM, comprendendo Angular, evita di rompere binding/direttive/`@for`, mentre lo Static (meccanico) produce in maggioranza mutanti non compilabili.

### 4. Cause di non-validità (not-compiled) per operatore

| Op | Static non-validi/tot | LLM non-validi/tot |
|--|--|--|
| a | 32/54 | 2/133 |
| b | 0/54 | 0/80 |
| c | 0/4 | 7/119 |
| d | 0/75 | 0/59 |
| e | 18/76 | 2/49 |
| f | 2/118 | 0/10 |
| g | 82/115 | 0/12 |
| h | 94/163 | 80/104 |
| i | 0/161 | 0/20 |
| j | 6/161 | 3/120 |
| k | 1/161 | 0/101 |

## RQ2 — Utilità differenziale delle strategie di locatori

*Nelle due tabelle riassuntive ogni conteggio è espresso come **esito / totale delle esecuzioni di quella strategia** (Success + Fragility + Obsolescence + NotCompiled). Il **Success%** è invece calcolato sui soli mutanti **testabili**, cioè escludendo i NotCompiled: la sua frazione `(passed/testabili)` ha quindi un denominatore più piccolo.*

### 1. Static — riassuntiva per strategia
| Strategia | Success | Fragility | Obsolescence | NotCompiled | Success% (passed/testabili) |
|--|--|--|--|--|--|
| Absolute | 575/1142 | 210/1142 | 122/1142 | 235/1142 | 63.4% (575/907) |
| Relative | 748/1142 | 37/1142 | 122/1142 | 235/1142 | 82.5% (748/907) |
| Robula | 732/1142 | 53/1142 | 122/1142 | 235/1142 | 80.7% (732/907) |
| Robula+ | 768/1142 | 17/1142 | 122/1142 | 235/1142 | 84.7% (768/907) |
| Selenium | 758/1142 | 27/1142 | 122/1142 | 235/1142 | 83.6% (758/907) |
| Katalon | 648/1142 | 137/1142 | 122/1142 | 235/1142 | 71.4% (648/907) |

### 2. LLM — riassuntiva per strategia
| Strategia | Success | Fragility | Obsolescence | NotCompiled | Success% (passed/testabili) |
|--|--|--|--|--|--|
| Absolute | 573/807 | 119/807 | 21/807 | 94/807 | 80.4% (573/713) |
| Relative | 654/807 | 38/807 | 21/807 | 94/807 | 91.7% (654/713) |
| Robula | 634/807 | 58/807 | 21/807 | 94/807 | 88.9% (634/713) |
| Robula+ | 671/807 | 21/807 | 21/807 | 94/807 | 94.1% (671/713) |
| Selenium | 639/807 | 53/807 | 21/807 | 94/807 | 89.6% (639/713) |
| Katalon | 653/807 | 39/807 | 21/807 | 94/807 | 91.6% (653/713) |

### 3. Confronto robustezza per strategia (Success% sui testabili)

| Strategia | Static Success% (passed/testabili) | LLM Success% (passed/testabili) |
|--|--|--|
| Absolute | 63.4% (575/907) | 80.4% (573/713) |
| Relative | 82.5% (748/907) | 91.7% (654/713) |
| Robula | 80.7% (732/907) | 88.9% (634/713) |
| Robula+ | 84.7% (768/907) | 94.1% (671/713) |
| Selenium | 83.6% (758/907) | 89.6% (639/713) |
| Katalon | 71.4% (648/907) | 91.6% (653/713) |

### 4. Dettaglio Static (passed/validi) — strategia × operatore
| Strategia | a | b | c | d | e | f | g | h | i | j | k |
|--|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 19/22 | 51/54 | 4/4 | 61/75 | 44/58 | 76/116 | 18/33 | 41/69 | 70/161 | 57/155 | 134/160 |
| Relative | 19/22 | 49/54 | 3/4 | 61/75 | 44/58 | 101/116 | 23/33 | 42/69 | 127/161 | 126/155 | 153/160 |
| Robula | 16/22 | 45/54 | 3/4 | 61/75 | 44/58 | 100/116 | 23/33 | 41/69 | 124/161 | 125/155 | 150/160 |
| Robula+ | 19/22 | 49/54 | 3/4 | 61/75 | 44/58 | 101/116 | 23/33 | 41/69 | 132/161 | 142/155 | 153/160 |
| Selenium | 19/22 | 52/54 | 3/4 | 61/75 | 44/58 | 100/116 | 23/33 | 41/69 | 118/161 | 144/155 | 153/160 |
| Katalon | 12/22 | 42/54 | 3/4 | 47/75 | 35/58 | 76/116 | 22/33 | 36/69 | 122/161 | 115/155 | 138/160 |

### 5. Dettaglio LLM (passed/validi) — strategia × operatore
| Strategia | a | b | c | d | e | f | g | h | i | j | k |
|--|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 130/131 | 80/80 | 112/112 | 49/59 | 36/47 | 7/10 | 5/12 | 22/24 | 10/20 | 59/117 | 63/101 |
| Relative | 125/131 | 76/80 | 109/112 | 49/59 | 37/47 | 10/10 | 11/12 | 24/24 | 19/20 | 94/117 | 100/101 |
| Robula | 122/131 | 73/80 | 104/112 | 49/59 | 37/47 | 10/10 | 9/12 | 22/24 | 19/20 | 96/117 | 93/101 |
| Robula+ | 124/131 | 78/80 | 109/112 | 48/59 | 37/47 | 10/10 | 11/12 | 24/24 | 20/20 | 113/117 | 97/101 |
| Selenium | 108/131 | 71/80 | 97/112 | 49/59 | 37/47 | 10/10 | 11/12 | 24/24 | 20/20 | 116/117 | 96/101 |
| Katalon | 131/131 | 78/80 | 109/112 | 42/59 | 32/47 | 5/10 | 8/12 | 24/24 | 20/20 | 104/117 | 100/101 |

---

## Conclusione — quantità non è validità

Sugli **stessi 45 target**, la tecnica **Static genera più mutanti** della tecnica LLM — **1142** contro **807**, cioè **1.4× di più** — ma con una **quota di scarto doppia**: i mutanti Static validi sono **907/1142 = 79.4%** (mutanti validi / mutanti totali), contro **713/807 = 88.4%** (mutanti validi / mutanti totali) dell'LLM, con uno scarto di **8.9 punti percentuali**. In altre parole, lo Static scarta **235/1142 = 20.6%** dei mutanti prodotti perché non compilabili, mentre l'LLM ne scarta **94/807 = 11.6%**.
