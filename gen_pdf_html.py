# Genera l'HTML impaginato (poi convertito in PDF) con legenda + tabelle RQ1/RQ2 + riepilogo 807.
import csv, glob, re, json, sys
from collections import defaultdict
sys.stdout.reconfigure(encoding='utf-8')
ROLE = {'alf':'α','bet':'β','gam':'γ','del':'δ','eps':'ε'}
OPS = list('abcdefghijk')
STRAT = {'AbsoluteXPathTest':'Absolute','RelativeXPathTest':'Relative','SeleniumXPathTest':'Selenium',
         'KatalonXPathTest':'Katalon','RobulaXPathTest':'Robula','RobulaPlusXPathTest':'Robula+'}
SORDER = ['Absolute','Relative','Selenium','Katalon','Robula','Robula+']

def load(fn):
    m = {}
    for r in csv.DictReader(open(fn)):
        d = m.setdefault(r['Id'], {'op': r['Name'], 'st': {}})
        d['st'][STRAT.get(r['Locator'], r['Locator'])] = r['Status']
        mm = re.search(r'_(alf|bet|gam|del|eps)(?:_[a-k])?_LLM_ROLE', r['Id'])
        d['role'] = mm.group(1) if mm else '?'
    return m

mut = {}
for f in glob.glob('output/tests/cinelib-*-batches.csv'):
    if any(x in f for x in ['catalog', '-stats.csv', 'retry']):
        continue
    mut.update(load(f))
cat = load('output/tests/cinelib-catalog-batches.csv')
cat.update(load('output/tests/cinelib-catalog-retry-final.csv'))
mut.update(cat)
for d in mut.values():
    d['valid'] = any(s != 'NOT_APPLICABLE' for s in d['st'].values())
assert len(mut) == 807, len(mut)

types = {t['id']: t for t in json.load(open('mutation-types.json'))['types']}
roles = [r for r in ['alf','bet','gam','del'] if any(d['role'] == r for d in mut.values())]
grid = defaultdict(lambda: [0, 0])
for d in mut.values():
    grid[(d['op'], d['role'])][1] += 1
    grid[(d['op'], d['role'])][0] += 1 if d['valid'] else 0
nv = defaultdict(int)
for d in mut.values():
    if not d['valid']:
        nv[d['op']] += 1
sd = defaultdict(lambda: [0, 0])
for d in mut.values():
    for s, st in d['st'].items():
        if st == 'NOT_APPLICABLE':
            continue
        sd[(s, d['op'])][1] += 1
        sd[(s, d['op'])][0] += 1 if st == 'PASSED' else 0

notcomp = sum(1 for d in mut.values() if not d['valid'])
obs = 0
succ = defaultdict(int); frag = defaultdict(int)
for d in mut.values():
    if not d['valid']:
        continue
    sts = {s: d['st'].get(s) for s in SORDER}
    if all(v == 'FAILED' for v in sts.values()):
        obs += 1; continue
    for s in SORDER:
        if sts[s] == 'PASSED': succ[s] += 1
        elif sts[s] == 'FAILED': frag[s] += 1

T = 807
CSS = """<style>
@page{size:A4;margin:14mm}
body{font-family:'Segoe UI',Arial,sans-serif;color:#1a1a1a;font-size:11px;line-height:1.35}
h1{font-size:19px;margin:0 0 2px} h2{font-size:13.5px;margin:15px 0 5px;border-bottom:2px solid #444;padding-bottom:2px}
.sub{color:#666;font-size:10px;margin-bottom:8px}
table{border-collapse:collapse;width:100%;margin:5px 0 3px;font-size:10px}
th,td{border:1px solid #bbb;padding:3px 6px;text-align:center}
th{background:#2c3e50;color:#fff;font-weight:600}
td.l{text-align:left} tr:nth-child(even) td{background:#f4f6f8}
.tot td{font-weight:700;background:#e8ecf0 !important}
.note{font-size:9.5px;color:#555;margin:2px 0 8px}
.op{font-weight:700;background:#eef2f6} .big{font-weight:700;color:#0a7a4b}
</style>"""

H = ['<!doctype html><meta charset="utf-8">', CSS]
H.append('<h1>CineLib &mdash; Risultati mutation testing dei locatori</h1>')
H.append('<div class="sub">Applicazione Angular 19 &middot; 807 mutanti LLM &middot; 5 scenari (catalog, dettaglio, form, reviews, stats) &middot; 6 strategie di locatori &middot; 2026-07-10</div>')

H.append('<h2>Legenda &mdash; tipi di mutazione (operatori a&ndash;k)</h2>')
H.append('<table><tr><th>ID</th><th>Nome</th><th>Descrizione</th></tr>')
for op in OPS:
    t = types[op]
    H.append(f'<tr><td class="op">{op}</td><td class="l"><b>{t["name"]}</b></td><td class="l">{t["description"]}</td></tr>')
H.append('</table>')

