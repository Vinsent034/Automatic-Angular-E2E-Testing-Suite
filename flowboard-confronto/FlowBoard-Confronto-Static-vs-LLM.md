# FlowBoard — Confronto mutazioni **Static** vs **LLM**

**Robustezza dei locatori sotto mutazione** — mutation testing su app Angular (FlowBoard).  
Scope: tutti e 6 gli scenari (S1–S6), 9 componenti. 
Mutanti generati e testati: **Static = 2840**, **LLM = 1225**.  
*(Le due tecniche generano un numero diverso di mutanti — lo Static, meccanico e senza dedup, molti di più; il confronto è quindi sui tassi di validità/robustezza, non sui conteggi assoluti. Ogni percentuale è riportata con la frazione da cui deriva.)*

Le due tecniche generano mutazioni con gli **stessi 11 operatori (a–k)** sugli **stessi 152 target**; la differenza è **come**: lo Static muta meccanicamente (ignaro di Angular), l'LLM autorando mutazioni valide che preservano binding/`@if`/`@for`.

---

## RQ1 — Capacità di generare mutanti VALIDI

### 1. Static — validi/tot per operatore × ruolo
| Op | α | β | γ | δ | TOT |
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

**Validità Static: 1180/2840 = 41.5%** (mutanti validi / mutanti totali)

### 2. LLM — validi/tot per operatore × ruolo
| Op | α | β | γ | δ | TOT |
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

**Validità LLM: 1166/1225 = 95.2%** (mutanti validi / mutanti totali)

### 3. Confronto validità per operatore (Static vs LLM)

| Op | Static valid% (validi/tot) | LLM valid% (validi/tot) | Δ (LLM−Static) |
|--|--|--|--|
| a | 33.1% (55/166) | 99.2% (132/133) | +66.1 |
| b | 41.0% (68/166) | 99.2% (117/118) | +58.2 |
| c | 0.0% (0/18) | 100.0% (133/133) | +100.0 |
| d | 44.4% (80/180) | 100.0% (59/59) | +55.6 |
| e | 44.2% (80/181) | 100.0% (59/59) | +55.8 |
| f | 41.7% (118/283) | 98.1% (155/158) | +56.4 |
| g | 29.4% (58/197) | 98.6% (139/141) | +69.1 |
| h | 40.6% (230/566) | 68.8% (110/160) | +28.1 |
| i | 46.5% (168/361) | – | – |
| j | 44.3% (160/361) | 98.5% (129/131) | +54.2 |
| k | 45.2% (163/361) | 100.0% (133/133) | +54.8 |
| **TOT** | **41.5% (1180/2840)** | **95.2% (1166/1225)** | **+53.6** |

> **Finding RQ1:** lo Static genera mutanti validi al **41.5% (1180/2840)**, l'LLM al **95.2% (1166/1225)**. La tecnica LLM, comprendendo Angular, evita di rompere binding/direttive/`@for`, mentre lo Static (meccanico) produce in maggioranza mutanti non compilabili.

### 4. Cause di non-validità (not-compiled) per operatore

| Op | Static non-validi/tot | LLM non-validi/tot |
|--|--|--|
| a | 111/166 | 1/133 |
| b | 98/166 | 1/118 |
| c | 18/18 | 0/133 |
| d | 100/180 | 0/59 |
| e | 101/181 | 0/59 |
| f | 165/283 | 3/158 |
| g | 139/197 | 2/141 |
| h | 336/566 | 50/160 |
| i | 193/361 | 0/0 |
| j | 201/361 | 2/131 |
| k | 198/361 | 0/133 |

## RQ2 — Utilità differenziale delle strategie di locatori

*Nelle due tabelle riassuntive ogni conteggio è espresso come **esito / totale delle esecuzioni di quella strategia** (Success + Fragility + Obsolescence + NotCompiled). Il **Success%** è invece calcolato sui soli mutanti **testabili**, cioè escludendo i NotCompiled: la sua frazione `(passed/testabili)` ha quindi un denominatore più piccolo.*

### 1. Static — riassuntiva per strategia
| Strategia | Success | Fragility | Obsolescence | NotCompiled | Success% (passed/testabili) |
|--|--|--|--|--|--|
| Absolute | 701/3645 | 175/3645 | 367/3645 | 2402/3645 | 56.4% (701/1243) |
| Relative | 850/3645 | 26/3645 | 367/3645 | 2402/3645 | 68.4% (850/1243) |
| Robula | 837/3645 | 39/3645 | 367/3645 | 2402/3645 | 67.3% (837/1243) |
| Robula+ | 849/3645 | 27/3645 | 367/3645 | 2402/3645 | 68.3% (849/1243) |
| Selenium | 840/3645 | 36/3645 | 367/3645 | 2402/3645 | 67.6% (840/1243) |
| Katalon | 836/3645 | 40/3645 | 367/3645 | 2402/3645 | 67.3% (836/1243) |

