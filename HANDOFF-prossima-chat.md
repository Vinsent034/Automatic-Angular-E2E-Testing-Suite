> ## ⚠️ AGGIORNAMENTO 16/09/2026 — LEGGERE PRIMA DI TUTTO
>
> La tesi è stata revisionata dal professore (colloquio del 15/09/2026) e il lavoro in corso è la
> revisione, non più la sperimentazione originale. Il piano aggiornato, le decisioni prese, l'elenco
> completo delle modifiche richieste e le trascrizioni del colloquio sono qui:
>
> **`Desktop/Università/App tirocinio/tesi/revisione-2026-09/PIANO-REVISIONE.md`**
>
> I percorsi scritti nel resto di questo file sono **obsoleti**: tutto si è spostato sotto
> `Desktop/Università/` (tesi in `App tirocinio/tesi/`, progetto in `Tirocinio/progetto/`).
> Il contenuto tecnico resta valido.

# HANDOFF — stato del lavoro e cosa fare nella prossima sessione

> Leggi tutto prima di toccare qualcosa. Questo file è la memoria tra una chat e l'altra.
> Tesi/tirocinio sulla **fragilità dei test E2E** (framework di Liberti = `Automatic-Angular-E2E-Testing-Suite`).
> App sotto test: **angular-spotify** in `C:\Users\vince\OneDrive\Desktop\Tirocinio\angular-spotify` (gira in locale, usa l'API Spotify reale).
> Suite: `C:\Users\vince\OneDrive\Desktop\Tirocinio\progetto\Automatic-Angular-E2E-Testing-Suite`.

---

## ★ CINELIB — STATO A FINE SESSIONE 2026-07-06 (leggere questa sezione per prima)

CineLib è la **seconda app** creata per il punto "generalità" del prof (§4.6/§6 più sotto). A differenza di
Spotify: dati 100% locali, niente OAuth, niente rate-limit. Percorso: `C:\Users\vince\OneDrive\Desktop\App
tirocinio\cinelib`, gira su **http://localhost:4300** (mai 4200, in conflitto con Spotify).

### Cosa è stato creato — e cosa manca

| Pezzo | Stato | Dettaglio |
|---|---|---|
| **App CineLib** | ✅ completa | Angular 19, 7 componenti (catalog, movie-card, movie-detail, cast-row, reviews, stats, movie-form), attributi `x-test-*` come ground-truth. |
| **Mutazioni** | ✅ 807 generate | 703 per-file (11 operatori × 5 ruoli sui 45 target `x-test-*`) + 104 cross-template (`h`). Coprono **tutti e 7** i componenti. Dettaglio per componente: catalog 157, movie-form 132, movie-detail 124, reviews 118, stats 116, movie-card 111, cast-row 49. |
| **Test + locatori** | ⚠️ **SOLO 1 scenario su ~6 aree** | Vedi sotto. |

**Scenario completo (i 6 locatori ci sono tutti):** "Catalog → search filters by title" (`catalog.component.html`
+ `movie-card.component.html`, elementi search-input/card/card-title). File Java in
`ext-test-classes/src/main/java/org/ext/cinelib/catalogsearch/` (+ `CineLibBaseTest.java`), compilati e
funzionanti, **testati con successo su 306 mutanti reali** (vedi risultati sotto).

**Scenari/componenti SENZA locatori né test ancora (da autorare se si vuole coprirli):**
- `movie-detail.component.html` (124 mutazioni pronte, zero test)
- `movie-form.component.html` (132 mutazioni pronte, zero test)
- `reviews.component.html` (118 mutazioni pronte, zero test)
- `stats.component.html` (116 mutazioni pronte, zero test)
- `cast-row.component.html` (49 mutazioni pronte, zero test — parzialmente toccato via mutanti `h` che lo
  usano come sorgente/destinazione insieme a movie-card/movie-detail, ma nessuno scenario dedicato)

Per ciascuno di questi, il procedimento è quello già rodato in questa sessione: scegliere 2-3 elementi
target dal componente, autorare i 6 locatori (Absolute lo calcolo io via JS nel browser; Robula/Robula+ col
modulo Java; Relative/Katalon/Selenium richiedono l'utente con le estensioni — vedi §5.5 sotto per la
procedura passo-passo e gli intoppi già risolti), scrivere il file Java del test, aggiungerlo alla cartella
isolata `ext-test-classes/target/cinelib-only/` (vedi sotto), rilanciare il tester sul sottoinsieme di
mutazioni di quel componente (impostare PENDING solo quelle, HELD il resto — vedi script in §5.5).

### Primi risultati (306 mutanti: catalog + movie-card, TUTTI gli operatori incluso `h`)

| Strategia | Success | Fragilità | Obsolescenza | Non compilato |
|---|---|---|---|---|
| Robula+ | 230 (75%) | **6 (2.0%)** | 8 (2.6%) | 62 (20.3%) |
| Selenium | 228 (75%) | 8 (2.6%) | 8 | 62 |
| Katalon | 226 (74%) | 10 (3.3%) | 8 | 62 |
| Robula | 226 (74%) | 10 (3.3%) | 8 | 62 |
| Relative | 224 (73%) | 12 (3.9%) | 8 | 62 |
| **Absolute** | 194 (63%) | **42 (13.7%)** | 8 | 62 |

**Finding principale:** Absolute XPath è nettamente il più fragile (13,7%, ~7× più di Robula+), le altre 5
strategie (basate su attributi/classi/testo) restano tutte robuste in un intervallo 2-4%. Coerente con la
teoria (i locatori posizionali si rompono sotto mutazione strutturale). Dati grezzi in
`output/tests/batches.csv` / `stats.csv` (sovrascritti a ogni run — salvare/rinominare prima di un nuovo run
se si vogliono conservare risultati precedenti, vedi `cinelib-catalogsearch-block1.csv` come esempio).

**Sul 20,3% di non-compilato:** coincide quasi esattamente con i 66 mutanti `h` di questo sottoinsieme
(66/306 = 21,6%), che sono strutturalmente destinati a non compilare al 100% (spostano un elemento in un
componente con un contesto dati diverso). Il tasso "vero" (esclusi gli `h`) è vicinissimo a 0.

### I DUE OBIETTIVI APERTI per le prossime sessioni

1. **Prompt universale, non specifico per CineLib.** Finora la generazione delle 807 mutazioni è stata fatta
   "a mano" da Claude, guardando ogni volta la struttura ESATTA dei template di CineLib (elemento per
   elemento). Per essere un contributo di tesi valido, serve un **prompt/procedimento generale**, che un
   LLM possa applicare a **qualsiasi app Angular** (non solo CineLib) e produca comunque mutazioni valide e
   utili — non un procedimento "cucito addosso" a questa app specifica. Questo è l'obiettivo di fondo di
   tutto l'approccio "LLM vs static": va dimostrato che generalizza, non solo che funziona qui.
2. **Tenere `not_compiled < 20%`** (soglia già fissata, vedi memoria `not-compiled-soglia-20`). Il fix del
   bug jsoup/`&gt;` (vedi §5.5 sotto) ha già abbattuto il tasso dal 67% al 20,3% — siamo giusto al limite.
   Da vedere quando si testeranno gli altri componenti (stats/movie-form potrebbero avere altri `@if`/`@for`
   con `>`/`<` non ancora scoperti) se serve un intervento ulteriore sul prompt/generazione (non solo il
   fix tecnico di serializzazione già fatto, ma eventualmente sul TIPO di mutazioni generate, es. evitare
   di rinominare attributi che sono in realtà binding Angular — vedi analisi per-operatore in §5.5).

---

## 0. L'esperimento in due righe
Si confronta la **robustezza di 6 strategie di locatori XPath** (Absolute, Relative, Robula, Robula+, Selenium, Katalon — Hook escluso, toolchain VC++ non disponibile) sotto **mutazione** dell'HTML. Per ogni mutante × strategia si misura: `success / fragility / obsolescence / not-compiled`.
Obiettivo della tesi: dimostrare che una tecnica di generazione mutanti **basata su LLM** è **generale e più efficace** di quella **static** (analisi statica, operatori meccanici) della tesi precedente.

Tre modi di generare i mutanti, ora tutti implementati e testati:
1. **STATIC** (tesi precedente) — operatori meccanici a/k.
2. **LLM PARAMETRICO** — l'LLM genera secondo gli stessi 11 tipi dello static (gli si dice il TIPO, non dove).
3. **LLM REALISTICO/INDIPENDENTE** — l'LLM decide tutto (cosa e dove), modifiche "da programmatore vero".

---

## 1. STATO ATTUALE DEI DATI (dove siamo)

### Database `mutations.db` (root della suite)
Contiene DUE set LLM (lo static NON è qui — è nei CSV, vedi sotto), tutti con status `HELD`:
- **200 mutanti `mutation_type='LLM_GENERATED'`** = set **realistico/indipendente** (search 100 + card 100).
- **211 mutanti `mutation_type='LLM_PARAMETRIC'`** = set **parametrico** (search 110 = 11 tipi×10; card ~101).

**Backup db:** `mutations.db.static-20260613` (static), `mutations.db.llm200` (set indip. integro pre-test), `mutations.db.guided-rejected-20260615` (vecchio metodo script, NON usare).

### Cosa è stato TESTATO col mutation-tester (robustezza)
| Set | Generati | Robustezza testata | Output CSV |
|---|---|---|---|
| Static | 266 (Katalon 198) | ✅ completo | `output/tests/srch-0{1,2,3,4}-full/` |
| LLM realistico | 200 | ✅ completo (200) | `output/tests/llm-search-full/` + `llm-card-full/` |
| LLM parametrico | 211 | ⚠️ **161/200** testati | `output/tests/llm-param-robustness/block1-8.csv` |

- Parametrico: mancano **39 mutanti** (card, di movimento) per la parità a 200. Sono bloccati dal **rate-limit Spotify** (vedi §4). Lista dei 59 scelti in `param_robustness_target59.txt`; i 39 rimanenti = `target59[20:59]`. `block9.INVALID-429.csv` = scarto, ignorare.
- ⚠️ I 39 mancanti sono mutanti **card di movimento** → producono solo success/obsolescenza/NC, **NON cambiano la classifica** (la classifica parametrica è già stabile a 161).

---

## 2. RISULTATI (le tre tabelle + il finding)

Valori = `effettuate / totali`. Forma richiesta dal prof.

### Static (266 mut; Katalon su 198)
| Locator | Success | Fragility | Obsol | NotComp |
|---|---|---|---|---|
| Relative | 117/266 | 23/266 | 68/266 | 58/266 |
| Absolute | 105/266 | 35/266 | 68/266 | 58/266 |
| Robula | 128/266 | 12/266 | 68/266 | 58/266 |
| Katalon | 95/198 | 2/198 | 58/198 | 43/198 |
| Selenium | 133/266 | 7/266 | 68/266 | 58/266 |
| Robula+ | 131/266 | 9/266 | 68/266 | 58/266 |

### LLM parametrico (161 testati)
| Locator | Success | Fragility | Obsol | NotComp |
|---|---|---|---|---|
| Relative | 104/161 | 10/161 | 13/161 | 34/161 |
| Absolute | 104/161 | 10/161 | 13/161 | 34/161 |
| Robula | 113/161 | 1/161 | 13/161 | 34/161 |
| Katalon | 113/161 | 1/161 | 13/161 | 34/161 |
| Selenium | 107/161 | 7/161 | 13/161 | 34/161 |
| Robula+ | 107/161 | 7/161 | 13/161 | 34/161 |

### LLM realistico (200)
| Locator | Success | Fragility | Obsol | NotComp |
|---|---|---|---|---|
| Relative | 178/200 | 4/200 | 3/200 | 15/200 |
| Absolute | 176/200 | 6/200 | 3/200 | 15/200 |
| Robula | 182/200 | 0/200 | 3/200 | 15/200 |
| Katalon | 181/200 | 1/200 | 3/200 | 15/200 |
| Selenium | 175/200 | 7/200 | 3/200 | 15/200 |
| Robula+ | 170/200 | 12/200 | 3/200 | 15/200 |

### IL FINDING (cuore della tesi)
- **Static** e **LLM-parametrico** hanno la **stessa firma**: i locatori **posizionali** (Absolute/Relative) i più fragili → l'LLM, *vincolato al modello statico*, **riproduce lo static**.
- **LLM-realistico** ribalta: i più fragili diventano **Robula+/Selenium** (basati su classi/attributi). **Robula** resta robusto in tutti.
- **1.2 validità** (parametrico, 141 mutanti): **coerenti 79%, compilanti 75%, validi-entrambi 56%**. L'LLM **compila più dello static** (75% vs ~65% FTR) e non è bloccato dall'assenza di target applicabile, MA introduce **allucinazioni** che lo static non ha (per costruzione). Limiti per tipo: `c` (rinomina attributo) compila all'11% — in Angular gli attributi sono binding/input/direttive; `a` al 50% (cambia valori di binding inesistenti). Dettaglio: `output/risultati_1_2_validita.md`.

---

## 3. IL llm-generator (implementato in questa sessione)

`mutation-generator/llm-generator/` — era uno stub, ora completo. **Build (JDK 25):**
```
JAVA_HOME="C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot"
mvn -q install -pl :llm-generator -am -DskipTests
```
→ `mutation-generator/llm-generator/target/llm-generator-1.0.0-jar-with-dependencies.jar` (mainClass `org.unina.App`). Lanciare dalla root della suite.

File chiave: `App.java`, `GroqClient.java`, `resources/realistic-prompt.txt` (indipendente), `resources/parametric-prompt.txt` (parametrico), `mutation-types.json` (root, tassonomia a–k).

### Tre modalità (via variabili d'ambiente)
- **Realistico/indipendente** (default Groq): l'LLM decide tutto. Scrive `LLM_GENERATED`, `element`=fileSlug.
- **Parametrico**: `LLM_PARAMETRIC=1` + `LLM_MUTATION_TYPES=mutation-types.json` + `LLM_VARIANTS_PER_TYPE=10`. Per ogni tipo a–k chiede all'LLM di applicare QUEL tipo (decide lui dove). Scrive `LLM_PARAMETRIC`, `mutation_name`=id tipo (a..k), `mutation_id`=`LLMP_<file>_<tipo>_<i>`.
- **Ingest** (source-agnostica): `LLM_INGEST_DIR=<dir>` → legge blocchi `######### START n - label ######### … END` da `<fileSlug>.txt` invece di chiamare Groq. (Per generazione da ChatGPT ecc.)

### Variabili utili
`GROQ_API_KEYS="k1,k2,..."` (pool con rotazione), `LLM_VARIANTS_PER_FILE` (default 70), `LLM_VARIANTS_PER_TYPE` (default 10), `LLM_VARIANTS_PER_CALL` (6), `LLM_MAX_TOKENS` (9000), `LLM_TEMPERATURE` (1.0), `LLM_PROMPT_FILE` (override del system prompt — usare se il jar non è ricompilato).

### Prompt — principio di INDIPENDENZA (vincolo del prof, FERMO)
Le mutazioni LLM devono essere **generate dall'LLM**, non da regole nostre:
- ❌ vietato uno script find/replace (`llm-variants/author_variants.py` = SCARTATO).
- Realistico: NON si dice dove né come.
- Parametrico: si dice solo il TIPO (per coprire il modello statico), l'LLM sceglie dove. OK col prof.
- Validazione a valle (in `App.java`): parse jsoup OK + diverso dall'originale + non duplicato + `x-test` preservati. La compilazione Angular vera la verifica il mutation-tester (NOT_APPLICABLE).

### Resume/top-up
Rilanciando, il generatore conta i mutanti già presenti (per file e, in parametrico, per tipo) e completa solo i mancanti, senza duplicare. Salta i body già visti.

---

## 4. CONOSCENZA OPERATIVA CRITICA (non riscoprirla)

- **mutation-tester richiede Java 25.** Eseguire SEMPRE via classpath + `vintage-fix`, MAI `-jar`:
  ```
  "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe" -cp "mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar;vintage-fix" org.unina.MutationTester -td "ext-test-classes/target/classes"
  ```
  (`vintage-fix/` re-registra junit-vintage; senza, i test JUnit4 girano a vuoto.)
- **APRIRE L'APP SU `http://127.0.0.1:4200/`, MAI `localhost:4200`.** L'app Spotify "Tirocinio E2E" (client_id `43b0afe37354467289987f7dfb945ace`, hardcoded in `angular-spotify/libs/web/auth/data-access/src/lib/models/spotify-authorize.ts`) ha registrato come redirect URI **solo** `http://127.0.0.1:4200/`. Con `localhost` il login OAuth fallisce subito con *"redirect_uri: Not matching configuration"* (Spotify ha deprecato `localhost` come redirect). Senza login niente token → tutte le pagine che chiamano l'API (browse, collection/tracks, risultati search) restano vuote/bloccate: **era questo (non un bug dell'app) il "blocco" degli scenari preferiti/esplora.** La suite usa già 127.0.0.1 (`ext-test-classes/.../BaseTest.java:25`), NON modificarla.
- **Dev server / 4200:** il mutation-tester avvia il suo `npm start` → la **4200 deve essere LIBERA** quando parte.
- **COLD-START → PRE-WARM (importante):** dopo una pausa lunga la cache nx è fredda e il tester va in `Timeout: did not recompile in time` (timeout di avvio 120s, rebuild 20s, hardcoded). **Soluzione:** prima lanciare `npm start` in angular-spotify, attendere `Compiled successfully`, **killarlo** (liberare 4200), POI lanciare il tester (cache calda → parte in tempo).
- **Cleanup tra run (PowerShell):** killare `node.exe` con CommandLine `angular-spotify|nx|run-executor`, `chrome.exe` con `selenium-spotify-profile`, `chromedriver.exe`; verificare 4200 libera.
- **Esecuzione a BLOCCHI (~20 mutanti):** set PENDING 20, resto HELD; salvare `output/tests/batches.csv` come `blockN.csv`; aggregare con Python (chiave unica mutazione = `(Name,Id,Tag)`).
- **Compile-only check veloce (per 1.2 compilazione):** invece del tester completo (Selenium lento), usare `compile_check.py` (pilota il dev server, scrive il mutante, legge `Compiled successfully`/errore, ripristina; niente Selenium/Spotify). Output `output/tests/param-compile.csv`.
- **Profilo Chrome persistente:** `--user-data-dir=C:\Users\vince\selenium-spotify-profile` (login Spotify fatto una volta). ⚠️ Per ri-loggare: **chiudere PRIMA tutte le finestre Chrome**, altrimenti il flag `--user-data-dir` viene ignorato e il login va nel profilo sbagliato.

