# Runbook — Baseline + mutazioni STATIC sul sottoinsieme RICERCA (Spotify)

> Scope ridotto, riproducibile. **STOP prima dell'LLM.** Spotify è solo base di confronto con Liberti.

## Obiettivo
Riprodurre baseline + mutazioni **static** sui soli tc di **area-4-search** che girano verdi sull'app attuale, produrre `stats.csv`/`batches.csv` per tc, **fermarsi** e consegnare per revisione. **Non toccare il `llm-generator`.**

## Candidati
- `tc-srch-01`, `tc-srch-02`, `tc-srch-03`, `tc-srch-05` (tc-srch-04 già fatto).
- Escludere tc legati a endpoint **403 post-2024** (`browse/categories`, `artist-top-tracks`) o a **dati specifici dell'account**.
- Tenere solo i tc con **baseline ≥ 6/7** (Hook escluso, vedi sotto).

## Passi (per ogni tc candidato)
1. Caricare i 7 file del tc nello slot: `ext-test-classes/src/main/java/org/ext/`.
2. `mvn -q compile -pl ext-test-classes -am`.
3. **Baseline** (app non mutata) → vedere quante strategie passano.
4. Se ≥ 6/7 (Hook a parte) → **tenere**; altrimenti scartare/annotare.
5. Mettere i bersagli del `mutations.json` del tc in `generator-config.json` (path miei) → `static-generator` → `mutations.db`.
6. Mutazioni × 7 strategie col **mutation-tester** → `output/tests/{stats,batches}.csv`.
7. Annotare risultati. Passare al tc successivo **a blocchi**.

## Ambiente (fatti verificati questa sessione)
- **mutation-tester richiede Java 25**: `"C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe"`. Lo static-generator gira anche con Java 17.
- **Bug uber-jar**: il jar del mutation-tester ha perso la registrazione del `junit-vintage-engine` → senza fix i test JUnit 4 NON girano (tutti "PASSED" a vuoto). **Fix non invasivo**: cartella `vintage-fix/` con `META-INF/services/org.junit.platform.engine.TestEngine` che ri-registra Jupiter+Vintage. Eseguire SEMPRE via classpath:
  ```
  java -cp "mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar;vintage-fix" org.unina.MutationTester -td "ext-test-classes/target/classes"
  ```
- **Niente flag `-b`**: il mutation-tester ha solo `-c/--config` e `-td/--test-dir`. Il **baseline** si fa con run JUnit grezzo (JUnitCore) sull'app non mutata, stessi BaseTest/WebDriverFactory.
- **App**: `http://127.0.0.1:4200` (`npm start` in `angular-spotify`). Il **baseline (JUnit grezzo)** vuole il dev server SU; il **mutation-tester** lo avvia da sé → deve essere GIÙ quando parte.
- **Profilo Chrome persistente** (`--user-data-dir=C:\Users\vince\selenium-spotify-profile`) → login una volta sola. **Shutdown hook** in WebDriverFactory per non lasciare il profilo bloccato; se un run dà `DevToolsActivePort`/crash, killare le chrome del profilo prima di rilanciare.
- **Run a blocchi**: ~30-40s a mutazione (Selenium reale). Ridurre `mutations.db` (Python sqlite3) a ~15 per stare in una finestra; backup `mutations.db.bak`.

