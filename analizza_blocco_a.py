# Analisi dei CSV prodotti dalla riesecuzione della Procedura B a 7 strategie (Blocco A).
#
# Replica esattamente la classificazione dello strumento (MutationBatch.getBatchStatus e
# LocatorStats di mutation-tester), poi aggiunge la disaggregazione che il professore ha chiesto
# per Hook-Based: esito per RUOLO del nodo mutato (bersaglio contro padre, antenato, fratello).
#
# Tutto si ricava dai CSV, senza interrogare i database, perche' le due famiglie di campagne
# scrivono le stesse informazioni in colonne diverse:
#
#   campagne LINGUISTICHE   Name = operatore (a..k)     Tag = componente   ruolo nel campo Id (alf/bet/gam/del)
#   campagne STATICHE       Name = bersaglio            Tag = RUOLO        operatore nel campo Id
#
# Attenzione al raggruppamento. Nelle campagne statiche il campo Id da solo NON e' univoco (vale
# "mutation_<operatore>_<tipo>_<ruolo>", e si ripete per decine di mutanti); la coppia (Name, Id)
# invece lo e' in tutti e quattro i database (verificato il 18/09/2026). Un mutante e' quindi una
# sequenza contigua di righe con la stessa coppia (Name, Id). Non si puo' separare per nome della
# classe: nel gruppo FlowBoard s1s2 ogni mutante ha 14 righe e i nomi si ripetono (s1 e s2 hanno
# entrambi AbsoluteXPathTest...). Per lo stesso motivo gli esiti di un mutante sono una LISTA di
# (strategia, esito) e non un dizionario: come fa lo strumento, su s1s2 ogni strategia conta due
# esecuzioni per mutante.
#
# Uso: python analizza_blocco_a.py                      -> tutte le campagne che hanno dei CSV
#      python analizza_blocco_a.py cinelib-llm          -> una sola
#      python analizza_blocco_a.py --md rapporto.md     -> scrive anche il rapporto su file
import csv, glob, os, sys, collections, json, sqlite3, hashlib

SUITE = os.path.dirname(os.path.abspath(__file__))
RUOLI = {"alf": "α bersaglio", "bet": "β padre", "gam": "γ antenato", "del": "δ fratello", "eps": "ε componente"}
ALIAS = {"alpha": "alf", "beta": "bet", "gamma": "gam", "delta": "del", "epsilon": "eps"}
ORDINE_RUOLI = ["alf", "bet", "gam", "del", "eps"]
STRATEGIE = ["AbsoluteXPathTest", "RelativeXPathTest", "RobulaXPathTest", "RobulaPlusXPathTest",
             "SeleniumXPathTest", "KatalonXPathTest", "HookXPathTest"]
ETICHETTA = {"AbsoluteXPathTest": "Absolute", "RelativeXPathTest": "Relative", "RobulaXPathTest": "Robula",
             "RobulaPlusXPathTest": "Robula+", "SeleniumXPathTest": "Selenium", "KatalonXPathTest": "Katalon",
             "HookXPathTest": "Hook-Based"}

CAMPAGNE = {
 "cinelib-llm":      (SUITE,                           "CineLib linguistica"),
 "cinelib-static":   (SUITE + "/cinelib-static-run",   "CineLib statica"),
 "flowboard-llm":    (SUITE + "/flowboard-run",        "FlowBoard linguistica"),
 "flowboard-static": (SUITE + "/flowboard-static-run", "FlowBoard statica"),
 "cookbook-llm":     (SUITE + "/cookbook-run",         "CookBook linguistica"),
 "cookbook-static":  (SUITE + "/cookbook-static-run",  "CookBook statica"),
 # revisione di settembre 2026: tutte e tre le app su 11 x 5 (FlowBoard e CineLib ricostruite)
 "cookbook-ibersaglio":    (SUITE + "/cookbook-i-bersaglio-run", "CookBook, 10 mutanti «i» sul bersaglio"),
 "cookbook-static-nodup":  (SUITE + "/cookbook-static-run",      "CookBook statica, solo mutanti distinti"),
 "flowboard-v2-llm":       (SUITE + "/flowboard-v2-run",         "FlowBoard linguistica"),
 "flowboard-v2-static":    (SUITE + "/flowboard-v2-static-run",  "FlowBoard statica, mutanti distinti (testati)"),
 "flowboard-v2-static-dup":(SUITE + "/flowboard-v2-static-run",  "FlowBoard statica, con duplicati (ricostruita)"),
 "cinelib-v2-llm":         (SUITE + "/cinelib-v2-run",           "CineLib linguistica"),
 "cinelib-v2-static":      (SUITE + "/cinelib-v2-static-run",    "CineLib statica, mutanti distinti (testati)"),
 "cinelib-v2-static-dup":  (SUITE + "/cinelib-v2-static-run",    "CineLib statica, con duplicati (ricostruita)"),
}
# campagne della revisione, nell'ordine del rapporto finale
REVISIONE = ["cookbook-llm", "cookbook-static", "cookbook-static-nodup", "cookbook-ibersaglio",
             "flowboard-v2-llm", "flowboard-v2-static", "flowboard-v2-static-dup",
             "cinelib-v2-llm", "cinelib-v2-static", "cinelib-v2-static-dup"]

