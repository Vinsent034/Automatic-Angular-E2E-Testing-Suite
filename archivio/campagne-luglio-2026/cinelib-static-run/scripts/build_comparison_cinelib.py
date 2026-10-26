# -*- coding: utf-8 -*-
# CineLib — confronto Static vs LLM, stesso formato del PDF FlowBoard:
# prima Static poi LLM, ogni percentuale con la sua frazione, conclusione finale.
import csv, re, sys
from collections import defaultdict

SUITE = "C:/Users/vince/OneDrive/Desktop/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
LLM_FILES    = [SUITE + f"/output/tests/cinelib-{g}-batches.csv"
                for g in ["catalog","moviedetail","movieform","reviews","stats"]]
STATIC_FILES = [SUITE + f"/cinelib-static-run/output/tests/batches-{g}.csv"
                for g in ["catalog","moviedetail","movieform","reviews","stats"]]
OUT = sys.argv[1]

STRAT = {'AbsoluteXPathTest':'Absolute','RobulaXPathTest':'Robula','RobulaPlusXPathTest':'Robula+',
         'RelativeXPathTest':'Relative','KatalonXPathTest':'Katalon','SeleniumXPathTest':'Selenium'}
STRAT_ORDER = ['Absolute','Relative','Robula','Robula+','Selenium','Katalon']
OPS = list('abcdefghijk')
ROLE = {'alf':'alpha','bet':'beta','gam':'gamma','del':'delta','eps':'epsilon'}
ROLE_ORDER = ['alpha','beta','gamma','delta','epsilon']
RSYM = {'alpha':'α','beta':'β','gamma':'γ','delta':'δ','epsilon':'ε'}

def parse_op_role(name, mid, tag):
    # LLM: Name = operatore (lettera), ruolo dentro l'Id (_alf_/_bet_/...)
    if len(name) == 1 and name in 'abcdefghijk':
        m = re.search(r'_(alf|bet|gam|del|eps)(?:_[a-k])?_LLM_', mid)
        return name, (ROLE[m.group(1)] if m else 'unknown')
    # Static: Name = target, Id = mutation_<op>_<rule>_<role>, Tag = ruolo
    p = mid.split('_')
    return (p[1] if len(p) > 1 else '?'), (tag if tag in ROLE_ORDER else 'unknown')

def batch_status(execs):
    ap = [st for _, st in execs if st != 'NOT_APPLICABLE']
    if not ap: return 'NOT_TESTED'
    pw = sum(1 for st in ap if st == 'PASSED')
    return 'OBSOLETE' if pw == 0 else ('ROBUST' if pw == len(ap) else 'FRAGILE')

def aggregate(files):
    mut = {}
    for f in files:
        try: fh = open(f, encoding='utf-8', errors='ignore')
        except: print("MANCANTE:", f); continue
        r = csv.reader(fh); next(r, None)
        for row in r:
            if len(row) < 5 or not row[1].startswith('mutation_'): continue
            name, mid, loc, tag, status = row[0], row[1], row[2], row[3], row[4]
            s = STRAT.get(loc)
            if not s: continue
            op, role = parse_op_role(name, mid, tag)
            mut.setdefault((name, mid), {'op': op, 'role': role, 'execs': []})['execs'].append((s, status))
    rq1 = defaultdict(lambda: [0, 0]); rq1_op = defaultdict(lambda: [0, 0]); nc = defaultdict(int)
    r2 = {s: {'success': 0, 'fragility': 0, 'obsolescence': 0, 'notcompiled': 0} for s in STRAT_ORDER}
    r2d = defaultdict(lambda: [0, 0])
    for k, m in mut.items():
        bs = batch_status(m['execs']); op = m['op']; role = m['role']; v = 0 if bs == 'NOT_TESTED' else 1
        rq1[(op, role)][0] += v; rq1[(op, role)][1] += 1; rq1_op[op][0] += v; rq1_op[op][1] += 1
        if bs == 'NOT_TESTED': nc[op] += 1
        for s, st in m['execs']:
            if bs == 'NOT_TESTED': r2[s]['notcompiled'] += 1
            elif bs == 'OBSOLETE': r2[s]['obsolescence'] += 1; r2d[(s, op)][1] += 1
            elif bs == 'ROBUST': r2[s]['success'] += 1; r2d[(s, op)][0] += 1; r2d[(s, op)][1] += 1
            else:
                r2d[(s, op)][1] += 1
                if st == 'PASSED': r2[s]['success'] += 1; r2d[(s, op)][0] += 1
                else: r2[s]['fragility'] += 1
    return dict(rq1), dict(rq1_op), dict(nc), r2, dict(r2d), len(mut)

