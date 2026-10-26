# mutation-tester — LO STRUMENTO: esecuzione dei test sui mutanti

Questa cartella **fa parte dello strumento** (vedi [`STRUMENTO.md`](../STRUMENTO.md)).

Per ogni mutante nel database: scrive il template mutato nell'applicazione, aspetta che il server di sviluppo la **ricompili**
(se non ricompila, il mutante risulta *non compilato* e i suoi test non vengono eseguiti), esegue le classi di test con le sette
strategie di localizzazione e **ripristina** il template.

**Ingresso:** `mutations.db`, il file di configurazione dell'applicazione e le classi di test compilate.
**Uscita:** `output/tests/batches.csv` (ogni esecuzione con il suo esito) e `stats.csv` (conteggio per strategia).
Si usa con `.\mutanti.ps1 <applicazione> esegui`.
