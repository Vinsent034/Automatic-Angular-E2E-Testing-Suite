# Guida passo passo: generare i mutanti e provarli con i test

Questa guida porta **da zero** (nessun programma installato, repository non ancora scaricato) fino ai mutanti generati,
alla loro esecuzione con i test e alla lettura dei risultati. Ogni passo dice **dove** si trova ciò che serve,
**cosa fare**, **cosa si deve vedere** e **cosa fare se non si vede**. È stata provata passo per passo su Windows.

Si può fare con una delle tre applicazioni della tesi: **CineLib**, **FlowBoard** o **CookBook**. Nei comandi
`<nome-applicazione>` si sostituisce con il nome dell'applicazione: `cinelib`, `flowboard` o `cookbook`.

---

## Requisiti (da controllare prima di cominciare)

**Che cosa serve**

| Cosa | Perché | Come controllare |
|---|---|---|
| Un PC con **Windows 10 o 11** | gli script sono per Windows | — |
| **Internet** | si scaricano gli strumenti (circa 1,5 GB in tutto) e il driver di Chrome | — |
| Circa **2 GB liberi** sul disco | strumenti, dipendenze e risultati | — |
| **Google Chrome** installato | i test aprono Chrome da soli (solo per i passi 9 e 11b) | apri il menu Start e cerca "Chrome" |
| **Claude Code** con accesso a **Claude Opus 4.8** | scrive le risposte ai prompt (solo per il passo 6); consuma i crediti del tuo account | apri Claude Code e guarda l'elenco dei modelli |
| Un **browser** (Chrome, Edge o Firefox) | per scaricare il progetto da GitHub (passo 1) | — |

**Cosa NON serve:** permessi di amministratore, Git, Java, Maven, Node, Python, estensioni di Chrome (Selenium e Katalon sono già nei test).
Java, Maven, Node e Python li scarica da solo il passo 3, dentro la cartella del progetto, senza toccare il resto del computer.

**Chi fa che cosa**

| Chi | Che cosa |
|---|---|
| **Tu** (la persona) | scarichi il progetto, apri i file `.bat` e scrivi i comandi di questa guida |
| **Lo strumento** | prepara i prompt, controlla le risposte, salva i mutanti, esegue i test e mostra i risultati |
| **Il modello** (Claude Opus 4.8) | **una cosa sola**, al passo 6: legge un prompt e scrive la modifica dell'elemento |

> **Il modello non fa altro.** Non installa, non lancia comandi, non sceglie quali elementi o operatori mutare
> (lo ha già deciso lo strumento e sta scritto nel prompt) e non scrive nel database. Tutto il resto lo fai tu con i comandi.

**Panoramica dei passi, e chi fa che cosa in ognuno**

| Passo | Cosa si fa | Chi |
|---|---|---|
| 1 | Scaricare il progetto da GitHub | **utente** |
| 2 | Estrarre lo ZIP | **utente** |
| 3 | Installare gli strumenti (`installa.bat`) | **utente** avvia; lo strumento scarica e compila |
| 4 | Aprire il terminale (`terminale.bat`) | **utente** |
| 5 | Scegliere l'applicazione e preparare i prompt | **utente** scrive il comando; lo strumento prepara i prompt |
| 6 | Scrivere le risposte ai prompt | **utente** apre Claude Code e dà l'incarico; il **modello** scrive le risposte |
| 7 | Salvare i mutanti | **utente** scrive il comando; lo strumento salva |
| 8 | Guardare i mutanti (facoltativo) | **utente** |
| 9 | Far girare i mutanti con i test | **utente** scrive il comando; lo strumento esegue |
| 10 | Leggere i risultati | **utente** |
| 11 | Generatore statico e confronto (facoltativo) | **utente** scrive i comandi; lo strumento genera, esegue e confronta; nessun modello |
| 12 | Rifare da zero o cambiare applicazione | **utente** |

**Come si scrive un comando.** Si scrive (o si incolla) nella finestra nera, **su una sola riga**, poi si preme **Invio**.
Non si premono altri tasti e non si cambia il testo. Si incolla anche con il clic destro del mouse.

---

## Passo 1 — Scaricare il progetto (utente)

