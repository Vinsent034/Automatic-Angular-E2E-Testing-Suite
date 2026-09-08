# Compile-check (RQ 1.2) per i mutanti PARAMETRICI.
# Pilota il dev server Angular: per ogni mutante scrive il file, attende l'esito di
# (ri)compilazione ("Compiled successfully" = compila; errore/timeout = non compila),
# poi ripristina l'originale. Nessun Selenium, nessuna chiamata Spotify.
# Timeout generosi e controllati per evitare falsi "non compila".

import subprocess, threading, queue, time, re, sqlite3, os, sys

ANGULAR = r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify"
ORIG = {
    "search_component_html": r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/search/feature/src/lib/search.component.html",
    "card_component_html":   r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/shared/ui/media/src/lib/card.component.html",
}
SUCCESS = "compiled successfully"
ERRMARK = ["failed to compile", "error in", "template parse error", "errors.", "✖", " error ", "error:"]
ANSI = re.compile(r"\x1b\[[0-9;]*m")
INIT_TIMEOUT = 300
MUT_TIMEOUT = 45
OUT = "output/tests/param-compile.csv"

q = queue.Queue()
def reader(pipe):
    for line in pipe:
        q.put(ANSI.sub("", line).rstrip())

def drain():
    try:
        while True: q.get_nowait()
    except queue.Empty:
        pass

def wait_marker(timeout):
    end = time.time() + timeout
    while time.time() < end:
        try:
            line = q.get(timeout=1)
        except queue.Empty:
            continue
        low = line.lower()
        if SUCCESS in low:
            return "ok"
        if any(e in low for e in ERRMARK):
            return "fail"
    return "timeout"

def write(path, text):
    with open(path, "w", encoding="utf-8") as f:
        f.write(text)

# carica mutanti parametrici raggruppati per file
c = sqlite3.connect("mutations.db")
rows = c.execute(
    "SELECT m.element, m.mutation_name, m.mutation_id, mf.mutated_code "
    "FROM mutations m JOIN mutated_files mf ON mf.mutation_uuid = m.uuid "
    "WHERE m.mutation_type='LLM_PARAMETRIC' ORDER BY m.element, m.mutation_name"
).fetchall()
by_file = {}
for el, typ, mid, code in rows:
    by_file.setdefault(el, []).append((typ, mid, code))

originals = {el: open(p, encoding="utf-8").read() for el, p in ORIG.items()}
results = []  # (mutation_id, type, element, compiles)

print("Starting dev server...")
proc = subprocess.Popen("npm start", cwd=ANGULAR, shell=True,
                        stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, bufsize=1)
threading.Thread(target=reader, args=(proc.stdout,), daemon=True).start()

try:
    if wait_marker(INIT_TIMEOUT) != "ok":
        print("ERROR: dev server did not compile initially."); sys.exit(1)
    print("Dev server ready.\n")

    for el, muts in by_file.items():
        if el not in ORIG:
            continue
        path = ORIG[el]
        print(f"=== {el}: {len(muts)} mutants ===")
        try:
            for i, (typ, mid, code) in enumerate(muts, 1):
                drain()
                write(path, code)
                res = wait_marker(MUT_TIMEOUT)
                compiles = 1 if res == "ok" else 0
                results.append((mid, typ, el, compiles))
                if i % 10 == 0 or res != "ok":
                    print(f"  [{i}/{len(muts)}] {mid} type={typ} -> {'COMPILES' if compiles else 'NO ('+res+')'}")
                # se non compila, riporta a stato pulito prima del prossimo per evitare stati di errore incrostati
                if not compiles:
                    drain(); write(path, originals[el]); wait_marker(MUT_TIMEOUT)
        finally:
            write(path, originals[el])  # ripristina sempre
        wait_marker(MUT_TIMEOUT)
finally:
    try: proc.terminate()
    except Exception: pass
    for el, p in ORIG.items():
        write(p, originals[el])  # garanzia di ripristino

os.makedirs("output/tests", exist_ok=True)
with open(OUT, "w", encoding="utf-8") as f:
    f.write("MutationId,Type,Element,Compiles\n")
    for mid, typ, el, comp in results:
        f.write(f"{mid},{typ},{el},{comp}\n")
print(f"\nSaved {len(results)} results -> {OUT}")