### PROTOCOLLO 429 / rate-limit Spotify (BLOCCANTE — letto bene)
La search Spotify si rate-limita dopo molte ricerche (ogni test = 1 ricerca; un blocco da 20 mut × 6 strategie = 120 ricerche).
- **Sintomo:** fallimento di massa con "no such element" sui RISULTATI (`div[3]`/`common-grid`) → **obsolescenza alta uniforme su tutte le strategie**. È un 429, run **INVALIDO** (scartare, non aggregare).
- **Distinguere da obsolescenza genuina:** fare un **probe** con UN mutante non-distruttivo (es. `search` tipo `j`) — se PASSA, Spotify è vivo e l'obsolescenza è genuina; se fallisce, è 429.
- ⚠️ **Scoperta importante di questa sessione:** dopo uso intensivo il blocco è a **livello account** e **persistente**: né 2.5h né 4.7h di cooldown né il re-login l'hanno sbloccato. Probabilmente serve molto più tempo (un giorno). Pianificare i run con parsimonia. (Le NUOVE app non avranno questo limite: niente API Spotify.)

### Vincoli FERMI
- **NON toccare** `static-generator`, `mutation-tester`, `custom-locators`, `hook-injector`, e i **locatori** (mai a mano: variabili sperimentali). `common` modificabile con cautela (lo usano tutti).
- API key **da env**, mai hardcoded. Niente normalizzazione CRLF/.gitattributes.

