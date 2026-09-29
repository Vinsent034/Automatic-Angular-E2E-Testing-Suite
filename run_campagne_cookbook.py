# CookBook (Blocco B, passo 10) — Procedura A e B sulle due campagne, 7 strategie.
# Stessa struttura di run_blocco_a.py: controlli iniziali, PC tenuto sveglio, campagne in sequenza,
# ciascuna con ripresa per gruppo e riparazione degli inceppamenti (run_cookbook_resume.py).
#   1. CookBook linguistica (cookbook-run, 1929 mutanti)
#   2. CookBook statica     (cookbook-static-run, 1503 mutanti)
# Le campagne non possono girare in parallelo: condividono il profilo Chrome, i sorgenti e la porta.
# Uso: python run_campagne_cookbook.py            -> tutte e due
#      python run_campagne_cookbook.py 2          -> solo la statica
#      python run_campagne_cookbook.py --check    -> solo i controlli iniziali
import subprocess, sys, os, time, glob, sqlite3

SUITE = "C:/Users/vince/OneDrive/Desktop/Università/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
JAVA  = "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe"
JAR   = SUITE + "/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar"

CAMPAGNE = [
 ("CookBook linguistica", [sys.executable, "-u", SUITE + "/run_cookbook_resume.py", "llm"],    SUITE),
 ("CookBook statica",     [sys.executable, "-u", SUITE + "/run_cookbook_resume.py", "static"], SUITE),
]

def log(m): print(f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] {m}", flush=True)

def tieni_sveglio():
    """Come in run_blocco_a.py: niente sospensione né schermo spento finché il processo è vivo."""
    try:
        import ctypes
        ES_CONTINUOUS, ES_SYSTEM_REQUIRED, ES_DISPLAY_REQUIRED = 0x80000000, 0x00000001, 0x00000002
        ok = ctypes.windll.kernel32.SetThreadExecutionState(ES_CONTINUOUS | ES_SYSTEM_REQUIRED | ES_DISPLAY_REQUIRED)
        log("PC e schermo tenuti svegli fino a fine run" if ok else "SetThreadExecutionState fallita: il PC potrebbe sospendersi")
    except Exception as e:
        log(f"keep-awake non disponibile: {e}")

def preflight():
    ok = True
    if not os.path.isfile(JAVA): log("PREFLIGHT: manca Java 25 -> " + JAVA); ok = False
    if not os.path.isfile(JAR):  log("PREFLIGHT: manca il jar del mutation-tester"); ok = False
    for td in glob.glob(SUITE + "/cookbook-run/td-*"):
        k = len(glob.glob(td + "/**/*XPathTest.class", recursive=True))
        atteso = 14 if td.endswith("td-s1s2") else 7
        if k != atteso:
            log(f"PREFLIGHT: {os.path.basename(td)} ha {k} classi di test, attese {atteso}"); ok = False
    for db, atteso in [(SUITE + "/cookbook-run/mutations.db", 1929),
                       (SUITE + "/cookbook-static-run/mutations.db", 1503)]:
        con = sqlite3.connect(db)
        n = con.execute("select count(*) from mutations").fetchone()[0]
        mancanti = sum(1 for (p,) in con.execute("select distinct target_file_path from mutated_files")
                       if not os.path.exists(p))
        con.close()
        if n != atteso: log(f"PREFLIGHT: {os.path.basename(os.path.dirname(db))}/mutations.db ha {n} mutanti, attesi {atteso}"); ok = False
        if mancanti:    log(f"PREFLIGHT: {db} -> {mancanti} percorsi sorgente inesistenti"); ok = False
    log("PREFLIGHT: " + ("tutto a posto" if ok else "PROBLEMI, vedi sopra"))
    return ok

scelte = [int(a) for a in sys.argv[1:] if a.isdigit() and 1 <= int(a) <= 2] or [1, 2]
ok = preflight()
if "--check" in sys.argv:
    sys.exit(0 if ok else 1)
if not ok:
    sys.exit("preflight fallito: non lancio nulla.")

log(f"COOKBOOK — campagne da eseguire: {scelte}")
tieni_sveglio()
overall = time.time()
falliti = []
for i in scelte:
    nome, cmd, cwd = CAMPAGNE[i - 1]
    log(f"===== CAMPAGNA {i}/2 — {nome} — avvio =====")
    t = time.time()
    rc = subprocess.run(cmd, cwd=cwd).returncode
    dur = int(time.time() - t)
    if rc == 0:
        log(f"===== CAMPAGNA {i}/2 — {nome} — COMPLETATA in {dur//3600}h{(dur%3600)//60:02d}m =====")
    else:
        falliti.append((i, nome))
        log(f"===== CAMPAGNA {i}/2 — {nome} — INTERROTTA (exit {rc}) dopo {dur//3600}h{(dur%3600)//60:02d}m =====")

dur = int(time.time() - overall)
log(f"COOKBOOK finito in {dur//3600}h{(dur%3600)//60:02d}m")
if falliti:
    log("INCOMPLETE, da rilanciare: " + ", ".join(f"{i} ({n})" for i, n in falliti))
    sys.exit(1)
log("tutte le campagne complete.")