### 2. LLM — riassuntiva per strategia
| Strategia | Success | Fragility | Obsolescence | NotCompiled | Success% (passed/testabili) |
|--|--|--|--|--|--|
| Absolute | 1244/1543 | 188/1543 | 25/1543 | 86/1543 | 85.4% (1244/1457) |
| Relative | 1400/1543 | 32/1543 | 25/1543 | 86/1543 | 96.1% (1400/1457) |
| Robula | 1388/1543 | 44/1543 | 25/1543 | 86/1543 | 95.3% (1388/1457) |
| Robula+ | 1410/1543 | 22/1543 | 25/1543 | 86/1543 | 96.8% (1410/1457) |
| Selenium | 1397/1543 | 35/1543 | 25/1543 | 86/1543 | 95.9% (1397/1457) |
| Katalon | 1356/1543 | 76/1543 | 25/1543 | 86/1543 | 93.1% (1356/1457) |

### 3. Confronto robustezza per strategia (Success% sui testabili)

| Strategia | Static Success% (passed/testabili) | LLM Success% (passed/testabili) |
|--|--|--|
| Absolute | 56.4% (701/1243) | 85.4% (1244/1457) |
| Relative | 68.4% (850/1243) | 96.1% (1400/1457) |
| Robula | 67.3% (837/1243) | 95.3% (1388/1457) |
| Robula+ | 68.3% (849/1243) | 96.8% (1410/1457) |
| Selenium | 67.6% (840/1243) | 95.9% (1397/1457) |
| Katalon | 67.3% (836/1243) | 93.1% (1356/1457) |

> **Finding RQ2:** in entrambe le tecniche **Absolute è la strategia più fragile** e le strategie basate su attributi/classi/testo (Relative, Robula, Robula+, Selenium) sono le più robuste — conferma dell'utilità differenziale. La firma di fragilità è coerente tra Static e LLM.

### 4. Dettaglio Static (passed/validi) — strategia × operatore
| Strategia | a | b | c | d | e | f | g | h | i | j | k |
|--|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 44/59 | 54/70 | – | 57/82 | 61/82 | 67/125 | 14/68 | 103/239 | 89/181 | 84/166 | 128/171 |
| Relative | 42/59 | 53/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 122/239 | 136/181 | 119/166 | 141/171 |
| Robula | 38/59 | 50/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 121/239 | 134/181 | 116/166 | 141/171 |
| Robula+ | 38/59 | 51/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 121/239 | 134/181 | 127/166 | 141/171 |
| Selenium | 46/59 | 56/70 | – | 58/82 | 61/82 | 85/125 | 16/68 | 120/239 | 130/181 | 129/166 | 139/171 |
| Katalon | 44/59 | 55/70 | – | 54/82 | 57/82 | 97/125 | 15/68 | 117/239 | 133/181 | 123/166 | 141/171 |

### 5. Dettaglio LLM (passed/validi) — strategia × operatore
| Strategia | a | b | c | d | e | f | g | h | j | k |
|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 166/166 | 149/151 | 167/168 | 69/72 | 69/72 | 157/195 | 125/173 | 92/130 | 119/162 | 131/168 |
| Relative | 162/166 | 145/151 | 162/168 | 68/72 | 68/72 | 194/195 | 172/173 | 113/130 | 148/162 | 168/168 |
| Robula | 160/166 | 143/151 | 160/168 | 69/72 | 69/72 | 194/195 | 169/173 | 113/130 | 147/162 | 164/168 |
| Robula+ | 164/166 | 147/151 | 164/168 | 69/72 | 69/72 | 194/195 | 169/173 | 113/130 | 157/162 | 164/168 |
| Selenium | 166/166 | 143/151 | 160/168 | 69/72 | 69/72 | 189/195 | 166/173 | 111/130 | 161/162 | 163/168 |
| Katalon | 165/166 | 149/151 | 166/168 | 61/72 | 61/72 | 177/195 | 157/173 | 101/130 | 151/162 | 168/168 |

---

## Conclusione — quantità non è validità

La tecnica **Static genera molti più mutanti** della tecnica LLM — **2840** contro **1225**, cioè **2.3× di più** — ma **solo una minima parte di essi compila**: i mutanti Static validi sono **1180/2840 = 41.5%** (mutanti validi / mutanti totali), contro **1166/1225 = 95.2%** (mutanti validi / mutanti totali) dell'LLM, con uno scarto di **53.6 punti percentuali**. In altre parole, lo Static scarta **1660/2840 = 58.5%** dei mutanti prodotti perché non compilabili, mentre l'LLM ne scarta appena **59/1225 = 4.8%**.

Il vantaggio numerico dello Static è quindi **apparente**: mutando il template in modo meccanico e ignaro di Angular, rompe binding, direttive strutturali e `@for`, e i mutanti risultanti non arrivano nemmeno alla fase di test. Al netto della compilazione, i mutanti effettivamente utilizzabili sono **1180** per lo Static e **1166** per l'LLM: a fronte di **2.3×** mutanti generati, lo Static ne rende testabili solo **1.01×** rispetto all'LLM. È per questa ragione che il confronto tra le due tecniche va condotto **sui tassi** (validi/totali) e **mai sui conteggi assoluti**.