H.append('<h2>Legenda &mdash; ruoli relazionali (target della mutazione)</h2>')
H.append('<table><tr><th>Simbolo</th><th>Ruolo</th><th>Descrizione</th></tr>'
    '<tr><td class="op">α</td><td class="l">target</td><td class="l">l\'elemento bersaglio del test</td></tr>'
    '<tr><td class="op">β</td><td class="l">parent</td><td class="l">il genitore diretto del target</td></tr>'
    '<tr><td class="op">γ</td><td class="l">ancestor</td><td class="l">un antenato (nonno) del target</td></tr>'
    '<tr><td class="op">δ</td><td class="l">sibling</td><td class="l">un fratello del target</td></tr>'
    '<tr><td class="op">ε</td><td class="l">containing component</td><td class="l">antenato con tag app-* (0 mutanti per-file)</td></tr></table>')

H.append('<h2>RQ1 &mdash; Validità dei mutanti generati (validi / totali)</h2>')
H.append('<div class="note">Frazione di mutanti che compilano, per combinazione operatore &times; ruolo.</div>')
H.append('<table><tr><th>Op</th>' + ''.join(f'<th>{ROLE[r]}</th>' for r in roles) + '<th>TOTALE</th></tr>')
for op in OPS:
    cells = []; tv = tt = 0
    for r in roles:
        v, t = grid[(op, r)]; tv += v; tt += t
        cells.append(f'{v}/{t}' if t else '&ndash;')
    if tt:
        H.append(f'<tr><td class="op">{op}</td>' + ''.join(f'<td>{c}</td>' for c in cells) + f'<td><b>{tv}/{tt}</b></td></tr>')
tot = [[sum(grid[(op, r)][i] for op in OPS) for i in (0, 1)] for r in roles]
gv = sum(x[0] for x in tot); gt = sum(x[1] for x in tot)
H.append('<tr class="tot"><td>TOT</td>' + ''.join(f'<td>{a}/{b}</td>' for a, b in tot) + f'<td>{gv}/{gt}</td></tr></table>')
vh = sum(1 for d in mut.values() if d['op'] != 'h' and d['valid'])
th = sum(1 for d in mut.values() if d['op'] != 'h')
H.append(f'<div class="note"><span class="big">Validità complessiva: {gv}/{gt}.</span> Escludendo l\'operatore h (cross-template, not-compilable per costruzione): <b>{vh}/{th} = {100*vh/th:.1f}%</b>.</div>')

H.append('<h2>RQ1 &mdash; Cause di non-validità</h2>')
H.append('<table><tr><th>Operatore</th><th>Mutanti non validi</th><th>Causa</th></tr>')
for op in OPS:
    if nv[op]:
        cause = 'spostamento cross-template: not-compilable per costruzione' if op == 'h' else 'rompe binding/struttura del template Angular'
        H.append(f'<tr><td class="op">{op}</td><td>{nv[op]}</td><td class="l">{cause}</td></tr>')
H.append(f'<tr class="tot"><td>TOT</td><td>{sum(nv.values())}/807</td><td class="l">di cui {nv["h"]} da h (attesi)</td></tr></table>')

opsp = [o for o in OPS if any((s, o) in sd for s in SORDER)]
H.append('<h2>RQ2 &mdash; Dettaglio robustezza: passed / compilati, per strategia &times; operatore</h2>')
H.append('<div class="note">Per ogni strategia: quanti mutanti (validi) di quel tipo il locatore continua a trovare (Success).</div>')
H.append('<table><tr><th>Strategia</th>' + ''.join(f'<th>{o}</th>' for o in opsp) + '</tr>')
for s in SORDER:
    H.append(f'<tr><td class="l op">{s}</td>' + ''.join((f'<td>{sd[(s,o)][0]}/{sd[(s,o)][1]}</td>' if sd[(s,o)][1] else '<td>&ndash;</td>') for o in opsp) + '</tr>')
H.append('</table>')

H.append('<h2>Tabella riassuntiva &mdash; robustezza per strategia (su 807 mutanti)</h2>')
H.append('<div class="note">Obsolescence e Not Compiled dipendono dalla mutazione (uguali per tutte le strategie). <b>I not_compiled includono i mutanti h.</b></div>')
H.append('<table><tr><th>Strategia</th><th>Success</th><th>Fragility</th><th>Obsolescence</th><th>Not Compiled</th></tr>')
for s in SORDER:
    H.append(f'<tr><td class="l op">{s}</td><td class="big">{succ[s]}/{T}</td><td>{frag[s]}/{T}</td><td>{obs}/{T}</td><td>{notcomp}/{T}</td></tr>')
H.append('</table>')
H.append(f'<div class="note">Ogni riga somma a 807 = Success + Fragility + Obsolescence ({obs}) + Not Compiled ({notcomp}).</div>')

open('output/RISULTATI-807.html', 'w', encoding='utf-8').write('\n'.join(H))
print('OK. Riepilogo 807: NotCompiled', notcomp, '(h=' + str(nv['h']) + ') | Obsolescence', obs)
for s in SORDER:
    print(f'  {s:9} Success {succ[s]}/807  Fragility {frag[s]}/807')
