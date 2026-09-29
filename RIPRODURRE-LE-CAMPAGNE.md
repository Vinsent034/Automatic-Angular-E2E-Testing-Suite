# Come rifare le campagne della tesi

Questa guida spiega come ripetere, passo per passo, le campagne del Capitolo 4 della tesi:
generazione dei mutanti (statica e linguistica), esecuzione dei test con le sette strategie e
calcolo delle misure. I comandi sono per **Windows PowerShell**, lanciati dalla radice del
progetto salvo dove indicato.

## 0. Prerequisiti

- JDK 25 (negli esperimenti: Eclipse Adoptium 25.0.2) e Maven;
- Node.js e npm, per le applicazioni Angular;
- Google Chrome, **chiuso** durante le campagne di test;
- Python 3, per i programmi di controllo e di analisi.

Compilare lo strumento:

```powershell
mvn -f mutation-generator/pom.xml package
mvn -f mutation-tester/pom.xml package
```

Preparare le applicazioni ([`applicazioni-soggetto/`](applicazioni-soggetto)): `npm install`
in ciascuna, e correggere i percorsi assoluti nei file `generator-config-*.json` e
`role-targets*.json` se il progetto è in un'altra posizione.

Nel terminale si definiscono una volta due variabili (negli esperimenti il jar era una copia di
nome `…-eps.jar`, con lo stesso contenuto):

```powershell
$JAVA = "C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe"
$LLMJAR = "..\mutation-generator\llm-generator\target\llm-generator-1.0.0-jar-with-dependencies.jar"
```

## 1. File di ogni campagna

| Campagna | Cartella | Bersagli | Configurazione |
|---|---|---|---|
| CineLib linguistica | `cinelib-v2-run/` | `role-targets.json` | `generator-config-cinelib.json` |
| CineLib statica | `cinelib-v2-static-run/` | — | `generator-config-cinelib-static.json` |
| FlowBoard linguistica | `flowboard-v2-run/` | `role-targets-flowboard.json` | `generator-config-flowboard.json` |
| FlowBoard statica | `flowboard-v2-static-run/` | — | `generator-config-flowboard-static.json` |
| CookBook linguistica | `cookbook-run/` | `role-targets-cookbook.json` | `generator-config-cookbook.json` |
| CookBook statica | `cookbook-static-run/` | — | `generator-config-cookbook-static.json` |

Le definizioni degli undici operatori per il generatore linguistico sono in
`mutation-types-modello.json` (uguale per tutte le applicazioni).

Ogni cartella di campagna contiene: `output/mutations/role-prompts/` (i prompt),
`output/mutations/role-ingest/` (le risposte del modello), `role-mutations-matrix.csv` (una riga
per ogni combinazione bersaglio × ruolo × operatore, con l'esito e il motivo),
`output/tests/batches-*.csv` (gli esiti dei test) e i log. Il database `mutations.db` non è
nel repository: si ricostruisce con i passi seguenti.

## 2. Generazione linguistica

Ogni passo si esegue **dentro la cartella della campagna** (esempio: CineLib).

```powershell
cd cinelib-v2-run
$env:LLM_ROLE="1"
$env:LLM_ROLE_TARGETS="..\role-targets.json"
$env:LLM_MUTATION_TYPES="..\mutation-types-modello.json"
```

**2a. Prompt (dump).** Lo strumento scrive un prompt per ogni bersaglio e ruolo in
`output/mutations/role-prompts/` e crea `mutations.db`.

```powershell
$env:LLM_ROLE_DUMP="1"
& $JAVA -jar $LLMJAR ..\generator-config-cinelib.json
Remove-Item Env:LLM_ROLE_DUMP
```

L'ultima riga (`DONE … | Applicable: N`) dà il numero di blocchi da chiedere al modello.

**2b. Risposte del modello.** In una sessione di **Claude Code** impostata su **Claude Opus 4.8**,
con la cartella della campagna come cartella di lavoro, si invia:

```
Leggi il file ISTRUZIONI-OPUS-4.8.md in questa cartella ed eseguilo.
```

Il file di istruzioni è lo stesso per le tre applicazioni: per una campagna nuova si copia da
una cartella esistente e si cambiano il nome dell'applicazione, il numero di prompt e il totale
dei blocchi. Il modello scrive una risposta per ogni prompt in `output/mutations/role-ingest/`
e un registro `_registro.txt` con il modello usato e i blocchi scritti.

**Per ottenere di nuovo i mutanti della tesi non serve rifare questo passo**: le risposte usate
sono già in `role-ingest/`, e i passi seguenti le ritrasformano negli stessi mutanti.

**2c. Operatori f, g e h.** Sono spostamenti meccanici, generati dallo strumento senza modello:

```powershell
$env:LLM_H_AUTO="1";  & $JAVA -jar $LLMJAR ..\generator-config-cinelib.json; Remove-Item Env:LLM_H_AUTO
$env:LLM_FG_AUTO="1"; & $JAVA -jar $LLMJAR ..\generator-config-cinelib.json; Remove-Item Env:LLM_FG_AUTO
```

**2d. Acquisizione (ingest).** Lo strumento controlla le risposte e salva i mutanti validi;
scarta i blocchi mancanti, quelli che toccano un attributo `x-test`, quelli che non cambiano
nulla e i duplicati, e scrive il motivo nella matrice.

```powershell
$env:LLM_ROLE_INGEST_DIR="output/mutations/role-ingest"
$env:LLM_MODEL="claude-opus-4-8"
& $JAVA -jar $LLMJAR ..\generator-config-cinelib.json
```

Controllo: il numero di mutanti nel database deve coincidere con la Tabella 4.1 della tesi
(944 per CineLib, 1 559 per FlowBoard, 1 929 per CookBook).

## 3. Generazione statica

Dalla cartella della campagna statica:

```powershell
cd cinelib-v2-static-run
& $JAVA -jar ..\mutation-generator\static-generator\target\static-generator-1.0.0-jar-with-dependencies.jar ..\generator-config-cinelib-static.json
```

Il generatore statico usa il seme fisso `1234`, quindi produce sempre gli stessi mutanti,
duplicati compresi. Negli esperimenti su CineLib e FlowBoard i duplicati sono stati tolti prima
dei test (la mappa è in `mappa-duplicati.json`); su CookBook sono stati provati tutti.

## 4. Test sui mutanti

Chiudere Chrome, poi fare doppio clic su (o lanciare dalla radice):

| Applicazione | File da avviare | Registro |
|---|---|---|
| CineLib | `avvia_cinelib_v2.bat` | `cinelib-v2.log` |
| FlowBoard | `avvia_flowboard_v2.bat` | `flowboard-v2.log` |
| CookBook | `avvia_cookbook.bat` | `cookbook.log` |

Ogni file lancia le due campagne (linguistica e statica). Per ogni mutante il programma inserisce
il template mutato nell'applicazione, aspetta la ricompilazione, esegue i test dello scenario
con le sette strategie e rimette il template originale. Se si interrompe, rilanciandolo
riprende dal gruppo non finito. Gli esiti vanno in `output/tests/batches-*.csv`.

## 5. Misure

```powershell
python numeri_capitolo4.py
python analizza_blocco_a.py --revisione
```

Il primo scrive `numeri-capitolo4.json` con tutte le misure del Capitolo 4 (validità per
operatore e ruolo, tassi di stabilità e di obsolescenza); il secondo produce il report di
confronto fra le tre applicazioni.