---

## 4.5 ANGULAR-SPOTIFY: stato app + nuovi scenari (sessione 2026-06-21)

Sessione dedicata a far ripartire l'app e preparare nuovi scenari (Prof Q3, generalità).

### Mappa pagine → testabilità (endpoint testati uno per uno col token reale)
| Pagina / route | Endpoint | Stato | Buona per scenari |
|---|---|---|---|
| Search `/search` | `search` | ✅ vivo | sì (già scenario) |
| Liked `/collection/tracks` | `me/tracks` | ✅ 200 | ✅ stabile, popolata (18 brani) |
| My Albums lista `/albums` | `me/albums` | ✅ 200 | ✅ stabile (8 album) |
| My Playlists lista `/collection/playlists` | `me/playlists` | ✅ 200 | ✅ stabile (4 playlist) |
| **Album detail** `/albums/:id` | `albums/{id}` + `/tracks` | ✅ **entrambi 200** | ✅ **migliore scenario nuovo** (DOM ricco, tutto vivo) |
| Home `/` | `recently-played` | ⚠️ 200 ma **volatile** | usabile ma instabile (14 brani ora) |
| Playlist detail `/playlist/:id` | detail 200, **`/tracks` 403** | ⚠️ parziale | solo header |
| Artist `/artist/:id` | info 200, **top-tracks 403** | ⚠️ parziale | solo header |
| Browse `/browse` | `categories` | ❌ **403 morto** | no (irrecuperabile) |

### Endpoint MORTI (403, deprecati da Spotify per app in Development Mode — NON insistere)
`browse/featured-playlists`, `browse/new-releases`, `browse/categories`, `artists/{id}/top-tracks`,
`artists/{id}/related-artists`, `playlists/{id}/tracks`, `audio-features`, `audio-analysis`.
→ La home featured-playlists è già disabilitata di proposito in `home.component.ts` (commento nel codice).

### Token dell'app = READ-ONLY
Tutte le scritture API falliscono **403** (`PUT me/albums`, `PUT me/tracks`, ...). **NON si può seminare la
libreria via API.** Album/brani/playlist vanno aggiunti a mano dalla **Spotify reale** (app o open.spotify.com).

### Stato dati attuale (seminato a mano in questa sessione, sufficiente per gli scenari)
My Albums **8** · Liked **18** · My Playlists **4** · Recently played **14**. (My Albums ha un doppione "Evolve"
innocuo.) Per il mutation testing conta la STRUTTURA del DOM, non la quantità: questi numeri bastano.

### PROSSIMO STEP deciso
Costruire il nuovo scenario E2E su **My Albums / Album detail** (vivo, stabile, DOM ricco), come per search:
test case + le 6 strategie di locatori. Il punto "target sistematico" del prof (§5.1) è accantonato per ora.

---

## 4.6 NUOVE APPLICAZIONI per la generalità (Prof Q3) — CineLib (creata)

**CineLib = la PRIMA delle nuove applicazioni create** per rispondere al punto generalità del prof
(servono app "template based" generate ad hoc). **Da mostrare al prof** come esempio concreto.

