# flowboard-v2-run — DATI della campagna (FlowBoard, con il generatore con modello linguistico)

Questa cartella **non è lo strumento**: sono i **dati** di una campagna della tesi, cioè ciò che lo strumento ha letto e prodotto
(vedi [`STRUMENTO.md`](../STRUMENTO.md)). Contiene, secondo la campagna: prompt e risposte del modello (`output/mutations/`),
le classi di test compilate (`td-*`), i registri e gli esiti dei test (`output/tests/batches-*.csv`).

I mutanti di questa campagna, in forma leggibile, sono in [`mutazioni-generate/`](../mutazioni-generate).
Per generare nuovi mutanti non si lavora qui ma in una cartella nuova, `generazione-<applicazione>`, creata da `mutanti.ps1`.
