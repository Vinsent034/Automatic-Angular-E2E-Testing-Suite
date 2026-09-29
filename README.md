# Automatic Angular E2E Testing Suite
Uno strumento di automazione per creare e collaudare mutazioni dei file HTML di progetti Angular. Semplifica il testing End-to-End (E2E): genera mutazioni mirate e le verifica eseguendo i test dell'applicazione.

---

## Questa versione del progetto

Questo repository estende lo strumento originale sviluppato da S. Liberti con un **secondo
generatore di mutanti basato su modelli linguistici di grandi dimensioni (LLM)** e con le
campagne sperimentali che lo valutano. È il materiale software della tesi *Generazione di
mutanti per applicazioni web template-based mediante modelli linguistici di grandi dimensioni*
(V. Di Carluccio, Università degli Studi di Napoli Federico II, A.A. 2025–2026).

**Che cosa è stato aggiunto**

- **`llm-generator`** — generazione dei mutanti affidata a un modello linguistico e legata allo
  schema di 11 operatori di mutazione × 5 ruoli strutturali, così che ogni mutante appartenga a
  una categoria precisa. Modalità: API diretta, scrittura dei prompt (dump), acquisizione delle
  risposte (ingest) e simulazione (dry run). Gli operatori f, g e h, che sono semplici
  spostamenti, sono generati da programma. I duplicati vengono scartati durante l'acquisizione.
- **`MutationPorter`** (in `common`) — esportazione e reimportazione dei mutanti come file di
  testo leggibili, per consultarli e modificarli fuori dal database.
- **[`applicazioni-soggetto/`](applicazioni-soggetto)** — le tre applicazioni Angular create per
  l'esperimento (CineLib, FlowBoard, CookBook), progettate perché si possano usare tutti gli
  undici operatori e tutti e cinque i ruoli.
- **[`mutazioni-generate/`](mutazioni-generate)** — gli **8 978 mutanti** delle sei campagne,
  esportati in forma leggibile e consultabili direttamente qui.
- **[`RIPRODURRE-LE-CAMPAGNE.md`](RIPRODURRE-LE-CAMPAGNE.md)** — guida passo passo per rigenerare
  i mutanti, rieseguire i test con le sette strategie di localizzazione e ricalcolare le misure.
  I prompt e le risposte del modello usati nella tesi sono conservati nelle cartelle delle
  campagne, quindi i mutanti linguistici si possono ricostruire senza interrogare di nuovo il
  modello.

**Risultato principale** — su tutte e tre le applicazioni il generatore linguistico ha prodotto
una quota di mutanti validi più alta di quello statico (88,0–95,6% contro 72,9–82,3%, cioè da
11,7 a 18,1 punti percentuali in più) e molte meno esecuzioni che non danno informazioni
(1,3–2,8% contro 10,2–24,1%). La classifica di robustezza è stabile agli estremi: i locatori
assoluti sono sempre i più fragili, ROBULA+ il più robusto fra le strategie che non usano gli
attributi di test.

| Campagna | Applicazione | Tecnica | Mutanti | Validi | Cartella della campagna |
|---|---|---|---|---|---|
| [`cinelib-llm`](mutazioni-generate/cinelib-llm) | CineLib | linguistica | 944 | 831 | `cinelib-v2-run/` |
| [`cinelib-static`](mutazioni-generate/cinelib-static) | CineLib | statica | 944 | 688 | `cinelib-v2-static-run/` |
| [`flowboard-llm`](mutazioni-generate/flowboard-llm) | FlowBoard | linguistica | 1 559 | 1 491 | `flowboard-v2-run/` |
| [`flowboard-static`](mutazioni-generate/flowboard-static) | FlowBoard | statica | 2 148 | 1 664 | `flowboard-v2-static-run/` |
| [`cookbook-llm`](mutazioni-generate/cookbook-llm) | CookBook | linguistica | 1 929 | 1 813 | `cookbook-run/` |
| [`cookbook-static`](mutazioni-generate/cookbook-static) | CookBook | statica | 1 454 | 1 197 | `cookbook-static-run/` |

Per la tecnica statica sono contati solo i mutanti distinti (vedi
`mutazioni-generate/README.md`). Le cartelle `cinelib-static-run/`, `flowboard-run/`,
`flowboard-static-run/` e `output/` contengono campagne precedenti (luglio 2026, versione
precedente delle applicazioni): sono conservate solo come storico e non sono usate nei
risultati della tesi.

La documentazione originale che segue vale ancora per le parti comuni dello strumento.

---

## Struttura del progetto
Il progetto è diviso in moduli:
```txt
(radice)
├── 📁 custom-locators       // Modulo per creare i locatori che Katalon/Selenium non forniscono
├── 📁 hook-injector         // Modulo per aggiungere attributi (hook) all'applicazione sotto test
├── 📁 mutation-generator
|   ├── 📁 common            // Logica e servizi comuni
|   ├── 📁 llm-generator     // Generatore di mutazioni tramite prompt a un LLM
|   └── 📁 static-generator  // Generatore di mutazioni con regole statiche
├── 📁 mutation-tester       // Modulo che esegue i test automatici sulle mutazioni generate
├── 📁 applicazioni-soggetto // Le tre applicazioni soggetto (CineLib, FlowBoard, CookBook)
├── 📁 mutazioni-generate    // Gli 8 978 mutanti delle sei campagne, in forma leggibile
├── generator-config.json    // File di configurazione principale
└── pom.xml                  // Modulo Maven principale
```