- **Percorso:** `C:\Users\vince\OneDrive\Desktop\App tirocinio\cinelib` (cartella "App tirocinio" sul Desktop,
  pensata per contenere anche le prossime app). Avvio: `cd` lì → `npm start` → `http://localhost:4300`.
- **Tecnologia:** Angular 19 standalone, costrutti `@if`/`@for`/`[binding]`/`{{ }}` (mutabili e confrontabili
  con angular-spotify). Template in file `.html` separati. Attributi `x-test-…` come ground-truth.
- **Dominio:** catalogo di Film, **CRUD su una entità** (Film). Tema visivo "cinema" (scuro + accento oro), **solo CSS**.
- **Struttura "template su template":**
  `App → Header` · `App → Catalog → MovieCard` (griglia→card) · `App → MovieDetail → CastRow` (dettaglio→riga cast) ·
  `App → MovieForm` (form CRUD). Service `MovieService` con **dati seed locali in-memory**.
- **3 scenari E2E pronti da autorare:** (1) **Catalogo** (griglia di card + ricerca, come search-spotify),
  (2) **Dettaglio** (header + lista cast, come album-detail), (3) **Form** (input/select/textarea/button =
  **famiglia DOM nuova** non coperta da angular-spotify).
- **PRINCIPIO CHIAVE — zero API esterne:** dati 100% locali → niente OAuth, niente 429, niente endpoint
  deprecati, **dati deterministici** → locatori stabili (risolve tutti i problemi avuti con Spotify/Home).
- **Stato:** scaffold + componenti scritti, **compila e gira** (build OK), tema applicato. Pronta come 2ª app.
- **DA FARE (sessione dedicata):** autorare gli scenari E2E su CineLib (stessa toolchain: Absolute/Robula via
  modulo, Relative/Selenium/Katalon coi tool del browser) e far girare il mutation-tester → confronto di
  generalità (validi/non-compilanti) tra angular-spotify e CineLib. Poi eventualmente una 2ª/3ª app (CookBook/TaskFlow).

---

## 4.7 CineLib — generazione 11×5 LLM_ROLE (sessione 2026-06-28) — IN CORSO

Nuova modalità **`LLM_ROLE`** nel `llm-generator` (`App.java`): genera l'**11×5** (11 operatori a–k × 5 ruoli)
**ancorato ai target dei test**, FEDELE allo static (riusa `common/ElementExtension`, ruoli identici
α target / β parent / γ ancestor / δ sibling / ε containing-component — vedi `MutationEngine.initializeTargets`).

- **Input:** `role-targets.json` (root suite) = **24 target distinti** estratti dai 29 test e2e di CineLib
  (`App tirocinio/cinelib/e2e/*.spec.ts`). Tutti i target risolvono su un elemento `x-test`.
- **Applicabilità (dry-run):** tetto 24×55=1320; **applicabili reali = 688**.
  - **ε = 0/264**: in generazione per-file non esistono antenati `app-*` → ε non scatta mai (vale anche
    per lo static → confronto equo). Da riportare come risultato, non bug.
  - **operatore h = 0/120**: impossibile per-file (un template per volta). Da implementare cross-template
    (catalog↔movie-card, detail↔cast-row/reviews); `saveMutation` supporta mutanti multi-file.
- **STATO: 73/688 generati e salvati** (mutation_type=`LLM_ROLE`, id `LLMR_<file>_<target>_<ruolo>_<op>`).
  Run 1 (2026-06-28): 30. Run 2 (2026-06-29, dopo reset quota): +43 → 73. Per operatore:
  a9 b9 c9 d5 e5 f7 g7 i4 j9 k9 (h=0 sempre NA, ε=0 sempre NA).
- **BLOCCANTE CONFERMATO 2 volte: quota GIORNALIERA Groq (TPD) del free tier.** Ogni run le 3 chiavi vanno
  in "daily quota exhausted, no more keys left" dopo **~40-70 mutanti/giorno**. I ~615 "rejected" del report =
  `no-valid-output` per quota finita (NON scarti veri; quelli sono pochi: duplicati + `dropped-x-test`).
  Pipeline OK, è la QUOTA il muro. Di questo passo servirebbero ~10 giorni di batch.
- **DECISIONE utente (2026-06-29): fermarsi; l'utente cercherà una soluzione alternativa per i token/quota.**

### NUOVO APPROCCIO (2026-06-29 ter): generazione MANUALE via INGEST (deciso dall'utente)
Per evitare la quota Groq, le mutazioni le genera **Claude (o ChatGPT/Gemini) a mano** e si ingeriscono.
Implementato nel `llm-generator`:
- **`LLM_ROLE_DUMP=1`** → scrive un prompt per ogni (target,ruolo) in `output/mutations/role-prompts/`
  (`<target>_<rolecode>.txt`, rolecode = alf/bet/gam/del/eps): contiene l'ELEMENTO-ruolo esatto e gli operatori applicabili.
- Un LLM scrive l'**elemento mutato** di ogni operatore in `output/mutations/role-ingest/<target>_<rolecode>.txt`
  con blocchi `######### START n - <opid> #########` … `END`. Per **f/g (strutturali)** il blocco è il **template intero**
  (cambia la posizione); per gli altri operatori è **solo l'elemento** (reincollo via jsoup, splice per indice).
- **`LLM_ROLE_INGEST_DIR=output/mutations/role-ingest`** → valida (parse + diff + non-dup + x-test preservati),
  reincolla e salva. Resume per id: rilanciando salta i già fatti, niente duplicati.
- Guard aggiunto: operatore **i (unwrap)** su elementi con x-test = NA (toglierebbe la ground-truth) → applicabili scesi a **643**.

**AGGIORNAMENTO SCOPE (2026-06-29 quater):** l'utente ha chiesto di **alzare il tetto a 1000-1100** → aggiunti
**21 nuovi target** (elementi x-test extra) in `role-targets.json`. Ora **45 target, 1277 combinazioni applicabili**
(max teorico 2475). Guard aggiunto: op **i** su elementi con x-test = NA. Poi: "generarli tutti".

**OPERATORE h (between-templates) COPERTO (2026-06-29 quinquies):** implementata generazione CROSS-TEMPLATE
multi-file. Nuova modalità `LLM_H_INGEST_DIR=<dir>`: ogni file `.txt` definisce un mutante che SPOSTA un elemento
tra due template (formato `@@@ SOURCE <path>` / template-senza-elemento / `@@@ DEST <path>` / template-con-elemento /
`@@@ END`). Salvato come mutante a 2 file (`saveMutation` con 2 Document, uno per baseUri). File in `output/mutations/role-h/`.
Inoltre, modalità **`LLM_H_AUTO=1`**: genera AUTOMATICAMENTE tutti gli h (per ogni target × ruolo, sposta l'elemento
in un template correlato via mappa sorgente→destinazione). h è un puro spostamento (nessun contenuto da inventare),
quindi generato meccanicamente — stessa natura della regola static. Comando: `LLM_H_AUTO=1 java -jar ...`.
**Generati 104 mutanti h** (tutti a 2 file, 57 duplicati saltati per ruoli sovrapposti). L'operatore h è ora PIENO.
NB: i binding non compilano nella destinazione → esito misurato (not_compiled/obsolescenza), come per lo static.
Resta a 0 solo il ruolo **ε** (containing-component, sempre nullo per-file — identico allo static).

**TOTALE LLM_ROLE = 807** (703 per-file + 104 cross-template h).
Per operatore: a133 b80 c119 d59 e49 f10 g12 **h104** i20 j120 k101 — tutti gli 11 operatori rappresentati.

