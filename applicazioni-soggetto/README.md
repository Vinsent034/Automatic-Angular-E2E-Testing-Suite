# Applicazioni soggetto

Il codice delle tre applicazioni Angular 19 create per l'esperimento (tesi, § 3.4). Sono banchi
di prova, non prodotti reali: nessun backend, dati solo in memoria e fissi, un attributo
`x-test-*` su ogni elemento bersaglio.

| Cartella | Applicazione | Porta usata negli esperimenti | Note |
|---|---|---|---|
| [`cinelib/`](cinelib) | CineLib, catalogo di film | 4300 | prima applicazione |
| [`flowboard/`](flowboard) | FlowBoard, bacheca Kanban | 4400 | dati in un contenitore comune: l'operatore `h` produce mutanti validi |
| [`cookbook/`](cookbook) | CookBook, ricettario | 4500 | [`BRIEF.md`](cookbook/BRIEF.md): il brief di progetto |

I template sono quelli **originali**, non mutati. Ogni cartella contiene anche la suite
Playwright (`e2e/`) usata durante lo sviluppo per verificare che l'applicazione funzioni.

## Avvio

```
npm install
npm start -- --port 4300
```

(con la porta della tabella). Mancano `node_modules`, `dist` e i report dei test: si
rigenerano con i comandi qui sopra.

## Percorsi nei file di configurazione

I file `generator-config-*.json` e `role-targets*.json` nella radice del progetto contengono i
percorsi assoluti usati sul computer dell'autore (`C:/Users/vince/.../App tirocinio/<app>`).
Per rifare gli esperimenti altrove basta sostituirli con i percorsi di queste cartelle
(`cinelib`, `flowboard` al posto di `Kanban board`, `cookbook`).