(Lr1, Lop, Lnc, Lr2, Lr2d, Ln) = aggregate(LLM_FILES)
(Sr1, Sop, Snc, Sr2, Sr2d, Sn) = aggregate(STATIC_FILES)

def roles_of(rq1): return [r for r in ROLE_ORDER if any(role == r for (_, role) in rq1)]
def ops_of(rq1op): return [o for o in OPS if o in rq1op]
def pct(v, t): return f"{100*v/t:.1f}% ({v}/{t})" if t else "–"

def rq1_table(rq1, rq1op, roles, ops):
    out = ["| Op | " + " | ".join(RSYM[r] for r in roles) + " | TOT |", "|" + "--|" * (len(roles) + 2)]
    cv = defaultdict(int); ct = defaultdict(int); TV = TT = 0
    for op in ops:
        cells = []
        for r in roles:
            v, t = rq1.get((op, r), [0, 0]); cells.append(f"{v}/{t}" if t else "–"); cv[r] += v; ct[r] += t
        v, t = rq1op[op]; TV += v; TT += t
        out.append(f"| {op} | " + " | ".join(cells) + f" | {v}/{t} |")
    out.append("| **TOT** | " + " | ".join(f"{cv[r]}/{ct[r]}" for r in roles) + f" | **{TV}/{TT}** |")
    return "\n".join(out), TV, TT

Lroles = roles_of(Lr1); Lops = ops_of(Lop); Sroles = roles_of(Sr1); Sops = ops_of(Sop)
lt, LTV, LTT = rq1_table(Lr1, Lop, Lroles, Lops)
st, STV, STT = rq1_table(Sr1, Sop, Sroles, Sops)
Lpct = 100 * LTV / LTT; Spct = 100 * STV / STT

md = []
md.append("# CineLib — Confronto mutazioni **Static** vs **LLM**\n")
md.append("**Robustezza dei locatori sotto mutazione** — mutation testing su app Angular (CineLib).  ")
md.append("Scope: 5 scenari (catalog, movie-detail, movie-form, reviews, stats), 7 componenti, **45 target** identici per le due tecniche. ")
md.append(f"Mutanti generati e testati: **Static = {Sn}**, **LLM = {Ln}**.  ")
md.append("*(Le due tecniche generano un numero diverso di mutanti — lo Static, meccanico e senza dedup, molti di più; il confronto è quindi sui tassi di validità/robustezza, non sui conteggi assoluti. Ogni percentuale è riportata con la frazione da cui deriva.)*\n")
md.append("Le due tecniche generano mutazioni con gli **stessi 11 operatori (a–k)** sugli **stessi 45 target**; la differenza è **come**: lo Static muta meccanicamente (ignaro di Angular), l'LLM autorando mutazioni valide che preservano binding/`@if`/`@for`.\n")
md.append("---\n")

md.append("## RQ1 — Capacità di generare mutanti VALIDI\n")
md.append("### 1. Static — validi/tot per operatore × ruolo\n" + st + f"\n\n**Validità Static: {STV}/{STT} = {Spct:.1f}%** (mutanti validi / mutanti totali)\n")
md.append("### 2. LLM — validi/tot per operatore × ruolo\n" + lt + f"\n\n**Validità LLM: {LTV}/{LTT} = {Lpct:.1f}%** (mutanti validi / mutanti totali)\n")

md.append("### 3. Confronto validità per operatore (Static vs LLM)\n")
md.append("| Op | Static valid% (validi/tot) | LLM valid% (validi/tot) | Δ (LLM−Static) |")
md.append("|--|--|--|--|")
allops = [o for o in OPS if o in Lop or o in Sop]
for op in allops:
    lv, lt2 = Lop.get(op, [0, 0]); sv, st2 = Sop.get(op, [0, 0])
    lp = 100 * lv / lt2 if lt2 else None; sp = 100 * sv / st2 if st2 else None
    d = f"{lp-sp:+.1f}" if (lp is not None and sp is not None) else "–"
    md.append(f"| {op} | {pct(sv, st2)} | {pct(lv, lt2)} | {d} |")
md.append(f"| **TOT** | **{Spct:.1f}% ({STV}/{STT})** | **{Lpct:.1f}% ({LTV}/{LTT})** | **{Lpct-Spct:+.1f}** |")
md.append(f"\n> **Finding RQ1:** lo Static genera mutanti validi al **{Spct:.1f}% ({STV}/{STT})**, l'LLM al **{Lpct:.1f}% ({LTV}/{LTT})**. La tecnica LLM, comprendendo Angular, evita di rompere binding/direttive/`@for`, mentre lo Static (meccanico) produce in maggioranza mutanti non compilabili.\n")