def descrivi(riga):
    """(ruolo, operatore) di un mutante, ricavati dalle colonne della riga."""
    tag, nome, bid = riga.get("Tag", ""), riga.get("Name", ""), riga.get("Id", "")
    if tag in ALIAS:                       # campagna statica: il ruolo sta in Tag
        ruolo = ALIAS[tag]
        pezzi = bid[len("mutation_"):].split("_") if bid.startswith("mutation_") else []
        operatore = pezzi[0] if pezzi and len(pezzi[0]) == 1 else None
    else:                                  # campagna linguistica: il ruolo sta nell'Id
        trovati = [t for t in bid.split("_") if t in RUOLI]
        ruolo = trovati[0] if len(trovati) == 1 else None
        operatore = nome if len(nome) == 1 and nome.isalpha() else None
    return ruolo, operatore

def chiave_statica(meta):
    """(Name, Id) del CSV statico ricavati dai campi del database (element = ruolo, mutation_name =
    bersaglio, mutation_type, mutation_id = operatore). Verificato il 27/09: nei tre database statici
    la quaterna è univoca, quindi lo è anche la chiave."""
    ruolo, bersaglio, tipo, op = meta
    return (bersaglio, f"mutation_{op}_{tipo}_{ruolo}")

def con_duplicati(cartella, mutanti_chiave):
    """Tabella con duplicati ricostruita ESATTAMENTE: ogni duplicato (tolto prima della campagna)
    riceve gli esiti del mutante identico che è stato testato, con il PROPRIO ruolo e operatore."""
    m = json.load(open(cartella + "/mappa-duplicati.json", encoding="utf-8"))
    extra = []
    for dup, rap in m["mappa"].items():
        esiti = mutanti_chiave[chiave_statica(m["meta"][rap])][2]
        ruolo, _, _, op = m["meta"][dup]
        extra.append((ALIAS.get(ruolo), op, esiti))
    return extra

def duplicati_da_db(cartella):
    """Chiavi CSV dei mutanti statici identici a uno precedente (stesso codice mutato), con la stessa
    regola usata per FlowBoard e CineLib: il primo di ogni gruppo di identici resta."""
    c = sqlite3.connect(cartella + "/mutations.db")
    files = collections.defaultdict(list)
    for u, p, code in c.execute("select mutation_uuid, target_file_path, mutated_code from mutated_files"):
        files[u].append((p, hashlib.md5(code.encode()).hexdigest()))
    visti, dup = set(), set()
    for u, e, n, t, i in c.execute("select uuid, element, mutation_name, mutation_type, mutation_id "
                                   "from mutations order by rowid"):
        k = tuple(sorted(files[u]))
        if k in visti: dup.add(chiave_statica((e, n, t, i)))
        else: visti.add(k)
    return dup

def leggi_batch(cartella, con_chiavi=False):
    """Lista di mutanti: (ruolo, operatore, [(strategia, esito), ...]).
    Un mutante = sequenza contigua di righe con la stessa coppia (Name, Id)."""
    mutanti, file_letti, chiavi = [], [], []
    for f in sorted(glob.glob(cartella + "/output/tests/batches-*.csv")):
        file_letti.append(os.path.basename(f))
        corrente, chiave, meta = [], None, (None, None)
        with open(f, encoding="utf-8", errors="replace", newline="") as fh:
            for riga in csv.DictReader(fh):
                loc = riga.get("Locator")
                if not loc: continue
                k = (riga.get("Name"), riga.get("Id"))
                if k != chiave:
                    if corrente: mutanti.append((*meta, corrente)); chiavi.append(chiave)
                    corrente, chiave, meta = [], k, descrivi(riga)
                corrente.append((loc, riga.get("Status")))
        if corrente: mutanti.append((*meta, corrente)); chiavi.append(chiave)
    if con_chiavi: return mutanti, file_letti, chiavi
    return mutanti, file_letti

def stato_batch(esiti):
    applicabili = [s for _, s in esiti if s != "NOT_APPLICABLE"]
    if not applicabili:             return "NOT_TESTED"
    passati = sum(1 for s in applicabili if s == "PASSED")
    if passati == 0:                return "OBSOLETE"
    if passati == len(applicabili): return "ROBUST"
    return "FRAGILE"

def conta(mutanti):
    """Riproduce LocatorStats: per ogni strategia successi, fragilita', obsolescenza, non testati."""
    st = collections.defaultdict(lambda: dict(successi=0, fragilita=0, obsolescenza=0, non_testati=0))
    esiti_batch = collections.Counter()
    for _, _, esiti in mutanti:
        s = stato_batch(esiti); esiti_batch[s] += 1
        for strategia, esito in esiti:
            c = st[strategia]
            if   s == "OBSOLETE":   c["obsolescenza"] += 1
            elif s == "ROBUST":     c["successi"] += 1
            elif s == "NOT_TESTED": c["non_testati"] += 1
            else:                   c["successi" if esito == "PASSED" else "fragilita"] += 1
    return st, esiti_batch

