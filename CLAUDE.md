# Contesto progetto (leggere all'avvio)

Tesi/tirocinio sulla **fragilità dei test E2E** su app Angular. Si confronta la robustezza di 6 strategie
di locatori XPath (Absolute, Relative, Robula, Robula+, Selenium, Katalon) sotto mutazione dell'HTML,
generata in 3 modi: **static** (tesi precedente), **LLM parametrico** (stessi tipi a–k dello static),
**LLM realistico** (l'LLM decide tutto). Obiettivo: dimostrare che la generazione mutanti via LLM è
**generale e più efficace** dello static.

## ⚠️ PRIMA DI FARE QUALSIASI COSA
**Leggi `HANDOFF-prossima-chat.md`**: contiene lo stato esatto dei dati, i risultati, come usare gli
strumenti, il feedback del professore e i TODO. È la memoria tra le sessioni. Leggi anche
`RUNBOOK-static-baseline-ricerca.md` per il dettaglio della fase static.

## Gotcha critici (per non rompere nulla)
- **mutation-tester**: richiede **Java 25**; eseguire SEMPRE via classpath + `vintage-fix/`, MAI `-jar`:
  `java -cp "mutation-tester/target/...jar-with-dependencies.jar;vintage-fix" org.unina.MutationTester -td "ext-test-classes/target/classes"`
- **Dev server lento (OneDrive)**: prima di lanciare il tester fai il **pre-warm** (`npm start` in
  `../../angular-spotify` fino a "Compiled successfully", poi killalo) altrimenti va in `did not recompile in time`.
  La 4200 deve essere LIBERA quando parte il tester.
- **URL = 127.0.0.1, MAI localhost**: l'app Spotify "Tirocinio E2E" (client_id `43b0afe3…`) ha registrato come
  redirect URI **solo** `http://127.0.0.1:4200/`. Aprendo l'app via `localhost:4200` il login OAuth fallisce con
  *"redirect_uri: Not matching configuration"* (Spotify ha deprecato `localhost`). Aprire SEMPRE
  `http://127.0.0.1:4200/`. La suite (`BaseTest.java`) usa già 127.0.0.1: non cambiarlo.
- **Spotify 429**: la search si rate-limita dopo molte ricerche (blocco a livello account, persistente).
  Sintomo = obsolescenza alta uniforme su tutte le strategie. Protocollo: probe + scarta i run invalidi (vedi handoff §4).
- **NON toccare**: `static-generator`, `mutation-tester`, `custom-locators`, `hook-injector`, i locatori.
- **Indipendenza LLM (vincolo del prof)**: le mutazioni le genera l'LLM; vietati script find/replace e
  istruzioni su DOVE colpire. Vedi handoff §3.
- API key Groq **da variabile d'ambiente** (`GROQ_API_KEYS`), mai hardcoded.

## Scenari: perché solo SEARCH è riusabile + come autorarne di nuovi
- **Perché search ha funzionato**: è l'**unico scenario del predecessore (Liberti) indipendente dall'account**
  — lavora sul **catalogo pubblico** (cerca un brano, ne verifica la comparsa), non sulla libreria personale.
  Per questo "trasloca" sul nostro account Spotify. NON è stata fortuna: è una proprietà dello scenario.
- **Gli altri scenari del predecessore NON sono riusabili** (verificato, tutti e 15 i tc): sidebar/playlist/player
  falliscono per dato personale (playlist "The Goats"), locatori posizionali disallineati col DOM attuale,
  asserzioni `getDomAttribute('active')` (classe runtime → null), o richiesta di N playlist che non abbiamo.
  Es. tc-side-01 baseline = **3/6** (sotto soglia ≥6/7). Quindi per AGGIUNGERE scenari bisogna **autorarli da zero**.
- **Per autorare i locatori di un nuovo scenario servono strumenti diversi per strategia:**
  - **Absolute** → DevTools del browser ("Copy full XPath"). Nessuna estensione.
  - **Relative** → estensione **SelectorsHub** (browser).
  - **Selenium** → **Selenium IDE** — l'estensione Chrome è stata RIMOSSA (MV2); ora solo via **Firefox** (add-on).
  - **Katalon** → estensione **Katalon Recorder (Selenium tests generator)** (browser).
  - **Robula / Robula+** → **NON** è un'estensione: si generano col modulo Java **`custom-locators`** del progetto
    (gli si passano nome target + XPath assoluto + file HTML, poi compila/esegui). Lo guida Claude.
  - **Hook** → escluso (toolchain iniezione non disponibile).
  → Quindi: estensioni necessarie = **SelectorsHub + Katalon Recorder** (Chrome) + **Selenium IDE** (Firefox).

### Toolchain di autorale — INSTALLATA (2026-06-22)
- **SelectorsHub** (Relative) e **Katalon Recorder – Selenium tests generator** (Katalon): installate in **Chrome**,
  nel profilo persistente `C:\Users\vince\selenium-spotify-profile` (lo stesso loggato a Spotify e usato dai test).
- **Selenium IDE** (Selenium): installato in **Firefox** (profilo `cizq7n2g.default-release`). NB: l'estensione Chrome
  è stata RIMOSSA (MV2) → si usa la **stessa** Selenium IDE via Firefox. **Stesso tool, stessi locatori**: la
  generazione dipende dal DOM, non dal browser-contenitore. Deviazione da documentare al prof (Chrome→Firefox).
- **Robula/Robula+**: nessuna installazione, modulo `custom-locators` del progetto. **Absolute**: DevTools (nativo).
- **Caratteristiche d'accesso (come usarle)**: la cattura dei locatori avviene **interagendo con l'app live**
  (`http://127.0.0.1:4200`, dev server su) → la pagina deve mostrare gli elementi, quindi serve **login Spotify**
  nel browser che cattura. Chrome `selenium-spotify-profile` è **già loggato**; **Firefox va loggato una volta**
  (il redirect `127.0.0.1:4200` vale per ogni browser). Il locatore catturato è una stringa **browser-agnostica**:
  finisce in `*XPathTest.java` e **gira poi in Chrome** (ChromeDriver) col mutation-tester. Firefox NON esegue i test.
- Le estensioni prima NON erano installate; il predecessore le usò (su Chrome) per autorare i locatori ereditati.

## Struttura
- llm-generator implementato: `mutation-generator/llm-generator/` (modalità realistica/parametrica/ingest).
- Dati: `mutations.db` (root). Risultati e deliverable: `output/`.
