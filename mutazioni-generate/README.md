# Mutazioni generate

Questa cartella contiene **i mutanti usati negli esperimenti** della tesi (Capitolo 4), nelle
sei campagne di generazione: due tecniche (statica e linguistica) su tre applicazioni
(CineLib, FlowBoard, CookBook). Ogni mutazione è un file `.mut`, apribile con qualsiasi editor
e consultabile online senza scaricare nulla.

Gli stessi mutanti si trovano nelle basi di dati SQLite delle cartelle di campagna
(`mutations.db`), che non sono versionate perché non si leggono senza strumenti appositi. I file
qui sono la loro esportazione, fatta con la funzione descritta nel § 2.5.3 della tesi.

## Contenuto

| Cartella | Soggetto | Tecnica | Mutanti | Validi | Campagna di origine |
|---|---|---|---|---|---|
| [`cinelib-llm/`](cinelib-llm) | CineLib | linguistica | 944 | 831 | `cinelib-v2-run/` |
| [`cinelib-static/`](cinelib-static) | CineLib | statica | 944 | 688 | `cinelib-v2-static-run/` |
| [`flowboard-llm/`](flowboard-llm) | FlowBoard | linguistica | 1 559 | 1 491 | `flowboard-v2-run/` |
| [`flowboard-static/`](flowboard-static) | FlowBoard | statica | 2 148 | 1 664 | `flowboard-v2-static-run/` |
| [`cookbook-llm/`](cookbook-llm) | CookBook | linguistica | 1 929 | 1 813 | `cookbook-run/` |
| [`cookbook-static/`](cookbook-static) | CookBook | statica | 1 454 | 1 197 | `cookbook-static-run/` |

**Totale: 8 978 mutanti** (4 432 linguistici, 4 546 statici).

**Duplicati del generatore statico.** Il generatore statico produce anche mutanti identici fra
loro (§ 3.5.2 della tesi). Qui ci sono solo i mutanti **distinti**: 310 duplicati su CineLib,
2 115 su FlowBoard e 49 su CookBook non sono ripetuti. L'elenco completo, con il mutante
identico a cui ciascun duplicato corrisponde, è in `mappa-duplicati.json` nelle cartelle di
campagna di CineLib e FlowBoard. I mutanti linguistici non hanno duplicati: lo strumento li
scarta al momento dell'acquisizione (§ 2.4.6).

**Esiti dei test.** Il campo `status` dei file indica solo lo stato interno dello strumento.
Gli esiti dei test (per mutante e per strategia) sono nei file `output/tests/batches-*.csv`
delle cartelle di campagna.

In ogni cartella il file `manifest.csv` elenca le mutazioni con identificativo, elemento,
operatore, tecnica, stato e nome del file.

## Nomi dei file

- **Linguistici** — `<template>__<identificativo>.mut`. L'identificativo contiene template,
  bersaglio, ruolo e operatore, per esempio
  `LLMR_card_component_html_card_title_alf_a` = bersaglio `card-title`, ruolo α (bersaglio),
  operatore `a`. Ruoli: `alf` α bersaglio, `bet` β padre, `gam` γ antenato, `del` δ fratello,
  `eps` ε componente.
- **Statici** — `<ruolo>__<bersaglio>__<operatore>.mut`, per esempio
  `beta__card-title__d.mut`.

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

L'intestazione riporta i dati della mutazione:

- `mutation-id` — nome logico;
- `element` — template (linguistici) o ruolo (statici);
- `name` — operatore `a`–`k` (linguistici) o bersaglio (statici);
- `type` — `LLM_ROLE` per i linguistici, nome della regola per gli statici;
- `status` — stato interno dello strumento.

Nei mutanti statici gli stessi dati stanno in campi diversi (§ 2.5.1 della tesi): l'operatore
è in `mutation-id`, il ruolo in `element`, il bersaglio in `name`.

Seguono uno o più blocchi `FILE`, ciascuno con il percorso di un file coinvolto e il suo
contenuto dopo la mutazione. Due blocchi indicano una mutazione su due template, come
richiede l'operatore `h`.

## Come leggere le mutazioni

Per vedere **che cosa** ha cambiato una mutazione, si confronta il blocco `FILE` con il
template originale in [`../applicazioni-soggetto/`](../applicazioni-soggetto). Ogni mutante
modifica un solo elemento, quindi di solito cambia una sola riga. Un esempio, con lo stesso
elemento sottoposto a operatori diversi, è nel § 2.4.4 della tesi (Tabella 2.4).

## Esportazione e reimportazione

```
java -cp "<classpath>" org.unina.data.MutationPorter export <cartella> [tipo]
java -cp "<classpath>" org.unina.data.MutationPorter import <cartella>
```

Il comando va lanciato dalla cartella che contiene il `mutations.db` di riferimento; il
classpath comprende `mutation-generator/common/target/classes` e un jar con il driver SQLite
(per esempio quello di `llm-generator`). La reimportazione riconosce le mutazioni dal loro
`mutation-id` e ne sostituisce il contenuto. Poiché nei mutanti statici il `mutation-id` è
solo la lettera dell'operatore, la reimportazione va usata sui mutanti linguistici.
