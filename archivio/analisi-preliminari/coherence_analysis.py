# Analisi di coerenza (RQ 1.2) per i mutanti PARAMETRICI.
# Per ogni mutante confronta con l'originale, rileva il tipo di modifica EFFETTIVA
# e lo confronta col tipo RICHIESTO (mutation_name a-k). Output: coerenti / incoerenti per tipo.

import sqlite3, sys
from collections import Counter
from bs4 import BeautifulSoup

ORIG = {
    "search_component_html": r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/search/feature/src/lib/search.component.html",
    "card_component_html":   r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/shared/ui/media/src/lib/card.component.html",
}

def feats(html):
    soup = BeautifulSoup(html, "html.parser")
    els = soup.find_all(True)
    tags = Counter(e.name for e in els)
    tagseq = [e.name for e in els]
    nattr = 0
    keys = Counter()        # (tag, attr-name)
    keyval = Counter()      # (tag, attr-name, value)
    parentpair = Counter()  # (tag, parent-tag) : relazione figlio->genitore
    for e in els:
        for k, v in e.attrs.items():
            val = " ".join(v) if isinstance(v, list) else v
            keys[(e.name, k)] += 1
            keyval[(e.name, k, val)] += 1
            nattr += 1
        parentpair[(e.name, e.parent.name if e.parent else None)] += 1
    toks = Counter(soup.get_text().split())   # multiset di token di testo (ordine-indipendente)
    return dict(tags=tags, tagseq=tagseq, nattr=nattr, keys=keys, keyval=keyval,
                toks=toks, ntoks=sum(toks.values()), parentpair=parentpair,
                ntags=sum(tags.values()))

def classify(O, M):
    # tag multiset cambiato?
    if O["tags"] != M["tags"]:
        if M["ntags"] > O["ntags"]:
            return "k"               # un tag in piu -> insertion
        if M["ntags"] < O["ntags"]:
            return "i"               # un tag in meno -> removal (unwrap)
        return "j"                   # stesso numero, nomi diversi -> type modification
    # stesso multiset di tag
    if M["nattr"] < O["nattr"]:
        return "b"                   # attributo rimosso
    if M["nattr"] > O["nattr"]:
        return "attr+"               # attributo aggiunto (anomalo)
    if O["keys"] != M["keys"]:
        return "c"                   # nome attributo cambiato -> identifier mod
    if O["keyval"] != M["keyval"]:
        return "a"                   # valore attributo cambiato
    # tag + attributi identici -> guarda testo (ordine-indipendente)
    if O["toks"] != M["toks"]:
        if M["ntoks"] < O["ntoks"]:
            return "e"               # token di testo rimossi -> text removal
        return "d"                   # token cambiati, stesso numero -> text modification
    # testo identico (come multiset) -> movimento?
    if O["tagseq"] != M["tagseq"]:
        if O["parentpair"] == M["parentpair"]:
            return "f"               # stessa relazione figlio-genitore, solo ordine -> within container
        return "g"                   # cambia il genitore di qualche elemento -> any point
    return "none"                    # nessuna differenza rilevata

def is_coherent(requested, detected):
    if requested == detected:
        return True
    # 'h' (tra template) in contesto a singolo file non e' realizzabile:
    # lo accettiamo come coerente solo se l'LLM ha prodotto un movimento (g) o una rimozione (i)
    if requested == "h" and detected in ("g", "i"):
        return True
    return False

c = sqlite3.connect("mutations.db")
cur = c.cursor()
rows = cur.execute(
    "SELECT m.element, m.mutation_name, m.mutation_id, mf.mutated_code "
    "FROM mutations m JOIN mutated_files mf ON mf.mutation_uuid = m.uuid "
    "WHERE m.mutation_type='LLM_PARAMETRIC' ORDER BY m.mutation_name"
).fetchall()

orig_feats = {k: feats(open(v, encoding="utf-8").read()) for k, v in ORIG.items()}

per_type = {}   # type -> [coherent, incoherent, Counter(detected)]
for element, req, mid, code in rows:
    if element not in orig_feats:
        continue
    det = classify(orig_feats[element], feats(code))
    coh = is_coherent(req, det)
    d = per_type.setdefault(req, [0, 0, Counter()])
    d[0 if coh else 1] += 1
    d[2][det] += 1

print(f"{'Tipo':4}{'Tot':>5}{'Coerenti':>10}{'Incoerenti':>12}   distribuzione tipo rilevato")
tot_c = tot_i = 0
for t in sorted(per_type):
    coh, inc, dist = per_type[t]
    tot_c += coh; tot_i += inc
    print(f"{t:4}{coh+inc:>5}{coh:>10}{inc:>12}   {dict(dist)}")
print(f"\nTOTALE: coerenti {tot_c} / {tot_c+tot_i}  ({100*tot_c/max(1,tot_c+tot_i):.1f}%)")
