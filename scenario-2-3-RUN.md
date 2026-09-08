# Come eseguire il mutation-tester su Scenario 2 e 3 (CineLib)

Tutto è PRONTO. I mutanti restano `HELD` finché non li attivi. Il tester gira **tutte** le
classi della `--test-dir` contro **ogni** mutante PENDING → si esegue **uno scenario alla volta**.

## Cosa è già pronto
- 12 classi di test compilate: `ext-test-classes/src/main/java/org/ext/cinelib/moviedetail/` e `.../movieform/`
- Due test-dir separate (già popolate coi `.class`):
  - `ext-test-classes/target/cinelib-only-moviedetail/`  (6 classi + base + factory)
  - `ext-test-classes/target/cinelib-only-movieform/`     (6 classi + base + factory)
- Helper stati: `set-pending.py`
- Locatori: `scenario2-dettaglio-locatori.md`, `scenario3-form-locatori.md`

## Mutanti attivabili (attualmente HELD)
- Scenario 2 (test-dir `cinelib-only-moviedetail`): `movie_detail` 124 + `cast_row` 49 = **173**
- Scenario 3 (test-dir `cinelib-only-movieform`): `movie_form` **113** (nota: 19 movie_form risultano già COMPLETE da run precedenti; questi 113 sono i restanti HELD)

## Prerequisiti del run (dal HANDOFF)
1. Dev server CineLib attivo su **http://localhost:4300** (`cd cinelib && npm start -- --port 4300`).
2. Chrome + chromedriver compatibili (WebDriverFactory usa Chrome condiviso).
3. Java per il tester come da HANDOFF (§5.5 usa Java 25 + `vintage-fix`); cross-check lì per la riga esatta.
4. Attenzione ai lock OneDrive su `*.component.html` durante la scrittura del mutante (già mitigati con retry, ma se crasha vedi HANDOFF).

## FASE A — Scenario 2 (movie-detail + cast-row)
```bash
# 1. attiva SOLO i mutanti dello scenario 2
python set-pending.py moviedetail          # 173 HELD -> PENDING

# 2. lancia il tester con la test-dir dello scenario 2
java -jar mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar \
     --test-dir ext-test-classes/target/cinelib-only-moviedetail
# (in alternativa, come nelle note: mvn exec:java -pl :mutation-tester -Dexec.args="--test-dir .../cinelib-only-moviedetail")
```
A fine run i mutanti passano a COMPLETE. Risultati nei CSV di `output/tests/` (batches.csv viene sovrascritto → salvane una copia).

## FASE B — Scenario 3 (movie-form) — solo DOPO che A è finita
```bash
python set-pending.py movieform            # 113 HELD -> PENDING
java -jar mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar \
     --test-dir ext-test-classes/target/cinelib-only-movieform
```

## Utili
- `python set-pending.py status`                 # distribuzione stati
- `python set-pending.py moviedetail --reset`    # rimette quei mutanti a HELD (annulla l'attivazione)

## Nota di validazione (importante per la tesi)
Prima di fidarsi dei numeri serve un **baseline**: con l'app NON mutata, ogni locatore deve trovare
esattamente l'elemento giusto.
✅ **Baseline già eseguito il 2026-07-09** (sul browser reale, via Playwright): tutti i **36 locatori**
(3 elementi × 6 strategie × 2 scenari) matchano esattamente 1 elemento col testo atteso.
Resta comunque consigliato un baseline completo col mutation-tester (JUnit+chromedriver) prima del run vero,
per confermare anche l'ambiente di esecuzione.
