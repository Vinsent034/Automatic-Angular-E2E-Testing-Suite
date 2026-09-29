# Driver run su CineLib v2 (settembre 2026) — copia di run_flowboard_v2_resume.py con le sole costanti
# dell'applicazione cambiate (27/09/2026) e il controllo «CSV byte-identico» che salta i gruppi vuoti.
# Uso: python run_cinelib_v2_resume.py llm | static [gruppi...]
# La spiegazione della ripresa per gruppo e della riparazione degli inceppamenti è in run_flowboard_v2_resume.py.
import sqlite3, subprocess, os, sys, time, shutil, glob, hashlib, ntpath, csv, json
from collections import defaultdict

SUITE  = "C:/Users/vince/OneDrive/Desktop/Università/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
TDBASE = SUITE + "/cinelib-v2-run"        # le test-dir sono condivise fra le due campagne
JAVA   = "C:/Program Files/Eclipse Adoptium/jdk-25.0.2.10-hotspot/bin/java.exe"
CP     = SUITE + "/mutation-tester/target/mutation-tester-1.0.0-jar-with-dependencies.jar;" + SUITE + "/vintage-fix"
CFG    = SUITE + "/generator-config-cinelib.json"
PORT   = 4300
APPROOT= "C:/Users/vince/OneDrive/Desktop/Università/App tirocinio/cinelib"
APPSRC = APPROOT + "/src/app"
BACKUP = "C:/Users/vince/OneDrive/Desktop/Università/App tirocinio/cinelib-v2-templates-backup-20260926"
MAX_AVVII   = 3    # tentativi per esecuzione se il dev server non parte entro i 120 s dello strumento
MAX_MANCATI = 3    # quante volte un mutante puo' mancare dall'output prima di essere abbandonato

MODES = {
 "llm":    SUITE + "/cinelib-v2-run",
 "static": SUITE + "/cinelib-v2-static-run",
}
COMP2GROUP = {  # come la campagna di luglio (run_cinelib_llm_resume.py), gruppi = pacchetti delle classi
 "catalog": "catalogsearch", "movie-card": "catalogsearch",
 "movie-detail": "moviedetail", "cast-row": "moviedetail",
 "movie-form": "movieform",
 "reviews": "reviews",
 "stats": "stats",
}
GROUP_ORDER = ["catalogsearch", "moviedetail", "movieform", "reviews", "stats"]
GROUP_TD = {g: f"{TDBASE}/td-{g}" for g in GROUP_ORDER}
INTESTAZIONE = "Name,Id,Locator,Tag,Status,Error"

if len(sys.argv) < 2 or sys.argv[1] not in MODES:
    sys.exit(f"uso: python run_cinelib_v2_resume.py <{'|'.join(MODES)}> [gruppi...]")
MODE = sys.argv[1]
RUN  = MODES[MODE]
DB   = RUN + "/mutations.db"
OUT  = RUN + "/output/tests"

def log(m): print(f"[{time.strftime('%H:%M:%S')}] [{MODE}] {m}", flush=True)

def kill_port():
    try:
        subprocess.run(["powershell","-NoProfile","-Command",
          f"Get-NetTCPConnection -LocalPort {PORT} -State Listen -ErrorAction SilentlyContinue | "
          "ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }"], timeout=30)
    except Exception as e: log(f"kill_port: {e}")

def restore_templates():
    n = 0
    for src in glob.glob(BACKUP + "/**/*.component.html", recursive=True):
        dst = os.path.join(APPSRC, os.path.relpath(src, BACKUP))
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        shutil.copyfile(src, dst); n += 1
    return n

def n_classes(td):
    return len(glob.glob(td + "/**/*XPathTest.class", recursive=True))

def md5(path):
    h = hashlib.md5()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(65536), b""): h.update(chunk)
    return h.hexdigest()

# ---------- identita' dei mutanti nei CSV ----------------------------------------------------------
# Lo strumento scrive Name = mutation_name e Id = "mutation_<mutation_id>_<mutation_type>_<element>".
# La quaterna (nome, id, tipo, elemento) e' univoca in tutti i database (verificato il 18/09/2026),
# quindi la coppia (Name, Id) individua un mutante e se ne ricava l'uuid.
def mappa_chiavi():
    con = sqlite3.connect(DB); m = {}
    for uuid, el, name, mtype, mid in con.execute(
            "select uuid, element, mutation_name, mutation_type, mutation_id from mutations"):
        m[(name, f"mutation_{mid}_{mtype}_{el}")] = uuid
    con.close()
    return m

