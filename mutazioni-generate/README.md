# Mutazioni generate

Questa cartella contiene **l'insieme completo dei mutanti prodotti** nelle quattro campagne
di generazione descritte nel Capitolo 4 della tesi, in una forma testuale direttamente
leggibile: ogni mutazione è un file `.mut` apribile con un qualsiasi editor e consultabile
online senza scaricare nulla.

Gli stessi mutanti risiedono nelle basi di dati SQLite delle rispettive cartelle di campagna
(`mutations.db`), che però non sono versionate e non sono ispezionabili senza strumenti
dedicati. I file presenti qui ne sono l'esportazione, prodotta dalla funzione di interscambio
descritta nel § 2.5.3 della tesi.

## Contenuto

| Cartella | Soggetto | Tecnica | Mutanti |
|---|---|---|---|
| `cinelib-llm/` | CineLib | modello linguistico | 1 263 |
| `cinelib-static/` | CineLib | analisi statica | 1 142 |
| `flowboard-llm/` | FlowBoard | modello linguistico | 1 225 |
| `flowboard-static/` | FlowBoard | analisi statica | 2 840 |

**Totale: 6 470 mutanti.**

> Nota su `cinelib-llm`: la cartella contiene i 1 263 mutanti presenti nella base di dati,
> mentre le misure riportate nella tesi si riferiscono ai **807** effettivamente sottoposti
> alla campagna di collaudo. La differenza è costituita da mutanti generati in fasi
> successive e non inclusi nella verifica; il criterio è dichiarato nel § 4.1 della tesi.

In ogni cartella il file `manifest.csv` elenca le mutazioni con il rispettivo identificativo,
elemento bersaglio, operatore applicato, tecnica e nome del file corrispondente.

## Formato di un file `.mut`

```
#mutation-id: LLMR_board_component_html_board_title_alf_a
#element: board_component_html
#name: a
#type: LLM_ROLE
#status: HELD
######### FILE .../board/board.component.html #########
...contenuto completo del template mutato...
######### END FILE #########
```

L'intestazione riporta gli attributi anagrafici della mutazione:

- `mutation-id` — chiave logica, che codifica bersaglio, ruolo strutturale e operatore;
- `element` — template o componente su cui la mutazione insiste;
- `name` — operatore applicato, secondo gli identificativi `a`–`k` del modello (§ 1.4.2);
- `type` — tecnica di generazione impiegata;
- `status` — stato di avanzamento del collaudo.

Seguono uno o più blocchi `FILE`, ciascuno con il percorso del file coinvolto e il suo
contenuto dopo l'applicazione della mutazione. La presenza di più blocchi rappresenta le
mutazioni che interessano due template distinti, come richiesto dall'operatore `h`.

## Come leggere le mutazioni

Il modo più rapido per vedere **che cosa** una mutazione ha cambiato è confrontare il
contenuto del blocco `FILE` con il template originale dell'applicazione corrispondente.
Poiché ogni mutante altera un solo elemento, la differenza è di norma una singola riga.

Un esempio di lettura, con lo stesso elemento sottoposto a operatori diversi, è riportato
nel § 2.4.4 della tesi (Tabella 2.4).

## Reimportazione

I file possono essere modificati e reintrodotti nella base di dati, per correggere o
personalizzare un mutante senza rigenerare l'intero insieme:

```
java -cp "<classpath>" org.unina.data.MutationPorter export <cartella> [tipo]
java -cp "<classpath>" org.unina.data.MutationPorter import <cartella>
```

Il comando va eseguito dalla cartella che contiene il `mutations.db` di riferimento.
La reimportazione riconosce le mutazioni già presenti dalla loro chiave logica e ne
sostituisce il contenuto, senza generare duplicati.
