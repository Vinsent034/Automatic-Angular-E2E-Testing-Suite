# Risultati del Mutation Testing — Mutazioni generate tramite LLM

Estensione della sperimentazione static: i mutanti sono stati generati in modo **indipendente** da un LLM (Llama 3.3 70B), che decide autonomamente *cosa* e *dove* modificare nel template, simulando refactoring realistici da sviluppatore. La fase di collaudo riusa lo stesso strumento e le stesse metriche dello studio static. Strategia **Hook-Based esclusa** (toolchain di iniezione non disponibile in questo ambiente).

### Tabella — Risultati globali (mutazioni LLM, 200 mutanti per strategia)

| Locator | Success | Fragility | Obsolescence | Not Compiled |
|---|---|---|---|---|
| Relative | 178/200 | 4/200  | 3/200 | 15/200 |
| Absolute | 176/200 | 6/200  | 3/200 | 15/200 |
| Robula   | 182/200 | 0/200  | 3/200 | 15/200 |
| Katalon  | 181/200 | 1/200  | 3/200 | 15/200 |
| Selenium | 175/200 | 7/200  | 3/200 | 15/200 |
| Robula+  | 170/200 | 12/200 | 3/200 | 15/200 |

### Disaggregazione per area di test

**Area Motore di Ricerca (`search.component.html`, 100 mutanti)**

| Locator | Success | Fragility | Obsolescence | Not Compiled |
|---|---|---|---|---|
| Relative | 85/100 | 4/100  | 2/100 | 9/100 |
| Absolute | 84/100 | 5/100  | 2/100 | 9/100 |
| Robula   | 89/100 | 0/100  | 2/100 | 9/100 |
| Katalon  | 88/100 | 1/100  | 2/100 | 9/100 |
| Selenium | 83/100 | 6/100  | 2/100 | 9/100 |
| Robula+  | 77/100 | 12/100 | 2/100 | 9/100 |

**Area Card dei risultati (`card.component.html`, 100 mutanti)**

| Locator | Success | Fragility | Obsolescence | Not Compiled |
|---|---|---|---|---|
| Relative | 93/100 | 0/100 | 1/100 | 6/100 |
| Absolute | 92/100 | 1/100 | 1/100 | 6/100 |
| Robula   | 93/100 | 0/100 | 1/100 | 6/100 |
| Katalon  | 93/100 | 0/100 | 1/100 | 6/100 |
| Selenium | 92/100 | 1/100 | 1/100 | 6/100 |
| Robula+  | 93/100 | 0/100 | 1/100 | 6/100 |

> **Questa tabella costituisce la baseline dell'approccio basato su mutazioni generate da LLM**, da affiancare e confrontare con la sperimentazione static: rispetto a quest'ultima — che colpisce soprattutto i locatori posizionali (Absolute, Relative) tramite operatori strutturali mirati — le mutazioni realistiche generate dall'LLM mettono sotto stress in misura maggiore i locatori basati su classi e attributi (Robula+, Selenium), mentre Robula si conferma il più robusto in entrambi i contesti.
