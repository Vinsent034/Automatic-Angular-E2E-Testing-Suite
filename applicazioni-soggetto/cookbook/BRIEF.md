# CookBook — brief di progetto

Terza applicazione soggetto della sperimentazione. Il brief specifica dominio, tecnologia e,
soprattutto, le proprietà strutturali richieste dall'esperimento (tesi, § 3.4.2, Tabella 3.1),
più le regole introdotte per coprire i ruoli di contesto.

## Dominio

Un ricettario: elenco di ricette con ricerca, filtri e ordinamento; scheda della ricetta con
ingredienti, procedimento e valori nutrizionali, e porzioni ricalcolabili; modulo di creazione e
modifica; lista della spesa alimentata dalle ricette; pagina di statistiche.

## Tecnologia

Angular 19.2, componenti standalone, template in file `.html` separati, costrutti `@if` / `@for`,
binding di proprietà, attributi ed eventi, interpolazioni. Solo CSS globale, nessuna libreria di
componenti. Stesso `package-lock.json` delle altre due applicazioni. Porta 4500.

## Requisiti sperimentali (Tabella 3.1 della tesi)

1. Nessun backend e nessuna chiamata a servizi esterni.
2. Stato interamente in memoria, nessuna persistenza locale: il ricaricamento della pagina
   ripristina lo stato iniziale.
3. Dati iniziali fissi e cablati nel codice, senza valori casuali né riferimenti temporali.
4. Nessuna animazione, transizione o attesa temporizzata (i messaggi di stato restano finché non
   vengono sostituiti o chiusi).
5. Un attributo `x-test-*` su ogni elemento bersaglio.
6. Ogni bersaglio raggiungibile anche per classe, testo, ruolo, attributo `data-*` e posizione.
7. Nel markup, i costrutti richiesti dagli undici operatori.
8. Un unico contenitore di stato (`CookStore`), iniettato con il nome `store` in **ogni**
   componente: un elemento che legge lo stato attraverso `store` resta valido ovunque venga spostato.

## Regole per i ruoli di contesto

Lo strumento sceglie come padre il genitore diretto, come antenato il nonno, come fratello il
fratello successivo (o il precedente) e come componente contenitore il primo antenato con tag
personalizzato. Per ciascun bersaglio:

- **R1** — almeno tre livelli di profondità dalla radice del template, oppure un template così
  piccolo che la radice stessa è un contenitore minimo;
- **R2** — padre e nonno propri, condivisi al massimo con un altro bersaglio;
- **R3** — almeno un fratello elemento, privo di `x-test-*`;
- **R4** — `x-test-*` soltanto sui bersagli: padri, nonni e fratelli ne sono privi;
- **R5** — contenitori con attributi, testo e figli, così che ogni operatore trovi dove applicarsi;
- **R6** — alcuni bersagli dentro un componente contenitore dello stesso template
  (`<app-panel>` con proiezione del contenuto), perché esista anche il quinto ruolo.

## Viste e componenti

| Percorso | Componente | Contenuto |
|---|---|---|
| `/` | `recipe-list` + `recipe-card` | ricerca, filtro per portata, solo vegetariane, ordinamento, conteggio, griglia |
| `/recipe/:id` | `recipe-detail` + `ingredient-row` + `step-item` | titolo, autore, porzioni, ingredienti ricalcolati, procedimento, valori nutrizionali, aggiunta alla spesa, eliminazione |
| `/recipe/new`, `/recipe/:id/edit` | `recipe-form` | modulo di creazione e modifica |
| `/shopping` | `shopping-list` | voci raggruppate per ricetta, spunta, rimozione delle voci comprate |
| `/stats` | `stats` | totali, ricette per portata, migliori per voto |
| (sempre) | `app`, `header`, `toast`, `panel` | intestazione, navigazione, piè di pagina, messaggi, pannelli con proiezione |

## Dati iniziali

Otto ricette fisse (identificativi 1-8), tre preferite, sei vegetariane, portate: 3 principali,
2 antipasti, 2 dolci, 1 contorno. Lista della spesa iniziale: tre voci della ricetta 2, di cui una
già spuntata.
