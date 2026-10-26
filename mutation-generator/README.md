# mutation-generator — LO STRUMENTO: generazione dei mutanti

Questa cartella **fa parte dello strumento** (vedi [`STRUMENTO.md`](../STRUMENTO.md)). Contiene i due generatori di mutanti e il codice comune.

| Modulo | Ruolo |
|---|---|
| [`static-generator`](static-generator) | generatore **statico**: modifica l'albero HTML con regole fisse (JSoup) |
| [`llm-generator`](llm-generator) | generatore **con modello linguistico**: scrive i prompt, acquisisce le risposte, produce gli operatori f, g, h |
| [`common`](common) | servizi comuni: indicizzazione dei template, ricerca degli elementi, salvataggio ed esportazione dei mutanti |

**Ingresso:** il file di configurazione e il file dei bersagli di un'applicazione. **Uscita:** il database `mutations.db` con i mutanti.
Si compila dalla radice del progetto (`installa.bat` lo fa) e si usa con `.\mutanti.ps1 <applicazione> prompt` e `completa`.
