# Guida: generare i mutanti partendo da questo repository

Questa guida porta da zero (repository appena scaricato) ai mutanti nel database. È stata provata
passo per passo su Windows. Vale per le tre applicazioni della tesi; negli esempi c'è CineLib,
per le altre basta cambiare il nome dell'applicazione nei comandi.

## Chi fa che cosa

| Chi | Che cosa |
|---|---|
| **Tu** (la persona) | scarichi il repository, lanci l'installazione e i comandi di questa guida, scegli l'applicazione |
| **Lo strumento** (programma Java) | prepara i prompt, trova gli elementi da mutare, controlla le risposte, produce gli operatori f, g, h e salva i mutanti |
| **Il modello linguistico** (Claude Code, Claude Opus 4.8) | **una sola cosa**: scrive la risposta a ciascun prompt, cioè la modifica dell'elemento per gli operatori a, b, c, d, e, i, j, k |

Il modello non installa niente, non lancia comandi dello strumento e non tocca i file
dell'applicazione. Tutto il resto lo fanno la persona e lo strumento.

## Che cosa serve

- Windows con connessione a internet (circa 1,5 GB di spazio: strumenti e dipendenze).
- Per il passo 4: Claude Code, con accesso a **Claude Opus 4.8**.
- Google Chrome serve solo per eseguire i test sui mutanti (non per generarli).

Java, Maven, Node e Python **non vanno installati a mano**: li scarica il passo 2.

## Le tre applicazioni

Si sceglie con il nome nei comandi (`cinelib`, `flowboard`, `cookbook`). Valori attesi, quando il modello ha
risposto a tutti i prompt:

| Applicazione | Nome nel comando | Prompt | Blocchi da scrivere (`Applicable`) | Mutanti nel database |
|---|---|---|---|---|
| CineLib | `cinelib` | 171 | 1.057 | 944 |
| FlowBoard | `flowboard` | 590 | 3.425 | 1.559 |
| CookBook | `cookbook` | 215 | 1.426 | 1.939 (1.929 dopo il passo 6) |

## Passo 1 — Scaricare il repository

Con `git clone https://github.com/Vinsent034/Automatic-Angular-E2E-Testing-Suite.git`
oppure come ZIP da GitHub (poi estrarlo). Da qui in poi la "cartella principale" è quella che
contiene `installa.bat` e `README.md`.

## Passo 2 — Installare gli strumenti (una volta sola)

Doppio clic su **`installa.bat`**. Si apre una finestra nera che scarica gli strumenti nella
cartella `strumenti/` (non installa nulla nel sistema), compila lo strumento e prepara le tre
applicazioni. Ci vogliono alcune decine di minuti; non chiudere la finestra.

**Controllo:** alla fine compare `Tutto pronto.` Premi un tasto per chiuderla.

## Passo 3 — Preparare i prompt

Doppio clic su **`terminale.bat`** (apre una finestra con gli strumenti pronti). Poi scrivi, con
l'applicazione scelta:

```powershell
.\mutanti.ps1 cinelib prompt
```

Lo strumento scrive un prompt per ogni elemento da mutare e per ogni ruolo. **Non crea ancora
nessun mutante.** Crea la cartella `generazione-cinelib/` (è nuova: non tocca le cartelle delle campagne della
tesi) e copia al suo interno il file di istruzioni per il modello.

**Controllo:** `Fatto: 171 prompt …` e, sopra, `Applicable: 1057` (sono i blocchi che il
modello dovrà scrivere). I numeri attesi per ogni applicazione sono nella tabella qui sopra e lo script li ricorda.

## Passo 4 — Le risposte del modello

Questo è l'unico passo del modello.

1. Apri **Claude Code** con `generazione-cinelib` come cartella di lavoro (quella creata al passo 3).
2. Imposta il modello su **Claude Opus 4.8**.
3. Scrivi:

   ```
   Leggi il file ISTRUZIONI-OPUS-4.8.md in questa cartella ed eseguilo.
   ```

