# Automatic Angular E2E Testing Suite

Strumento che genera **mutanti** dell'interfaccia di applicazioni Angular (copie dei template con una sola modifica) e li usa
per misurare quanto sono fragili i locatori dei test End-to-End. Questa versione estende lo strumento di S. Liberti con un
**generatore di mutanti basato su un modello linguistico (LLM)** ed è il materiale software della tesi *Generazione di mutanti
per applicazioni web template-based mediante modelli linguistici di grandi dimensioni* (V. Di Carluccio, Università degli Studi
di Napoli Federico II, A.A. 2025–2026).

Qui sotto ci sono, **passo per passo**, le operazioni per **scaricare** il progetto, **creare i mutanti** di una delle tre
applicazioni (CineLib, FlowBoard, CookBook) ed **eseguirli** con i test. Per ogni passo si indica **chi lo fa**: l'**utente**
o il **modello**. La versione più dettagliata, con "cosa devi vedere" e gli errori comuni, è in
**[`GUIDA-RIPRODUZIONE.md`](GUIDA-RIPRODUZIONE.md)**.

**Dov'è lo strumento?** Lo strumento è il progetto Maven (`mutation-generator`, `mutation-tester`, `ext-test-classes`, ...) più il
programma di controllo (`mutanti.ps1`, `esegui_mutanti.py`); le altre cartelle sono applicazioni, dati o risultati. Dove parte, dove
finisce e quali cartelle ne fanno parte è spiegato, con uno schema, in **[`STRUMENTO.md`](STRUMENTO.md)**.

---

## Requisiti

| Cosa | Perché |
|---|---|
| **Windows 10 o 11** | gli script sono per Windows |
| **Internet** e circa **2 GB liberi** | si scaricano gli strumenti (circa 1,5 GB) e il driver di Chrome |
| **Google Chrome** | i test aprono Chrome da soli (passi 9 e 11) |
| **Claude Code** con accesso a **Claude Opus 4.8** | scrive le risposte ai prompt (passo 6); consuma i crediti dell'account |
| Un browser qualsiasi | per scaricare il progetto da GitHub |

**Non servono:** permessi di amministratore, Git, Java, Maven, Node, Python, né estensioni di Chrome. Java, Maven, Node e Python
li scarica da solo il passo 3, dentro la cartella del progetto, senza toccare il resto del computer.

## Chi fa che cosa

| Chi | Che cosa |
|---|---|
| **Utente** | scarica il progetto, apre `installa.bat` e `terminale.bat`, scrive i comandi |
| **Lo strumento** | prepara i prompt, controlla le risposte, salva i mutanti, esegue i test, mostra i risultati |
| **Modello** (Claude Opus 4.8) | **una cosa sola** (passo 6): legge un prompt e scrive la modifica dell'elemento |

Il modello non installa, non lancia comandi, non sceglie quali elementi o operatori mutare (è già scritto nel prompt) e non scrive
nel database. Tutto il resto lo fa l'utente con i comandi.

---

## I passi

> I comandi si scrivono nella finestra nera aperta con `terminale.bat`, **su una sola riga**, seguiti da Invio.
> Negli esempi c'è FlowBoard: per un'altra applicazione si scrive `cinelib` o `cookbook`.

### 1. Scaricare il progetto — *utente*
1. Apri **`https://github.com/Vinsent034/Automatic-Angular-E2E-Testing-Suite`** nel browser.
2. Clicca il pulsante verde **`<> Code`** e poi **`Download ZIP`**. Il file `Automatic-Angular-E2E-Testing-Suite-master.zip`
   finisce di solito nella cartella **Download**.

### 2. Estrarre lo ZIP — *utente*
1. In Esplora file: clic destro sullo ZIP → **`Estrai tutto…`** → **`Sfoglia…`** → scegli il **Desktop** → **`Estrai`**.
2. Entra nella cartella finché non vedi insieme `installa.bat`, `terminale.bat`, `mutanti.ps1` e `README.md`: è la **cartella principale**.

**Non avviare nulla da dentro lo ZIP** (Windows lo estrarrebbe in una cartella temporanea): `installa.bat` si ferma con un avviso.
Dopo il passo 3 **non spostare** più la cartella.

### 3. Installare gli strumenti, una volta sola — *utente avvia, lo strumento installa*
Doppio clic su **`installa.bat`**. Si apre una finestra nera: non chiuderla. Scarica Java, Maven, Node e Python portatili in una
cartella `strumenti`, compila lo strumento e prepara le tre applicazioni (fasi da `1/5` a `5/5`, **20–60 minuti**; l'ultima può
restare ferma diversi minuti). **Fine:** compare in verde `Tutto pronto.` Premi un tasto.

### 4. Aprire il terminale giusto — *utente*
Doppio clic su **`terminale.bat`**. Deve comparire in verde `Strumenti portatili attivi (Java 25, Maven, Node, Python).` e un prompt
`PS C:\...`. Tutti i comandi seguenti si scrivono **in questa finestra**.

