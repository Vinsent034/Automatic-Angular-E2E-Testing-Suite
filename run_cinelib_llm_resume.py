# Driver run LLM su CineLib (database `mutations.db` nella radice della suite) con RIPRESA PER GRUPPO.
# Gemello di cinelib-static-run/run_cinelib_static_resume.py, con due differenze:
#   - il database sta nella radice e contiene ANCHE le 456 mutazioni di angular-spotify:
#     il filtro per componente le lascia fuori (restano 807 mutanti CineLib);
#   - i CSV finiscono in output/tests/batches-llm-<gruppo>.csv.
# Uso: python run_cinelib_llm_resume.py              -> tutti i gruppi mancanti, in ordine
#      python run_cinelib_llm_resume.py moviedetail  -> solo quel gruppo (forzato)
import sqlite3, subprocess, os, sys, time, shutil, json, glob, hashlib, ntpath
from collections import defaultdict

SUITE  = "C:/Users/vince/OneDrive/Desktop/Università/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
RUN    = SUITE
TD     = SUITE + "/ext-test-classes/target"
DB     = SUITE + "/mutations.db"
JAVA   = "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe"
CP     = SUITE + "/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar;" + SUITE + "/vintage-fix"
CFG    = SUITE + "/generator-config-cinelib.json"
N_STRAT = 7   # strategie per mutante: 6 storiche + Hook-Based (reintegrata 09/2026)
PORT   = 4300
APPSRC = "C:/Users/vince/OneDrive/Desktop/Università/App tirocinio/cinelib/src/app"
BACKUP = "C:/Users/vince/OneDrive/Desktop/Università/App tirocinio/cinelib-templates-backup-20260717"

# componente -> gruppo. I componenti di angular-spotify (search, card) non compaiono:
# le loro mutazioni restano quindi fuori da ogni gruppo e non vengono mai messe in PENDING.
COMP2GROUP = {
 "catalog": "catalog", "movie-card": "catalog",
 "movie-detail": "moviedetail", "cast-row": "moviedetail",
 "movie-form": "movieform",
 "reviews": "reviews",
 "stats": "stats",
}
GROUP_TD = {
 "catalog":     TD + "/cinelib-only",
 "moviedetail": TD + "/cinelib-only-moviedetail",
 "movieform":   TD + "/cinelib-only-movieform",
 "reviews":     TD + "/cinelib-only-reviews",
 "stats":       TD + "/cinelib-only-stats",
}
GROUP_ORDER = ["catalog", "moviedetail", "movieform", "reviews", "stats"]

def log(m): print(f"[{time.strftime('%H:%M:%S')}] {m}", flush=True)

def kill_port():
    try:
        subprocess.run(["powershell","-NoProfile","-Command",
          f"Get-NetTCPConnection -LocalPort {PORT} -State Listen -ErrorAction SilentlyContinue | "
          "ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }"], timeout=30)
    except Exception as e: log(f"kill_port: {e}")

def restore_templates():
    n = 0
    for src in glob.glob(BACKUP + "/**/*.component.html", recursive=True):
        rel = os.path.relpath(src, BACKUP)
        dst = os.path.join(APPSRC, rel)
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copyfile(src, dst); n += 1
    return n

def md5(path):
    h = hashlib.md5()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(65536), b""): h.update(chunk)
    return h.hexdigest()

def csv_rows(path):
    with open(path, encoding="utf-8", errors="replace") as f:
        return max(0, sum(1 for _ in f) - 1)  # meno l'header

def check_csv(g, path, n_mut):
    """True se il CSV del gruppo sembra genuino."""
    if not os.path.exists(path):
        log(f"GROUP {g}: CHECK FAIL — batches-llm-{g}.csv non esiste"); return False
    rows, expected = csv_rows(path), n_mut * N_STRAT
    if rows < expected * 0.9:
        log(f"GROUP {g}: CHECK FAIL — {rows} righe, attese ~{expected} ({n_mut} mutanti x {N_STRAT} strategie)")
        return False
    mine = md5(path)
    for other in glob.glob(f"{RUN}/output/tests/batches-llm-*.csv"):
        if os.path.abspath(other) != os.path.abspath(path) and md5(other) == mine:
            log(f"GROUP {g}: CHECK FAIL — CSV byte-identico a {os.path.basename(other)} (bug 'app non parte')")
            return False
    log(f"GROUP {g}: CHECK OK — {rows} righe (attese ~{expected})")
    return True