def leggi_blocchi(path, k):
    """Righe del CSV divise in blocchi di k righe: un blocco per mutante, in ordine di esecuzione.
    La dimensione e' fissa (k = classi di test della test-dir) perche' in td-s1s2 i nomi delle classi
    si ripetono (s1 e s2 hanno entrambi AbsoluteXPathTest...) e non si puo' separare per nome."""
    with open(path, encoding="utf-8", errors="replace", newline="") as fh:
        righe = [r for r in csv.DictReader(fh) if r.get("Locator")]
    if len(righe) % k:
        raise ValueError(f"{os.path.basename(path)}: {len(righe)} righe non divisibili per {k}")
    blocchi = [righe[i:i + k] for i in range(0, len(righe), k)]
    for b in blocchi:
        if len({(r["Name"], r["Id"]) for r in b}) != 1:
            raise ValueError(f"{os.path.basename(path)}: blocco con mutanti diversi, "
                             f"separazione sbagliata (k={k})")
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

# ---------- esecuzione dello strumento --------------------------------------------------------------
def prewarm():
    """Avvia e ferma una volta il dev server: scalda le cache su disco, cosi' l'avvio fatto dallo
    strumento (che concede solo 120 s) non va in scadenza. Su OneDrive il primo avvio e' lento."""
    kill_port(); time.sleep(2)
    t = time.time()
    p = subprocess.Popen(["cmd.exe", "/c", f"npm start -- --port {PORT}"], cwd=APPROOT,
                         stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True,
                         encoding="utf-8", errors="replace")
    pronto = False
    try:
        for riga in p.stdout:
            if "Application bundle generation complete" in riga or "Compiled successfully" in riga:
                pronto = True; break
            if time.time() - t > 300: break
    finally:
        subprocess.run(["taskkill", "/PID", str(p.pid), "/T", "/F"], capture_output=True)
        kill_port(); time.sleep(3)
    log(f"pre-riscaldamento dev server: {'pronto' if pronto else 'NON pronto'} in {int(time.time()-t)} s")

def esegui(uuids, k, etichetta):
    """Esegue lo strumento sui soli mutanti indicati, con un dev server nuovo.
    Ritorna i blocchi prodotti in ordine di esecuzione (possono essere meno dei richiesti se lo
    strumento si interrompe) oppure None se il dev server non parte dopo MAX_AVVII tentativi."""
    batches = f"{OUT}/batches.csv"
    for tentativo in range(1, MAX_AVVII + 1):
        con = sqlite3.connect(DB)
        con.execute("update mutations set status='HELD'")
        con.executemany("update mutations set status='PENDING' where uuid=?", [(u,) for u in uuids])
        con.commit(); con.close()
        restore_templates()
        kill_port(); time.sleep(3)
        if os.path.exists(batches): os.remove(batches)
        nome_log = f"{RUN}/log-{etichetta}" + (f"-avvio{tentativo}" if tentativo > 1 else "") + ".txt"
        with open(nome_log, "w", encoding="utf-8") as lf:
            try:
                subprocess.run([JAVA, "-cp", CP, "org.unina.MutationTester", "-td", TD, "--config", CFG],
                               cwd=RUN, stdout=lf, stderr=subprocess.STDOUT, timeout=10 * 3600)
            except Exception as e:
                log(f"{etichetta}: errore dello strumento: {e}")
        kill_port(); time.sleep(3)
        restore_templates()
        if os.path.exists(batches):
            return leggi_blocchi(batches, k)
        log(f"{etichetta}: nessun CSV (dev server non partito?) — tentativo {tentativo}/{MAX_AVVII}")
        if tentativo < MAX_AVVII:
            prewarm()
    return None

