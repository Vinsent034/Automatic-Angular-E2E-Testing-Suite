# Guida per l'assistente (Claude Code) — generazione di mutanti

Questo repository contiene lo strumento della tesi di Vincenzo Di Carluccio (Federico II,
relatore Prof. Porfirio Tramontana): genera **mutanti dell'interfaccia** di applicazioni Angular,
cioè copie dei template con una sola modifica, con due tecniche:

- **statica**: regole fisse applicate all'albero HTML (`mutation-generator/static-generator`);
- **linguistica**: un modello linguistico scrive la modifica, lo strumento decide dove e quale
  (`mutation-generator/llm-generator`).

Lo schema di mutazione ha **11 operatori** (a–k, definiti in `mutation-types-modello.json`) e
**5 ruoli** rispetto all'elemento bersaglio: α bersaglio, β padre, γ antenato, δ fratello,
ε componente contenitore. Gli operatori a, b, c, d, e, i, j, k li scrive il modello; f, g, h
(spostamenti) li produce lo strumento da programma.

Il tuo compito tipico: **preparare il PC e generare i mutanti di un'applicazione** che l'utente
indica. Segui i passi nell'ordine e, dopo ognuno, fai il controllo indicato e riferisci l'esito.

---

## Regole da rispettare sempre

1. **Non modificare** le cartelle delle campagne della tesi (`cinelib-v2-run`, `cinelib-v2-static-run`,
   `flowboard-v2-run`, `flowboard-v2-static-run`, `cookbook-run`, `cookbook-static-run` e simili) né i
   loro database. Ogni nuova generazione va in una **cartella nuova** (es. `<app>-run`).
2. **Chiedi prima di modificare**: i template di un'applicazione (aggiunta degli attributi `x-test`)
   e il codice dello strumento (mappa degli spostamenti h, passo 3.4). Spiega cosa cambi e perché.
3. **Non fare commit né push** su Git, se l'utente non lo chiede esplicitamente.
4. **Non usare sotto-agenti** per scrivere le risposte ai prompt (passo 4.3): girerebbero con un
   modello diverso da quello dichiarato.
5. I comandi sono per **Windows PowerShell**. Usa sempre il terminale preparato al passo 1
   (Java 25 portatile): il `java` di sistema potrebbe essere di un'altra versione e non funzionare.

---

## 1. Preparare il PC (una volta sola)

Dalla radice del repository lancia `installa.bat` (oppure
`powershell -ExecutionPolicy Bypass -File installa.ps1`). Lo script, senza installare nulla nel
sistema e senza permessi di amministratore:

1. scarica in `strumenti/` Java 25 (Temurin 25.0.2), Maven 3.9.9, Node.js 22 e Python 3.13 portatili;
2. riscrive i percorsi nei file `generator-config-*.json` e `role-targets*.json` verso
   `applicazioni-soggetto/` (script `configura_percorsi.ps1`);
3. compila lo strumento (`mvn package -DskipTests` dalla radice: produce i tre jar);
4. installa le dipendenze npm delle tre applicazioni di `applicazioni-soggetto/`;
5. controlla che ci sia Google Chrome (serve solo per i test sui mutanti).

**Controllo:** l'ultima riga dev'essere `Tutto pronto.` Se la compilazione fallisce, guarda che
`java -version` (nel terminale preparato) dica 25.

Per ogni comando successivo, in una finestra PowerShell nella radice del repository, attiva prima
gli strumenti portatili (oppure apri il terminale con `terminale.bat`):

```powershell
. .\ambiente.ps1        # imposta JAVA_HOME, PATH e le variabili $JAVA e $LLMJAR
```

---

## 2. Quale applicazione

- **Una delle tre della tesi** (CineLib, FlowBoard, CookBook): sono già in `applicazioni-soggetto/`,
  con i file pronti nella radice (`role-targets.json` per CineLib, `role-targets-flowboard.json`,
  `role-targets-cookbook.json`, e i `generator-config-*.json`). Vai al passo 4.
  Per **riottenere i mutanti della tesi** non serve il modello: usa le risposte salvate
  (passo 4.3, variante A).
- **Un'applicazione nuova**: **chiedi all'utente il percorso della cartella** dell'applicazione
  (quella che contiene `src/app`). Deve essere un'applicazione Angular con i template in file
  `*.component.html`. Vai al passo 3.

---

## 3. Preparare un'applicazione nuova

Nei file JSON i percorsi sono **assoluti** e scritti con le barre `/`
(es. `C:/Users/nome/progetti/miaapp`).

### 3.1 Gli attributi di test

Lo strumento riconosce ogni bersaglio da un **attributo di test** presente nel template, per
esempio `<button class="btn" x-test-search>Cerca</button>`. Cerca nei template gli attributi che
iniziano con `x-test`:

- se ci sono, usa quelli;
- se mancano, **proponi all'utente** quali elementi marcare (quelli su cui agirebbe o
  controllerebbe un test: campi, bottoni, titoli, conteggi) e, **solo dopo il suo consenso**,
  aggiungi l'attributo nel template (un nome diverso per ogni elemento). Se il valore è un binding
  dinamico, nel file dei bersagli si scrive nella forma `[attr.x-test-nome]`.

Il modello ha la regola di non toccare mai questi attributi: servono anche alla strategia
Hook-Based e a ritrovare l'elemento.

### 3.2 Il file dei bersagli: `role-targets-<app>.json` (nella radice del repository)

```json
{
  "app": "<app>",
  "appRoot": "C:/percorso/della/app",
  "targets": [
    {
      "id": "search-input",
      "targetAttr": "x-test-search",
      "componentHtml": "C:/percorso/della/app/src/app/search/search.component.html",
      "tests": ["descrizione libera di chi usa questo elemento"]
    }
  ]
}
```

| Campo | Significato |
|---|---|
| `app` | nome breve dell'applicazione, in minuscolo; lo stesso usato nella mappa h (3.4) |
| `appRoot` | cartella dell'applicazione (quella che contiene `src/app`) |
| `id` | nome del bersaglio, univoco; compare nei nomi dei prompt |
| `targetAttr` | l'attributo di test che identifica l'elemento nel template |
| `componentHtml` | percorso assoluto del template che contiene l'elemento |
| `tests` | solo informativo |

Controllo: per ogni bersaglio il file `componentHtml` esiste e contiene `targetAttr`.

### 3.3 La configurazione: `generator-config-<app>.json` (nella radice)

```json
{
  "seed": "1234",
  "repositoryRootPath": "C:/percorso/della/app",
  "npmRunCommand": "npm start -- --port 4600",
  "mutations": []
}
```

`seed` fissa le scelte casuali; `npmRunCommand` serve solo ai test (porta libera a scelta);
`mutations` resta vuoto per il generatore linguistico.

### 3.4 La mappa degli spostamenti h (modifica del codice: chiedi prima)

L'operatore h sposta un nodo **da un template a un altro**. Le coppie sorgente → destinazione sono
scritte in `mutation-generator/llm-generator/src/main/java/org/unina/App.java`, metodo
`runAutoH`, con un ramo per ogni applicazione (`flowboard`, `cookbook`; il ramo finale `else` è
quello di CineLib). Per un'app nuova:

1. aggiungi un ramo `else if ("<app>".equalsIgnoreCase(tf.app)) { ... }` **prima** dell'`else`, con
   una riga `destMap.put("<template>.component.html", base + "<cartella>/<template>.component.html");`
   per ogni template che contiene bersagli; come destinazione scegli un componente collegato
   (padre ↔ figlio, oppure `app.component.html` ↔ intestazione), come negli altri rami;
2. ricompila: `mvn package -DskipTests` dalla radice (con `ambiente.ps1` attivo).

Senza questo ramo l'app nuova userebbe la mappa di CineLib: gli h vengono saltati oppure il
generatore si ferma perché non trova i template di destinazione. In alternativa, con il consenso
dell'utente, salta il comando h al passo 4.4 e dichiara che l'operatore h non è stato generato.

### 3.5 (facoltativo) Configurazione per il generatore statico

Se l'utente vuole anche i mutanti statici, crea `generator-config-<app>-static.json` uguale al
3.3 ma con un elemento in `mutations` per ogni bersaglio:

```json
{ "name": "search-input",
  "file_path": "C:/percorso/della/app/src/app/search/search.component.html",
  "target_matcher": { "type": "attribute", "key": "x-test-search", "value": "" } }
```

---

## 4. Generare i mutanti linguistici

Tutti i comandi si lanciano **dentro la cartella della campagna**, con `ambiente.ps1` attivo.

### 4.1 Cartella e variabili

```powershell
. .\ambiente.ps1
New-Item -ItemType Directory <app>-run; cd <app>-run
$env:LLM_ROLE = "1"
$env:LLM_ROLE_TARGETS = "..\role-targets-<app>.json"
$env:LLM_MUTATION_TYPES = "..\mutation-types-modello.json"
```

### 4.2 Prompt (riversamento)

```powershell
$env:LLM_ROLE_DUMP = "1"; & $JAVA -jar $LLMJAR ..\generator-config-<app>.json; Remove-Item Env:LLM_ROLE_DUMP
```

Crea `mutations.db` (vuoto), un prompt per ogni bersaglio e ruolo in
`output/mutations/role-prompts/` e la matrice `output/mutations/role-mutations-matrix.csv`
(una riga per bersaglio × ruolo × operatore, con il motivo se non applicabile).
La riga finale `DONE ... | Applicable: N` dà il **numero di blocchi** che il modello deve scrivere.
La scritta `model=llama-...` all'inizio è solo il valore predefinito e qui non conta.

Controllo: numero di file in `role-prompts` = numero di prompt; annota prompt e blocchi.