### 5. Scegliere l'applicazione e preparare i prompt — *utente scrive, lo strumento prepara*
In questo lavoro sono state implementate solo 3 applicazioni -> cinelib, flowboard, cookbook
```powershell
.\mutanti.ps1 nome_applicazione prompt
```
Crea la cartella `generazione-nome_dlla_cartella_inserita` con un prompt per ogni elemento da mutare e il file di istruzioni per il modello. **Non crea
ancora nessun mutante.**

| Applicazione | Prompt | `Applicable` (blocchi) |
|---|---|---|
| CineLib (`cinelib`) | 171 | 1057 |
| FlowBoard (`flowboard`) | 590 | 3425 |
| CookBook (`cookbook`) | 215 | 1426 |

### 6. Far scrivere le risposte al modello — *utente avvia, modello scrive*
1. *(utente)* Apri **Claude Code**, nuova sessione, con cartella di lavoro `generazione-flowboard` (quella creata al passo 5, se si mette flowboard come aplicazione).
2. *(utente)* Imposta il modello **Claude Opus 4.8**.
3. *(utente)* Scrivi e invia: `Leggi il file ISTRUZIONI-OPUS-4.8.md in questa cartella ed eseguilo.`
   Per una **prova breve** aggiungi: `Fai solo i primi 20 blocchi.` (la generazione completa dura molto e consuma molti crediti).