def completa_gruppo(g, uuids, blocchi_iniziali):
    """Porta a termine il gruppo applicando la regola di riparazione. Se blocchi_iniziali e' dato
    (CSV gia' prodotto prima), si parte da quello e si rifanno solo i mutanti sospetti o mancanti."""
    global TD
    TD = GROUP_TD[g]
    k = n_classes(TD)
    mappa = mappa_chiavi()
    accettati, ordine = {}, []                   # uuid -> blocco, e ordine di accettazione
    inneschi, abbandonati = [], []
    mancati = defaultdict(int)
    richiesti = set(uuids)

    coda, giro, blocchi = list(uuids), 0, blocchi_iniziali
    da_subito = 0                                # giri consecutivi inceppati gia' al primo mutante

    def salva(completo, in_sospeso):
        """Scrive il CSV con i mutanti accettati finora e il file di verifica. Se il gruppo non e'
        completo, la ripresa rilegge questo CSV e rifa' solo i mutanti che mancano."""
        scrivi_blocchi(f"{OUT}/batches-{g}.csv", [accettati[u] for u in ordine])
        verifica = {
            "gruppo": g, "campagna": MODE, "completo": completo,
            "mutanti": len(uuids), "accettati": len(accettati), "in_sospeso": len(in_sospeso),
            "classi_per_mutante": k, "giri": giro, "inneschi": inneschi, "abbandonati": abbandonati,
            "data": time.strftime("%Y-%m-%d %H:%M:%S"),
            "regola": "coda finale di >=2 non compilati = dev server incastrato; primo della coda "
                      "valido, gli altri rieseguiti con dev server nuovo",
        }
        with open(f"{OUT}/batches-{g}.verifica.json", "w", encoding="utf-8") as fh:
            json.dump(verifica, fh, ensure_ascii=False, indent=2)
        return verifica

    while coda:
        giro += 1
        if blocchi is None:
            etichetta = g if giro == 1 else f"{g}-rip{giro - 1}"
            blocchi = esegui(coda, k, etichetta)
            if blocchi is None:
                log(f"GROUP {g}: dev server mai partito dopo {MAX_AVVII} tentativi — salvo e mi fermo")
                salva(completo=False, in_sospeso=coda)
                return None
        ids = []
        for b in blocchi:
            chiave = (b[0]["Name"], b[0]["Id"])
            if chiave not in mappa:
                raise KeyError(f"mutante del CSV non trovato nel database: {chiave}")
            ids.append(mappa[chiave])
        # ignora eventuali mutanti non richiesti in questo giro (non dovrebbero esserci)
        coppie = [(u, b) for u, b in zip(ids, blocchi) if u in richiesti and u not in accettati]

        coda_nc = 0
        for _, b in reversed(coppie):
            if non_compila(b): coda_nc += 1
            else: break
        if coda_nc >= 2:
            taglio = len(coppie) - coda_nc         # l'innesco: girato su server sano, vale
            validi, sospetti = coppie[:taglio + 1], [u for u, _ in coppie[taglio + 1:]]
            inneschi.append(coppie[taglio][0])
            # Sicura: se un dev server appena avviato non compila nulla fin dal primo mutante, con
            # ancora tanti sospetti in coda, per 5 giri di fila, il problema non e' un mutante ma
            # l'ambiente. Meglio fermarsi (salvando) che accettare un "non compila" a giro.
            esecuzione_nuova = giro > 1 or blocchi_iniziali is None
            if esecuzione_nuova and taglio == 0 and len(coppie) >= 10:
                da_subito += 1
            else:
                da_subito = 0
            if da_subito >= 5:
                log(f"GROUP {g}: per 5 giri di fila un dev server appena avviato non ha compilato nulla "
                    f"fin dal primo mutante — guasto dell'ambiente, salvo i progressi e mi fermo")
                salva(completo=False, in_sospeso=[u for u, _ in coppie[taglio + 1:]])
                return None
        else:
            validi, sospetti = coppie, []
            da_subito = 0
        for u, b in validi:
            accettati[u] = b; ordine.append(u)

        visti = {u for u, _ in coppie}
        mancanti = [u for u in coda if u not in visti and u not in accettati]
        for u in mancanti: mancati[u] += 1
        persi = [u for u in mancanti if mancati[u] > MAX_MANCATI]
        abbandonati += persi
        coda = sospetti + [u for u in mancanti if mancati[u] <= MAX_MANCATI]

        nota = (f"inceppato dopo il mutante {len(validi)}: {len(sospetti)} sospetti da rifare"
                if sospetti else "nessun inceppamento")
        log(f"GROUP {g} giro {giro}{' (CSV esistente)' if giro == 1 and blocchi_iniziali else ''}: "
            f"{len(coppie)} eseguiti, {len(validi)} accettati, {nota}"
            + (f", {len(mancanti)} mancanti" if mancanti else ""))
        blocchi = None

    return salva(completo=True, in_sospeso=[])

