# I test: quali sono, dove stanno, a cosa servono

Nel progetto ci sono **due tipi di test**, con due compiti diversi:

| Tipo | A cosa serve | Dove |
|---|---|---|
| **Test delle applicazioni** (Playwright, TypeScript) | descrivono come si usa l'applicazione; da questi si sono **scelti i bersagli** da mutare | `applicazioni-soggetto/<app>/e2e/*.spec.ts` |
| **Test di robustezza** (Java, JUnit + Selenium) | sono quelli che girano sui **mutanti** con il comando `esegui`, una volta per ciascuna delle **sette strategie di localizzazione** | sorgenti in `ext-test-classes/src/main/java/org/ext/<app>/`, compilati nelle cartelle `td-*` delle campagne |

Gli scenari sono gli stessi nei due tipi; cambiano il programma e lo scopo.

---

## 1. Test delle applicazioni (Playwright)

| Applicazione | File | Casi di test |
|---|---|---|
| CineLib | `applicazioni-soggetto/cinelib/e2e/`: `catalog`, `detail`, `favorites`, `form`, `reviews`, `stats.spec.ts` | 29 (7 + 6 + 4 + 4 + 4 + 4) |
| FlowBoard | `applicazioni-soggetto/flowboard/e2e/board.spec.ts` (scenari S1–S6, descritti in `SCENARIOS-flowboard.md`) | 24 (4 per scenario) |
| CookBook | `applicazioni-soggetto/cookbook/e2e/cookbook.spec.ts` (scenari S1–S6, descritti in `SCENARIOS-cookbook.md`) | 23 |

Ogni caso di test agisce o controlla elementi marcati con un attributo `x-test-…` (per esempio `x-test-search`).
**Da qui nascono i bersagli** dei file `role-targets-<app>.json`: ogni bersaglio ha, nel campo `tests`, il caso di test a cui
appartiene. Ai bersagli tratti dai test se ne sono aggiunti altri vicini, dello stesso componente o scenario (nel campo
`tests` sono indicati come «extended target» o «nessun caso Playwright diretto»):

| Applicazione | Bersagli | legati a un caso di test | aggiunti come contesto |
|---|---|---|---|
| CineLib | 45 | 24 | 21 |
| FlowBoard | 152 | 76 | 76 |
| CookBook | 52 | 39 | 13 |

Questi test **non fanno parte della procedura** di replica: non servono per generare né per eseguire i mutanti.

---

## 2. Test di robustezza (Java + Selenium): quelli che girano sui mutanti

### Come sono fatti

Per ogni **scenario** c'è una classe per ciascuna delle **sette strategie**. Le sette classi fanno **le stesse azioni e gli stessi
controlli**; cambia solo il modo di trovare gli elementi nella pagina (il **locatore**):

| Classe | Strategia | Esempio di locatore (campo di ricerca di CineLib) |
|---|---|---|
| `AbsoluteXPathTest` | **Absolute**: percorso completo dalla radice della pagina | `/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[1]/div[1]/input[1]` |
| `RelativeXPathTest` | **Relative**: XPath relativo, basato su attributi | `//input[@placeholder='Search a movie…']` |
| `RobulaXPathTest` | **ROBULA**: XPath calcolato dall'algoritmo ROBULA (modulo `custom-locators`) | `//input` |
| `RobulaPlusXPathTest` | **ROBULA+**: XPath calcolato dall'algoritmo ROBULA+ (modulo `custom-locators`) | `//input` |
| `SeleniumXPathTest` | **Selenium**: locatore nella forma prodotta dallo strumento Selenium | `css=.search-input` |
| `KatalonXPathTest` | **Katalon**: locatore nella forma prodotta dallo strumento Katalon | `xpath=//input[@type='text']` |
| `HookXPathTest` | **Hook-Based**: usa l'attributo di test | `//*[@x-test-search-input]` |

I locatori di ogni elemento, per le sette strategie, stanno in `locatori/locatori.json` (`cinelib-v2/`, `flowboard-v2/`,
`cookbook-run/`). Le classi Java sono **generate** da questi file con `genera_classi_<app>.py` (stessa cartella): non si scrivono a mano.
Non servono estensioni del browser: i locatori Selenium e Katalon sono già scritti nelle classi.

Ogni applicazione ha anche una classe di base (`CineLibBaseTest`, `FlowBoardBaseTest`, `CookBookBaseTest`) che apre Chrome
sull'indirizzo dell'applicazione; Chrome è preparato da `WebDriverFactory` (in `ext-test-classes/src/main/java/org/ext/`),
con un profilo temporaneo.

### Gli scenari

**CineLib** (5 scenari, 35 classi)

| Scenario | Cosa fa il test |
|---|---|
| `catalogsearch` | apre il catalogo, cerca «quiet», controlla che resti una scheda e che il titolo sia «Quiet Harbor» |
| `moviedetail` | apre il film 3, controlla il titolo «Quiet Harbor», la seconda riga del cast e il nome «Sam Whitfield» |
| `movieform` | apre il modulo di nuovo film, controlla i campi titolo e genere e il bottone «Create movie» |
| `reviews` | apre il film 3, controlla la media delle recensioni, il primo autore «Dario» e il bottone «Post review» |
| `stats` | apre le statistiche, controlla il totale «8», il primo titolo «Quiet Harbor» e il primo conteggio per genere «2» |

**FlowBoard** (6 scenari, 42 classi)

