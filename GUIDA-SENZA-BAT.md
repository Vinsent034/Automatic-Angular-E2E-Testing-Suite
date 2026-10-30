# Guida senza file `.bat`: gli stessi passi, scritti a mano

Questa guida serve se i file `.bat` non si possono usare (bloccati da Windows, dall'antivirus o da una regola del PC).
I due file `installa.bat` e `terminale.bat` sono solo **scorciatoie**: lanciano comandi PowerShell. Qui gli stessi comandi
si scrivono a mano, e il resto della procedura è **identico** a [`GUIDA-RIPRODUZIONE.md`](GUIDA-RIPRODUZIONE.md).

Chi fa che cosa: l'**utente** scrive i comandi, lo **strumento** lavora, il **modello** (Claude Opus 4.8) scrive solo le
risposte ai prompt (passo 7). Nei comandi `<nome-applicazione>` si sostituisce con `cinelib`, `flowboard` o `cookbook`.

**Requisiti:** Windows 10 o 11, internet, circa 2 GB liberi, Google Chrome (solo per far girare i test), Claude Code con accesso
a Claude Opus 4.8 (solo per il passo 7).

---

## Passo 1 — Scaricare il progetto (utente)

1. Apri `https://github.com/Vinsent034/Automatic-Angular-E2E-Testing-Suite`.
2. Premi **Code** → **Download ZIP**.

## Passo 2 — Estrarre lo ZIP (utente)

1. Apri la cartella **Download**, tasto destro sul file ZIP → **Estrai tutto…** → **Estrai**.
2. Entra nella cartella estratta finché vedi insieme i file `installa.ps1`, `mutanti.ps1` e `README.md`. **Quella è la cartella
   principale del progetto.**
3. **Non usare i file direttamente da dentro lo ZIP:** vanno estratti prima.

## Passo 3 — Aprire PowerShell nella cartella principale (utente)

1. Nella cartella principale, clicca sulla **barra dell'indirizzo** di Esplora file (in alto, dove c'è il percorso).
2. Scrivi `powershell` e premi **Invio**.
3. Si apre una finestra blu con scritto `PS C:\...` e il percorso della cartella principale. **Tutti i comandi seguenti si
   scrivono in questa finestra**, uno alla volta, seguiti da Invio.

## Passo 4 — Permettere gli script, solo per questa finestra (utente)

```powershell
Set-ExecutionPolicy -Scope Process -ExecutionPolicy Bypass
```

Non cambia niente nel computer: vale solo finché non chiudi la finestra. Se compare una domanda, rispondi `S` (sì) e premi Invio.
**Ogni volta che apri una finestra nuova, rifai i passi 3 e 4.**

*(Alternativa senza questo passo: scrivere davanti a ogni comando `powershell -ExecutionPolicy Bypass -File`, per esempio
`powershell -ExecutionPolicy Bypass -File .\mutanti.ps1 <nome-applicazione> prompt`.)*

## Passo 5 — Installare gli strumenti, una volta sola (utente avvia, lo strumento installa)

```powershell
.\installa.ps1
```

Scarica Java, Maven, Node e Python portatili dentro la cartella `strumenti`, compila lo strumento e installa le dipendenze
delle applicazioni. Serve internet e ci vuole un po' (scarica circa 1,5 GB). Non installa niente nel sistema e non chiede
permessi di amministratore. Lascia la finestra aperta finché il comando non è finito e compare di nuovo `PS C:\...`.

**Dopo questo passo non spostare più la cartella.**

## Passo 6 — Preparare i prompt (utente scrive, lo strumento prepara)

```powershell
.\mutanti.ps1 <nome-applicazione> prompt
```

Non serve altro: lo script carica da solo gli strumenti portatili (è il lavoro che faceva `terminale.bat`).
Crea la cartella `generazione-<nome-applicazione>` con un prompt per ogni elemento da mutare. **Non crea ancora nessun mutante.**

| Applicazione | Prompt attesi | `Applicable` (blocchi) |
|---|---|---|
| CineLib (`cinelib`) | 171 | 1057 |
| FlowBoard (`flowboard`) | 590 | 3425 |
| CookBook (`cookbook`) | 215 | 1426 |

Se i numeri sono uguali, il passo è riuscito.

## Passo 7 — Far scrivere le risposte al modello (utente avvia, modello scrive)

1. Apri **Claude Code**, nuova sessione, con cartella di lavoro `generazione-<nome-applicazione>` (quella creata al passo 6).
2. Imposta il modello **Claude Opus 4.8**.
3. Scrivi e invia: `Leggi il file ISTRUZIONI-OPUS-4.8.md in questa cartella ed eseguilo.`
   Per una **prova breve** aggiungi: `Fai solo i primi 20 blocchi.` (la generazione completa dura molto e consuma molti crediti).