1. Apri il browser (Chrome, Edge o Firefox).
2. Nella barra degli indirizzi scrivi **`https://github.com/Vinsent034/Automatic-Angular-E2E-Testing-Suite`** e premi Invio.
   È la pagina del repository del progetto.
3. In alto a destra, sopra l'elenco dei file, clicca il pulsante verde **`<> Code`**.
4. Nel menu che si apre clicca **`Download ZIP`**.
5. Si scarica un file `Automatic-Angular-E2E-Testing-Suite-master.zip`, di solito nella cartella **Download**
   (la trovi anche in Esplora file, nella colonna a sinistra).

**Devi vedere:** il file `.zip` nella cartella Download.

## Passo 2 — Estrarre lo ZIP (utente)

> **Non avviare mai nulla da dentro lo ZIP.** Se fai doppio clic su un file mentre lo ZIP è aperto, Windows lo estrae in una cartella
> temporanea che poi cancella. Va prima estratto.

1. Apri **Esplora file** e vai nella cartella **Download**.
2. Clic **destro** sul file `Automatic-Angular-E2E-Testing-Suite-master.zip`.
3. Clicca **`Estrai tutto…`**.
4. Si apre una finestra con un percorso di destinazione. Clicca **`Sfoglia…`**, scegli il **Desktop** e conferma con `Seleziona cartella`.
   Poi clicca **`Estrai`**.
