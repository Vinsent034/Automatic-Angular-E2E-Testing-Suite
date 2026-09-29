# Blocco A — riesecuzione della Procedura B a 7 strategie

Classificazione identica a quella dello strumento: un mutante è *obsoleto* se nessuna strategia lo supera, *robusto* se le superano tutte, *fragile* altrimenti. La fragilità di una strategia si conta solo sui mutanti fragili.


## CookBook linguistica

File letti: batches-s1s2.csv, batches-s3.csv, batches-s4.csv, batches-s5.csv, batches-s6.csv

**Complessivo** — 1929 mutanti (robusti 1431, fragili 359, obsoleti 23, non compilati 116)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 2354 | 1905 | 283 | 23 | 143 | 12.8 |
| Relative | 2354 | 2150 | 38 | 23 | 143 | 1.7 |
| Robula | 2354 | 2123 | 65 | 23 | 143 | 2.9 |
| Robula+ | 2354 | 2155 | 33 | 23 | 143 | 1.5 |
| Selenium | 2354 | 2124 | 64 | 23 | 143 | 2.9 |
| Katalon | 2354 | 2092 | 96 | 23 | 143 | 4.3 |
| Hook-Based | 2354 | 2176 | 12 | 23 | 143 | 0.5 |

### Per ruolo del nodo mutato

Il ruolo è la posizione del nodo mutato rispetto al bersaglio del test. Sul bersaglio l'attributo di riferimento è preservato per costruzione: uno zero di fragilità in quella riga è una proprietà della progettazione dell'esperimento, non una misura di robustezza. Sui ruoli di contesto la misura è invece informativa.

**α bersaglio** — 492 mutanti (robusti 327, fragili 111, obsoleti 16, non compilati 38)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 602 | 481 | 58 | 16 | 47 | 10.5 |
| Relative | 602 | 504 | 35 | 16 | 47 | 6.3 |
| Robula | 602 | 491 | 48 | 16 | 47 | 8.6 |
| Robula+ | 602 | 514 | 25 | 16 | 47 | 4.5 |
| Selenium | 602 | 499 | 40 | 16 | 47 | 7.2 |
| Katalon | 602 | 501 | 38 | 16 | 47 | 6.8 |
| Hook-Based | 602 | 530 | 9 | 16 | 47 | 1.6 |

**β padre** — 430 mutanti (robusti 296, fragili 100, obsoleti 2, non compilati 32)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 525 | 397 | 87 | 2 | 39 | 17.9 |
| Relative | 525 | 483 | 1 | 2 | 39 | 0.2 |
| Robula | 525 | 474 | 10 | 2 | 39 | 2.1 |
| Robula+ | 525 | 478 | 6 | 2 | 39 | 1.2 |
| Selenium | 525 | 471 | 13 | 2 | 39 | 2.7 |
| Katalon | 525 | 467 | 17 | 2 | 39 | 3.5 |
| Hook-Based | 525 | 483 | 1 | 2 | 39 | 0.2 |

**γ antenato** — 389 mutanti (robusti 252, fragili 108, obsoleti 5, non compilati 24)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 485 | 337 | 111 | 5 | 32 | 24.5 |
| Relative | 485 | 446 | 2 | 5 | 32 | 0.4 |
| Robula | 485 | 441 | 7 | 5 | 32 | 1.5 |
| Robula+ | 485 | 446 | 2 | 5 | 32 | 0.4 |
| Selenium | 485 | 437 | 11 | 5 | 32 | 2.4 |
| Katalon | 485 | 428 | 20 | 5 | 32 | 4.4 |
| Hook-Based | 485 | 446 | 2 | 5 | 32 | 0.4 |

**δ fratello** — 560 mutanti (robusti 520, fragili 25, obsoleti 0, non compilati 15)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 684 | 654 | 12 | 0 | 18 | 1.8 |
| Relative | 684 | 666 | 0 | 0 | 18 | 0.0 |
| Robula | 684 | 666 | 0 | 0 | 18 | 0.0 |
| Robula+ | 684 | 666 | 0 | 0 | 18 | 0.0 |
| Selenium | 684 | 666 | 0 | 0 | 18 | 0.0 |
| Katalon | 684 | 645 | 21 | 0 | 18 | 3.2 |
| Hook-Based | 684 | 666 | 0 | 0 | 18 | 0.0 |

**ε componente** — 58 mutanti (robusti 36, fragili 15, obsoleti 0, non compilati 7)

| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |
|---|---:|---:|---:|---:|---:|---:|
| Absolute | 58 | 36 | 15 | 0 | 7 | 29.4 |
| Relative | 58 | 51 | 0 | 0 | 7 | 0.0 |
| Robula | 58 | 51 | 0 | 0 | 7 | 0.0 |
| Robula+ | 58 | 51 | 0 | 0 | 7 | 0.0 |
| Selenium | 58 | 51 | 0 | 0 | 7 | 0.0 |
| Katalon | 58 | 51 | 0 | 0 | 7 | 0.0 |
| Hook-Based | 58 | 51 | 0 | 0 | 7 | 0.0 |

### Hook-Based per operatore

| Operatore | Mutanti | Successi | Fragilità | Obsolescenza | Non compilati |
|---|---:|---:|---:|---:|---:|
| a | 215 | 263 | 0 | 0 | 0 |
| b | 203 | 248 | 0 | 0 | 0 |
| c | 215 | 262 | 0 | 0 | 1 |
| d | 91 | 101 | 2 | 7 | 0 |
| e | 92 | 100 | 3 | 7 | 1 |
| f | 169 | 206 | 0 | 0 | 0 |
| g | 180 | 211 | 1 | 0 | 8 |
| h | 202 | 110 | 4 | 9 | 124 |
| i | 144 | 174 | 0 | 0 | 2 |
| j | 210 | 248 | 2 | 0 | 6 |
| k | 208 | 253 | 0 | 0 | 1 |

---

## Note di lettura (22/09/2026)

**Confronto con le altre due applicazioni (campagne linguistiche, fragilità %):**

| Strategia | CineLib (807) | FlowBoard (1225) | CookBook (1929) |
|---|---:|---:|---:|
| Absolute | 16,1 | 13,1 | 12,8 |
| Relative | 5,6 | 2,3 | 1,7 |
| Robula | 8,3 | 3,1 | 2,9 |
| Robula+ | 3,1 | 1,7 | 1,5 |
| Selenium | 7,6 | 2,5 | 2,9 |
| Katalon | 5,7 | 5,2 | 4,3 |
| Hook-Based | 0,1 | 0,1 | 0,5 |
| Non compilati (mutanti) | 93 (11,5 %) | 61 (5,0 %) | 116 (6,0 %) |
| Obsoleti (mutanti) | 23 | 23 | 23 |

Stesso ordine delle strategie nelle tre app: Absolute la più fragile, Hook-Based la più robusta,
Robula+ la migliore fra quelle senza attributi dedicati. La gerarchia si conferma sulla terza app.

**Per ruolo (novità di CookBook: tutti i ruoli coperti dal modello).** La fragilità di Absolute
cresce allontanandosi dal bersaglio verso l'alto: α 10,5 %, β 17,9 %, γ 24,5 %, **ε 29,4 %**
(mutare il componente contenitore sposta tutto il percorso assoluto), mentre δ fratello vale solo
1,8 %. Le strategie relative sono quasi immuni ai ruoli di contesto (β, γ, δ, ε ≤ 4,4 %) e cedono
soprattutto sul bersaglio stesso (α). Su ε tutte le strategie tranne Absolute sono a 0.

**I 12 casi di fragilità di Hook-Based, esaminati uno per uno — nessuno è una vera fragilità del
locatore:**
- 7 nel gruppo s1s2 (14 test per mutante: scenari S1 e S2 insieme): il mutante cambia il testo o la
  struttura di un elemento verificato da uno solo dei due scenari (d, e, g sul titolo della scheda,
  d, e sul conteggio, j sul filtro, h su un antenato). In quello scenario falliscono **tutte** le
  strategie (asserzione sul testo, o elemento spostato), nell'altro passano tutte: per lo strumento il
  mutante è «fragile» perché conta i due scenari insieme, ma per scenario è obsolescenza. Effetto
  della classificazione dello strumento sui gruppi da 14, uguale per tutte le app con gruppi doppi.
- 3 mutanti h sul pulsante «Clear bought items» (α, β, γ): h sposta il pulsante in un altro template,
  quindi nella pagina della lista non esiste più. Falliscono giustamente tutte le strategie tranne
  **Katalon, che «passa» per un falso positivo**: `//button[@type='button']` trova un altro pulsante.
- 2 casi di **rumore di temporizzazione dello strumento**: (1) `e` sulla versione del piè di pagina
  (testo tolto): tutte falliscono tranne Absolute, primo test eseguito dopo la ricompilazione, che ha
  visto ancora la pagina vecchia; (2) `j` sul titolo delle statistiche (h1→h2, elemento non usato dal
  test): Hook, secondo test, va in timeout durante il ricaricamento, gli altri passano.

**Da tenere presente nella tesi:** la classificazione «fragile» dello strumento sui gruppi a 14 test
mescola due scenari; il falso positivo di Katalon con locatori generici; il rumore di
temporizzazione del primo/secondo test dopo la ricompilazione (2 casi su 1929 qui).