def tabella(mutanti, titolo):
    st, esiti = conta(mutanti)
    righe = [f"\n**{titolo}** — {len(mutanti)} mutanti "
             f"(robusti {esiti['ROBUST']}, fragili {esiti['FRAGILE']}, "
             f"obsoleti {esiti['OBSOLETE']}, non compilati {esiti['NOT_TESTED']})\n",
             "| Strategia | Testati | Successi | Fragilità | Obsolescenza | Non compilati | Fragilità % |",
             "|---|---:|---:|---:|---:|---:|---:|"]
    for s in STRATEGIE:
        if s not in st: continue
        c = st[s]; tot = sum(c.values()); base = tot - c["non_testati"]
        perc = f"{100*c['fragilita']/base:.1f}" if base else "—"
        righe.append(f"| {ETICHETTA[s]} | {tot} | {c['successi']} | {c['fragilita']} | "
                     f"{c['obsolescenza']} | {c['non_testati']} | {perc} |")
    ignote = [s for s in st if s not in STRATEGIE]
    if ignote: righe.append(f"\n> classi non riconosciute nei CSV: {ignote}")
    return "\n".join(righe)

def analizza(chiave):
    cartella, nome = CAMPAGNE[chiave]
    mutanti, file_letti, chiavi = leggi_batch(cartella, con_chiavi=True)
    if not mutanti:
        return f"\n## {nome}\n\nNessun `batches-*.csv`: campagna non ancora eseguita.\n"
    nota = ""
    if chiave.endswith("-dup"):
        extra = con_duplicati(cartella, dict(zip(chiavi, mutanti)))
        nota = (f"\n\n{len(mutanti)} mutanti testati + {len(extra)} duplicati ricostruiti = "
                f"{len(mutanti) + len(extra)} mutanti prodotti dal generatore statico.")
        mutanti = mutanti + extra
    elif chiave.endswith("-nodup"):
        dup = duplicati_da_db(cartella)
        prima = len(mutanti)
        mutanti = [m for m, k in zip(mutanti, chiavi) if k not in dup]
        nota = f"\n\n{prima} mutanti testati, di cui {prima - len(mutanti)} duplicati tolti: {len(mutanti)} distinti."

    out = [f"\n## {nome}\n", f"File letti: {', '.join(file_letti)}" + nota]
    incoerenti = [m for m in mutanti if len(m[2]) not in (7, 14)]
    if incoerenti:
        out.append(f"\n> **attenzione**: {len(incoerenti)} mutanti con un numero di esecuzioni "
                   f"diverso da 7 o 14 (il primo ne ha {len(incoerenti[0][2])}). Blocchi mal separati?")
    out.append(tabella(mutanti, "Complessivo"))

    if any(r for r, _, _ in mutanti):
        out.append("\n### Per ruolo del nodo mutato\n")
        out.append("Il ruolo è la posizione del nodo mutato rispetto al bersaglio del test. "
                   "Sul bersaglio l'attributo di riferimento è preservato per costruzione: uno zero "
                   "di fragilità in quella riga è una proprietà della progettazione dell'esperimento, "
                   "non una misura di robustezza. Sui ruoli di contesto la misura è invece informativa.")
        for r in ORDINE_RUOLI:
            sel = [m for m in mutanti if m[0] == r]
            if sel: out.append(tabella(sel, RUOLI[r]))
        senza = [m for m in mutanti if m[0] is None]
        if senza: out.append(tabella(senza, "senza ruolo riconosciuto"))

    ops = sorted({o for _, o, _ in mutanti if o})
    if ops:
        out.append("\n### Hook-Based per operatore\n")
        out.append("| Operatore | Mutanti | Successi | Fragilità | Obsolescenza | Non compilati |")
        out.append("|---|---:|---:|---:|---:|---:|")
        for o in ops:
            sel = [m for m in mutanti if m[1] == o]
            c = conta(sel)[0].get("HookXPathTest")
            if not c: continue
            out.append(f"| {o} | {len(sel)} | {c['successi']} | {c['fragilita']} | "
                       f"{c['obsolescenza']} | {c['non_testati']} |")
    return "\n".join(out) + "\n"

scelte = ([a for a in sys.argv[1:] if a in CAMPAGNE] or
          (REVISIONE if "--revisione" in sys.argv else list(CAMPAGNE)))
titolo = ("# Revisione (settembre 2026) — tre app su 11 × 5, 7 strategie\n" if "--revisione" in sys.argv
          else "# Blocco A — riesecuzione della Procedura B a 7 strategie\n")
rapporto = [titolo,
            "Classificazione identica a quella dello strumento: un mutante è *obsoleto* se nessuna "
            "strategia lo supera, *robusto* se le superano tutte, *fragile* altrimenti. "
            "La fragilità di una strategia si conta solo sui mutanti fragili.\n"]
for k in scelte:
    rapporto.append(analizza(k))
testo = "\n".join(rapporto)
print(testo)
if "--md" in sys.argv:
    dest = sys.argv[sys.argv.index("--md") + 1]
    open(dest, "w", encoding="utf-8").write(testo)
    print(f"\n[scritto {dest}]")