5. Quando ha finito si apre la cartella estratta. **Chiudi la finestra dello ZIP, se è ancora aperta.**
6. Entra nella cartella finché non vedi **insieme** questi file: `installa.bat`, `terminale.bat`, `mutanti.ps1`, `README.md`,
   `GUIDA-RIPRODUZIONE.md`. Quella è la **cartella principale del progetto**.
   (Windows può creare due cartelle con lo stesso nome una dentro l'altra: quella giusta è la più interna, quella con questi file.)

**Devi vedere:** nella cartella principale i file `installa.bat`, `terminale.bat`, `mutanti.ps1` e alcune cartelle
(`applicazioni-soggetto`, `mutation-generator`, …).

> **Dopo il passo 3 non spostare più la cartella.** I percorsi vengono scritti al momento dell'installazione.
> Se la sposti, rilancia `installa.bat`.

## Passo 3 — Installare gli strumenti, una volta sola (utente)

1. Nella cartella principale fai **doppio clic** su **`installa.bat`**.
2. Se Windows mostra un avviso ("Windows ha protetto il PC" o simile), clicca **`Ulteriori informazioni`** e poi **`Esegui comunque`**.
3. Si apre una **finestra nera**. **Non chiuderla** e non spegnere il PC. Scrive da sola cinque fasi, una dopo l'altra:

   ```
   === 1/5  Scarico gli strumenti portatili ...
   === 2/5  ...
   ... fino a  === 5/5
   ```

   Nella fase 1 compare una barra con la percentuale mentre scarica Java, Maven, Node e Python. L'ultima fase installa le dipendenze
   delle tre applicazioni e può restare ferma **diversi minuti**: è normale.
4. Tutta l'installazione dura **da 20 a 60 minuti**, a seconda della connessione.
5. Alla fine compare in verde **`Tutto pronto.`** e sotto "Premere un tasto per continuare…". Premi **un tasto qualsiasi**: la finestra si chiude.

**Devi vedere:** la scritta verde `Tutto pronto.` e, nella cartella principale, una nuova cartella **`strumenti`**.

**Se compare un errore rosso** o la finestra si chiude da sola prima del messaggio verde: rilancia `installa.bat` (riparte da dove era arrivato)
e, se riappare, fotografa lo schermo.

Se compare invece "stai avviando installa.bat da dentro un file ZIP", torna al passo 2: lo ZIP non è stato estratto.

## Passo 4 — Aprire il terminale giusto (utente)

Tutti i comandi dei passi seguenti si scrivono in **questa** finestra, non in un altro PowerShell: è l'unica che ha già gli strumenti pronti.

1. Nella cartella principale fai **doppio clic** su **`terminale.bat`**.
2. Si apre una finestra nera (nel titolo c'è scritto "cmd.exe": è normale, dentro c'è PowerShell).

**Devi vedere**, in alto, in verde: `Strumenti portatili attivi (Java 25, Maven, Node, Python).`
e sotto una riga che inizia con **`PS C:\Users\...`** e finisce con il nome della cartella del progetto, seguita da `>` e dal cursore.

**Non chiudere questa finestra** finché non hai finito. Se la chiudi per sbaglio, riaprila con `terminale.bat`: non si perde niente.

## Passo 5 — Scegliere l'applicazione e preparare i prompt (utente)

In questo lavoro sono state implementate solo 3 applicazioni: `cinelib`, `flowboard`, `cookbook`.

Nella finestra del passo 4 scrivi **uno** di questi comandi, a seconda dell'applicazione scelta, e premi Invio:

```powershell
.\mutanti.ps1 <nome-applicazione> prompt
```

(oppure `.\mutanti.ps1 cinelib prompt`, oppure `.\mutanti.ps1 cookbook prompt`)

Lo strumento prepara, per ogni elemento da mutare, un **prompt**: un testo che dice al modello quale elemento modificare e con
quali operatori. **Non crea ancora nessun mutante.** Dura da pochi secondi a un minuto.

**Devi vedere**, alla fine, una riga verde e le righe sotto:

| Applicazione | `Fatto: … prompt` | `Applicable` (in alto) |
|---|---|---|
| CineLib | 171 | 1057 |
| FlowBoard | 590 | 3425 |
| CookBook | 215 | 1426 |

Lo script indica anche il numero atteso (`Attesi per questa applicazione: …`). **Se i numeri sono uguali, il passo è riuscito.**

**Dove sono finiti i file.** Nella cartella principale è comparsa una nuova cartella **`generazione-<nome-applicazione>`** (il nome dipende
dall'applicazione). Dentro ci sono i prompt, il file di istruzioni per il modello (`ISTRUZIONI-OPUS-4.8.md`) e il database dei mutanti,
ancora vuoto. Nome e posizione sono sempre questi: li decide lo script, tu non scegli niente.

**Se compare "Manca il file …"** hai scritto male il nome dell'applicazione: i nomi validi sono `cinelib`, `flowboard`, `cookbook`.

## Passo 6 — Far scrivere le risposte al modello (utente avvia, modello scrive)

Questa è l'**unica parte che fa il modello**. Si fa in **Claude Code**, non nella finestra nera.

1. **(utente)** Apri **Claude Code** e inizia una **nuova sessione**.
2. **(utente)** Come **cartella di lavoro** scegli la cartella creata al passo 5:
   `…\Automatic-Angular-E2E-Testing-Suite-master\generazione-<nome-applicazione>`
   (nell'app Claude Code: seleziona questa cartella come cartella di lavoro della nuova sessione; da riga di comando: `cd` in quella cartella e poi `claude`).
3. **(utente)** Imposta il modello **Claude Opus 4.8** dal selettore dei modelli.
4. **(utente)** Scrivi questo messaggio e invialo:

   ```
   Leggi il file ISTRUZIONI-OPUS-4.8.md in questa cartella ed eseguilo.
   ```

   **Per una prova breve**, aggiungi alla fine del messaggio: `Fai solo i primi 20 blocchi.`
   (La generazione completa dura molto tempo e consuma molti crediti: 590 prompt per FlowBoard. Per vedere che tutto funziona bastano pochi.)
5. **(modello)** Il modello legge i prompt uno alla volta e scrive le risposte in
   `generazione-<nome-applicazione>\output\mutations\role-ingest\`, un file per prompt, più il file `_registro.txt`.
   Può chiedere il permesso di scrivere i file: **(utente)** concedilo e aspetta che il modello dica di aver finito.

**(utente)** **Devi vedere**, al termine, nella cartella `role-ingest` dei file di testo (uno per prompt) e il file **`_registro.txt`**.
Aprilo (doppio clic): la prima riga deve riportare **`Claude Opus 4.8`**. Se il modello è un altro, i mutanti non saranno quelli della tesi.

Il modello non deve lanciare comandi né toccare altri file. Se lo fa, interrompilo e ripeti il passo dall'inizio.

## Passo 7 — Salvare i mutanti (utente)

Torna alla **finestra nera del passo 4** e scrivi:

```powershell
.\mutanti.ps1 <nome-applicazione> completa
```

Lo strumento, in ordine: produce gli operatori **h** (spostamenti fra file) ed **f, g** (spostamenti nello stesso file) da programma;
legge le risposte del modello e rimette ogni modifica nel suo file; scarta le risposte mancanti, quelle che toccano un attributo
`x-test`, quelle che non cambiano nulla e i duplicati; salva tutto nel database. Dura circa uno o due minuti.

**Devi vedere** tre righe `DONE` e, alla fine, in verde:
**`Fatto. Mutanti nel database: N`**.

| Applicazione | h | f + g | Con **tutte** le risposte del modello |
|---|---|---|---|
| CineLib | 107 | 171 | N = **944** |
| FlowBoard | 176 | 302 | N = **1.559** |
| CookBook | 202 | 349 | N = **1.939** |

I valori di h e di f + g sono **sempre quelli della tabella**, anche con una prova breve. Quello che cambia con le risposte del
modello è solo il totale: con una prova breve N è più basso e lo script scrive "è una prova parziale". È normale.

**Per CookBook** (facoltativo): `.\mutanti.ps1 cookbook escludi-i` toglie i 10 mutanti dell'operatore i sul bersaglio, per ottenere i
**1.929** mutanti della tesi.

**Non lanciare `completa` due volte** sulla stessa applicazione: aggiungerebbe mutanti doppi, e lo script rifiuta di farlo.
Per rifare da zero: vedi il passo 12.

## Passo 8 — Guardare i mutanti, facoltativo (utente)

```powershell
.\mutanti.ps1 <nome-applicazione> esporta
```

**Devi vedere** `Esportate N mutazioni in …\generazione-<nome-applicazione>\esporta`. Apri la cartella **`esporta`**: c'è un file `.mut` per
ogni mutante, apribile con il Blocco note. Dentro c'è il template HTML intero con **una sola modifica**: confrontandolo con
l'originale in `applicazioni-soggetto\<nome-applicazione>\src\app\...` si trova la riga cambiata.

## Passo 9 — Far girare i mutanti con i test (utente)

Qui si inserisce un mutante alla volta nell'applicazione e si eseguono i test con le **sette strategie di localizzazione**
(Absolute, Relative, ROBULA, ROBULA+, Selenium, Katalon, Hook-Based). Serve **Google Chrome**.

1. **Chiudi** gli altri programmi pesanti e **non usare mouse e tastiera** mentre gira: si aprono finestre di Chrome da sole.
   Non serve installare estensioni: le strategie Selenium e Katalon sono già incluse nei test compilati.
2. Nella finestra nera scrivi:

```powershell
.\mutanti.ps1 <nome-applicazione> esegui 20
```

Il numero (20) è quanti mutanti provare. Se lo ometti sono 20. Sono scelti a caso con un **seme fisso**, quindi sono sempre gli
stessi. Più mutanti vuol dire più tempo (circa **13–20 secondi a mutante**, misurati nelle campagne della tesi).

**Quali mutanti prova.** Di base il campione è scelto dentro **un solo gruppo di test**, quello con più mutanti (un solo avvio
del server, quindi più veloce). Per sceglierlo fra **tutti** i gruppi si aggiunge la parola `tutti` dopo la tecnica:
`.\mutanti.ps1 <nome-applicazione> esegui 20 llm tutti`. Per provare **tutti** i mutanti: un numero grande più `tutti`
(per esempio `esegui 99999 llm tutti`; richiede ore). I gruppi e i test sono descritti in [`TEST.md`](TEST.md).

3. Cosa succede, da solo, per ogni gruppo di test:
   - lo script scrive `applicazione X: N mutanti nel database; ne provo 20 …`;
   - avvia l'applicazione una prima volta per "scaldarla" (`avvio di prova … pronto in … s`: **può durare un paio di minuti**);
   - per ogni mutante: inserisce la modifica, aspetta la ricompilazione, esegue i test con le sette strategie e **rimette il template
     originale**;
   - ogni 20 secondi circa scrive `in corso, mancano N mutanti su 20`.
4. Alla fine scrive `template dell'applicazione rimessi come all'inizio` e `finito in … secondi`.

**Tempo totale per 20 mutanti: circa 5–10 minuti.**

**Se si chiude Chrome o appare un errore** a metà: aspetta, poi **rilancia lo stesso comando**; riparte da capo sul gruppo.
In ogni caso i template dell'applicazione vengono rimessi a posto.

## Passo 10 — Leggere i risultati (utente)

Alla fine del passo 9 compare in finestra una tabella come questa:

```
RISULTATI - flowboard - 20 mutanti provati (campione casuale, seme 1234)
  Mutanti che compilano (validi): 14   |   non compilati: 6
  Prove sprecate (falliscono con tutte le strategie): 1 su 14 provate (7.1%)

  Strategia     Successi  Fragilita   Stabilita (successi/valutabili)
  Absolute            12          1                          92.3%
  Relative            12          1                          92.3%
  ...
```

**Come si legge**

| Voce | Significato |
|---|---|
| **Mutanti che compilano** | la modifica ha lasciato l'applicazione funzionante: il mutante è **valido** |
| **Non compilati** | dopo la modifica l'applicazione non si ricompila: il mutante non viene provato e non conta come fragilità |
| **Prove sprecate** | prove in cui falliscono **tutte e sette** le strategie: la modifica ha rotto la funzione, e non dice niente sui locatori |
| **Successi** | il test passa con quella strategia: il locatore ha resistito alla modifica |
| **Fragilità** | il test fallisce con quella strategia mentre con altre passa: la colpa è del locatore |
| **Stabilità** | successi diviso (successi + fragilità), in percentuale: più è alta, più la strategia è robusta |

**Dove sono i file** (cartella `generazione-<nome-applicazione>\output\tests\`, apribili con il Blocco note o con Excel):

| File | Contenuto |
|---|---|
| `riepilogo.txt` | la tabella qui sopra |
| `mutanti-provati.csv` | per ogni mutante provato: nome, identificativo e se **compila** o **non compila** (è la **verifica della compilazione**) |
| `batches-<gruppo>.csv` | il dettaglio di ogni prova: mutante, strategia, esito (`PASSED` passa, `FAILED` fallisce, `NOT_APPLICABLE` non compilato) |
| `stats.csv` | il conteggio per strategia fatto dallo strumento |

**Con pochi mutanti i numeri sono solo indicativi.** Nella tesi si usano tutti i mutanti di ogni applicazione: 944, 1.559 e
1.929. Il risultato che si ripete è che **Absolute** è la strategia più fragile e **ROBULA+** la più robusta fra quelle che non
usano gli attributi di test.

## Passo 11 — Il generatore statico e il confronto, facoltativo (utente)

Nella tesi i mutanti del modello sono confrontati con quelli del **generatore statico**, che modifica i template con regole fisse
e **non usa nessun modello**: niente prompt e niente risposte da scrivere. Questo passo lo rifà. Si può fare anche **senza** aver fatto
i passi 5–10 (la parte statica è indipendente da quella del modello).

**11a. Generare i mutanti statici** (nella finestra di `terminale.bat`):

```powershell
.\mutanti.ps1 <nome-applicazione> statico
```

Dura qualche minuto. Crea la cartella `generazione-<nome-applicazione>-statico` (separata da quella del modello, così non si mescolano).
Il generatore produce anche mutanti **identici fra loro**: per CineLib e FlowBoard lo strumento li toglie, come nella tesi, e
tiene una copia completa in `mutations-completo-con-duplicati.db`.

Cosa si deve vedere:

| Applicazione | Generati | Dopo aver tolto i duplicati |
|---|---|---|
| CineLib | 1.254 | **944** |
| FlowBoard | 4.263 | **2.148** |
| CookBook | 1.503 nella tesi (1.504 rigenerandoli) | non si tolgono: nella tesi su CookBook sono stati provati tutti |

Su CookBook la rigenerazione differisce di poco dalla tesi (un mutante in più e 152 mutanti dal codice diverso, per lo più spostamenti verso un altro
file di destinazione). L'insieme esatto della tesi è nel file `cookbook-static-run\mutations.db`.

**11b. Provare i mutanti statici con i test** (come il passo 9, con una parola in più in fondo; stessa avvertenza su Chrome,
mouse e tastiera):

```powershell
.\mutanti.ps1 <nome-applicazione> esegui 20 statico
```

I risultati sono in `generazione-<nome-applicazione>-statico\output\tests\` (stessi file e stessa tabella del passo 10).

**11c. Confrontare le due tecniche**:

```powershell
.\mutanti.ps1 <nome-applicazione> confronta
```

Mostra, affiancati: quanti mutanti ha prodotto ciascuna tecnica, quante delle 55 combinazioni operatore × ruolo sono coperte,
quanti mutanti per operatore e, se hai fatto il passo 9 e il 11b, quanti compilano, le prove sprecate e la stabilità di ogni
strategia. Il testo è salvato in `generazione-<nome-applicazione>\confronto.txt` (o nella cartella statica, se manca quella del modello).
Con pochi mutanti i numeri sono solo indicativi.

## Passo 12 — Rifare da zero o cambiare applicazione (utente)

- **Cambiare applicazione:** ripeti dal passo 5 con il nome di un'altra applicazione (creerà un'altra cartella `generazione-…`).
- **Rifare la stessa applicazione:** chiudi tutto, **cancella la cartella** `generazione-<nome-applicazione>` (clic destro → Elimina) e ricomincia
  dal passo 5. Lo strumento non riparte sopra una generazione già fatta, per non creare mutanti doppi.

---

## Se qualcosa non va

**Se i file `.bat` non partono** (bloccati da Windows, dall'antivirus o da una regola del PC), usa la versione senza `.bat`: [`GUIDA-SENZA-BAT.md`](GUIDA-SENZA-BAT.md).

| Cosa vedi | Cosa fare |
|---|---|
| "stai avviando installa.bat da dentro un file ZIP" | estrai lo ZIP (passo 2) e riapri `installa.bat` dalla cartella estratta |
| Errore rosso durante `installa.bat` | rilancia `installa.bat`; se ricompare, fotografa lo schermo |
| "Manca la cartella strumenti" / "Manca lo strumento compilato" | non hai finito il passo 3: rilancia `installa.bat` |
| Nella finestra non c'è la scritta verde "Strumenti portatili attivi" | chiudi la finestra e riaprila con `terminale.bat` (passo 4) |
| "non può essere eseguito" / "non è firmato digitalmente" | stai usando un PowerShell aperto da te: usa **solo** la finestra di `terminale.bat` |
| "Manca il file role-targets-…" | nome dell'applicazione sbagliato (valgono `cinelib`, `flowboard`, `cookbook`) |
| "Non ci sono risposte del modello" | il passo 6 non è stato fatto, oppure `role-ingest` è vuota |
| "Nel database ci sono già N mutanti" | hai già fatto `completa` (o `statico`): per rifare, passo 12 |
| "Non ci sono mutanti … fai prima 'prompt', il modello e 'completa'" | il passo 7 non è stato fatto per questa applicazione |
| "Non ci sono mutanti statici … fai prima … statico" | prima di `esegui … statico` serve `.\mutanti.ps1 <nome-applicazione> statico` (passo 11a) |
| "Non c'è nessuna generazione per …" (confronta) | non è stata fatta né la generazione del modello né quella statica |
| "Google Chrome non trovato" | installa Chrome da `https://www.google.com/chrome/` |
| "la classe del driver di Chrome non è aggiornata" | rilancia `installa.bat` (ricompila lo strumento) |
| "non trovo l'applicazione" / "mancano le dipendenze" | hai spostato la cartella dopo l'installazione: rilancia `installa.bat` |
| Numeri diversi da quelli delle tabelle | normale se hai fatto una prova breve (passo 6) o se il modello non è Opus 4.8 |

## Un'applicazione tua (facoltativo)

Servono gli attributi `x-test`, il file dei bersagli e la configurazione: vedi `CLAUDE.md`, sezione "Preparare un'applicazione nuova".
Per i test servono anche le classi di test scritte per quella applicazione, quindi il passo 9 oggi vale solo per CineLib, FlowBoard e CookBook.

Per rifare le campagne **complete** della tesi (tutti i mutanti) vedi `RIPRODURRE-LE-CAMPAGNE.md`: i suoi programmi contengono ancora
percorsi del PC su cui sono stati usati. Il passo 9 di questa guida usa invece uno script nuovo (`esegui_mutanti.py`), che funziona su
qualsiasi PC.
