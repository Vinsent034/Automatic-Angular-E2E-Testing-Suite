# Guida per l'assistente (Claude Code)

Questo repository contiene lo strumento della tesi di Vincenzo Di Carluccio (Federico II,
relatore Prof. Porfirio Tramontana): genera **mutanti dell'interfaccia** di applicazioni Angular,
cioè copie dei template con una sola modifica, con due tecniche:

- **statica**: regole fisse applicate all'albero HTML (`mutation-generator/static-generator`);
- **linguistica** (con LLM): un modello linguistico scrive la modifica, lo strumento decide dove e quale
  (`mutation-generator/llm-generator`).

Lo schema di mutazione ha **11 operatori** (a–k, definiti in `mutation-types-modello.json`) e
**5 ruoli** rispetto all'elemento bersaglio: α bersaglio, β padre, γ antenato, δ fratello,
ε componente contenitore. Gli operatori a, b, c, d, e, i, j, k li scrive il modello; f, g, h
(spostamenti) li produce lo strumento da programma.

---

## Chi fa che cosa (leggere per primo)

| Chi | Compito |
|---|---|
| **L'utente** | scarica il repository, lancia `installa.bat`, apre `terminale.bat` e lancia i comandi `mutanti.ps1` |
| **Lo strumento** | prepara i prompt, controlla le risposte, produce f/g/h, salva i mutanti |
| **Tu (il modello)** | **solo scrivere le risposte ai prompt**, uno per uno |

La procedura completa, passo per passo, è in **[`GUIDA-RIPRODUZIONE.md`](GUIDA-RIPRODUZIONE.md)**.

### Se la tua cartella di lavoro contiene `ISTRUZIONI-OPUS-4.8.md`
(di solito `generazione-<applicazione>/`, e l'utente ti scrive «Leggi il file ISTRUZIONI-OPUS-4.8.md in questa
cartella ed eseguilo»)

Il tuo compito è **solo quello**: leggi il file e scrivi le risposte come descrive. Non installare niente, non lanciare
`mutanti.ps1`, non compilare, non modificare altri file del progetto. Il file stesso dice di fermarti e avvisare se il modello
della sessione non è Claude Opus 4.8: rispettalo.

### Se l'utente ti chiede aiuto per preparare o capire la procedura
Spiega i passi di `GUIDA-RIPRODUZIONE.md` e lascia che sia l'utente a lanciare i comandi. Li lanci tu solo se te lo
chiede esplicitamente, e riferisci l'esito di ogni passo (i numeri attesi sono nella guida).

---

## Regole da rispettare sempre

1. **Non modificare** le cartelle delle campagne della tesi (`cinelib-v2-run`, `cinelib-v2-static-run`,
   `flowboard-v2-run`, `flowboard-v2-static-run`, `cookbook-run`, `cookbook-static-run` e simili) né i
   loro database. Ogni nuova generazione sta in una cartella nuova: `mutanti.ps1` usa `generazione-<applicazione>`.
2. **Chiedi prima di modificare** i template di un'applicazione (aggiunta degli attributi `x-test`) e il codice dello
   strumento (mappa degli spostamenti h). Spiega cosa cambi e perché.
3. **Non fare commit né push** su Git, se l'utente non lo chiede esplicitamente.
4. **Non usare sotto-agenti** per scrivere le risposte ai prompt: girerebbero con un modello diverso da quello dichiarato.
5. I comandi sono per **Windows PowerShell**, dal terminale aperto con `terminale.bat` (Java 25 portatile): il `java`
   di sistema potrebbe essere di un'altra versione e non funzionare.

---

## Preparare un'applicazione nuova (solo su richiesta)

Per le tre applicazioni della tesi (CineLib, FlowBoard, CookBook) non serve niente di questo: sono già pronte.
Per un'applicazione tua, **chiedi all'utente il percorso della cartella** (quella che contiene `src/app`): deve essere
un'applicazione Angular con i template in file `*.component.html`. Nei file JSON i percorsi sono **assoluti** con le
barre `/` (es. `C:/Users/nome/progetti/miaapp`).

### Gli attributi di test

Lo strumento riconosce ogni bersaglio da un **attributo di test** presente nel template, per esempio
`<button class="btn" x-test-search>Cerca</button>`. Cerca nei template gli attributi che iniziano con `x-test`:

- se ci sono, usa quelli;
- se mancano, **proponi all'utente** quali elementi marcare (quelli su cui agirebbe o controllerebbe un test:
  campi, bottoni, titoli, conteggi) e, **solo dopo il suo consenso**, aggiungi l'attributo nel template (un nome diverso
  per ogni elemento). Se il valore è un binding dinamico, nel file dei bersagli si scrive nella forma `[attr.x-test-nome]`.

Il modello ha la regola di non toccare mai questi attributi: servono anche alla strategia Hook-Based e a ritrovare l'elemento.

### Il file dei bersagli: `role-targets-<app>.json` (nella cartella principale)

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
| `app` | nome breve dell'applicazione, in minuscolo; lo stesso usato nella mappa h |
| `appRoot` | cartella dell'applicazione (quella che contiene `src/app`) |
| `id` | nome del bersaglio, univoco; compare nei nomi dei prompt |
| `targetAttr` | l'attributo di test che identifica l'elemento nel template |
| `componentHtml` | percorso assoluto del template che contiene l'elemento |
| `tests` | solo informativo |

Controllo: per ogni bersaglio il file `componentHtml` esiste e contiene `targetAttr`.

### La configurazione: `generator-config-<app>.json` (nella cartella principale)

```json
{
  "seed": "1234",
  "repositoryRootPath": "C:/percorso/della/app",
  "npmRunCommand": "npm start -- --port 4600",
  "mutations": []
}
```

`seed` fissa le scelte casuali; `npmRunCommand` serve solo ai test (porta libera a scelta); `mutations` resta vuoto per il
generatore linguistico.

### La mappa degli spostamenti h (modifica del codice: chiedi prima)

L'operatore h sposta un nodo **da un template a un altro**. Le coppie sorgente → destinazione sono scritte in
`mutation-generator/llm-generator/src/main/java/org/unina/App.java`, metodo `runAutoH`, con un ramo per ogni applicazione
(`flowboard`, `cookbook`; il ramo finale `else` è quello di CineLib). Per un'app nuova:

1. aggiungi un ramo `else if ("<app>".equalsIgnoreCase(tf.app)) { ... }` **prima** dell'`else`, con una riga
   `destMap.put("<template>.component.html", base + "<cartella>/<template>.component.html");` per ogni template che
   contiene bersagli; come destinazione scegli un componente collegato (padre ↔ figlio, oppure `app.component.html` ↔
   intestazione), come negli altri rami;
2. ricompila dalla cartella principale: `mvn package -DskipTests` (terminale aperto con `terminale.bat`).

Senza questo ramo l'app nuova userebbe la mappa di CineLib: gli h vengono saltati oppure il generatore si ferma perché non
trova i template di destinazione. In alternativa, con il consenso dell'utente, si può saltare l'operatore h e dichiarare
che non è stato generato.

### Il file di istruzioni per il modello

Per le applicazioni della tesi `mutanti.ps1 <app> prompt` lo copia da solo. Per una applicazione nuova: copia
`ISTRUZIONI-MODELLO.template.md` (cartella principale) in `generazione-<app>/` con il nome `ISTRUZIONI-MODELLO.md` e
sostituisci i segnaposto `<APP>`, `<N_PROMPT>`, `<N_BLOCCHI>` (dalla riga `Applicable` del passo `prompt`) ed `<ESEMPIO>`
(il nome di uno dei prompt).

### Configurazione per il generatore statico (facoltativa)

Per i mutanti statici crea `generator-config-<app>-static.json` uguale a quello sopra ma con un elemento in `mutations`
per ogni bersaglio:

```json
{ "name": "search-input",
  "file_path": "C:/percorso/della/app/src/app/search/search.component.html",
  "target_matcher": { "type": "attribute", "key": "x-test-search", "value": "" } }
```

Poi, in una cartella nuova, con `terminale.bat`:
`& $JAVA -jar ..\mutation-generator\static-generator\target\static-generator-1.0.0-jar-with-dependencies.jar ..\generator-config-<app>-static.json`.
Il generatore statico usa il seme fisso e produce sempre gli stessi mutanti, duplicati compresi.

---

## Per approfondire

- `GUIDA-RIPRODUZIONE.md`: la procedura completa per l'utente.
- `RIPRODURRE-LE-CAMPAGNE.md`: come rifare le campagne della tesi, test compresi.
- `README.md` e `applicazioni-soggetto/README.md`: struttura del repository e applicazioni.
- I programmi di controllo dei test (`programmi-di-controllo/run_*_resume.py`) contengono ancora i percorsi del PC su cui sono state fatte le
  campagne: su un altro PC vanno adattati prima dell'uso.