def gruppo_completo(g, n_mut):
    """Un gruppo e' completo se ha il CSV, il file di verifica, e il CSV ha un blocco per mutante."""
    csvp, ver = f"{OUT}/batches-{g}.csv", f"{OUT}/batches-{g}.verifica.json"
    if not (os.path.exists(csvp) and os.path.exists(ver)):
        return False
    try:
        blocchi = leggi_blocchi(csvp, n_classes(GROUP_TD[g]))
    except Exception as e:
        log(f"GROUP {g}: CSV illeggibile ({e})"); return False
    v = json.load(open(ver, encoding="utf-8"))
    if not v.get("completo"):
        return False
    attesi = n_mut - len(v.get("abbandonati", []))
    if len(blocchi) != attesi:
        log(f"GROUP {g}: CHECK FAIL — {len(blocchi)} mutanti nel CSV, attesi {attesi}"); return False
    if attesi == 0:  # gruppo vuoto: il CSV vuoto è uguale a quello di ogni altro gruppo vuoto
        return True
    mine = md5(csvp)
    for other in glob.glob(f"{OUT}/batches-*.csv"):
        if os.path.abspath(other) != os.path.abspath(csvp) and md5(other) == mine:
            log(f"GROUP {g}: CHECK FAIL — CSV byte-identico a {os.path.basename(other)}"); return False
    return True

# ---------- programma principale ----------------------------------------------------------------------
con = sqlite3.connect(DB)
comps = defaultdict(list)
for uuid, path in con.execute("select mutation_uuid, target_file_path from mutated_files"):
    comps[uuid].append(ntpath.basename(path).replace(".component.html", ""))
grp_uuids = defaultdict(list); fuori = 0
for (uuid,) in con.execute("select uuid from mutations"):
    g = next((COMP2GROUP[c] for c in comps.get(uuid, []) if c in COMP2GROUP), None)
    if g: grp_uuids[g].append(uuid)
    else: fuori += 1
con.close()

forzati = [g for g in sys.argv[2:] if g in GROUP_TD]
if len(sys.argv) > 2 and not forzati:
    sys.exit(f"gruppo sconosciuto: {sys.argv[2:]} (validi: {list(GROUP_TD)})")
todo = forzati or [g for g in GROUP_ORDER if not gruppo_completo(g, len(grp_uuids[g]))]
fatti = [g for g in GROUP_ORDER if g not in todo]
if fatti: log(f"RIPRESA: gruppi gia' completi e verificati: {fatti}")
log("da eseguire: " + ", ".join(f"{g}={len(grp_uuids[g])}" for g in todo)
    + (f" | senza gruppo={fuori}" if fuori else ""))
if not todo:
    log("niente da fare: tutti i gruppi sono completi e verificati."); sys.exit(0)

os.makedirs(OUT, exist_ok=True)
overall = time.time()
esito = 0
primo = True
for g in todo:
    t = time.time()
    csvp = f"{OUT}/batches-{g}.csv"
    blocchi_iniziali = None
    if os.path.exists(csvp) and not forzati:
        try:
            blocchi_iniziali = leggi_blocchi(csvp, n_classes(GROUP_TD[g]))
            log(f"GROUP {g}: riesamino il CSV esistente ({len(blocchi_iniziali)} mutanti) invece di rifarlo")
            shutil.copyfile(csvp, csvp + ".prima-della-verifica")
        except Exception as e:
            log(f"GROUP {g}: CSV esistente inutilizzabile ({e}), rifaccio il gruppo")
    if blocchi_iniziali is None:
        if primo: prewarm()
        primo = False
    log(f"GROUP {g}: {len(grp_uuids[g])} mutanti | classi nella td={n_classes(GROUP_TD[g])}")
    v = completa_gruppo(g, grp_uuids[g], blocchi_iniziali)
    if v is None or not gruppo_completo(g, len(grp_uuids[g])):
        restore_templates()
        log(f"GROUP {g}: FALLITO — mi fermo qui. Rilancia lo script: riprendera' da questo gruppo.")
        sys.exit(1)
    log(f"GROUP {g}: done in {int(time.time()-t)}s -> batches-{g}.csv | giri {v['giri']}, "
        f"inneschi {len(v['inneschi'])}, abbandonati {len(v['abbandonati'])}")

restore_templates()
log(f"TUTTI I GRUPPI DONE in {int(time.time()-overall)}s | template ripristinati a fine run")