4. *(modello)* Legge i prompt uno alla volta e scrive le risposte in `generazione-flowboard\output\mutations\role-ingest\`, più il
   file `_registro.txt`. *(utente)* Concedi i permessi di scrittura e aspetta che finisca.
5. *(utente)* Apri `_registro.txt`: la prima riga deve riportare `Claude Opus 4.8`.

### 7. Salvare i mutanti — *utente scrive, lo strumento salva*
```powershell
.\mutanti.ps1 flowboard completa
```
Produce gli operatori h, f e g da programma, legge le risposte del modello e salva tutto nel database. **Fine:** `Mutanti nel database: N`.

| Applicazione | h | f + g | N con **tutte** le risposte del modello |
|---|---|---|---|
| CineLib | 107 | 171 | 944 |
| FlowBoard | 176 | 302 | 1.559 |
| CookBook | 202 | 349 | 1.939 (1.929 con `.\mutanti.ps1 cookbook escludi-i`) |

h e f + g non cambiano con una prova breve; cambia solo il totale. Non rilanciare `completa` sulla stessa applicazione: lo script rifiuta.

### 8. Guardare i mutanti (facoltativo) — *utente*
```powershell
.\mutanti.ps1 flowboard esporta
```
Crea `generazione-flowboard\esporta` con un file `.mut` per mutante, apribile con il Blocco note: il template HTML con **una sola modifica**.

### 9. Eseguire i mutanti con i test — *utente scrive, lo strumento esegue*
```powershell
.\mutanti.ps1 flowboard esegui 20
```
Prova 20 mutanti (campione casuale con seme fisso, quindi sempre gli stessi) con i test delle **sette strategie** di localizzazione
(Absolute, Relative, ROBULA, ROBULA+, Selenium, Katalon, Hook-Based). Si aprono finestre di Chrome da sole: **non toccare mouse e
tastiera**. Per ogni mutante lo strumento lo inserisce nell'applicazione, aspetta la ricompilazione, esegue i test e **rimette il
template originale**. Servono circa **5–10 minuti** per 20 mutanti.

### 10. Leggere i risultati — *utente*
Alla fine compare una tabella: mutanti che **compilano** (validi) e **non compilati**, **prove sprecate** (falliscono con tutte le
strategie) e, per ogni strategia, **successi**, **fragilità** e **stabilità**. I file sono in `generazione-flowboard\output\tests\`:

| File | Contenuto |
|---|---|
| `riepilogo.txt` | la tabella finale |
| `mutanti-provati.csv` | per ogni mutante, se compila o no |
| `batches-<gruppo>.csv` | il dettaglio di ogni prova (`PASSED`, `FAILED`, `NOT_APPLICABLE`) |

Con pochi mutanti i numeri sono indicativi. Nella tesi, con tutti i mutanti, Absolute è la strategia più fragile e ROBULA+ la più
robusta fra quelle che non usano gli attributi di test.

### 11. Generatore statico e confronto (facoltativo) — *utente*
Il generatore statico non usa il modello: niente prompt e niente risposte da scrivere. Si può fare anche senza i passi 5-10.

```powershell
.\mutanti.ps1 flowboard statico             # genera i mutanti statici (cartella generazione-flowboard-statico)
.\mutanti.ps1 flowboard esegui 20 statico   # li prova con i test (Chrome: non toccare mouse e tastiera)
.\mutanti.ps1 flowboard confronta           # mette a confronto le due tecniche
```

Dopo `statico` si devono vedere 1.254 mutanti generati e 944 distinti per CineLib, 4.263 e 2.148 per FlowBoard; per CookBook la tesi
ne ha 1.503 e li ha provati tutti (rigenerandoli ne escono 1.504). I dettagli sono al passo 11 di [`GUIDA-RIPRODUZIONE.md`](GUIDA-RIPRODUZIONE.md).

### 12. Rifare da zero o cambiare applicazione — *utente*
Per un'altra applicazione si ripete dal passo 5 con un altro nome. Per rifare la stessa si **cancella la cartella**
`generazione-flowboard` (e `generazione-flowboard-statico`) e si riparte dal passo 5.

---

## Se qualcosa non va
La tabella degli errori con i messaggi esatti e i rimedi è in fondo a [`GUIDA-RIPRODUZIONE.md`](GUIDA-RIPRODUZIONE.md).

## Risultati della tesi
Su tutte e tre le applicazioni il generatore con LLM ha prodotto una quota di mutanti validi più alta di quello statico
(88,0–95,6% contro 72,9–82,3%, cioè da 11,7 a 18,1 punti percentuali in più) e molte meno prove sprecate (1,3–2,8% contro
10,2–24,1%). Gli **8.978 mutanti** delle sei campagne sono consultabili in [`mutazioni-generate/`](mutazioni-generate).

## Contenuto del repository

Ogni cartella dice nel proprio `README.md` se **fa parte dello strumento** o se contiene **dati**. Lo schema completo, con dove
parte e dove finisce lo strumento, è in [`STRUMENTO.md`](STRUMENTO.md).

### LO STRUMENTO (codice)

| Cartella o file | Ruolo |
|---|---|
| [`mutation-generator/`](mutation-generator) | **generazione dei mutanti**: `llm-generator` (con modello linguistico), `static-generator` (regole fisse), `common` (servizi comuni) |
| [`mutation-tester/`](mutation-tester) | **esecuzione dei test** sui mutanti: inserisce il mutante, controlla la ricompilazione, lancia i test, ripristina |
| [`ext-test-classes/`](ext-test-classes), [`vintage-fix/`](vintage-fix) | classi di base dei test (con la preparazione di Chrome) e impostazione di JUnit |
| [`custom-locators/`](custom-locators), [`hook-injector/`](hook-injector) | moduli ereditati dallo strumento originale (locatori ROBULA e ROBULA+; attributi di test, non usato) |
| `pom.xml` | il progetto Maven che compila tutti i moduli |
| `mutanti.ps1`, `esegui_mutanti.py`, `conta_mutanti.py`, `escludi_i_su_bersaglio.py`, `deduplica_statici.py`, `confronta_tecniche.py` | **programma di controllo**: lancia generazione ed esecuzione e mostra i risultati (passi 5-11) |
| `installa.bat`, `installa.ps1`, `terminale.bat`, `ambiente.ps1`, `configura_percorsi.ps1` | installazione e terminale (passi 3-4) |

### INGRESSO (descrive un'applicazione allo strumento)

| Cartella o file | Contenuto |
|---|---|
| [`applicazioni-soggetto/`](applicazioni-soggetto) | le tre applicazioni Angular (CineLib, FlowBoard, CookBook) |
| `generator-config-*.json`, `role-targets*.json`, `mutation-types*.json`, `generator-config.json` | configurazione di ogni applicazione e definizione degli 11 operatori |
| `ISTRUZIONI-MODELLO.template.md`, `CLAUDE.md` | istruzioni per il modello e preparazione di un'applicazione nuova |

### USCITA (creata quando si usa lo strumento)

| Cartella | Contenuto |
|---|---|
| `generazione-<applicazione>/`, `generazione-<applicazione>-statico/` | prompt, risposte, database `mutations.db` ed esiti di **una** generazione (la crea `mutanti.ps1`; non è versionata) |

### DATI E RISULTATI DELLA TESI (non sono lo strumento)

| Cartella | Contenuto |
|---|---|
| [`mutazioni-generate/`](mutazioni-generate) | gli 8.978 mutanti delle sei campagne, in forma leggibile |
| `cinelib-v2-run/`, `flowboard-v2-run/`, `cookbook-run/` | campagne con il modello: prompt, risposte, test compilati ed esiti |
| `cinelib-v2-static-run/`, `flowboard-v2-static-run/`, `cookbook-static-run/`, `cookbook-i-bersaglio-run/` | campagne con il generatore statico e i 10 mutanti dell'operatore i sul bersaglio di CookBook |
| `cinelib-v2/`, `flowboard-v2/` | i locatori delle sette strategie e lo script che genera le classi di test |
| [`analisi-risultati/`](analisi-risultati) | script che calcolano le misure del Capitolo 4 |
| [`programmi-di-controllo/`](programmi-di-controllo) | i programmi con cui sono state eseguite le campagne complete della tesi (percorsi del PC originale) |
| [`test-suite/`](test-suite) | test del lavoro precedente su Angular-Spotify |

### GUIDE

[`GUIDA-RIPRODUZIONE.md`](GUIDA-RIPRODUZIONE.md) (passi dettagliati), [`STRUMENTO.md`](STRUMENTO.md) (che cos'è lo strumento),
[`RIPRODURRE-LE-CAMPAGNE.md`](RIPRODURRE-LE-CAMPAGNE.md) (campagne complete della tesi),
[`README-STRUMENTO-ORIGINALE.md`](README-STRUMENTO-ORIGINALE.md) (strumento originale), [`archivio/`](archivio) (materiale di lavoro, solo storico).
