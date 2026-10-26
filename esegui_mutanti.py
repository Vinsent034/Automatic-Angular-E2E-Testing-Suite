# -*- coding: utf-8 -*-
"""Esegue i test sui mutanti di una generazione (cartella generazione-<applicazione>, creata da
mutanti.ps1) e stampa i risultati. Funziona da qualsiasi cartella in cui sia stato scaricato il progetto:
i percorsi si ricavano dalla posizione di questo file e dai file di configurazione.

Uso (dal terminale aperto con terminale.bat):   .\\mutanti.ps1 flowboard esegui 20

Ripresa dal programma di controllo delle campagne della tesi (run_*_resume.py), con queste differenze:
  - niente percorsi del PC originale: Java, applicazione e test sono quelli del progetto scaricato;
  - si esegue un campione di N mutanti (seme fisso 1234, quindi sempre gli stessi) e non tutta la campagna;
  - i template originali dell'applicazione sono copiati prima e rimessi a posto alla fine, sempre;
  - Chrome usa un profilo temporaneo (non il profilo personale).
Resta la regola di riparazione delle campagne: se il server di sviluppo resta incastrato dopo un mutante che
non compila, i mutanti successivi sono rifatti con un server nuovo."""
import argparse, csv, glob, json, ntpath, os, random, re, shutil, sqlite3, subprocess, sys, time
from collections import defaultdict

SUITE = os.path.dirname(os.path.abspath(__file__))
SEME = 1234
MAX_AVVII = 3     # tentativi se il server di sviluppo non parte
MAX_GIRI = 6      # giri di riparazione al massimo per gruppo

# applicazione -> file di configurazione, cartella dei test compilati (campagna della tesi), gruppi di test
APPS = {
    "cinelib": {
        "config": "generator-config-cinelib.json", "tesi": "cinelib-v2-run",
        "ordine": ["catalogsearch", "moviedetail", "movieform", "reviews", "stats"],
        "comp2group": {"catalog": "catalogsearch", "movie-card": "catalogsearch",
                       "movie-detail": "moviedetail", "cast-row": "moviedetail",
                       "movie-form": "movieform", "reviews": "reviews", "stats": "stats"},
    },
    "flowboard": {
        "config": "generator-config-flowboard.json", "tesi": "flowboard-v2-run",
        "ordine": ["s1s2", "s3", "s4", "s5", "s6"],
        "comp2group": {"board": "s1s2", "card": "s1s2", "column": "s1s2", "card-form": "s3",
                       "card-detail": "s4", "toast": "s4", "stats": "s5", "header": "s6", "app": "s6"},
    },
    "cookbook": {
        "config": "generator-config-cookbook.json", "tesi": "cookbook-run",
        "ordine": ["s1s2", "s3", "s4", "s5", "s6"],
        "comp2group": {"recipe-list": "s1s2", "recipe-card": "s1s2",
                       "recipe-detail": "s3", "ingredient-row": "s3", "step-item": "s3", "panel": "s3",
                       "recipe-form": "s4", "shopping-list": "s5",
                       "stats": "s6", "app": "s6", "header": "s6", "toast": "s6"},
    },
}
STRATEGIE = [("AbsoluteXPathTest", "Absolute"), ("RelativeXPathTest", "Relative"), ("RobulaXPathTest", "ROBULA"),
             ("RobulaPlusXPathTest", "ROBULA+"), ("SeleniumXPathTest", "Selenium"), ("KatalonXPathTest", "Katalon"),
             ("HookXPathTest", "Hook-Based")]
INTESTAZIONE = "Name,Id,Locator,Tag,Status,Error"


def log(m):
    print(f"[{time.strftime('%H:%M:%S')}] {m}", flush=True)


def esci(msg):
    print("\nERRORE: " + msg, flush=True)
    sys.exit(1)


# ------------------------------------------------------------------ argomenti e controlli
ap = argparse.ArgumentParser(description="Esegue i test su un campione di mutanti di una generazione.")
ap.add_argument("app", choices=sorted(APPS))
ap.add_argument("--quanti", type=int, default=20, help="numero di mutanti da provare (default 20)")
ap.add_argument("--tecnica", choices=["llm", "statico"], default="llm", help="quale generazione provare: con il modello (default) o con il generatore statico")
ap.add_argument("--gruppo", help="un solo gruppo di test (default: quello con piu' mutanti)")
ap.add_argument("--tutti-i-gruppi", action="store_true", help="campione su tutti i gruppi (piu' lento: un avvio del server per gruppo)")
args = ap.parse_args()
A = APPS[args.app]

