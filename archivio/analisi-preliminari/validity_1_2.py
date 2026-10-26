# RQ 1.2 combinato: per ogni mutante parametrico -> coerente? compila?
# Aggrega per tipo: coerenti, compilanti, e "validi" (coerenti AND compilanti).

import sqlite3, csv
from collections import Counter, defaultdict
from bs4 import BeautifulSoup

ORIG = {
    "search_component_html": r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/search/feature/src/lib/search.component.html",
    "card_component_html":   r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/shared/ui/media/src/lib/card.component.html",
}

def feats(html):
    soup = BeautifulSoup(html, "html.parser"); els = soup.find_all(True)
    tags = Counter(e.name for e in els); tagseq = [e.name for e in els]
    nattr = 0; keys = Counter(); keyval = Counter(); pp = Counter()
    for e in els:
        for k, v in e.attrs.items():
            val = " ".join(v) if isinstance(v, list) else v
            keys[(e.name, k)] += 1; keyval[(e.name, k, val)] += 1; nattr += 1
        pp[(e.name, e.parent.name if e.parent else None)] += 1
    toks = Counter(soup.get_text().split())
    return dict(tags=tags, tagseq=tagseq, nattr=nattr, keys=keys, keyval=keyval,
                toks=toks, ntoks=sum(toks.values()), pp=pp, ntags=sum(tags.values()))

def classify(O, M):
    if O["tags"] != M["tags"]:
        if M["ntags"] > O["ntags"]: return "k"
        if M["ntags"] < O["ntags"]: return "i"
        return "j"
    if M["nattr"] < O["nattr"]: return "b"
    if M["nattr"] > O["nattr"]: return "attr+"
    if O["keys"] != M["keys"]: return "c"
    if O["keyval"] != M["keyval"]: return "a"
    if O["toks"] != M["toks"]:
        return "e" if M["ntoks"] < O["ntoks"] else "d"
    if O["tagseq"] != M["tagseq"]:
        return "f" if O["pp"] == M["pp"] else "g"
    return "none"

def coherent(req, det):
    if req == det: return True
    if req == "h" and det in ("g", "i"): return True   # h non fattibile single-file
    if req in ("f", "g") and det in ("f", "g"): return True  # famiglia movimento (f/g non distinguibili)
    return False

# compile results
compile_map = {}
for r in csv.DictReader(open("output/tests/param-compile.csv")):
    compile_map[r["MutationId"]] = int(r["Compiles"])

c = sqlite3.connect("mutations.db")
rows = c.execute("SELECT m.element,m.mutation_name,m.mutation_id,mf.mutated_code FROM mutations m "
                 "JOIN mutated_files mf ON mf.mutation_uuid=m.uuid WHERE m.mutation_type='LLM_PARAMETRIC'").fetchall()
of = {k: feats(open(v, encoding="utf-8").read()) for k, v in ORIG.items()}

agg = defaultdict(lambda: [0, 0, 0, 0])  # type -> [tot, coher, comp, valid(both)]
for el, req, mid, code in rows:
    if el not in of: continue
    det = classify(of[el], feats(code))
    coh = coherent(req, det)
    comp = compile_map.get(mid, 0)
    a = agg[req]; a[0]+=1; a[1]+=int(coh); a[2]+=comp; a[3]+=int(coh and comp == 1)

print(f"{'Tipo':5}{'Tot':>5}{'Coerenti':>10}{'Compila':>9}{'Validi(both)':>14}")
T=[0,0,0,0]
for t in sorted(agg):
    a=agg[t]
    for i in range(4): T[i]+=a[i]
    print(f"{t:5}{a[0]:>5}{a[1]:>10}{a[2]:>9}{a[3]:>14}")
print(f"{'TOT':5}{T[0]:>5}{T[1]:>10}{T[2]:>9}{T[3]:>14}")
print(f"\nCoerenti: {T[1]}/{T[0]} ({100*T[1]/T[0]:.0f}%) | Compilanti: {T[2]}/{T[0]} ({100*T[2]/T[0]:.0f}%) | Validi (coerenti & compilanti): {T[3]}/{T[0]} ({100*T[3]/T[0]:.0f}%)")