## Per iniziare
### Prerequisiti
- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/)
- [Maven](https://maven.apache.org/download.cgi)
- Progetto da testare: un progetto front-end basato su [Angular](https://angular.dev/).

### Release
Lo strumento si può usare compilandolo dai sorgenti oppure con i file già compilati.
Per saltare la compilazione si possono scaricare i file .jar pronti dalla sezione Releases di questo repository. Dopo averli scaricati, si mettono i file `.jar` nella radice del progetto e si passa direttamente a configurazione ed esecuzione.

### Configurazione
Per cominciare si configura il file `generator-config.json`.

**Parametri di configurazione**
- `seed`: (facoltativo) seme usato per inizializzare il [RandomSelector](https://github.com/sim-liberti/Automatic-Angular-E2E-Testing-Suite/blob/master/mutation-generator/common/src/main/java/org/unina/util/RandomSelector.java), così che i risultati siano riproducibili. Lasciarlo vuoto per un'esecuzione casuale.
- `repositoryRootPath`: percorso assoluto del progetto Angular da mutare.
- `npmRunCommand`: comando con cui si avvia l'applicazione Angular (per esempio `npm run dev`).
- `mutations`: elenco di oggetti che definiscono le regole di mutazione.
  - `name`: nome della mutazione.
  - `file_path`: percorso assoluto del file che contiene il tag da mutare.
  - `target_matcher`: oggetto che serve a trovare il tag da mutare nel file indicato sopra.
    - `type`: tipo di ricerca da usare: `class`, `text`, `id` oppure `attribute`.
    - `key`: nome dell'attributo dell'elemento bersaglio. Serve solo con il tipo `attribute`.
    - `value`: valore della classe, del testo, dell'id o dell'attributo dell'elemento bersaglio.

> **NOTA:** tutti i comandi di questa guida vanno eseguiti dalla radice del progetto, indicata come (radice).

### Generare le mutazioni
Con la configurazione pronta, si compila il modulo generatore e poi lo si esegue. Chi ha scaricato il file .jar già compilato passa direttamente al passo 2.

**Passo 1: compilare il modulo**

Il comando Maven seguente compila solo il modulo static-generator e le sue dipendenze:
```bash
mvn clean install -pl :static-generator -am
```
Al termine, il file compilato si trova in `(radice)/mutation-generator/static-generator/target/static-generator-1.0.0-jar-with-dependencies.jar`. Copiarlo nella radice del progetto.

**Passo 2: eseguire il generatore**

Dopo averlo scaricato o compilato, si esegue il file .jar con:
```bash
java -jar static-generator.jar
```
_Nota: se il file è stato compilato, usare `static-generator-1.0.0-jar-with-dependencies.jar`._

Nella radice del progetto viene creato un file `mutations.db`. È il database che conserva tutte le mutazioni, con nome, tipo, identificativo e percorso del file coinvolto.

Per il generatore linguistico (dump, risposte del modello, operatori f/g/h e acquisizione) vedi [`RIPRODURRE-LE-CAMPAGNE.md`](RIPRODURRE-LE-CAMPAGNE.md).

### Collaudare l'applicazione
Con la configurazione e le mutazioni pronte, si compila il modulo che esegue i test. Chi ha scaricato il file .jar già compilato passa direttamente al passo 2.

**Requisiti:**
- **Compilazione:** tutte le classi di test, comprese le classi base e le dipendenze, devono essere compilate;
- **Framework:** i test devono essere scritti con **JUnit**;
- **Dipendenze:** tutte le classi necessarie devono essere presenti nel classpath.

**Passo 1: compilare il modulo**

Il comando Maven seguente compila solo il modulo mutation-tester e le sue dipendenze:
```bash
mvn clean install -pl :mutation-tester -am
```
Al termine, il file compilato si trova in `(radice)/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar`. Copiarlo nella radice del progetto.

**Passo 2: eseguire il tester**

Dopo averlo scaricato o compilato, si esegue il file .jar con:
```bash
java -jar mutation-tester.jar -td "percorso/delle/classi/di/test/compilate"
```
_Nota: se il file è stato compilato, usare `mutation-tester-1.0.0-jar-with-dependencies.jar`._

**Risultati dei test**

Al termine, nella cartella di output vengono creati due file:
- `stats.csv`: risultati raggruppati per classe di test (fragilità, obsolescenza e test saltati);
- `batches.csv`: registro dettagliato di ogni esecuzione, con esiti e messaggi di errore.

### Moduli secondari
Per l'uso avanzato dei locatori personalizzati o dell'aggiunta degli hook, vedi la documentazione dei singoli moduli:
- [Documentazione di Custom Locators](custom-locators/README.md)
- [Documentazione di Hook Injector](hook-injector/README.md)

## Test sull'applicazione Angular-Spotify (lavoro precedente)
Per configurare l'applicazione [Angular-Spotify](https://github.com/trungvose/angular-spotify) e ripetere i risultati del lavoro precedente contenuti nella cartella test-suite, vedi il relativo [file readme](test-suite/AnuglarSpotifyTests.md).
