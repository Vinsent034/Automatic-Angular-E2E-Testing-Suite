# BLOCCO A — riesecuzione della Procedura B sulle 4 campagne con 7 strategie
# (le 6 storiche piu' Hook-Based, reintegrata dopo la revisione del 15/09/2026).
#
# Esegue in sequenza, dalla piu' corta alla piu' lunga:
#   1. CineLib linguistica   (mutations.db nella radice, 807 mutanti)   ~3-4 h
#   2. CineLib statica       (cinelib-static-run, 1142 mutanti)         ~6-7 h
#   3. FlowBoard linguistica (flowboard-run, 1225 mutanti)              ~5-6 h
#   4. FlowBoard statica     (flowboard-static-run, 2840 mutanti)       ~7 h
#
# Le campagne NON possono girare in parallelo: condividono il profilo Chrome persistente
# (una sola istanza per volta) e, a coppie, i sorgenti dell'applicazione e la porta.
#
# Ogni script figlio ha ripresa per gruppo: se una campagna fallisce si passa comunque alla
# successiva e alla fine viene stampato l'elenco di quelle incomplete, da rilanciare.
# Uso: python run_blocco_a.py            -> tutte e quattro
#      python run_blocco_a.py 3 4        -> solo le campagne 3 e 4
import subprocess, sys, os, time, glob, sqlite3

SUITE = "C:/Users/vince/OneDrive/Desktop/Università/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
JAVA  = "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe"
JAR   = SUITE + "/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar"

CAMPAGNE = [
 ("CineLib linguistica",   [sys.executable, "-u", SUITE + "/run_cinelib_llm_resume.py"],                SUITE),
 ("CineLib statica",       [sys.executable, "-u", SUITE + "/cinelib-static-run/run_cinelib_static_resume.py"], SUITE + "/cinelib-static-run"),
 ("FlowBoard linguistica", [sys.executable, "-u", SUITE + "/run_flowboard_resume.py", "llm"],            SUITE),
 ("FlowBoard statica",     [sys.executable, "-u", SUITE + "/run_flowboard_resume.py", "static"],         SUITE),
]

def log(m): print(f"[{time.strftime('%Y-%m-%d %H:%M:%S')}] {m}", flush=True)

def tieni_sveglio():
    """Impedisce sospensione e spegnimento dello schermo per inattivita' finche' questo processo e'
    vivo. Non modifica le impostazioni di risparmio energetico: la richiesta decade all'uscita.
    Lo schermo resta acceso di proposito: con la sessione bloccata Chrome puo' rallentare il
    rendering delle finestre coperte, e i test ne uscirebbero falsati rispetto ai gruppi gia' fatti."""
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
    for td in glob.glob(SUITE + "/ext-test-classes/target/cinelib-only*") + glob.glob(SUITE + "/flowboard-run/td-*"):
        k = len(glob.glob(td + "/**/*XPathTest.class", recursive=True))
        atteso = 14 if td.endswith("td-s1s2") else 7
        if k != atteso:
            log(f"PREFLIGHT: {os.path.basename(td)} ha {k} classi di test, attese {atteso}"); ok = False
    for db, atteso in [(SUITE + "/mutations.db", 1263),
                       (SUITE + "/cinelib-static-run/mutations.db", 1142),
                       (SUITE + "/flowboard-run/mutations.db", 1225),
                       (SUITE + "/flowboard-static-run/mutations.db", 2840)]:
        con = sqlite3.connect(db)
        n = con.execute("select count(*) from mutations").fetchone()[0]
        mancanti = sum(1 for (p,) in con.execute("select distinct target_file_path from mutated_files")
                       if not os.path.exists(p))
        con.close()
        if n != atteso: log(f"PREFLIGHT: {os.path.basename(os.path.dirname(db))}/mutations.db ha {n} mutanti, attesi {atteso}")
        if mancanti:    log(f"PREFLIGHT: {db} -> {mancanti} percorsi sorgente inesistenti"); ok = False
    log("PREFLIGHT: " + ("tutto a posto" if ok else "PROBLEMI, vedi sopra"))
    return ok

scelte = [int(a) for a in sys.argv[1:] if a.isdigit() and 1 <= int(a) <= 4] or [1, 2, 3, 4]
ok = preflight()
if "--check" in sys.argv:
    sys.exit(0 if ok else 1)   # solo verifica, non esegue nulla
if not ok:
    sys.exit("preflight fallito: non lancio nulla.")

log(f"BLOCCO A — campagne da eseguire: {scelte}")
tieni_sveglio()
overall = time.time()
falliti = []
for i in scelte:
    nome, cmd, cwd = CAMPAGNE[i - 1]
    log(f"===== CAMPAGNA {i}/4 — {nome} — avvio =====")
    t = time.time()
    rc = subprocess.run(cmd, cwd=cwd).returncode
    dur = int(time.time() - t)
    if rc == 0:
        log(f"===== CAMPAGNA {i}/4 — {nome} — COMPLETATA in {dur//3600}h{(dur%3600)//60:02d}m =====")
    else:
        falliti.append((i, nome))
        log(f"===== CAMPAGNA {i}/4 — {nome} — INTERROTTA (exit {rc}) dopo {dur//3600}h{(dur%3600)//60:02d}m =====")

dur = int(time.time() - overall)
log(f"BLOCCO A finito in {dur//3600}h{(dur%3600)//60:02d}m")
if falliti:
    log("INCOMPLETE, da rilanciare: " + ", ".join(f"{i} ({n})" for i, n in falliti))
    sys.exit(1)
log("tutte le campagne complete.")