CAMP = os.path.join(SUITE, f"generazione-{args.app}" + ("-statico" if args.tecnica == "statico" else ""))
DB = os.path.join(CAMP, "mutations.db")
OUT = os.path.join(CAMP, "output", "tests")
CFG = os.path.join(SUITE, A["config"])
TDBASE = os.path.join(SUITE, A["tesi"])
GROUP_TD = {g: os.path.join(TDBASE, f"td-{g}") for g in A["ordine"]}

if not os.path.exists(DB):
    esci(f"non trovo {DB}. Prima si prepara la generazione: " + (".\\mutanti.ps1 " + args.app + " statico" if args.tecnica == "statico" else "prompt, modello e completa") + ".")
con = sqlite3.connect(DB)
n_mut = con.execute("select count(*) from mutations").fetchone()[0]
con.close()
if n_mut == 0:
    esci("il database e' vuoto: fai prima  .\\mutanti.ps1 " + args.app + (" statico" if args.tecnica == "statico" else " completa"))
if not os.path.exists(CFG):
    esci(f"manca {CFG}")
cfg = json.load(open(CFG, encoding="utf-8"))
APPROOT = cfg["repositoryRootPath"]
NPM = cfg["npmRunCommand"]
m = re.search(r"--port\s+(\d+)", NPM)
PORT = int(m.group(1)) if m else 4200
APPSRC = os.path.join(APPROOT, "src", "app")
if not os.path.isdir(APPSRC):
    esci(f"non trovo l'applicazione in {APPROOT}. Hai spostato la cartella dopo installa.bat? Rilancia installa.bat.")
if not os.path.isdir(os.path.join(APPROOT, "node_modules")):
    esci("mancano le dipendenze dell'applicazione (node_modules): rilancia installa.bat.")

JAVA = None
for cand in (os.path.join(os.environ.get("JAVA_HOME", ""), "bin", "java.exe"),
             os.path.join(SUITE, "strumenti", "jdk", "bin", "java.exe")):
    if os.path.exists(cand):
        JAVA = cand
        break
if JAVA is None:
    esci("non trovo Java: apri il terminale con terminale.bat (o lancia installa.bat).")
JAR = os.path.join(SUITE, "mutation-tester", "target", "mutation-tester-1.0.0-jar-with-dependencies.jar")
if not os.path.exists(JAR):
    esci("manca lo strumento compilato (mutation-tester): lancia installa.bat.")
VINTAGE = os.path.join(SUITE, "vintage-fix")
DRIVER_SRC = os.path.join(SUITE, "ext-test-classes", "target", "classes", "org", "ext", "WebDriverFactory.class")
if not os.path.exists(DRIVER_SRC) or b"selenium.profile" not in open(DRIVER_SRC, "rb").read():
    esci("la classe del driver di Chrome non e' aggiornata: rilancia installa.bat (ricompila lo strumento).")
for g, td in GROUP_TD.items():
    if not os.path.isdir(td):
        esci(f"mancano i test compilati {td}")

# ------------------------------------------------------------------ utilita'
def n_classes(td):
    return len(glob.glob(td + "/**/*XPathTest.class", recursive=True))


def kill_port():
    try:
        subprocess.run(["powershell", "-NoProfile", "-Command",
                        f"Get-NetTCPConnection -LocalPort {PORT} -State Listen -ErrorAction SilentlyContinue | "
                        "ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }"],
                       timeout=60, capture_output=True)
    except Exception as e:
        log(f"kill_port: {e}")


ORIGINALI = os.path.join(CAMP, "_template-originali")


def salva_originali():
    """Copia i template dell'applicazione al loro stato iniziale. Se la cartella esiste gia', una corsa
    precedente e' stata interrotta: i template potrebbero essere mutati, quindi si ripristinano PRIMA da li'."""
    if os.path.isdir(ORIGINALI):
        ripristina()
        return
    for src in glob.glob(APPSRC + "/**/*.component.html", recursive=True):
        dst = os.path.join(ORIGINALI, os.path.relpath(src, APPSRC))
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copyfile(src, dst)


def ripristina():
    n = 0
    for src in glob.glob(ORIGINALI + "/**/*.component.html", recursive=True):
        dst = os.path.join(APPSRC, os.path.relpath(src, ORIGINALI))
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copyfile(src, dst)
        n += 1
    return n