### 4.3 Risposte del modello

**Variante A — mutanti della tesi (nessun modello).** Copia le risposte salvate della campagna
della tesi nella cartella nuova:
`Copy-Item ..\cinelib-v2-run\output\mutations\role-ingest output\mutations\role-ingest -Recurse`
(per FlowBoard `flowboard-v2-run`, per CookBook `cookbook-run`). Poi vai al 4.4.

**Variante B — risposte nuove.** Copia `ISTRUZIONI-MODELLO.template.md` (radice) nella cartella
della campagna con il nome `ISTRUZIONI-MODELLO.md` e sostituisci i segnaposto `<APP>`, `<N_PROMPT>`,
`<N_BLOCCHI>` e `<ESEMPIO>` (il nome di uno dei prompt). Poi esegui tu quelle istruzioni, oppure
l'utente le fa eseguire a un'altra sessione: le risposte vanno in `output/mutations/role-ingest/`,
un file per prompt con lo stesso nome, e il registro `_registro.txt` riporta il modello usato.

Sul modello: i mutanti della tesi sono stati scritti da **Claude Opus 4.8** (tramite Claude Code).
Se il modello di questa sessione è diverso, **dillo all'utente prima di cominciare**: la procedura
funziona allo stesso modo, ma i mutanti non saranno gli stessi della tesi e il modello va
dichiarato. Procedi solo se l'utente conferma.

Controllo: in `role-ingest` un file per ogni prompt; il totale dei blocchi nel registro = N.

### 4.4 Spostamenti f, g, h (da programma)

```powershell
$env:LLM_H_AUTO = "1";  & $JAVA -jar $LLMJAR ..\generator-config-<app>.json; Remove-Item Env:LLM_H_AUTO
$env:LLM_FG_AUTO = "1"; & $JAVA -jar $LLMJAR ..\generator-config-<app>.json; Remove-Item Env:LLM_FG_AUTO
```

Controllo: le righe `DONE (h auto). Saved ...` e `DONE (f/g auto). Saved ...`.

### 4.5 Acquisizione delle risposte

```powershell
$env:LLM_ROLE_INGEST_DIR = "output/mutations/role-ingest"
$env:LLM_MODEL = "<identificativo del modello che ha scritto le risposte, es. claude-opus-4-8>"
& $JAVA -jar $LLMJAR ..\generator-config-<app>.json
```

Lo strumento rimette ogni elemento modificato nel suo template e scarta i blocchi mancanti, quelli
che toccano un attributo `x-test`, quelli che non cambiano nulla e i duplicati; il motivo finisce
nella matrice. Riga finale: `DONE (role 11x5). Saved: S | Rejected: R | ...`.

**Controllo finale:** numero di mutanti nel database

```powershell
python -c "import sqlite3; print(sqlite3.connect('mutations.db').execute('select count(*) from mutations').fetchone()[0])"
```

= mutanti h + mutanti f/g + `Saved` dell'acquisizione.

**Solo per CookBook (variante A):** nella tesi 10 mutanti dell'operatore i applicato al bersaglio
sono stati tolti dalla campagna principale (§ 4.1: l'attributo di test è scritto in forma dinamica
e il controllo non lo riconosce). Per ottenere lo stesso insieme, dalla cartella della campagna:

```powershell
python ..\escludi_i_su_bersaglio.py
```

Non lanciarlo su CineLib e FlowBoard se l'obiettivo è riottenere i mutanti della tesi: lì questi
mutanti non sono stati esclusi. Su un'applicazione nuova, chiedi all'utente se applicarlo.

Con la variante A i totali della tesi (Tabella 4.1) sono: **CineLib 944**, **FlowBoard 1 559**,
**CookBook 1 939 → 1 929** dopo l'esclusione.

---

## 5. Generare i mutanti statici (facoltativo)

In una cartella nuova (es. `<app>-static-run`), con `ambiente.ps1` attivo:

```powershell
& $JAVA -jar ..\mutation-generator\static-generator\target\static-generator-1.0.0-jar-with-dependencies.jar ..\generator-config-<app>-static.json
```

Il generatore statico usa il seme fisso e produce sempre gli stessi mutanti, duplicati compresi.

---

## 6. Cosa riferire all'utente alla fine

Numero di prompt e di blocchi, modello usato, mutanti salvati per tipo (dal modello, h, f/g),
scarti con i motivi principali (dalla matrice) e il totale nel database.

## Per approfondire

- `RIPRODURRE-LE-CAMPAGNE.md`: come rifare le campagne della tesi, test compresi.
- `README.md` e `applicazioni-soggetto/README.md`: struttura del repository e applicazioni.
- **Test sui mutanti** (mutation-tester, sette strategie di localizzazione): i programmi di
  controllo `run_*_resume.py` contengono ancora i percorsi del PC su cui sono state fatte le
  campagne; su un altro PC vanno adattati prima dell'uso.
