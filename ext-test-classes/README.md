# ext-test-classes — LO STRUMENTO: classi di base dei test

Questa cartella **fa parte dello strumento** (vedi [`STRUMENTO.md`](../STRUMENTO.md)). Contiene le classi comuni a tutti i casi di test:
la classe di base (`BaseTest`), la preparazione di Chrome (`WebDriverFactory`, con profilo temporaneo) e le classi delle sette strategie.

I sorgenti dei test di ogni applicazione sono in `src/main/java/org/ext/cinelib/`, `flowboard/` e `cookbook/` (una cartella per
scenario, una classe per strategia). Le versioni compilate che usa il comando `esegui` stanno nelle cartelle `td-*` delle campagne
(`cinelib-v2-run/`, `flowboard-v2-run/`, `cookbook-run/`). La descrizione completa dei test è in [`TEST.md`](../TEST.md).
