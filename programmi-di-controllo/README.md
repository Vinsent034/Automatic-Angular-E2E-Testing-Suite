# Programmi di controllo delle campagne

Programmi (Python e `.bat`) con cui sono state eseguite le **campagne complete** della tesi: raggruppano i mutanti per template,
assegnano i test, riprendono dopo un'interruzione e riparano il server di sviluppo incastrato. Descrizione in
[`RIPRODURRE-LE-CAMPAGNE.md`](../RIPRODURRE-LE-CAMPAGNE.md).

> I percorsi dentro questi file sono quelli del PC su cui sono state fatte le campagne: sono **da adattare** prima dell'uso su un
> altro PC. Per far girare i test su un campione di mutanti, su qualsiasi PC, si usa invece `.\mutanti.ps1 <applicazione> esegui`
> (vedi [`GUIDA-RIPRODUZIONE.md`](../GUIDA-RIPRODUZIONE.md)).

| File | Campagna |
|---|---|
| `run_cinelib_v2_resume.py`, `run_flowboard_v2_resume.py`, `run_cookbook_resume.py` | esecuzione di una campagna, con ripresa e riparazione |
| `run_campagne_*.py` | esecuzione in sequenza delle campagne di un'applicazione |
| `avvia_*.bat`, `acquisisci_flowboard_v2.bat` | avvio con doppio clic |
