# Generazione dei mutanti linguistici di CookBook — istruzioni per Claude Opus 4.8

Questo file è rivolto al modello che esegue la generazione. Va eseguito in una sessione di
Claude Code impostata sul modello **Claude Opus 4.8**. Se il modello della sessione non è
Claude Opus 4.8, **fermati subito** e dillo all'utente senza fare altro.

Queste istruzioni riguardano solo **come leggere i prompt e dove salvare le risposte**. Come
generare le mutazioni lo dice ciascun prompt, e va seguito così com'è: non aggiungere regole
tue e non cercarne altrove.

## Il compito

In `output/mutations/role-prompts/` (relativo alla cartella di questo file) ci sono **215 prompt**.
Per ciascuno:

1. leggi il file del prompt;
2. rispondi **tu** al prompt, esattamente come se ti fosse stato incollato in una chat;
3. salva la risposta in `output/mutations/role-ingest/` con **lo stesso nome file** del prompt
   (es. `role-prompts/app_footer_brand_alf.txt` → `role-ingest/app_footer_brand_alf.txt`).

Il file di risposta contiene solo i blocchi `######### START … ######### … ######### END … #########`
richiesti dal prompt, senza testo prima, dopo o fra i blocchi e senza recinti di codice.

Procedi in ordine alfabetico di nome file. Se un file di risposta esiste già in `role-ingest/`,
saltalo: è già stato fatto (serve a riprendere dopo un'interruzione).

## Vincoli di metodo

- **Ogni risposta la scrivi tu, prompt per prompt.** È vietato scrivere o eseguire script,
  programmi o comandi che producano o trasformino le risposte.
- **Non usare sotto-agenti** (strumento Agent o simili): girerebbero con un modello diverso.
- Non leggere altro che il prompt corrente: né i sorgenti dell'applicazione, né altri file del
  progetto, né le risposte già scritte, né altri prompt da riusare come esempio.
- Un prompt, un file di risposta: non raggruppare più prompt.
- Non modificare nessun file al di fuori di `output/mutations/role-ingest/`.

## Registro

Crea `output/mutations/role-ingest/_registro.txt` e, **prima di cominciare**, scrivi nella prima
riga il nome e l'identificativo esatto del modello che sta eseguendo (quello indicato dal tuo
sistema) e la data. Poi aggiungi una riga per ogni prompt completato:
`<nome file>  <numero di blocchi scritti>`.

Alla fine scrivi nel registro il totale dei file e dei blocchi, e dillo all'utente. Il totale
atteso è **215 file e 1426 blocchi**.
