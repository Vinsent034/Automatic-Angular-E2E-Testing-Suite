import csv, glob, re
from collections import defaultdict, Counter

# raggruppa le righe per mutazione (Id), su tutti i block static
rows_by_mut = defaultdict(list)
for f in glob.glob('output/tests/srch-0*-full/block*.csv'):
    tc = f.split('srch-0')[1][:1]   # numero del test case (1..4)
    for r in csv.DictReader(open(f)):
        rows_by_mut[(tc, r['Id'])].append(r)

op_pat = re.compile(r'mutation_[a-k]_(.+?)_mut_')

cat_by_op = defaultdict(Counter)   # operatore -> categoria -> conteggio
examples = defaultdict(list)

for (tc, mid), rows in rows_by_mut.items():
    statuses = [r['Status'] for r in rows]
    if all(s == 'NOT_APPLICABLE' for s in statuses):
        cat = 'not_compiled'
    elif all(s == 'FAILED' for s in statuses):
        cat = 'obsolescence'
    elif any(s == 'FAILED' for s in statuses) and any(s == 'PASSED' for s in statuses):
        cat = 'fragility'
    elif all(s == 'PASSED' for s in statuses):
        cat = 'success'
    else:
        cat = 'mixed/other'
    m = op_pat.search(mid)
    op = m.group(1) if m else '?'
    cat_by_op[op][cat] += 1
    if len(examples[cat]) < 4:
        examples[cat].append((op, mid))

print("=== Operatori -> categoria (n. mutazioni) ===")
for op in sorted(cat_by_op):
    print(f"  {op:<20} {dict(cat_by_op[op])}")

print("\n=== Riepilogo per categoria: quali operatori la causano ===")
by_cat = defaultdict(Counter)
for op, cats in cat_by_op.items():
    for c, n in cats.items():
        by_cat[c][op] += n
for cat in ('not_compiled', 'obsolescence', 'fragility', 'success'):
    print(f"\n[{cat.upper()}] operatori responsabili:")
    for op, n in by_cat[cat].most_common():
        print(f"   {op:<20} {n}")