Il modello scrive un file di risposta per ogni prompt in `generazione-cinelib/output/mutations/role-ingest/`
e un registro `_registro.txt` con il nome del modello. Completo richiede molto tempo (un prompt alla volta). Per una
prova veloce si può chiedere al modello di fare solo i primi 10 prompt: i mutanti saranno meno e lo strumento segnerà
gli altri come "blocco mancante".

**Controllo:** apri `_registro.txt`: deve riportare `Claude Opus 4.8` e il totale dei blocchi.

## Passo 5 — Completare e salvare i mutanti

Torna al terminale del passo 3 (o riaprilo con `terminale.bat`) e scrivi:

```powershell
.\mutanti.ps1 cinelib completa
```

Lo strumento, in ordine: produce gli operatori h (spostamenti fra file) e f, g (spostamenti
nello stesso file); poi legge le risposte del modello, rimette ogni modifica nel suo file e
scarta i blocchi mancanti, quelli che toccano un attributo `x-test`, quelli che non cambiano
nulla e i duplicati.

**Controllo con tutte le risposte (CineLib, 171 file):**

| Riga | Valore atteso |
|---|---|
| `DONE (h auto)` | `Saved (+resumed): 107` |
| `DONE (f/g auto)` | `Saved (+resumed): 171` |
| `DONE (role 11x5)` | `Saved: 666 \| Rejected: 391` |

Totale nel database: 107 + 171 + 666 = **944 mutanti** (come nella tesi, Tabella 4.1). Lo script lo scrive alla fine
(`Mutanti nel database: 944`). I mutanti sono in `generazione-cinelib/mutations.db`.

**Non rilanciare `completa`** sulla stessa generazione: aggiungerebbe mutanti doppi, e lo script lo rifiuta. Per rifare
da zero si cancella la cartella `generazione-cinelib` e si riparte dal passo 3.

## Passo 6 — Solo per CookBook: gli stessi mutanti della tesi (facoltativo)

Nella tesi 10 mutanti dell'operatore i applicato al bersaglio sono stati tolti dalla campagna principale di CookBook
(§ 4.1). Per avere lo stesso insieme (1.929):

```powershell
.\mutanti.ps1 cookbook escludi-i
```

## Passo 7 — Guardare i mutanti (facoltativo)

```powershell
.\mutanti.ps1 cinelib esporta
```

Crea la cartella `generazione-cinelib/esporta/` con un file `.mut` per mutante, leggibile con qualsiasi
editor: il template HTML intero con una sola modifica. Per vederla, confrontalo con l'originale
in `applicazioni-soggetto/`.

## Se qualcosa non va

| Sintomo | Causa e rimedio |
|---|---|
| `Manca la cartella strumenti` o `Manca lo strumento compilato` | non hai fatto il passo 2 |
| `Non ci sono risposte del modello` | non hai fatto il passo 4 (o `role-ingest` è vuota) |
| `Nel database ci sono già N mutanti` | hai già fatto `completa`: cancella `generazione-<app>` per rifare da zero |
| `Saved: 0` all'acquisizione | le risposte non sono nella cartella giusta: devono stare in `generazione-<app>/output/mutations/role-ingest/` |
| Numeri diversi dalla tabella | normale se il modello è un altro o se hai fatto solo una parte dei prompt |

Ogni fase di `mutanti.ps1` parte da impostazioni pulite: non dipende da ciò che è rimasto nella finestra.

## Un'applicazione tua

Servono gli attributi `x-test`, il file dei bersagli e la configurazione; vedi `CLAUDE.md`, passo 3.
Poi si usa lo stesso script con il nome della tua applicazione (`.\mutanti.ps1 miaapp prompt`), a patto di avere
`role-targets-miaapp.json` e `generator-config-miaapp.json` nella cartella principale.

Per rifare anche i test sui mutanti e le misure della tesi vedi `RIPRODURRE-LE-CAMPAGNE.md`
(i programmi di test contengono ancora percorsi del PC su cui sono stati usati).
