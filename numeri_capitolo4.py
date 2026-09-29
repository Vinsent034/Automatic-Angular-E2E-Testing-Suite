"""Numeri del Capitolo 4 (revisione, settembre 2026), con le formule del Capitolo 3:
TV = M_v / M (3.1); TV(c) per operatore e ruolo (3.3); TO = E_obs / (E - E_nc) (3.7);
TS = E_succ / E_val con E_val = E - E_nc - E_obs (3.5). Legge i CSV delle campagne con le funzioni di
analizza_blocco_a.py (stessa classificazione dello strumento). Scrive numeri-capitolo4.json."""
import sys, io, json, contextlib, collections, importlib.util, os

SUITE = os.path.dirname(os.path.abspath(__file__))
spec = importlib.util.spec_from_file_location("ab", os.path.join(SUITE, "analizza_blocco_a.py"))
ab = importlib.util.module_from_spec(spec)
sys.argv = ["x", "cookbook-ibersaglio"]
with contextlib.redirect_stdout(io.StringIO()):
    spec.loader.exec_module(ab)

RUNS = {  # app, generatore -> (cartella, con duplicati?)
    ("CineLib", "statica"): ("cinelib-v2-static-run", False),
    ("CineLib", "statica+dup"): ("cinelib-v2-static-run", True),
    ("CineLib", "linguistica"): ("cinelib-v2-run", False),
    ("FlowBoard", "statica"): ("flowboard-v2-static-run", False),
    ("FlowBoard", "statica+dup"): ("flowboard-v2-static-run", True),
    ("FlowBoard", "linguistica"): ("flowboard-v2-run", False),
    ("CookBook", "statica+dup"): ("cookbook-static-run", False),
    ("CookBook", "statica"): ("cookbook-static-run", "nodup"),
    ("CookBook", "linguistica"): ("cookbook-run", False),
    ("CookBook", "i-bersaglio"): ("cookbook-i-bersaglio-run", False),
}

def mutanti(cartella, dup):
    m, _, chiavi = ab.leggi_batch(os.path.join(SUITE, cartella), con_chiavi=True)
    if dup == "nodup":   # CookBook: testati tutti, si tolgono i duplicati per avere i distinti
        tolti = ab.duplicati_da_db(os.path.join(SUITE, cartella))
        m = [x for x, k in zip(m, chiavi) if k not in tolti]
    elif dup:
        m = m + ab.con_duplicati(os.path.join(SUITE, cartella), dict(zip(chiavi, m)))
    return m

def validita(ms):
    nc = sum(1 for _, _, e in ms if ab.stato_batch(e) == "NOT_TESTED")
    return {"M": len(ms), "Mv": len(ms) - nc, "Mnc": nc}

out = {}
for (app, gen), (cart, dup) in RUNS.items():
    ms = mutanti(cart, dup)
    r = {"validita": validita(ms)}
    r["per_operatore"] = {o: validita([m for m in ms if m[1] == o]) for o in "abcdefghijk"}
    r["per_ruolo"] = {ru: validita([m for m in ms if m[0] == ru]) for ru in ab.ORDINE_RUOLI}
    r["senza_h"] = validita([m for m in ms if m[1] != "h"])
    # esiti dei test: nel gruppo FlowBoard s1s2 ogni mutante ha 14 esecuzioni, due scenari da 7 in
    # blocchi consecutivi; ogni scenario si classifica da solo (confronto fra strategie dello stesso test)
    unita = [u for m in ms for u in ([(m[0], m[1], m[2][:7]), (m[0], m[1], m[2][7:])] if len(m[2]) == 14 else [m])]
    stati = collections.Counter(ab.stato_batch(e) for _, _, e in unita)
    r["stati"] = dict(stati)
    st, _ = ab.conta(unita)
    r["strategie"] = {}
    for s in ab.STRATEGIE:
        c = st.get(s)
        if not c: continue
        e = sum(c.values()); val = c["successi"] + c["fragilita"]
        r["strategie"][ab.ETICHETTA[s]] = {"E": e, "E_nc": c["non_testati"], "E_obs": c["obsolescenza"],
                                           "succ": c["successi"], "frag": c["fragilita"],
                                           "TS": round(100 * c["successi"] / val, 1) if val else None}
    # obsolescenza sui mutanti testabili (uguale per tutte le strategie: 3.7)
    testabili = sum(1 for _, _, e in unita if ab.stato_batch(e) != "NOT_TESTED")
    r["TO"] = {"obs": stati["OBSOLETE"], "testabili": testabili,
               "perc": round(100 * stati["OBSOLETE"] / testabili, 1) if testabili else None}
    r["hook_per_ruolo"] = {}
    for ru in ab.ORDINE_RUOLI:
        c = ab.conta([u for u in unita if u[0] == ru])[0].get("HookXPathTest")
        if c: r["hook_per_ruolo"][ru] = {"succ": c["successi"], "frag": c["fragilita"]}
    # celle operatore x ruolo coperte (almeno un mutante valido)
    celle = {(m[1], m[0]) for m in ms if ab.stato_batch(m[2]) != "NOT_TESTED" and m[0] and m[1]}
    r["celle_coperte"] = len(celle)
    r["operatori_coperti"] = sorted({o for o, _ in celle})
    r["ruoli_coperti"] = sorted({ru for _, ru in celle})
    out[f"{app}|{gen}"] = r

json.dump(out, open(os.path.join(SUITE, "numeri-capitolo4.json"), "w", encoding="utf-8"), indent=1, ensure_ascii=False)
for k, r in out.items():
    v = r["validita"]
    print(f"{k:24s} M={v['M']:5d} Mv={v['Mv']:5d} TV={100*v['Mv']/v['M']:5.1f}%  TO={r['TO']['perc']}%  celle={r['celle_coperte']}"
          f"  ruoli={r['ruoli_coperti']}  TS=" + " ".join(f"{s[:4]}:{d['TS']}" for s, d in r["strategie"].items()))