| Scenario | Cosa fa il test |
|---|---|
| S1 | cerca «login» nella bacheca, controlla la scheda 3 con titolo «Design login» |
| S2 | controlla il conteggio «4» della colonna Backlog, il comando di spostamento della scheda 1 e la colonna In Progress |
| S3 | apre il modulo di nuova scheda, controlla titolo, priorità e il bottone «Save card» |
| S4 | apre la scheda 3, controlla titolo «Design login», assegnatario «Marco» e il bottone di cancellazione |
| S5 | apre le statistiche, controlla il totale «10», il Backlog «4» e la riga della scheda 3 |
| S6 | controlla il menu «Stats», il contatore «10» nell'intestazione e la versione «v1.0» nel piè di pagina |

**CookBook** (6 scenari, 42 classi)

| Scenario | Cosa fa il test |
|---|---|
| S1 | cerca «risotto», controlla la ricetta 2 con titolo «Mushroom Risotto» |
| S2 | attiva «solo vegetariane», filtra i dolci, controlla che resti «1» risultato |
| S3 | apre la ricetta 1 («Tomato Pasta»), porta le porzioni a 4, controlla la quantità «400» del primo ingrediente |
| S4 | apre il modulo di nuova ricetta, controlla titolo, portata e il bottone «Create recipe» |
| S5 | apre la lista della spesa, controlla «2» da comprare, spunta una riga, controlla «1» e il bottone di pulizia |
| S6 | apre le statistiche, controlla il totale «8», i primi piatti «3» e la versione «v1.0» |

### I gruppi: quale test prova quale mutante

Ogni mutante modifica un template; lo provano solo gli scenari che usano quel template. I mutanti sono quindi divisi in **gruppi**
in base al template modificato (tabella `comp2group` in `esegui_mutanti.py`):

| Applicazione | Gruppo (cartella dei test compilati) | Template che ci finiscono |
|---|---|---|
| CineLib | `td-catalogsearch` | catalog, movie-card |
| | `td-moviedetail` | movie-detail, cast-row |
| | `td-movieform` | movie-form |
| | `td-reviews` | reviews |
| | `td-stats` | stats |
| FlowBoard | `td-s1s2` (S1 e S2 insieme) | board, card, column |
| | `td-s3` | card-form |
| | `td-s4` | card-detail, toast |
| | `td-s5` | stats |
| | `td-s6` | header, app |
| CookBook | `td-s1s2` (S1 e S2 insieme) | recipe-list, recipe-card |
| | `td-s3` | recipe-detail, ingredient-row, step-item, panel |
| | `td-s4` | recipe-form |
| | `td-s5` | shopping-list |
| | `td-s6` | stats, app, header, toast |

Le cartelle `td-*` stanno in `cinelib-v2-run/`, `flowboard-v2-run/` e `cookbook-run/` e contengono le classi **già compilate**
(file `.class`). Nei gruppi `s1s2` ogni mutante è provato da 14 classi (7 per S1 e 7 per S2); negli altri da 7.

---

## 3. Che cosa succede a un mutante durante `esegui`

1. Il template mutato viene scritto nell'applicazione (l'originale è salvato prima e rimesso alla fine).
2. Si aspetta che il server di sviluppo **ricompili** l'applicazione. Se non ricompila, il mutante è **non compilato**: i test
   sono saltati (`Skipping test … Application did not recompile`) e Chrome non si apre.
3. Se ricompila, si eseguono le classi del gruppo: Chrome si apre e ogni classe dà `PASSED` o `FAILED`.
4. Gli esiti si leggono così:

| Esito | Significato |
|---|---|
| **Successo** | il test passa con quella strategia: il locatore ha resistito alla modifica |
| **Fragilità** | il test fallisce con quella strategia mentre con altre passa: la colpa è del locatore |
| **Prova sprecata** | falliscono tutte e sette: la modifica ha rotto la funzione, e non dice niente sui locatori |
| **Non compilato** | (`NOT_APPLICABLE`) il mutante non è valutabile |

**Quanti mutanti prova `esegui`.** Di base `esegui` sceglie il campione dentro **un solo gruppo**, quello con più mutanti (un solo
avvio del server: è il modo più veloce). Per sceglierlo fra **tutti** i gruppi si aggiunge la parola `tutti` dopo la tecnica:

```powershell
.\mutanti.ps1 <nome-applicazione> esegui 20 llm tutti
.\mutanti.ps1 <nome-applicazione> esegui 20 statico tutti
```

Per provare **tutti** i mutanti di una generazione: un numero grande più `tutti`, per esempio `esegui 99999 llm tutti`
(richiede ore: circa 13–20 secondi a mutante, misurati nelle campagne della tesi).

---

## 4. Rigenerare le classi di test (solo se si cambiano i locatori)

Non serve per la replica: le classi compilate sono già nelle cartelle `td-*`. Se si cambia un locatore in `locatori.json`:

1. `python cinelib-v2/genera_classi_cinelib.py` (oppure `flowboard-v2/genera_classi_flowboard.py`, `cookbook-run/genera_classi_cookbook.py`):
   riscrive le classi Java in `ext-test-classes/src/main/java/org/ext/<app>/`;
2. si ricompila (`mvn package -DskipTests` dalla cartella principale, nel terminale con gli strumenti portatili);
3. si aggiornano le cartelle `td-*`: ognuna contiene le classi compilate del proprio scenario, più la classe di base e
   `WebDriverFactory`, prese da `ext-test-classes/target/classes/org/ext/`.