def prewarm():
    """Avvia e ferma una volta il server dell'applicazione: scalda le cache, cosi' l'avvio fatto dallo strumento
    (che concede solo 120 secondi) non va in scadenza. Il primo avvio e' il piu' lento."""
    kill_port(); time.sleep(2)
    t = time.time()
    p = subprocess.Popen(["cmd.exe", "/c", NPM], cwd=APPROOT, stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                         text=True, encoding="utf-8", errors="replace")
    pronto = False
    try:
        for riga in p.stdout:
            if "Application bundle generation complete" in riga or "Compiled successfully" in riga:
                pronto = True
                break
            if time.time() - t > 300:
                break
    finally:
        subprocess.run(["taskkill", "/PID", str(p.pid), "/T", "/F"], capture_output=True)
        kill_port(); time.sleep(3)
    log(f"avvio di prova del server dell'applicazione: {'pronto' if pronto else 'NON pronto'} in {int(time.time() - t)} s")


def mappa_chiavi():
    con = sqlite3.connect(DB)
    mp = {}
    for uuid, el, name, mtype, mid in con.execute(
            "select uuid, element, mutation_name, mutation_type, mutation_id from mutations"):
        mp[(name, f"mutation_{mid}_{mtype}_{el}")] = uuid
    con.close()
    return mp


def leggi_blocchi(path, k):
    with open(path, encoding="utf-8", errors="replace", newline="") as fh:
        righe = [r for r in csv.DictReader(fh) if r.get("Locator")]
    if len(righe) % k:
        raise ValueError(f"{os.path.basename(path)}: {len(righe)} righe non divisibili per {k}")
    blocchi = [righe[i:i + k] for i in range(0, len(righe), k)]
    for b in blocchi:
        if len({(r["Name"], r["Id"]) for r in b}) != 1:
            raise ValueError("blocco con mutanti diversi")
    return blocchi


def non_compila(blocco):
    return all(r["Status"] == "NOT_APPLICABLE" for r in blocco)


def scrivi_blocchi(path, blocchi):
    campi = INTESTAZIONE.split(",")
    with open(path, "w", encoding="utf-8", newline="") as fh:
        fh.write(INTESTAZIONE + "\n")
        for b in blocchi:
            for r in b:
                fh.write(",".join((r.get(c) or "") for c in campi) + "\n")


# ------------------------------------------------------------------ scelta dei mutanti
con = sqlite3.connect(DB)
comps = defaultdict(list)
for uuid, path in con.execute("select mutation_uuid, target_file_path from mutated_files"):
    comps[uuid].append(ntpath.basename(path).replace("\\", "/").split("/")[-1].replace(".component.html", ""))
grp = defaultdict(list)
for (uuid,) in con.execute("select uuid from mutations order by rowid"):
    g = next((A["comp2group"][c] for c in comps.get(uuid, []) if c in A["comp2group"]), None)
    if g:
        grp[g].append(uuid)
con.close()

if args.gruppo:
    if args.gruppo not in A["ordine"]:
        esci(f"gruppo sconosciuto: {args.gruppo} (validi: {', '.join(A['ordine'])})")
    scelta_gruppi = [args.gruppo]
elif args.tutti_i_gruppi:
    scelta_gruppi = list(A["ordine"])
else:
    scelta_gruppi = [max(A["ordine"], key=lambda g: len(grp[g]))]
pool = [(g, u) for g in scelta_gruppi for u in grp[g]]
if not pool:
    esci("nessun mutante nei gruppi scelti.")
rnd = random.Random(SEME)
campione = rnd.sample(pool, min(args.quanti, len(pool)))
per_gruppo = defaultdict(list)
for g, u in campione:
    per_gruppo[g].append(u)
todo = [g for g in A["ordine"] if per_gruppo[g]]
log(f"applicazione {args.app}: {n_mut} mutanti nel database; ne provo {len(campione)} "
    f"(campione casuale, seme {SEME}) nei gruppi di test: "
    + ", ".join(f"{g}={len(per_gruppo[g])}" for g in todo))
log("si apriranno finestre di Chrome da sole: non toccare mouse e tastiera finche' non finisce.")

# ------------------------------------------------------------------ classe del driver con profilo temporaneo
DRIVERDIR = os.path.join(CAMP, "_classi-driver")
os.makedirs(os.path.join(DRIVERDIR, "org", "ext"), exist_ok=True)
shutil.copyfile(DRIVER_SRC, os.path.join(DRIVERDIR, "org", "ext", "WebDriverFactory.class"))
PROFILO = os.path.join(CAMP, "_profilo-chrome")
CP = os.pathsep.join([DRIVERDIR, JAR, VINTAGE])