# uuid -> gruppo, ricavato dal componente mutato (non dal nome del target:
# nel database LLM i nomi sono quelli dei bersagli, ma il componente e' piu' affidabile)
con = sqlite3.connect(DB)
comps = defaultdict(list)
for uuid, path in con.execute("select mutation_uuid, target_file_path from mutated_files"):
    comps[uuid].append(ntpath.basename(path).replace(".component.html", ""))
grp_uuids = defaultdict(list); fuori = 0
for (uuid,) in con.execute("select uuid from mutations"):
    # l'operatore h tocca due template: conta il primo componente riconosciuto,
    # cosi' il mutante finisce nel gruppo il cui scenario lo collauda davvero
    g = next((COMP2GROUP[c] for c in comps.get(uuid, []) if c in COMP2GROUP), None)
    if g: grp_uuids[g].append(uuid)
    else: fuori += 1
con.close()

if len(sys.argv) > 1:
    todo = [g for g in sys.argv[1:] if g in GROUP_TD]
    if not todo: sys.exit(f"gruppo sconosciuto: {sys.argv[1:]} (validi: {list(GROUP_TD)})")
else:
    todo, skipped = [], []
    for g in GROUP_ORDER:
        csv = f"{RUN}/output/tests/batches-llm-{g}.csv"
        if os.path.exists(csv) and check_csv(g, csv, len(grp_uuids[g])): skipped.append(g)
        else: todo.append(g)
    if skipped: log(f"RIPRESA: salto gruppi gia' completi: {skipped}")

log("da eseguire: " + ", ".join(f"{g}={len(grp_uuids[g])}" for g in todo)
    + f" | fuori campagna (angular-spotify e altro)={fuori}")
if not todo:
    log("niente da fare: tutti i gruppi hanno un CSV valido."); sys.exit(0)

overall = time.time()
for g in todo:
    con = sqlite3.connect(DB)
    con.execute("update mutations set status='HELD'")
    con.executemany("update mutations set status='PENDING' where uuid=?", [(u,) for u in grp_uuids[g]])
    con.commit()
    n = con.execute("select count(*) from mutations where status='PENDING'").fetchone()[0]
    con.close()
    r = restore_templates()
    log(f"GROUP {g}: PENDING={n} | template ripristinati={r}")
    kill_port(); time.sleep(3)
    t = time.time()
    old = f"{RUN}/output/tests/batches.csv"
    if os.path.exists(old): os.remove(old)
    with open(f"{RUN}/log-llm-{g}.txt","w",encoding="utf-8") as lf:
        try:
            subprocess.run([JAVA,"-cp",CP,"org.unina.MutationTester","-td",GROUP_TD[g],"--config",CFG],
                           cwd=RUN, stdout=lf, stderr=subprocess.STDOUT, timeout=8*3600)
        except Exception as e: log(f"GROUP {g} error: {e}")
    kill_port(); time.sleep(3)
    dest = f"{RUN}/output/tests/batches-llm-{g}.csv"
    if os.path.exists(old): shutil.copy(old, dest)
    if not check_csv(g, dest, len(grp_uuids[g])):
        if os.path.exists(dest) and os.path.exists(old) and md5(dest) == md5(old):
            os.remove(dest)  # copia appena fatta ma invalida: via, cosi' la ripresa lo rifara'
        restore_templates()
        log(f"GROUP {g}: FALLITO — mi fermo qui. Rilancia lo script: riprendera' da questo gruppo.")
        sys.exit(1)
    log(f"GROUP {g}: done in {int(time.time()-t)}s -> batches-llm-{g}.csv")

restore_templates()
log(f"ALL LLM GROUPS DONE in {int(time.time()-overall)}s | template ripristinati a fine run")