4. Il modello legge i prompt uno alla volta e scrive le risposte in `output\mutations\role-ingest\`. Non deve fare altro.

## Passo 8 — Salvare i mutanti (utente scrive, lo strumento salva)

Torna alla finestra PowerShell (se l'hai chiusa: passi 3 e 4) e scrivi:

```powershell
.\mutanti.ps1 <nome-applicazione> completa
```

Lo strumento controlla le risposte, produce da solo gli operatori f, g, h e salva i mutanti nel database. Con **tutte** le risposte
del modello si attendono 944 mutanti (CineLib), 1559 (FlowBoard), 1939 (CookBook; 1929 con
`.\mutanti.ps1 cookbook escludi-i`). Con una prova breve sono meno. Lo script rifiuta di rieseguirsi se il database è già pieno.

## Passo 9 — Guardare i mutanti, facoltativo (utente)

```powershell
.\mutanti.ps1 <nome-applicazione> esporta
```

Crea la cartella `generazione-<nome-applicazione>\esporta` con un file `.mut` per mutante, apribile con il Blocco note.

## Passo 10 — Far girare i mutanti con i test (utente scrive, lo strumento esegue)

Serve Google Chrome. **Chiudi i programmi pesanti e non usare mouse e tastiera** mentre gira: si aprono finestre di Chrome da sole.

```powershell
.\mutanti.ps1 <nome-applicazione> esegui 20
```

Il numero è quanti mutanti provare (sono scelti a caso con un seme fisso, quindi sempre gli stessi). Circa 13–20 secondi a
mutante; per 20 mutanti circa 5–10 minuti. Alla fine compare la tabella dei risultati per le sette strategie; i file sono in
`generazione-<nome-applicazione>\output\tests\`.

**Quali mutanti prova.** Di base il campione è scelto dentro **un solo gruppo di test**, quello con più mutanti (un solo avvio
del server, quindi più veloce). Per sceglierlo fra **tutti** i gruppi si aggiunge la parola `tutti` dopo la tecnica:
`.\mutanti.ps1 <nome-applicazione> esegui 20 llm tutti`. Per provare **tutti** i mutanti: un numero grande più `tutti`
(per esempio `esegui 99999 llm tutti`; richiede ore). I gruppi e i test sono descritti in [`TEST.md`](TEST.md).

## Passo 11 — Generatore statico e confronto, facoltativo (utente)

Non usa il modello, quindi si può fare anche senza i passi 6–10.

```powershell
.\mutanti.ps1 <nome-applicazione> statico
.\mutanti.ps1 <nome-applicazione> esegui 20 statico
.\mutanti.ps1 <nome-applicazione> confronta
```

Dopo `statico` si devono vedere 1.254 mutanti generati e 944 distinti per CineLib, 4.263 e 2.148 per FlowBoard; per CookBook
1.503 nella tesi (rigenerandoli ne escono 1.504). `esegui … statico` ripete la prova del passo 10 sui mutanti statici (cartella
`generazione-<nome-applicazione>-statico`), e `confronta` mette a confronto le due tecniche.

## Passo 12 — Rifare da zero o cambiare applicazione (utente)

- **Cambiare applicazione:** ripeti dal passo 6 con un altro nome.
- **Rifare la stessa:** chiudi tutto, **cancella la cartella** `generazione-<nome-applicazione>` (e
  `generazione-<nome-applicazione>-statico`) e riparti dal passo 6.

---

## Se qualcosa non va

| Cosa vedi | Cosa fare |
|---|---|
| «non può essere eseguito» / «non è firmato digitalmente» | non hai fatto il passo 4 in questa finestra: rifallo |
| «Manca la cartella strumenti» / «Manca lo strumento compilato» | non hai finito il passo 5: rilancia `.\installa.ps1` |
| Il percorso della finestra non è la cartella principale | chiudi la finestra e rifai il passo 3 dalla cartella giusta |
| Errore rosso durante `.\installa.ps1` | rilancialo; se ricompare, fotografa lo schermo |
| «Manca il file role-targets-…» | nome dell'applicazione sbagliato (valgono `cinelib`, `flowboard`, `cookbook`) |
| «Nel database ci sono già N mutanti» | hai già fatto `completa` (o `statico`): per rifare, passo 12 |
| «Google Chrome non trovato» | installa Chrome da `https://www.google.com/chrome/` |

Per la tabella completa degli errori vedi in fondo a [`GUIDA-RIPRODUZIONE.md`](GUIDA-RIPRODUZIONE.md).