def esegui(uuids, td, k, etichetta):
    """Esegue lo strumento sui soli mutanti indicati, con un server dell'applicazione nuovo.
    Ritorna i blocchi prodotti, o None se il server non parte dopo MAX_AVVII tentativi."""
    batches = os.path.join(OUT, "batches.csv")
    for tentativo in range(1, MAX_AVVII + 1):
        con = sqlite3.connect(DB)
        con.execute("update mutations set status='HELD'")
        con.executemany("update mutations set status='PENDING' where uuid=?", [(u,) for u in uuids])
        con.commit(); con.close()
        ripristina()
        kill_port(); time.sleep(3)
        if os.path.exists(batches):
            os.remove(batches)
        shutil.rmtree(PROFILO, ignore_errors=True)
        nome_log = os.path.join(CAMP, f"log-{etichetta}" + (f"-avvio{tentativo}" if tentativo > 1 else "") + ".txt")
        t0 = time.time()
        with open(nome_log, "w", encoding="utf-8") as lf:
            try:
                p = subprocess.Popen(
                    [JAVA, f"-Dselenium.profile={PROFILO}", "-cp", CP, "org.unina.MutationTester",
                     "-td", td, "--config", CFG], cwd=CAMP, stdout=lf, stderr=subprocess.STDOUT)
                ultimo = ""
                while p.poll() is None:
                    time.sleep(20)
                    try:
                        testo = open(nome_log, encoding="utf-8", errors="replace").read()
                        righe = re.findall(r"(\d+) out of (\d+) remaining", testo)
                        if righe and righe[-1][0] != ultimo:
                            ultimo = righe[-1][0]
                            log(f"{etichetta}: in corso, mancano {righe[-1][0]} mutanti su {righe[-1][1]} "
                                f"({int(time.time() - t0)} s)")
                    except OSError:
                        pass
                    if time.time() - t0 > 4 * 3600:
                        p.kill()
                        break
            except Exception as e:
                log(f"{etichetta}: errore dello strumento: {e}")
        kill_port(); time.sleep(3)
        ripristina()
        if os.path.exists(batches):
            return leggi_blocchi(batches, k)
        log(f"{etichetta}: nessun risultato (il server dell'applicazione non e' partito?) - tentativo {tentativo}/{MAX_AVVII}")
        if tentativo < MAX_AVVII:
            prewarm()
    return None


def completa_gruppo(g, uuids):
    td = GROUP_TD[g]
    k = n_classes(td)
    mappa = mappa_chiavi()
    accettati, ordine = {}, []
    inneschi = []
    richiesti = set(uuids)
    coda, giro = list(uuids), 0
    while coda and giro < MAX_GIRI:
        giro += 1
        etichetta = g if giro == 1 else f"{g}-rip{giro - 1}"
        blocchi = esegui(coda, td, k, etichetta)
        if blocchi is None:
            log(f"gruppo {g}: il server non e' mai partito dopo {MAX_AVVII} tentativi - mi fermo")
            return None
        coppie = []
        for b in blocchi:
            chiave = (b[0]["Name"], b[0]["Id"])
            if chiave in mappa and mappa[chiave] in richiesti and mappa[chiave] not in accettati:
                coppie.append((mappa[chiave], b))
        # coda finale di mutanti non compilati: il primo e' girato su un server sano e vale, gli altri sono sospetti
        coda_nc = 0
        for _, b in reversed(coppie):
            if non_compila(b):
                coda_nc += 1
            else:
                break
        if coda_nc >= 2:
            taglio = len(coppie) - coda_nc
            validi, sospetti = coppie[:taglio + 1], [u for u, _ in coppie[taglio + 1:]]
            inneschi.append(coppie[taglio][0])
        else:
            validi, sospetti = coppie, []
        for u, b in validi:
            accettati[u] = b
            ordine.append(u)
        visti = {u for u, _ in coppie}
        mancanti = [u for u in coda if u not in visti and u not in accettati]
        nota = (f"server incastrato dopo il mutante {len(validi)}: rifaccio {len(sospetti)} mutanti con un server nuovo"
                if sospetti else "nessun inceppamento")
        log(f"gruppo {g} giro {giro}: {len(coppie)} eseguiti, {len(validi)} accettati, {nota}")
        coda = sospetti + mancanti
        if coda and not sospetti and not coppie:
            break
    scrivi_blocchi(os.path.join(OUT, f"batches-{g}.csv"), [accettati[u] for u in ordine])
    return [accettati[u] for u in ordine], k


