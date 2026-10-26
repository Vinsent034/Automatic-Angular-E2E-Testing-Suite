# FlowBoard — Confronto mutazioni **LLM** vs **Static**

**Robustezza dei locatori sotto mutazione** — mutation testing su app Angular (FlowBoard).  
Scope: tutti e 6 gli scenari (S1–S6), 9 componenti. 
Mutanti testati: **LLM = 1225**, **Static = 2840**.  
*(Le due tecniche generano un numero diverso di mutanti — lo Static, meccanico e senza dedup, molti di più; il confronto è quindi sui tassi di validità/robustezza, non sui conteggi assoluti.)*

Le due tecniche generano mutazioni con gli **stessi 11 operatori (a–k)** sugli **stessi 152 target**; la differenza è **come**: lo Static muta meccanicamente (ignaro di Angular), l'LLM autorando mutazioni valide che preservano binding/`@if`/`@for`.

---

## RQ1 — Capacità di generare mutanti VALIDI

### LLM — validi/tot per operatore × ruolo
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

**Validità LLM: 1166/1225 = 95.2%**

### Static — validi/tot per operatore × ruolo
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

**Validità Static: 1180/2840 = 41.5%**

### Confronto validità per operatore (LLM vs Static)

| Op | LLM valid% | Static valid% | Δ (LLM−Static) |
|--|--|--|--|
| a | 99.2% (132/133) | 33.1% (55/166) | +66.1 |
| b | 99.2% (117/118) | 41.0% (68/166) | +58.2 |
| c | 100.0% (133/133) | 0.0% (0/18) | +100.0 |
| d | 100.0% (59/59) | 44.4% (80/180) | +55.6 |
| e | 100.0% (59/59) | 44.2% (80/181) | +55.8 |
| f | 98.1% (155/158) | 41.7% (118/283) | +56.4 |
| g | 98.6% (139/141) | 29.4% (58/197) | +69.1 |
| h | 68.8% (110/160) | 40.6% (230/566) | +28.1 |
| i | – | 46.5% (168/361) | – |
| j | 98.5% (129/131) | 44.3% (160/361) | +54.2 |
| k | 100.0% (133/133) | 45.2% (163/361) | +54.8 |
| **TOT** | **95.2%** | **41.5%** | **+53.6** |

> **Finding RQ1:** l'LLM genera mutanti validi al **95.2%** contro il **41.5%** dello Static. La tecnica LLM, comprendendo Angular, evita di rompere binding/direttive/`@for`, mentre lo Static (meccanico) produce in maggioranza mutanti non compilabili.

### Cause di non-validità (not-compiled) per operatore

| Op | LLM non-validi/tot | Static non-validi/tot |
|--|--|--|
| a | 1/133 | 111/166 |
| b | 1/118 | 98/166 |
| c | 0/133 | 18/18 |
| d | 0/59 | 100/180 |
| e | 0/59 | 101/181 |
| f | 3/158 | 165/283 |
| g | 2/141 | 139/197 |
| h | 50/160 | 336/566 |
| i | 0/0 | 193/361 |
| j | 2/131 | 201/361 |
| k | 0/133 | 198/361 |

## RQ2 — Utilità differenziale delle strategie di locatori

### LLM — riassuntiva per strategia
| Strategia | Success | Fragility | Obsolescence | NotCompiled | Success% |
|--|--|--|--|--|--|
| Absolute | 1244 | 188 | 25 | 86 | 85.4% |
| Relative | 1400 | 32 | 25 | 86 | 96.1% |
| Robula | 1388 | 44 | 25 | 86 | 95.3% |
| Robula+ | 1410 | 22 | 25 | 86 | 96.8% |
| Selenium | 1397 | 35 | 25 | 86 | 95.9% |
| Katalon | 1356 | 76 | 25 | 86 | 93.1% |

### Static — riassuntiva per strategia
| Strategia | Success | Fragility | Obsolescence | NotCompiled | Success% |
|--|--|--|--|--|--|
| Absolute | 701 | 175 | 367 | 2402 | 56.4% |
| Relative | 850 | 26 | 367 | 2402 | 68.4% |
| Robula | 837 | 39 | 367 | 2402 | 67.3% |
| Robula+ | 849 | 27 | 367 | 2402 | 68.3% |
| Selenium | 840 | 36 | 367 | 2402 | 67.6% |
| Katalon | 836 | 40 | 367 | 2402 | 67.3% |

### Confronto robustezza per strategia (Success% sui testabili)

| Strategia | LLM Success% | Static Success% |
|--|--|--|
| Absolute | 85.4% | 56.4% |
| Relative | 96.1% | 68.4% |
| Robula | 95.3% | 67.3% |
| Robula+ | 96.8% | 68.3% |
| Selenium | 95.9% | 67.6% |
| Katalon | 93.1% | 67.3% |

> **Finding RQ2:** in entrambe le tecniche **Absolute è la strategia più fragile** e le strategie basate su attributi/classi/testo (Relative, Robula, Robula+, Selenium) sono le più robuste — conferma dell'utilità differenziale. La firma di fragilità è coerente tra LLM e Static.

### Dettaglio LLM (passed/validi) — strategia × operatore
| Strategia | a | b | c | d | e | f | g | h | j | k |
|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 166/166 | 149/151 | 167/168 | 69/72 | 69/72 | 157/195 | 125/173 | 92/130 | 119/162 | 131/168 |
| Relative | 162/166 | 145/151 | 162/168 | 68/72 | 68/72 | 194/195 | 172/173 | 113/130 | 148/162 | 168/168 |
| Robula | 160/166 | 143/151 | 160/168 | 69/72 | 69/72 | 194/195 | 169/173 | 113/130 | 147/162 | 164/168 |
| Robula+ | 164/166 | 147/151 | 164/168 | 69/72 | 69/72 | 194/195 | 169/173 | 113/130 | 157/162 | 164/168 |
| Selenium | 166/166 | 143/151 | 160/168 | 69/72 | 69/72 | 189/195 | 166/173 | 111/130 | 161/162 | 163/168 |
| Katalon | 165/166 | 149/151 | 166/168 | 61/72 | 61/72 | 177/195 | 157/173 | 101/130 | 151/162 | 168/168 |

### Dettaglio Static (passed/validi) — strategia × operatore
| Strategia | a | b | c | d | e | f | g | h | i | j | k |
|--|--|--|--|--|--|--|--|--|--|--|--|
| Absolute | 44/59 | 54/70 | – | 57/82 | 61/82 | 67/125 | 14/68 | 103/239 | 89/181 | 84/166 | 128/171 |
| Relative | 42/59 | 53/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 122/239 | 136/181 | 119/166 | 141/171 |
| Robula | 38/59 | 50/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 121/239 | 134/181 | 116/166 | 141/171 |
| Robula+ | 38/59 | 51/70 | – | 58/82 | 61/82 | 102/125 | 16/68 | 121/239 | 134/181 | 127/166 | 141/171 |
| Selenium | 46/59 | 56/70 | – | 58/82 | 61/82 | 85/125 | 16/68 | 120/239 | 130/181 | 129/166 | 139/171 |
| Katalon | 44/59 | 55/70 | – | 54/82 | 57/82 | 97/125 | 15/68 | 117/239 | 133/181 | 123/166 | 141/171 |