## Vincoli (fermi)
- **Locatori MAI a mano.** Se un tc va ri-mirato → rigenerare coi tool (`custom-locators`/`hook-injector`), mai scrivere/ottimizzare xpath a mano. Anche un locatore rotto/vuoto si **rigenera**, non si inventa.
- **Asserzioni solo DOM.** Niente rete/timing/mock.
- **Non toccare** `custom-locators`, `hook-injector`, `mutation-generator`, `mutation-tester`, il codice dell'app (oltre ai guard noti `images?.[0]`, `followers?.total`).
- **Niente normalizzazione CRLF/.gitattributes**: stagiare solo i file realmente modificati.
- **Hook** = atteso rotto (toolchain VC++ per l'iniezione bloccata). NON contarlo come fragilità: è un problema di tool, documentarlo.
- **Katalon** = il prefisso `xpath=` (formato as-generated) fa `invalid selector`: rottura pre-esistente, non fragilità. Da rigenerare col tool, non a mano.
- **429 Spotify**: se durante un run arriva un 429 → marcare il run **INVALIDO** (non "obsolescenza"), fermarsi/aspettare. Andare a blocchi.

## Checkpoint
**STOP dopo lo static.** Consegnare `stats.csv`/`batches.csv` (o tabella per tc/strategia: robusto / fragile / obsolescenza / not-compiled) + totale mutazioni generate vs utili. **Non procedere con LLM** senza revisione.

## Stato (blocco static completato)
- [x] tc-srch-04 — 15 mut: strategie sane 10 success / 0 frag / 0 obsol / 5 NC. Katalon FALLISCE (xpath=, fragility 10). Hook escluso.
- [x] tc-srch-01 — 15 mut: 5 success / 0 frag / 6 obsol (mutazioni distruttive nav) / 4 NC. Output: `output/tests/srch-01/`.
- [x] tc-srch-02 — 15 mut: 7 success / 0 frag / 2 obsol / 6 NC. Output: `output/tests/srch-02/`.
- [x] tc-srch-03 — 15 mut: 7 success / 0 frag / 4 obsol / 4 NC. Output: `output/tests/srch-03/`.
- [~] tc-srch-05 — SCARTATO (baseline 0/7: asserzione getDomAttribute→null, non locatore).

## FULL static (2 tc, a blocchi di ~20) — risultato conclusivo
Obiettivo: stabilire se col SET COMPLETO (~65/tc, non 15) le strategie si differenziano. Risposta: **SÌ, discrimina** (il campione 15 lo nascondeva — sotto-campionamento).

**Fragility per strategia (full):**
| Strategia | srch-04 (68 mut, 5 str) | srch-01 (61 mut, 6 str) |
|---|---|---|
| Absolute | 7 (max) | 9 (max) |
| Relative | 6 | 5 |
| Robula | 4 | 0 |
| RobulaPlus | 1 | 1 |
| Selenium | 1 | 0 |
| Katalon | escluso (xpath=) | 0 |

- Gerarchia stabile: **Absolute < Relative < {Robula,RobulaPlus,Selenium,Katalon}** (posizionali = più fragili).
- Discriminano gli operatori **strutturali** `tag_rem`/`tag_type_mod`/`tag_ins`, NON quelli su attributi.
- 0 occorrenze 429 in tutti i blocchi.
- Output: `output/tests/srch-04-full/` e `output/tests/srch-01-full/` (block1-4 + stats-full.csv).
- 2° tc "annidato": tc-lib-* TUTTI account-specific (playlist 'The Goats'), tc-side-* non riproducibili (locatori testo imprecisi / getDomAttribute / account / incompleto) → sostituiti con **srch-01 full** (bersagli NavbarLinkLi = nav annidata).

## FULL — estensione a 4 tc (per arrivare a 200+ mutazioni)
- [x] tc-srch-02 full — 69 mut, 6 str: Absolute 8 > Relative 7 > Robula 5 > RobulaPlus 4 > Selenium 3 > Katalon 1 (fragility). 0 429. Output `output/tests/srch-02-full/`.
- **Totale a 3 tc: 198 mutazioni · 26 discriminanti · 0 429.** Gerarchia confermata 3 volte: Absolute < Relative < Robula < RobulaPlus < Selenium < Katalon.
- [x] **tc-srch-03 full — COMPLETATO** (dopo 429: pausa + probe + ripreso pulito). 68 mut, 6 str: Absolute 11 > Relative 5 > Robula/RobulaPlus/Selenium 3 > Katalon 1 (fragility). Output `output/tests/srch-03-full/`. Caveat: obsolescenza un po' gonfiata dal webpack error-overlay (mutazioni con warning) — discriminazione comunque valida.

## RISULTATO FINALE — 4 tc search full
**266 mutazioni · 37 discriminanti.** Fragility totale per strategia:
| Absolute 35 | Relative 23 | Robula 12 | RobulaPlus 9 | Selenium 7 | Katalon 2 |
**Gerarchia (4 tc, 4 conferme): Absolute < Relative < Robula < RobulaPlus < Selenium < Katalon.** Lo static DISCRIMINA; discriminano gli operatori strutturali (tag_rem/tag_type_mod/tag_ins/tag_mov_temp), non quelli su attributi.

- **CHECKPOINT FULL raggiunto. Stop prima dell'LLM.**