**GENERAZIONE COMPLETATA (2026-06-29): 703 mutanti `LLM_ROLE` distinti (Claude), qualità ~perfetta.**
137/161 file ingest scritti. I 24 file NON scritti = ruoli γ che risalgono al template INTERO (intero <form>,
intero componente reviews, intera <article> detail, intera <section> catalog): near-duplicati, valore distinto ~nullo,
alto rischio a trascriverli a mano → lasciati out di proposito. Se servissero, dare a ciascuno una classe unica sul
contenitore (così il dedup non li scarta) e ingerire. Per operatore: a133 b80 c119 d59 e49 f10 g12 i20 j120 k101.
Per componente: catalog137 detail109 card103 review98 stat97 cast87 form(field+form)72.
(riga storica precedente:)
**(precedente) set forte di 530 mutanti `LLM_ROLE` distinti (Claude), qualità ~perfetta.**
Decisione utente (opzione 2): chiudere a set forte invece di spremere gli ultimi f/g su template grandi
(riprodurre a mano 60-84 righe preservando ~20 x-test = fragile e a basso valore, per mutanti perlopiù duplicati).
- **Copertura:** TUTTI i 45 target (α completi) + elementi vicini distinti. Per operatore: a83 b67 c83 d50 e46 f10 g12 i12 j84 k83.
  Per componente: card103 stat88 catalog84 cast74 detail63 review57 field36 form25.
- **f/g strutturali:** presenti dove puliti (movie-card, cast-row, elementi piccoli); su template grandi SALTATI di proposito.
- Se in futuro servono più mutanti: aggiungere altri target x-test in role-targets.json e rifare dump→ingest, oppure
  scrivere i restanti file container/f-g in `role-ingest/` (il resume aggiunge solo i nuovi).

**PROSSIMO PASSO = mutation-tester (robustezza) sul set LLM_ROLE**, poi confronto con lo static. Vedi §4 per come lanciarlo
(Java 25, classpath + vintage-fix). NB: CineLib gira su http://localhost:4300 (`npm start` nella cartella cinelib),
niente API esterne → niente 429/OAuth. La 4300 (o la porta del tester) deve essere libera; pre-warm consigliato.
(riga storica sotto, non aggiornata:)
Qualità ~perfetta (unico reject ricorrente: `top-row` α op k → jsoup riloca le tabelle; ignorare).
FATTI: tutti gli α dei 24 target originali (operatori locali) + movie-card completo + cast-row completo + parte di stats.
**MANCANO (108 file):** in gran parte **container (β/γ/δ)** e **operatori strutturali f/g** (snippet = template intero, pesanti),
più i ruoli dei nuovi target. Sezioni residue: stats (in corso), form, reviews, movie-detail, catalog.
Come sapere cosa manca: `python` → confronta `role-prompts/` (161 file) con `role-ingest/`; oppure `role-mutations-matrix.csv`
(Decision=REJECTED, Reason=missing-in-ingest). NB molti container duplicano mutanti già salvati → il dedup li scarta;
per contarli distinti bisogna VARIARE l'edit in ogni file.
Comando ingest:
```
LLM_ROLE=1 LLM_ROLE_INGEST_DIR=output/mutations/role-ingest LLM_ROLE_TARGETS=role-targets.json \
  "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe" \
  -jar mutation-generator/llm-generator/target/llm-generator-1.0.0-jar-with-dependencies.jar
```
Per (ri)generare i prompt: stesso comando con `LLM_ROLE_DUMP=1` (invece di INGEST).

### ALTERNATIVA (se si torna a Groq) — ottimizzazione token
Lo stesso splice elemento-only abilita un run Groq ~10× più economico (free tier basterebbe). Percorso `groq` della
modalità ruolo ancora a template intero: andrebbe portato a output compatto come l'ingest. L'utente per ora ha scelto la via manuale.

### COME RIPRENDERE il vecchio percorso GROQ (se mai)
1. Aggiungere altre `GROQ_API_KEYS` (account diversi = quota giornaliera fresca).
2. Dalla root suite, stesso comando (il **resume/top-up salta i già fatti**, niente duplicati):
   ```
   export GROQ_API_KEYS="k1,k2,..."
   LLM_ROLE=1 LLM_VARIANTS_PER_CALL=4 LLM_ROLE_TARGETS=role-targets.json \
     "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe" \
     -jar mutation-generator/llm-generator/target/llm-generator-1.0.0-jar-with-dependencies.jar
   ```
   Dry-run (matrice applicabilità, senza API): aggiungere `LLM_ROLE_DRYRUN=1`.
3. Output: `output/mutations/role-mutations-matrix.csv` (per combinazione) + `role-per-test-report.csv`.
4. **Ottimizzazione token disponibile se le chiavi non bastano:** far restituire all'LLM solo l'elemento
   mutato (reincollo locale) invece dell'intero template → ~10× meno token/mutante. Non ancora implementata
   (l'utente ha scelto di aggiungere chiavi prima).
5. **Dopo il completamento dei 688:** implementare operatore `h` cross-template; poi lanciare il
   `mutation-tester` sui mutanti `LLM_ROLE` per la robustezza + confronto con lo static.

## 5. FEEDBACK DEL PROFESSORE (ultimo) — risposte e punti aperti

1. **Variare l'elemento target?** → Sì, in entrambe le modalità LLM l'elemento varia (il prompt chiede un target diverso per variante). MA è una variazione libera/emergente, **non** la tassonomia di ruoli dello static (elemento/genitore/antenato/fratello/componente). *Aperto:* si potrebbe aggiungere quella sistematica.
2. **Discutere i casi fragilità/obsolescenza/non-compilazione (cause + generalità).** → Dati per-caso presenti nei `batches.csv` (Status + Error per mutante×locatore). **DA FARE:** categorizzazione con esempi e cause.
3. **Quante app/scenari?** → Onestamente **1 app (angular-spotify), 1 solo scenario (ricerca), 2 file** (`search.component.html`, `card.component.html`). **Home NON testata.** La generalità su più app è il passo principale da fare.
4. **Riga Katalon "sbagliata".** → Ha ragione: `NotCompiled`/`Obsolescence` dipendono dalla MUTAZIONE, non dal locatore (infatti per gli altri 5 sono identiche). Katalon è su **198 invece di 266** perché **escluso da un test case** (srch-04) per un problema di formato dei suoi locatori (prefisso `xpath=`), non legato alle mutazioni → ha visto 68 mut in meno. **DA FARE:** correggere la presentazione — riportare NotCompiled/Obsolescence **una sola volta** per set, e per-locatore solo Success/Fragility (oppure riallineare/segnalare Katalon).

---

## 5.5 CineLib — autorale locatori (avviato 2026-06-29+n) + FIX METODOLOGICO importante

**FIX in `custom-locators` (Robula/Robula+):** scoperto che l'algoritmo trattava gli attributi `x-test-*`
(ground-truth della nostra mutation-testing, MAI toccati da nessuna mutazione per costruzione) come un
attributo qualsiasi → li sceglieva come locatore (es. `//div[@x-test-card='']`), rendendolo robusto al 100%
per costruzione e non per merito algoritmico, falsando il confronto 6-vie. **Fix applicato**: esclusi `x-test*`
dagli attributi candidati in `robula/Transformations.java` (transf2) e `robulaplus/Transformations.java`
(transfAddAttribute, transfAddAttributeSet), stessa logica già usata per `id`/`style`. Verificato: gli scenari
Spotify storici (AlbumTitle/PlayButton/TableHeader/FirstTrackRow) restano invariati (niente x-test lì, nessuna
regressione). **Stessa regola vale per Relative/Katalon/Selenium**: se il tool suggerisce un locatore basato
su `x-test-*`, va scartato a mano in favore dell'alternativa successiva.

**Toolchain per l'autorale (sessione 2026-06-29):**
- CineLib gira su **http://localhost:4300** (config in `Tirocinio/.claude/launch.json`, nome "cinelib";
  cwd relativa `../App tirocinio/cinelib`, porta forzata `--port 4300`). Il tool di **preview è sandboxato al
  progetto Tirocinio**: NON riesce a lanciare processi fuori da esso (cwd assoluta o `../` rifiutata) →
  per CineLib si avvia il dev server via **Bash in background**, non via `preview_start`.