md.append("### 4. Cause di non-validità (not-compiled) per operatore\n")
md.append("| Op | Static non-validi/tot | LLM non-validi/tot |")
md.append("|--|--|--|")
for op in allops:
    md.append(f"| {op} | {Snc.get(op,0)}/{Sop.get(op,[0,0])[1]} | {Lnc.get(op,0)}/{Lop.get(op,[0,0])[1]} |")
md.append("")

def r2sum_table(r2):
    out = ["| Strategia | Success | Fragility | Obsolescence | NotCompiled | Success% (passed/testabili) |", "|--|--|--|--|--|--|"]
    for s in STRAT_ORDER:
        d = r2[s]; tot = sum(d.values()); te = tot - d['notcompiled']
        sp = f"{100*d['success']/te:.1f}% ({d['success']}/{te})" if te else "–"
        out.append(f"| {s} | {d['success']}/{tot} | {d['fragility']}/{tot} | {d['obsolescence']}/{tot} | {d['notcompiled']}/{tot} | {sp} |")
    return "\n".join(out)

md.append("## RQ2 — Utilità differenziale delle strategie di locatori\n")
md.append("*Nelle due tabelle riassuntive ogni conteggio è espresso come **esito / totale delle esecuzioni di quella strategia** (Success + Fragility + Obsolescence + NotCompiled). Il **Success%** è invece calcolato sui soli mutanti **testabili**, cioè escludendo i NotCompiled: la sua frazione `(passed/testabili)` ha quindi un denominatore più piccolo.*\n")
md.append("### 1. Static — riassuntiva per strategia\n" + r2sum_table(Sr2) + "\n")
md.append("### 2. LLM — riassuntiva per strategia\n" + r2sum_table(Lr2) + "\n")
md.append("### 3. Confronto robustezza per strategia (Success% sui testabili)\n")
md.append("| Strategia | Static Success% (passed/testabili) | LLM Success% (passed/testabili) |")
md.append("|--|--|--|")
for s in STRAT_ORDER:
    ld = Lr2[s]; sd = Sr2[s]
    lte = sum(ld.values()) - ld['notcompiled']; ste = sum(sd.values()) - sd['notcompiled']
    ls = f"{100*ld['success']/lte:.1f}% ({ld['success']}/{lte})" if lte else "–"
    ss = f"{100*sd['success']/ste:.1f}% ({sd['success']}/{ste})" if ste else "–"
    md.append(f"| {s} | {ss} | {ls} |")
md.append("")

def r2det_table(r2d, ops):
    out = ["| Strategia | " + " | ".join(ops) + " |", "|" + "--|" * (len(ops) + 1)]
    for s in STRAT_ORDER:
        cells = []
        for op in ops:
            p, t = r2d.get((s, op), [0, 0]); cells.append(f"{p}/{t}" if t else "–")
        out.append(f"| {s} | " + " | ".join(cells) + " |")
    return "\n".join(out)
md.append("### 4. Dettaglio Static (passed/validi) — strategia × operatore\n" + r2det_table(Sr2d, Sops) + "\n")
md.append("### 5. Dettaglio LLM (passed/validi) — strategia × operatore\n" + r2det_table(Lr2d, Lops) + "\n")

Snv = Sn - STV; Lnv = Ln - LTV
ratio = Sn / Ln
md.append("---\n")
md.append("## Conclusione — quantità non è validità\n")
md.append(
 f"Sugli **stessi 45 target**, la tecnica **Static genera più mutanti** della tecnica LLM — **{Sn}** contro **{Ln}**, "
 f"cioè **{ratio:.1f}× di più** — ma con una **quota di scarto doppia**: "
 f"i mutanti Static validi sono **{STV}/{Sn} = {Spct:.1f}%** (mutanti validi / mutanti totali), "
 f"contro **{LTV}/{Ln} = {Lpct:.1f}%** (mutanti validi / mutanti totali) dell'LLM, con uno scarto di **{Lpct-Spct:.1f} punti percentuali**. "
 f"In altre parole, lo Static scarta **{Snv}/{Sn} = {100*Snv/Sn:.1f}%** dei mutanti prodotti perché non compilabili, "
 f"mentre l'LLM ne scarta **{Lnv}/{Ln} = {100*Lnv/Ln:.1f}%**.\n")

open(OUT, "w", encoding="utf-8").write("\n".join(md))
print("scritto", OUT, "| Static", Sn, "validi", STV, "| LLM", Ln, "validi", LTV)
