# Driver run STATIC su CineLib — gemello di flowboard-static-run/run_flowboard_static.py.
# Porta 4300, 5 gruppi-scenario, gruppo derivato dal TARGET (mutation_name) via role-targets.json.
# ANTI-TRAPPOLA: il mutation-tester lascia i template corrotti (&gt;) a fine gruppo, il che
# impedisce all'app di ripartire per il gruppo successivo. Rimedio: RIPRISTINO tutti i template
# dal backup PRIMA di ogni gruppo, cosi ogni gruppo parte da uno stato pulito.
import sqlite3, subprocess, os, time, shutil, json, glob
from collections import defaultdict

SUITE  = "C:/Users/vince/OneDrive/Desktop/Università/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
RUN    = SUITE + "/cinelib-static-run"
TD     = SUITE + "/ext-test-classes/target"
DB     = RUN + "/mutations.db"
JAVA   = "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe"
CP     = SUITE + "/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar;" + SUITE + "/vintage-fix"
CFG    = SUITE + "/generator-config-cinelib-static.json"
PORT   = 4300
APPSRC = "C:/Users/vince/OneDrive/Desktop/Università/App tirocinio/cinelib/src/app"
BACKUP = "C:/Users/vince/OneDrive/Desktop/Università/App tirocinio/cinelib-templates-backup-20260717"

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
GROUP_ORDER = ["moviedetail", "movieform", "reviews", "stats"]

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

# target -> gruppo
rt = json.load(open(SUITE + "/role-targets.json", encoding="utf-8"))
target_group = {}
for t in rt["targets"]:
    comp = os.path.basename(t["componentHtml"]).replace(".component.html", "")
    if comp in COMP2GROUP: target_group[t["id"]] = COMP2GROUP[comp]

con = sqlite3.connect(DB)
grp_uuids = defaultdict(list); orphans = []
for uuid, name in con.execute("select uuid, mutation_name from mutations"):
    g = target_group.get(name)
    if g: grp_uuids[g].append(uuid)
    else: orphans.append(name)
con.close()
log("assegnazione gruppi: " + ", ".join(f"{g}={len(grp_uuids[g])}" for g in GROUP_ORDER)
    + (f" | ORFANI={len(orphans)}" if orphans else ""))

overall = time.time()
for g in GROUP_ORDER:
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
    # rimuovo il batches.csv precedente cosi non si rischia di riusare quello vecchio come output
    old = f"{RUN}/output/tests/batches.csv"
    if os.path.exists(old): os.remove(old)
    with open(f"{RUN}/log-{g}.txt","w",encoding="utf-8") as lf:
        try:
            subprocess.run([JAVA,"-cp",CP,"org.unina.MutationTester","-td",GROUP_TD[g],"--config",CFG],
                           cwd=RUN, stdout=lf, stderr=subprocess.STDOUT, timeout=8*3600)
        except Exception as e: log(f"GROUP {g} error: {e}")
    if os.path.exists(old):
        shutil.copy(old, f"{RUN}/output/tests/batches-{g}.csv")
        log(f"GROUP {g}: done in {int(time.time()-t)}s -> batches-{g}.csv")
    else:
        log(f"GROUP {g}: *** NO batches.csv (app non partita?) ***")
    kill_port(); time.sleep(3)

restore_templates()
log(f"ALL STATIC GROUPS DONE in {int(time.time()-overall)}s | template ripristinati a fine run")
