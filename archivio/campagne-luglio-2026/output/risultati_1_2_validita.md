# RQ 1.2 — Validità delle mutazioni generate (LLM parametrico)

## Metodologia
Generazione **parametrica**: per ciascuno degli 11 tipi del modello della tesi precedente (a–k)
un prompt dice all'LLM *quale* tipo applicare; l'LLM sceglie *dove*. Misurate due dimensioni di
validità su 141 mutanti (search.component.html completo per tutti i tipi; card parziale):

- **Coerenza**: il mutante è davvero del tipo richiesto? (confronto strutturale con l'originale; le
  allucinazioni = tipo diverso da quello chiesto). I movimenti f/g si valutano come famiglia
  (non distinguibili in modo affidabile quando i contenitori hanno lo stesso tag); `h` (tra
  template) non è realizzabile con un solo template fornito.
- **Compilazione**: il mutante compila in Angular? (applicato al dev server, esito di ricompilazione).

## Risultati per tipo

| Tipo | Descrizione | Tot | Coerenti | Compila | Validi (coerente & compila) |
|---|---|---:|---:|---:|---:|
| a | Attribute Value Modification | 20 | 19 | 10 | 9 |
| b | Attribute Removal | 20 | 15 | 19 | 14 |
| c | Attribute Identifier Modification | 19 | 18 | 2 | 1 |
| d | Text Content Modification | 10 | 6 | 9 | 6 |
| e | Text Content Removal | 10 | 6 | 9 | 6 |
| f | Tag Movement (within container) | 10 | 6 | 9 | 6 |
| g | Tag Movement (any point) | 10 | 6 | 10 | 6 |
| h | Tag Movement (between templates) | 10 | 5 | 9 | 4 |
| i | Tag Removal | 10 | 9 | 9 | 8 |
| j | Tag Type Modification | 12 | 12 | 10 | 10 |
| k | Tag Insertion | 10 | 9 | 10 | 9 |
| **TOT** | | **141** | **111** | **106** | **79** |

**Globale: coerenti 79% · compilanti 75% · validi (entrambi) 56%.**

## Confronto con la generazione static (tesi precedente)
| Metrica | Static | LLM (parametrico) |
|---|---|---|
| Mutanti che **compilano** | ~65% (FTR 64.76%) | **75%** |
| **Coerenza** col tipo | 100% per costruzione (operatori deterministici) | 79% (presenza di allucinazioni) |
| Applicabilità in generazione | 64.65% (MAR): bloccata se manca il target (es. nessun id) | genera tutti i tipi (unico scarto: duplicati) |

## Lettura
- **L'LLM produce più mutanti compilanti dello static** (75% vs 65%) e **non è bloccato** dall'assenza
  di un target applicabile (lo static non poteva istanziare ~35% delle mutazioni teoriche).
- **Per contro l'LLM introduce incoerenze/allucinazioni** (coerenza 79%) che lo static, deterministico,
  non ha. Combinando le due dimensioni, i mutanti pienamente validi sono il 56%.
- **Limiti per tipo specifici e spiegabili:**
  - `c` (rinomina attributo) compila solo all'11%: in Angular gli attributi sono spesso binding/input/
    direttive → rinominarli rompe la compilazione (limite intrinseco del tipo su template Angular).
  - `a` (modifica valore attributo) compila al 50%: l'LLM cambia spesso il valore di un *binding*
    verso una proprietà inesistente → errore AOT.
  - I tipi strutturali e di testo (b, d, e, f, g, i, k) compilano al 90–100%.
- **Tipi generati molto bene** (coerenti e compilanti): `j` (tag type), `k` (insertion), `i` (removal),
  `b` (attr removal).

## Nota
Misura su una sola applicazione (Spotify); la generalizzabilità (RQ 1.3) richiede altre app.
Output grezzi: `output/tests/param-compile.csv`; script: `coherence_analysis.py`, `compile_check.py`, `validity_1_2.py`.
