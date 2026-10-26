# Risultati Base — Spotify (mutazioni generate da LLM)

## 1. Descrizione dell'approccio

I risultati riportati in questo documento sono stati ottenuti su **angular-spotify** utilizzando mutanti **generati da un LLM (Llama 3.3 70B) in maniera indipendente**.

A differenza della generazione *static* — che applica operatori meccanici predefiniti su un elemento bersaglio e sui suoi vicini — qui l'LLM riceve **solo il codice del template** e decide **autonomamente** cosa e dove modificare: nessun elemento bersaglio gli viene indicato, nessuna lista di operatori gli viene imposta. Il modello produce così **modifiche realistiche, come le farebbe un programmatore reale** durante il normale lavoro di UI o un redesign (rinomina di classi, wrapper di layout, cambio di tag, riordino, spostamenti, ritocchi di attributi non funzionali), mantenendo la pagina funzionante.

Ogni variante introduce **una sola modifica localizzata**, così da poter isolare l'effetto del singolo cambiamento sulle strategie di localizzazione, in modo confrontabile con la sperimentazione static.

## 2. Il prompt dato all'LLM

La generazione avviene con due messaggi. Il **system prompt** (istruzioni fisse) è il seguente:

```
# Prompt — Independent Realistic Angular Mutation (LLM strategy)

## Role
You are a front-end developer working on an Angular 19 template. You make the kind of
small, plausible changes a real developer or designer makes during everyday UI work and
redesigns. You decide entirely on your own what to change and where — there are NO
instructions telling you which element to touch or which kind of edit to apply.

## Task
You receive the complete source of one Angular template. Produce N variants of it. In each
variant you autonomously introduce ONE realistic change of your own choosing, somewhere in
the template. The change should be the sort of thing a developer would actually commit: it
keeps the page working, but it may reshape how the markup is organised, so test locators may
or may not react.

## Spread and depth (variety, not direction)
You decide everything; this only asks for a varied set, NOT where or how to act:
- Across the N variants, vary BOTH where in the template you act and HOW MUCH you change —
  from a minor touch up to a single, more substantial restructuring of a piece of markup.
- Do NOT restrict the set to the smallest cosmetic edits, and do NOT concentrate every
  variant on the same spot. Cover the template broadly.
- No two variants may make essentially the same edit.

## Application independence (CRITICAL)
Operate ONLY on the markup you are given. Do NOT assume anything about which application
this is, its domain, its features, or its data. Do not invent app-specific names. Use
exclusively the tags, classes and attributes already present in the provided HTML. The same
strategy must work unchanged on any Angular application.

## Validity constraints (correctness rules — NOT instructions on what to change)
1. The result MUST still be valid Angular 19 and compile.
2. Do NOT alter the syntax of control flow or bindings: `@if`, `@for`, `*ngrxLet`, `[prop]`,
   `(event)`, `[(two-way)]`, and `{{ }}` interpolations must stay valid.
3. Do NOT touch attributes starting with `x-test` (they are test ground truth).
4. Keep the feature usable: do not delete or disable the interactive elements a user needs.
5. Exactly ONE self-contained change per variant: it may be small or a single larger
   restructuring, but it must be one coherent edit, not several unrelated ones.
6. Each variant must genuinely differ from the original (no no-ops) and from every other variant.

## Output format
For EACH variant output exactly one block and nothing else:

######### START {n} - {label} #########
{full mutated template}
######### END {n} - {label} #########

- {n} = progressive number (1..N).
- {label} = a short free-text word YOU choose to describe the change you made (your own words).
- The block body is the COMPLETE mutated template.
- No comments, no explanations, no Markdown code fences.
```

Il secondo messaggio (**user prompt**, costruito a runtime) allega il **codice reale del template** da mutare:

```
Here is the complete source of one Angular template.
Produce exactly {N} variants of it, numbered 1..{N}, each in its own delimited block exactly
as specified in your instructions. Decide entirely on your own what to change in each variant.
Output ONLY the blocks, nothing else.

----- ORIGINAL TEMPLATE -----
{contenuto verbatim del file .html}
----- END ORIGINAL TEMPLATE -----
```

A valle, una pipeline automatica valida ogni variante (parsing corretto, diversa dall'originale, non duplicata, attributi `x-test` preservati) e la salva; la compilazione Angular effettiva viene verificata nella successiva fase di mutation testing (esito *Not Compiled* per i mutanti non compilabili).

## 3. Tabella dei risultati

Risultati su **200 mutanti** generati dall'LLM (Hook-Based escluso: toolchain di iniezione non disponibile). Valori in forma `conteggio / totale`.

### Risultati globali (200 mutanti per strategia)

| Locator | Success | Fragility | Obsolescence | Not Compiled |
|---|---|---|---|---|
| Relative | 178/200 | 4/200  | 3/200 | 15/200 |
| Absolute | 176/200 | 6/200  | 3/200 | 15/200 |
| Robula   | 182/200 | 0/200  | 3/200 | 15/200 |
| Katalon  | 181/200 | 1/200  | 3/200 | 15/200 |
| Selenium | 175/200 | 7/200  | 3/200 | 15/200 |
| Robula+  | 170/200 | 12/200 | 3/200 | 15/200 |

### Area Motore di Ricerca (`search.component.html`, 100 mutanti)

| Locator | Success | Fragility | Obsolescence | Not Compiled |
|---|---|---|---|---|
| Relative | 85/100 | 4/100  | 2/100 | 9/100 |
| Absolute | 84/100 | 5/100  | 2/100 | 9/100 |
| Robula   | 89/100 | 0/100  | 2/100 | 9/100 |
| Katalon  | 88/100 | 1/100  | 2/100 | 9/100 |
| Selenium | 83/100 | 6/100  | 2/100 | 9/100 |
| Robula+  | 77/100 | 12/100 | 2/100 | 9/100 |

### Area Card dei risultati (`card.component.html`, 100 mutanti)

| Locator | Success | Fragility | Obsolescence | Not Compiled |
|---|---|---|---|---|
| Relative | 93/100 | 0/100 | 1/100 | 6/100 |
| Absolute | 92/100 | 1/100 | 1/100 | 6/100 |
| Robula   | 93/100 | 0/100 | 1/100 | 6/100 |
| Katalon  | 93/100 | 0/100 | 1/100 | 6/100 |
| Selenium | 92/100 | 1/100 | 1/100 | 6/100 |
| Robula+  | 93/100 | 0/100 | 1/100 | 6/100 |

## 4. Nota di lettura

La robustezza di una strategia si legge dalla colonna **Fragility** (più bassa = più robusta): nel set LLM, **Robula** è la più robusta (0 fragilità) e **Robula+** la più fragile (12). Questi risultati costituiscono la **baseline dell'approccio basato su mutazioni generate da LLM** in modo indipendente, da affiancare e confrontare con la sperimentazione static.
