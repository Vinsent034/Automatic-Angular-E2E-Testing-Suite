import sqlite3, csv, glob, re

# mutation_id testati: estratti dalla colonna Id dei block CSV
pat = re.compile(r'LLMP_(?:card|search)_component_html_[a-k]_\d+')
tested = set()
for f in glob.glob('output/tests/llm-param-robustness/block*.csv'):
    if 'INVALID' in f:
        continue
    for row in csv.reader(open(f)):
        for cell in row:
            m = pat.search(cell)
            if m:
                tested.add(m.group(0))

con = sqlite3.connect('mutations.db')
allids = [r[0] for r in con.execute("SELECT mutation_id FROM mutations WHERE mutation_type='LLM_PARAMETRIC'")]
untested = sorted(set(allids) - tested)

print(f"parametrico totale: {len(allids)}")
print(f"testati (dai block): {len(tested)}")
print(f"NON testati: {len(untested)}")
open('param_untested.txt', 'w').write('\n'.join(untested) + '\n')
print("salvato param_untested.txt")
print("esempi:", untested[:3], "...", untested[-1] if untested else '')