# ------------------------------------------------------------------ programma principale
os.makedirs(OUT, exist_ok=True)
salva_originali()
tutti_i_blocchi = []
t_inizio = time.time()
try:
    prewarm()
    for g in todo:
        log(f"gruppo {g}: {len(per_gruppo[g])} mutanti | classi di test per mutante: {n_classes(GROUP_TD[g])}")
        r = completa_gruppo(g, per_gruppo[g])
        if r is None:
            ripristina()
            esci(f"il gruppo {g} non e' stato completato. Rilancia il comando: si riparte da capo su questo gruppo.")
        tutti_i_blocchi.append(r)
finally:
    kill_port()
    n = ripristina()
    shutil.rmtree(PROFILO, ignore_errors=True)
    log(f"template dell'applicazione rimessi come all'inizio ({n} file)")

# ------------------------------------------------------------------ risultati
def unita_di(blocco, k):
    """Un blocco ha k esecuzioni (una per classe di test). In FlowBoard s1s2 sono 14 = due scenari da 7:
    ogni scenario si classifica da solo (stesso criterio dell'analisi della tesi)."""
    return [blocco[i:i + 7] for i in range(0, len(blocco), 7)] if k == 14 else [blocco]


tot = {nome: {"succ": 0, "frag": 0, "nc": 0, "obs": 0} for _, nome in STRATEGIE}
mutanti_provati = mutanti_nc = unita_totali = unita_obs = 0
dettaglio = []
for blocchi, k in tutti_i_blocchi:
    for b in blocchi:
        mutanti_provati += 1
        if non_compila(b):
            mutanti_nc += 1
        for u in unita_di(b, k):
            unita_totali += 1
            nc = all(r["Status"] == "NOT_APPLICABLE" for r in u)
            obs = (not nc) and all(r["Status"] != "PASSED" for r in u)
            if obs:
                unita_obs += 1
            for r in u:
                nome = dict(STRATEGIE).get(r["Locator"])
                if nome is None:
                    continue
                if nc:
                    tot[nome]["nc"] += 1
                elif obs:
                    tot[nome]["obs"] += 1
                elif r["Status"] == "PASSED":
                    tot[nome]["succ"] += 1
                else:
                    tot[nome]["frag"] += 1
        dettaglio.append((b[0]["Name"], b[0]["Id"], "non compila" if non_compila(b) else "compila"))

righe = []
righe.append(f"RISULTATI - {args.app} ({'generatore statico' if args.tecnica == 'statico' else 'generatore con LLM'}) - {mutanti_provati} mutanti provati (campione casuale, seme {SEME})")
righe.append(f"  Mutanti che compilano (validi): {mutanti_provati - mutanti_nc}   |   non compilati: {mutanti_nc}")
testabili = unita_totali - sum(1 for blocchi, k in tutti_i_blocchi for b in blocchi if non_compila(b) for _ in unita_di(b, k))
righe.append(f"  Prove sprecate (falliscono con tutte le strategie): {unita_obs} su {testabili} provate"
             + (f" ({100 * unita_obs / testabili:.1f}%)" if testabili else ""))
righe.append("")
righe.append(f"  {'Strategia':<12} {'Successi':>9} {'Fragilita':>10} {'Stabilita (successi/valutabili)':>34}")
for _, nome in STRATEGIE:
    t = tot[nome]
    val = t["succ"] + t["frag"]
    ts = f"{100 * t['succ'] / val:.1f}%" if val else "-"
    righe.append(f"  {nome:<12} {t['succ']:>9} {t['frag']:>10} {ts:>34}")
righe.append("")
righe.append("  Con pochi mutanti i numeri sono solo indicativi: la tesi usa tutti i mutanti di ogni applicazione.")
righe.append(f"  File: {OUT}")
testo = "\n".join(righe)
print("\n" + testo, flush=True)
with open(os.path.join(OUT, "riepilogo.json"), "w", encoding="utf-8") as fh:
    json.dump({"app": args.app, "tecnica": args.tecnica, "provati": mutanti_provati, "validi": mutanti_provati - mutanti_nc,
               "non_compilati": mutanti_nc, "prove_valutabili": testabili, "prove_sprecate": unita_obs, "strategie": tot},
              fh, ensure_ascii=False, indent=1)
with open(os.path.join(OUT, "riepilogo.txt"), "w", encoding="utf-8") as fh:
    fh.write(testo + "\n")
with open(os.path.join(OUT, "mutanti-provati.csv"), "w", encoding="utf-8", newline="") as fh:
    fh.write("Nome,Id,Esito\n")
    for nome, id_, esito in dettaglio:
        fh.write(f"{nome},{id_},{esito}\n")
log(f"finito in {int(time.time() - t_inizio)} secondi")