- **Estensione Claude in Chrome connessa** (list_connected_browsers → "Browser 1", poi `select_browser`).
  Con quella si pilota il Chrome reale dell'utente (stesso profilo con SelectorsHub installato).
  `javascript_tool` tronca l'output a ~1000 caratteri: per leggere l'outerHTML completo di una pagina,
  leggere a fette da ≤800 caratteri (`.slice(i,i+800)`) e ricomporre.
- **Absolute XPath**: calcolato IO STESSO via JS (walk `previousElementSibling` per tag, stesso algoritmo di
  "Copy full XPath" di DevTools) — non serve nessuna estensione specifica, funziona su qualunque browser reale.
- **Robula/Robula+**: si aggiungono le entry (nome, absolute XPath, file HTML) in fondo alle liste `elements`
  già presenti in `custom-locators/src/main/java/org/unina/{robula/Robula.java, robulaplus/RobulaPlus.java}`
  (NON sostituire le entry storiche di Spotify, sono lo storico). Serve un **dump HTML statico completo**
  (`document.documentElement.outerHTML`, nello stesso stato/step del test) salvato in
  `custom-locators/src/main/resources/<nome>.html`. Build: `mvn -pl :custom-locators -am compile
  dependency:build-classpath -Dmdep.outputFile=cp.txt`; poi `java -cp "target/classes;$(cat cp.txt)"
  org.unina.robula.Robula` (idem `org.unina.robulaplus.RobulaPlus`).

**Scenario in corso: Catalog → "search filters by title"** (da `cinelib/e2e/catalog.spec.ts`).
Elementi: `x-test-search-input`, `x-test-card`, `x-test-card-title`. HTML dump salvato in
`custom-locators/src/main/resources/cinelib-catalog-search.html` (stato: dopo aver digitato "quiet", 1 card).
**FATTI: Absolute, Robula, Robula+, Relative.** Locatori raccolti per lo scenario:

| Elemento | Absolute | Robula | Robula+ | Relative |
|---|---|---|---|---|
| search-input | `/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[1]/div[1]/input[1]` | `//input` | `//input` | `//input[@placeholder='Search a movie…']` |
| card | `/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]` | `//div[@class='movie-card movie-card-grid is-favorite']` | `//*[@class='movie-card movie-card-grid is-favorite']` | `//div[@class='movie-card movie-card-grid is-favorite']` |
| card-title | `/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]/div[1]/a[1]/h2[1]` | `//h2` | `//h2` | `//h2[normalize-space()='Quiet Harbor']` |

Relative catturato con SelectorsHub (Chrome, guida passo-passo perché utente poco pratico). Nota: la traduzione
automatica di Chrome ha tradotto temporaneamente la pagina in italiano durante la cattura — VERIFICATO che il
placeholder originale inglese è `"Search a movie…"` (non la versione tradotta "Cerca un film…"), usato quello.
Nota su "card": la classe include `is-favorite`, dipendente dallo stato preferito — non è un problema per QUESTO
test perché "Quiet Harbor" è sempre favorita nel seed (deterministico), ma va tenuto a mente se si riusa altrove.

**Katalon Recorder: SBLOCCATO, parzialmente riuscito (sessione 2026-07-05/06 notte).** Cronologia problemi e
fix, per NON ripetere gli stessi tentativi:
1. Inizialmente la tabella comandi restava sempre vuota (Record/Stop si attivava ma zero cattura). Causa:
   NON permessi (già "Su tutti i siti") — l'utente ha risolto da solo riattivando/riconfigurando l'estensione
   da `chrome://extensions` (dettaglio esatto non noto, ma ha funzionato).
2. **BUG SERIO scoperto**: Chrome traduce automaticamente in italiano non solo la pagina target (CineLib) ma
   anche **l'interfaccia della finestra di Katalon Recorder stessa**, corrompendo comandi e valori registrati
   (es. "open"→"anche", valori digitati come "quiet"→tradotto/storpiato in "figo", "true"→"VERO"). **Fix**:
   disattivare la traduzione automatica GLOBALMENTE in `chrome://settings/languages` (non basta il "no" per-
   pagina, si riattiva). Dopo il fix, cattura pulita in inglese confermata.
3. **Il tasto destro (context menu) non funziona** né con SelectorsHub né con Katalon Recorder su questa
   versione di Chrome (149) — non apre il menu con le opzioni "Verify/Assert/Store", registra invece un
   click normale (che naviga via, effetto indesiderato). Non riprovare il tasto destro senza nuove idee
   (magari serve una versione Chrome diversa, o un modo alternativo di invocare le opzioni di verifica).

**KATALON COMPLETATO (2026-07-06 mattina)** per lo scenario. Trucco che ha sbloccato Card/CardTitle: dato
che il tasto destro (menu Verify/Assert) non funziona su questa versione Chrome, e i click su elementi
avvolti in `[routerLink]` venivano "collassati" da Katalon in un comando `open` (perdendo il locatore, non
un bug ma comportamento voluto dello strumento per i click che causano navigazione) — **abbiamo disattivato
TEMPORANEAMENTE i due `[routerLink]` in `movie-card.component.html`** (poster e card-title-link), catturato i
click in sicurezza (senza navigare via), poi **ripristinato subito il file identico all'originale** (verificato
diff pulito, ricompilazione senza warning). Tecnica riutilizzabile in futuro per altri elementi avvolti in link.

**Risultati finali Katalon per "Catalog → search filters by title":**
- SearchInput: `xpath=//input[@type='text']`
- Card: `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Reset catalog'])[1]/following::div[2]`
- CardTitle: `xpath=(.//*[normalize-space(text()) and normalize-space(.)='♥'])[1]/following::h2[1]`
(Stile caratteristico di Katalon: usa un elemento vicino con testo univoco come "ancora" + asse
`following::` per raggiungere il target, quando non trova un attributo diretto abbastanza unico.)

**SELENIUM COMPLETATO (2026-07-06)** — Selenium IDE (Firefox) NON ha avuto il problema di Katalon (click su
elementi con routerLink catturati direttamente, nessun collasso in "open", nessun trucco necessario).
Risultati: SearchInput=`css=.search-input`, Card=`css=.movie-card`, CardTitle=`css=.card-title`.

**SCENARIO "Catalog → search filters by title" COMPLETO — TUTTE E 6 LE STRATEGIE:**
| Elemento | Absolute | Robula | Robula+ | Relative | Katalon | Selenium |
|---|---|---|---|---|---|---|
| search-input | `/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[1]/div[1]/input[1]` | `//input` | `//input` | `//input[@placeholder='Search a movie…']` | `xpath=//input[@type='text']` | `css=.search-input` |
| card | `/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]` | `//div[@class='movie-card movie-card-grid is-favorite']` | `//*[@class='movie-card movie-card-grid is-favorite']` | `//div[@class='movie-card movie-card-grid is-favorite']` | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='Reset catalog'])[1]/following::div[2]` | `css=.movie-card` |
| card-title | `/html[1]/body[1]/app-root[1]/main[1]/app-catalog[1]/section[1]/div[3]/app-movie-card[1]/div[1]/div[1]/a[1]/h2[1]` | `//h2` | `//h2` | `//h2[normalize-space()='Quiet Harbor']` | `xpath=(.//*[normalize-space(text()) and normalize-space(.)='♥'])[1]/following::h2[1]` | `css=.card-title` |

