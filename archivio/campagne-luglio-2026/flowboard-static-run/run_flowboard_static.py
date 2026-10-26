import sqlite3, subprocess, os, time, shutil, ntpath
from collections import defaultdict

SUITE = "C:/Users/vince/OneDrive/Desktop/Università/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
RUN   = SUITE + "/flowboard-static-run"
TDBASE= SUITE + "/flowboard-run"          # test-dir riusate
DB    = RUN + "/mutations.db"
JAVA  = "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe"
CP    = SUITE + "/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar;" + SUITE + "/vintage-fix"
CFG   = SUITE + "/generator-config-flowboard.json"

# componente -> gruppo
COMP2GROUP = {
 "board":"s1s2","card":"s1s2","column":"s1s2",
 "card-form":"s3","card-detail":"s4","toast":"s4","stats":"s5","header":"s6","app":"s6",
}
GROUP_TD = {"s1s2":TDBASE+"/td-s1s2","s3":TDBASE+"/td-s3","s4":TDBASE+"/td-s4","s5":TDBASE+"/td-s5","s6":TDBASE+"/td-s6"}
GROUP_ORDER = ["s1s2","s3","s4","s5","s6"]

def comp_of(path):
    b = ntpath.basename(path).replace(".component.html","")
    return b

def log(m): print(f"[{time.strftime('%H:%M:%S')}] {m}", flush=True)

def kill4400():
    try:
        subprocess.run(["powershell","-NoProfile","-Command",
          "Get-NetTCPConnection -LocalPort 4400 -State Listen -ErrorAction SilentlyContinue | "
          "ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }"], timeout=30)
    except Exception as e: log(f"kill4400: {e}")

# precompute uuid -> group (by first matching component in mutated_files)
con = sqlite3.connect(DB)
mf = defaultdict(list)
for uuid, path in con.execute("select mutation_uuid, target_file_path from mutated_files"):
    mf[uuid].append(comp_of(path))
uuid_group = {}
for uuid, comps in mf.items():
    g = None
    for c in comps:
        if c in COMP2GROUP: g = COMP2GROUP[c]; break
    if g: uuid_group[uuid] = g
grp_uuids = defaultdict(list)
for uuid, g in uuid_group.items(): grp_uuids[g].append(uuid)
con.close()
log("assegnazione gruppi: " + ", ".join(f"{g}={len(grp_uuids[g])}" for g in GROUP_ORDER))

overall = time.time()
for g in GROUP_ORDER:
    con = sqlite3.connect(DB)
    con.execute("update mutations set status='HELD'")
    con.executemany("update mutations set status='PENDING' where uuid=?", [(u,) for u in grp_uuids[g]])
    con.commit()
    n = con.execute("select count(*) from mutations where status='PENDING'").fetchone()[0]
    con.close()
    log(f"GROUP {g}: PENDING={n}")
    kill4400(); time.sleep(3)
    t = time.time()
    with open(f"{RUN}/log-{g}.txt","w",encoding="utf-8") as lf:
        try:
            subprocess.run([JAVA,"-cp",CP,"org.unina.MutationTester","-td",GROUP_TD[g],"--config",CFG],
                           cwd=RUN, stdout=lf, stderr=subprocess.STDOUT, timeout=8*3600)
        except Exception as e: log(f"GROUP {g} error: {e}")
    src = f"{RUN}/output/tests/batches.csv"
    if os.path.exists(src):
        shutil.copy(src, f"{RUN}/output/tests/batches-{g}.csv")
        log(f"GROUP {g}: done in {int(time.time()-t)}s -> batches-{g}.csv")
    else:
        log(f"GROUP {g}: NO batches.csv")
    kill4400(); time.sleep(3)
log(f"ALL STATIC GROUPS DONE in {int(time.time()-overall)}s")
