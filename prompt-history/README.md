# Storico prompt di generazione mutazioni (strategia 11×5 LLM_ROLE) — CineLib

Registro dei prompt usati per generare le mutazioni + il loro tasso misurato.
Regola: se un nuovo prompt NON migliora, si ripristina il precedente (backup versionati qui dentro).

## Dove vive il "prompt" (2 pezzi)
1. **`role-taxonomy-prompt.txt`** (system prompt) — `mutation-generator/llm-generator/src/main/resources/`.
   È il prompt caricato in modalità Groq live (`App.java:146`). Copia versionata qui.
2. **Definizioni degli operatori** (a…k) — generate da `App.java` (`buildRoleUserPrompt` / dump `runRoleDump`).
   In modalità **ingest manuale** (quella usata per gli 807 mutanti) è QUESTO il testo effettivamente
   incollato all'LLM (vedi esempio in `output/mutations/role-prompts/*.txt`). È qui che si mettono
   eventuali vincoli per-operatore (es. limitare `a`/`c` agli attributi non-binding).

---

## Versioni

### v1-baseline — 2026-07-06  → file: `role-taxonomy-prompt__v1-baseline__20260706.txt`
Prompt attualmente in uso. **NON confondere con il fix che ha abbassato il not_compiled 60%→20%:
quel calo NON è stato un cambio di prompt**, ma un fix di serializzazione in Java
(`fixAngularControlFlowEntities` in `App.java`: jsoup convertiva `>` in `&gt;` dentro `@if`/`@for`
di Angular, rompendo la compilazione a prescindere dalla mutazione). Vedi HANDOFF §5.5 BUG 5.

**Tasso misurato (scenario Catalog search — 306 mutanti su catalog + movie-card, tutti gli operatori):**

| Metrica | Valore | Note |
|---|---|---|
| not_compiled totale | **20,3%** (~62/306) | quasi interamente l'operatore `h` |
| not_compiled sui non-`h` (generati dal prompt) | **≈ 0%** | 240 mutanti non-`h`, ~0 falliscono |
| mutanti `h` (cross-template) | 66/306 | not_compiled per costruzione (100% atteso) |

**Composizione dei 306 testati:** 66 `h` + 240 non-`h`.
Verifica indipendente (2026-07-06): 0/137 file `role-ingest/` contengono ancora `&gt;`/`&lt;` →
i mutanti generati dal prompt non hanno residui di corruzione.

**Interpretazione (importante):** il 20% NON è migliorabile agendo sul prompt, perché è dovuto
all'operatore `h`, che (a) è generato **meccanicamente** (`LLM_H_AUTO`, non dal prompt) e
(b) è not-compilable **per costruzione** (sposta un elemento in un componente con contesto dati
diverso → i binding non risolvono). Il prompt governa solo gli operatori non-`h`, già a ~0%.
Leve reali per far scendere il numero (nessuna è una modifica al prompt):
- escludere `h` dal calcolo del tasso (ma va tenuto per il confronto equo con lo static, che ha
  lo stesso operatore = `TagMovementBetweenTemplatesRule`);
- aumentare la quota di mutanti non-`h` (cambia il rapporto, non la qualità del prompt);
- cambiare la mappa destinazione di `h` (codice, non prompt).
Un miglioramento di prompt avrebbe senso solo sui **componenti non ancora testati**
(stats, movie-form, reviews, movie-detail, cast-row — 501 mutanti HELD), se lì emergessero
not_compiled reali (es. altri `@if`/`@for` con `>`/`<`, o `c`/`a` che rinominano binding Angular).
Ma prima vanno testati: oggi non c'è dato not_compiled per loro.