**FILE JAVA SCRITTI E COMPILATI (2026-07-06):**
- `ext-test-classes/src/main/java/org/ext/cinelib/CineLibBaseTest.java` — nuovo base test (package
  separato da `org.ext`, NON estende `org.ext.BaseTest` perché quello ha `setUp()` `final` con baseUrl
  hardcoded 4200 + `authenticate()` Spotify-specifico). baseUrl=`http://localhost:4300/`, niente login,
  riusa lo stesso `org.ext.WebDriverFactory` (driver Chrome condiviso).
- `ext-test-classes/src/main/java/org/ext/cinelib/catalogsearch/{Absolute,Robula,RobulaPlus,Relative,
  Katalon,Selenium}XPathTest.java` — i 6 test per lo scenario "Catalog → search filters by title", con
  i locatori della tabella sopra. Ognuno: apre baseUrl, cerca "quiet" nel search-input (locator della
  strategia), assert 1 card trovata, assert testo card-title = "Quiet Harbor".
- **Compilazione verificata**: `mvn -pl :ext-test-classes -am compile` → BUILD SUCCESS, 7 file compilati,
  i test Spotify esistenti in `org.ext` (root, non `org.ext.cinelib`) intatti/non toccati.
- **NON ancora eseguiti** contro il mutation-tester (serve verificare che il meccanismo di discovery del
  tester trovi anche le classi nel nuovo sotto-package `org.ext.cinelib.catalogsearch`, non solo `org.ext`
  root — da controllare alla prossima sessione leggendo `MutationTester.java` se necessario).

**MUTATION-TESTER: PRIMO RUN LANCIATO (2026-07-06 pomeriggio).** Setup e 2 bug risolti:
1. **`generator-config-cinelib.json`** creato (NON si è toccato `generator-config.json`, quello resta per
   Spotify): `repositoryRootPath` → CineLib, `npmRunCommand: "npm start -- --port 4300"`. Si lancia con
   `--config generator-config-cinelib.json`.
2. **Scoping mutazioni**: tutte le 807 LLM_ROLE erano PENDING (rischio di girarle tutte insieme). Impostate
   a **PENDING solo le 306** che toccano `catalog.component.html` o `movie-card.component.html` (incluse le
   `h` cross-template che li toccano come destinazione, non solo sorgente — verificato via `mutated_files`,
   non solo il campo `element`). Le altre 501 messe a **HELD**. Query/script di riferimento: contare
   `ntpath.basename(target_file_path)` per ogni `mutated_files` di ogni mutazione, non fidarsi del solo
   campo `element` (perde le destinazioni degli `h`).
3. **BUG: 12 classi invece di 6.** Il file-scanner di `TestRunnerEngine` è ricorsivo su tutta la cartella
   `-td` → caricava anche i 6 vecchi test Spotify (`org.ext.*`), che avrebbero fallito su OGNI mutante
   CineLib (puntano a `127.0.0.1:4200`, server non attivo). **Fix**: creata una cartella isolata
   `ext-test-classes/target/cinelib-only/` con SOLO una copia di `org/ext/WebDriverFactory.class` +
   `org/ext/cinelib/CineLibBaseTest.class` + `org/ext/cinelib/catalogsearch/*.class` (8 file, copia pura,
   NON tocca `target/classes` originale) → lanciare il tester con `-td "ext-test-classes/target/cinelib-only"`.
   ATTENZIONE: se si aggiungono nuovi scenari CineLib, vanno ricopiati anche lì (o rifare lo script di copia).
4. **BUG: `NpmConsoleWrapper` non rilevava mai la ricompilazione riuscita.** Cercava solo la stringa
   `"Compiled successfully"` (builder webpack, quello di Angular-Spotify) — Angular 19 con esbuild (CineLib)
   stampa invece `"Application bundle generation complete"`. **Fix applicato in
   `mutation-tester/src/main/java/org/unina/NpmConsoleWrapper.java`**: array `SUCCESS_MSGS` con entrambe le
   stringhe (non si è rotto nulla per Spotify, solo aggiunto un secondo pattern). Ricompilato il jar
   (`mvn -pl :mutation-tester -am package -DskipTests`). Fix minimo, non tocca la logica sperimentale.
