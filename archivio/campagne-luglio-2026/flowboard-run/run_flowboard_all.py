import sqlite3, subprocess, os, time, shutil, sys

SUITE = "C:/Users/vince/OneDrive/Desktop/Università/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
RUN   = SUITE + "/flowboard-run"
DB    = RUN + "/mutations.db"
JAVA  = "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe"
CP    = SUITE + "/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar;" + SUITE + "/vintage-fix"
CFG   = SUITE + "/generator-config-flowboard.json"

GROUPS = [
    ("s1s2", "td-s1s2", ["board_component_html", "card_component_html", "column_component_html"]),
    ("s3",   "td-s3",   ["card_form_component_html"]),
    ("s4",   "td-s4",   ["card_detail_component_html", "toast_component_html"]),
    ("s5",   "td-s5",   ["stats_component_html"]),
    ("s6",   "td-s6",   ["header_component_html", "app_component_html"]),
]

def log(msg):
    print(f"[{time.strftime('%H:%M:%S')}] {msg}", flush=True)

def kill_port_4400():
    try:
        subprocess.run(["powershell", "-NoProfile", "-Command",
            "Get-NetTCPConnection -LocalPort 4400 -State Listen -ErrorAction SilentlyContinue | "
            "ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }"],
            timeout=30)
    except Exception as e:
        log(f"kill_port_4400: {e}")

overall = time.time()
for name, td, elems in GROUPS:
    con = sqlite3.connect(DB)
    con.execute("update mutations set status='HELD'")
    for e in elems:
        con.execute("update mutations set status='PENDING' where element=?", (e,))
    con.commit()
    n = con.execute("select count(*) from mutations where status='PENDING'").fetchone()[0]
    con.close()
    log(f"GROUP {name}: PENDING={n} | td={td}")
    kill_port_4400()
    time.sleep(3)
    t = time.time()
    with open(f"{RUN}/log-{name}.txt", "w", encoding="utf-8") as lf:
        try:
            subprocess.run([JAVA, "-cp", CP, "org.unina.MutationTester", "-td", td, "--config", CFG],
                           cwd=RUN, stdout=lf, stderr=subprocess.STDOUT, timeout=6*3600)
        except Exception as e:
            log(f"GROUP {name} tester error: {e}")
    src = f"{RUN}/output/tests/batches.csv"
    if os.path.exists(src):
        shutil.copy(src, f"{RUN}/output/tests/batches-{name}.csv")
        log(f"GROUP {name}: done in {int(time.time()-t)}s -> batches-{name}.csv")
    else:
        log(f"GROUP {name}: NO batches.csv produced (see log-{name}.txt)")
    kill_port_4400()
    time.sleep(3)

log(f"ALL GROUPS DONE in {int(time.time()-overall)}s")
