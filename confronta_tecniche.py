# -*- coding: utf-8 -*-
"""Confronta le due generazioni di un'applicazione: con il modello linguistico (generazione-<app>) e con il generatore
statico (generazione-<app>-statico). Mostra, affiancati, quanti mutanti hanno prodotto, quante delle 55 combinazioni
operatore x ruolo sono presenti e, se si e' lanciato `esegui` su entrambe, i risultati dei test.

Uso: python confronta_tecniche.py <applicazione>      (di solito: .\\mutanti.ps1 <applicazione> confronta)"""
import json, os, sqlite3, sys

SUITE = os.path.dirname(os.path.abspath(__file__))
if len(sys.argv) != 2 or sys.argv[1] not in ("cinelib", "flowboard", "cookbook"):
    sys.exit("uso: python confronta_tecniche.py <cinelib|flowboard|cookbook>")
app = sys.argv[1]
CART = {"llm": os.path.join(SUITE, f"generazione-{app}"), "statico": os.path.join(SUITE, f"generazione-{app}-statico")}
ROLE_LLM = {"alf", "bet", "gam", "del", "eps"}
ROLE_STAT = {"alpha": "alf", "beta": "bet", "gamma": "gam", "delta": "del", "epsilon": "eps"}
OPS = "abcdefghijk"


def leggi(tec):
    db = os.path.join(CART[tec], "mutations.db")
    if not os.path.exists(db):
        return None
    c = sqlite3.connect(db)
    celle, per_op, n = set(), {o: 0 for o in OPS}, 0
    for el, nome, tipo, mid in c.execute("select element, mutation_name, mutation_type, mutation_id from mutations"):
        n += 1
        if tec == "llm":
            op = nome
            ruolo = next((t for t in reversed(mid.split("_")) if t in ROLE_LLM), None)
        else:
            op = mid
            ruolo = ROLE_STAT.get(el)
        if op in per_op:
            per_op[op] += 1
            if ruolo:
                celle.add((op, ruolo))
    c.close()
    tot_dup = None
    comp = os.path.join(CART[tec], "mutations-completo-con-duplicati.db")
    if os.path.exists(comp):
        cc = sqlite3.connect(comp)
        tot_dup = cc.execute("select count(*) from mutations").fetchone()[0]
        cc.close()
    rj = os.path.join(CART[tec], "output", "tests", "riepilogo.json")
    prove = json.load(open(rj, encoding="utf-8")) if os.path.exists(rj) else None
    return {"n": n, "celle": len(celle), "per_op": per_op, "con_duplicati": tot_dup, "prove": prove}


D = {t: leggi(t) for t in ("llm", "statico")}
if D["llm"] is None and D["statico"] is None:
    sys.exit(f"Non c'e' nessuna generazione per {app}: fai prima .\\mutanti.ps1 {app} prompt / completa e .\\mutanti.ps1 {app} statico")


def col(t, f, mancante="non generato"):
    return f(D[t]) if D[t] is not None else mancante


def pct(a, b):
    return f"{100 * a / b:.1f}%" if b else "-"


L = []
L.append(f"CONFRONTO FRA LE DUE TECNICHE - {app}")
L.append("")
L.append(f"  {'':<42}{'Generatore con LLM':>22}{'Generatore statico':>22}")
L.append(f"  {'Mutanti generati (distinti)':<42}{col('llm', lambda d: str(d['n'])):>22}{col('statico', lambda d: str(d['n'])):>22}")
if D["statico"] and D["statico"]["con_duplicati"]:
    L.append(f"  {'  statici: con i duplicati':<42}{'':>22}{str(D['statico']['con_duplicati']):>22}")
L.append(f"  {'Combinazioni operatore x ruolo presenti':<42}{col('llm', lambda d: str(d['celle']) + ' su 55'):>22}{col('statico', lambda d: str(d['celle']) + ' su 55'):>22}")
L.append("")
L.append("  Mutanti per operatore")
for o in OPS:
    a = col("llm", lambda d: str(d["per_op"][o]), "-")
    b = col("statico", lambda d: str(d["per_op"][o]), "-")
    L.append(f"    {o:<40}{a:>22}{b:>22}")
L.append("")
L.append("  Prova con i test (campione di mutanti; lanciare  .\\mutanti.ps1 " + app + " esegui 20  e  .\\mutanti.ps1 " + app + " esegui 20 statico)")
P = {t: (D[t]["prove"] if D[t] else None) for t in D}
if not P["llm"] and not P["statico"]:
    L.append("    non ancora provata: servono i risultati di esegui su almeno una delle due generazioni.")
else:
    def r(t, f):
        return f(P[t]) if P[t] else "non provato"
    L.append(f"    {'Mutanti provati':<40}{r('llm', lambda p: str(p['provati'])):>22}{r('statico', lambda p: str(p['provati'])):>22}")
    L.append(f"    {'  che compilano (validi)':<40}{r('llm', lambda p: str(p['validi']) + ' (' + pct(p['validi'], p['provati']) + ')'):>22}{r('statico', lambda p: str(p['validi']) + ' (' + pct(p['validi'], p['provati']) + ')'):>22}")
    L.append(f"    {'  non compilati':<40}{r('llm', lambda p: str(p['non_compilati'])):>22}{r('statico', lambda p: str(p['non_compilati'])):>22}")
    L.append(f"    {'Prove sprecate':<40}{r('llm', lambda p: str(p['prove_sprecate']) + ' su ' + str(p['prove_valutabili'])):>22}{r('statico', lambda p: str(p['prove_sprecate']) + ' su ' + str(p['prove_valutabili'])):>22}")
    L.append("")
    L.append("    Stabilita per strategia (successi / valutabili)")
    nomi = list((P["llm"] or P["statico"])["strategie"].keys())
    for nome in nomi:
        def st(p):
            t = p["strategie"][nome]
            val = t["succ"] + t["frag"]
            return pct(t["succ"], val)
        L.append(f"      {nome:<38}{r('llm', st):>22}{r('statico', st):>22}")
L.append("")
L.append("  Con pochi mutanti i numeri sono solo indicativi. Il confronto completo della tesi (tutti i mutanti) e' in analisi-risultati/.")
testo = "\n".join(L)
print(testo)
dest = CART["llm"] if D["llm"] is not None else CART["statico"]
with open(os.path.join(dest, "confronto.txt"), "w", encoding="utf-8") as fh:
    fh.write(testo + "\n")
print(f"\n  Salvato in {os.path.join(dest, 'confronto.txt')}")