5. Prima di lanciare/rilanciare: **fermare sempre il dev server manuale sulla 4300** (il tester lancia il
   suo, altrimenti conflitto di porta) — `Get-CimInstance Win32_Process -Filter "Name='node.exe'"` e
   `Stop-Process` sul PID trovato. Verificato più volte in sessione: uccidere il processo Java del tester
   PRIMA che entri nel ciclo mutanti (log ancora su "Starting TypeScript application...") è sicuro — non
   applica/lascia mutato nessun file (verificato leggendo `Mutation.applyMutationToRepository`/
   `revertMutations`, quest'ultimo in un blocco `finally`).

**PRIMO RUN (306 mutanti): si è fermato dopo 21** per un crash su `Files.move` (probabile blocco
transitorio OneDrive/antivirus su `catalog.component.html` appena scritto). **Verificato che il file
originale NON si sia corrotto** (Files.move è atomico: se fallisce, la destinazione resta intonsa — controllato
byte per byte, entrambi i file erano intatti). Risultati parziali salvati in
`output/tests/cinelib-catalogsearch-block1.csv` (+`-stats.csv`) prima che venissero sovrascritti.

**BUG 3 scoperto e risolto: `MutationDatabase.updateMutation()`** filtrava con `WHERE uuid = ?` ma riceveva
in input `mutation_id` (colonna diversa) → lo UPDATE non trovava mai la riga, lo stato non cambiava MAI da
PENDING a COMPLETE, quindi ogni run riprocessa sempre TUTTO da capo (bug preesistente, non introdotto in
questa sessione — spiega perché il predecessore gestiva i batch manualmente, vedi §"Esecuzione a BLOCCHI"
più sotto: era un workaround a questo bug, non solo comodità). **Fix**: `WHERE mutation_id = ?` in
`mutation-generator/common/src/main/java/org/unina/data/MutationDatabase.java`.

**BUG 4 scoperto e risolto: `Mutation.applyMutationToRepository()`** — `Files.move` con `ATOMIC_MOVE` può
fallire su Windows per lock transitori (OneDrive, watcher, antivirus). **Fix**: aggiunto retry con backoff
(5 tentativi, 300ms×tentativo) in `mutation-generator/common/src/main/java/org/unina/data/Mutation.java`
(nuovo metodo privato `moveWithRetry`). Se fallisce comunque dopo i retry, la destinazione resta intonsa
(garanzia di Files.move), quindi nessun rischio di corruzione anche in caso di fallimento persistente.

Dopo i fix: ricompilato `common`+`mutation-tester` (`mvn -pl :mutation-tester -am package -DskipTests`),
**segnati HELD i 21 mutation_id già testati** (script Python: parse del batchId composito
`mutation_<mutation_id>_<mutation_type>_<element>` nel CSV, non usare il campo "Id" direttamente).
**Run 2 rilanciato sui restanti 285**, in corso.

**BUG 5 — IL PIÙ GRANDE, TROVATO ANALIZZANDO I RISULTATI (2026-07-06 sera).** Run 2 completato senza crash
(285/285), ma con **not_compiled al 67.4%** (192/285), ben sopra la soglia 20%. Analisi per operatore:
`f`/`g` (spostamento puro) = **0%** non compilati; TUTTI gli altri operatori (a,b,c,d,e,i,j,k) = **50-75%**.
Diagnosticato applicando manualmente ~7 mutanti d'esempio al dev server e leggendo l'errore REALE di Angular
(non solo "did not recompile"): **quasi tutti mostravano lo STESSO errore**, indipendente dall'operatore:
`NG5002: Parser Error: Unexpected token '&' in [movies().length &gt]`.

**Causa**: jsoup non conosce la sintassi Angular `@if (...)`/`@for (...)` — quando serializza l'HTML per
salvarlo, tratta il contenuto come testo normale e **converte `>` in `&gt;`** (corretto per HTML puro, ma
rompe la sintassi Angular). Questo accade **indipendentemente da cosa la mutazione abbia effettivamente
cambiato** — corrompe qualunque mutazione su un file che contiene `@if`/`@for` con un confronto `>`/`<`
(es. `catalog.component.html` ha `@if (movies().length > 0)`). Verificato: **189/192 dei "non compilati"
(98%) contenevano questa corruzione** nel testo salvato — il vero tasso di non-compilazione è ~1%, non 58.5%.
Su TUTTO il DB: **334/807 mutazioni (41%) erano affette**.

**Fix applicato (confermato dall'utente, entrambe le parti):**
1. **Generatore** (`mutation-generator/llm-generator/src/main/java/org/unina/App.java`): nuova funzione
   `fixAngularControlFlowEntities()` + wrapper `bodyHtml(Document)` che de-escapa `&gt;`/`&lt;`/`&amp;`
   **solo** dentro la testa di un blocco `@if(...)`/`@for(...)` (fino alla `{` di apertura), lasciando
   intatto il resto del documento. Sostituite TUTTE le chiamate `.body().html()` con `bodyHtml(...)`
   (eccetto l'implementazione base della funzione stessa). Ricompilato (`mvn -pl :llm-generator -am install
   -DskipTests`).
2. **Correzione retroattiva del DB**: script Python che applica la stessa logica a `mutated_files.mutated_code`
   per tutte le righe con `&gt;`/`&lt;` — **321/334 corrette** (le altre 13 avevano entità legittime FUORI
   dai blocchi Angular, giustamente non toccate).
3. (Bonus, trovato per caso) **2 byte nulli** (`\x00`) erano finiti nel codice sorgente di `App.java` al
   posto di uno spazio (riga di dedup dei mutanti `h`) — corretti, causavano solo un "grep: binary file",
   nessun impatto sui risultati.

**Dopo il fix**: tutte le 306 mutazioni (catalog+movie-card) rimesse a **PENDING** (sia le 21 del block1 sia
le 285 del run2 — presumibilmente affette dalla stessa corruzione, si ritesta tutto pulito). Run 3 in corso.

⚠️ **NOTA IMPORTANTE per il futuro**: questo bug ha probabilmente gonfiato il not_compiled anche per i
restanti 501 mutanti CineLib (altri componenti) — **quando si testano gli altri scenari, verificare
PRIMA se i loro file hanno `@if`/`@for` con `>`/`<`** (es. `stats.component.html`, `movie-form.component.html`
potrebbero averne). Il fix nel generatore è già attivo per le mutazioni NON ancora generate; quelle già
salvate negli altri 501 mutanti sono state comunque incluse nella correzione retroattiva sopra (334 totali
corretti sull'intero DB, non solo sui 306 di questo scenario).

**PROSSIMO PASSO**: controllare l'esito del run 3, poi decidere con
l'utente/prof quanti altri scenari autorare (raccomandazione: pochi rappresentativi per area/componente, NON
tutti i 29 test — vedi discussione in chat 2026-07-06). Per ogni nuovo scenario scelto, ripetere il procedimento in
`completed-tests/CatalogSearch/` (o analogo), seguendo il pattern di `completed-tests/ArtistSearch/*.java`
— ATTENZIONE: serve un `BaseTest` NUOVO per CineLib (quello in `ext-test-classes/.../BaseTest.java` è
hardcoded su baseUrl 4200 + authenticate() Spotify-specifico: NON toccarlo, è condiviso con tutti i test
Spotify esistenti — creare una classe base separata per CineLib con baseUrl 4300 e senza authenticate()).

## 6. TODO PROSSIMA SESSIONE (in ordine di priorità)

0. **(IN CORSO — nuovo scenario su angular-spotify)** Costruire lo scenario E2E su **My Albums / Album detail** (test case + 6 strategie di locatori, come search). App già seminata e funzionante. Dettagli e mappa endpoint in **§4.5**. Aprire l'app su **127.0.0.1:4200** (mai localhost).
1. **(Prof Q3 / generalità — OBIETTIVO PRINCIPALE)** Creare **nuove applicazioni** per la generalizzabilità. Il prof suggerisce di **generarle con un LLM** (es. ChatGPT/Claude), guidando passo-passo: ≥ N pagine, uso di template, template che includono altri template, funzionalità CRUD su un tipo di dato, tecnologia imposta (Angular/Node), complessità sufficiente per applicare le mutazioni ma non eccessiva. Poi: generare test + mutazioni (sia LLM sia static) e confrontare valide/non-compilanti tra app. (Più app diverse = affermazione più forte. Bastano 2-3 che vanno bene.) → **AVVIATO: prima app `CineLib` creata e funzionante (Angular 19, dati locali) — vedi §4.6. Da mostrare al prof.** Prossimo: autorare scenari E2E su CineLib + mutation-tester; poi una 2ª app diversa.
2. **(Prof Q4) ✅ RISOLTO** Tabelle di confronto corrette: NotCompiled/Obsolescence riportate **una volta per set** (riga sopra ogni tabella), per-locatore solo **Success/Fragility**; nota Katalon (198 vs 266, prefisso `xpath=`). `output/confronto_robustezza.pdf` rigenerato (vecchio in `.pdf.bak`); sorgente `build_confronto.py` aggiornato.
2b. **(Prof — PAREGGIARE static↔parametrico)** Il prof vuole che **static (266)** e **LLM parametrico** abbiano un numero di mutazioni **comparabile** (poche di differenza). Oggi parametrico = **161 testate** (211 generate, ~50 generate-ma-non-testate). Il **realistico è fuori scope per ora**. **DA FARE:** portare il parametrico TESTATO da 161 verso ~250-266 → (a) testare le ~50 già generate, (b) generare+testare il resto. **Collo di bottiglia = TESTING** (mutation-tester + **429 Spotify**), non la generazione. La generazione richiede `GROQ_API_KEYS` da env.
3. **(Prof Q2)** Preparare l'analisi per-caso di fragilità/obsolescenza/non-compilazione con cause ed esempi (dai `batches.csv`).
4. **(Parità 200)** Completare la robustezza parametrica: 39 mutanti card rimanenti (`target59[20:59]`) quando Spotify è fresco (+ re-login fatto bene). Bassa priorità: non cambia la classifica.
5. **(Prof Q3)** Eventuale **scenario HOME** su angular-spotify (più scenari/aree del DOM) per rafforzare significatività.
6. Inviare al prof i risultati aggiornati.

---

## 7. RIFERIMENTI RAPIDI (file)
- **Deliverable** (`output/`): `confronto_robustezza.pdf` (3 tabelle + i prompt), `tabella_robustezza.pdf` (realistico), `tabella_robustezza_parametrica.pdf`, `risultati_base_spotify.md/.pdf`, `risultati_1_2_validita.md`, `RISULTATO-FINALE-llm.txt`, PNG `tabella_robustezza_llm.png` / `tabella_validita_1_2.png`.
- **Script di analisi** (root): `coherence_analysis.py` (coerenza 1.2), `compile_check.py` (compilazione 1.2), `validity_1_2.py` (incrocio), `render_tables.py` (PNG tabelle), `build_confronto.py` (PDF confronto), `param_robustness_target59.txt` (lista resume 39+).
- **Prompt**: `mutation-generator/llm-generator/src/main/resources/realistic-prompt.txt` e `parametric-prompt.txt`. Tassonomia: `mutation-types.json`.
- **CSV grezzi robustezza**: static `output/tests/srch-0X-full/`; realistico `output/tests/llm-search-full/` + `llm-card-full/`; parametrico `output/tests/llm-param-robustness/`.
- **Da ignorare/obsoleti**: `llm-variants/` (vecchio script find/replace scartato + file ingest vecchi), `Tabella-Risultati-LLM.md` (superato).
- Runbook static dettagliato: `RUNBOOK-static-baseline-ricerca.md`.
