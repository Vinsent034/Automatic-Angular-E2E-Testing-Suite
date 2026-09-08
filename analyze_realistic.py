import sqlite3, re, collections, difflib
con = sqlite3.connect('mutations.db'); cur = con.cursor()
orig = {
 'card_component_html': open(r"C:\Users\vince\OneDrive\Desktop\Tirocinio\angular-spotify\libs\web\shared\ui\media\src\lib\card.component.html", encoding='utf-8').read(),
 'search_component_html': open(r"C:\Users\vince\OneDrive\Desktop\Tirocinio\angular-spotify\libs\web\search\feature\src\lib\search.component.html", encoding='utf-8').read(),
}
def norm(s):
    return [ln.strip() for ln in s.splitlines() if ln.strip()]
def tagcount(s):
    return collections.Counter(re.findall(r'<([a-zA-Z][\w-]*)', s))
rows = list(cur.execute("""SELECT m.element, m.mutation_name, mf.mutated_code
   FROM mutations m JOIN mutated_files mf ON m.uuid = mf.mutation_uuid
   WHERE m.mutation_type='LLM_GENERATED'"""))
cls = collections.Counter()
multi = 0
examples = {'wrapper':None,'attr':None,'multi':None}
for elem, name, code in rows:
    o = norm(orig[elem]); m = norm(code)
    sm = difflib.SequenceMatcher(None, o, m)
    ops = [op for op in sm.get_opcodes() if op[0] != 'equal']
    lines_changed = sum(max(op[2]-op[1], op[4]-op[3]) for op in ops)
    blocks = len(ops)
    tag_added = sum((tagcount(code) - tagcount(orig[elem])).values())
    if tag_added > 0:
        c = 'WRAPPER/TAG aggiunto (tocca il vicinato: inserisce un tag)'
        if not examples['wrapper']: examples['wrapper'] = (elem,name,o,m,ops)
    elif blocks <= 1 and lines_changed <= 1:
        c = 'LOCALIZZATA (1 sola riga/elemento: classe, attributo o testo)'
        if not examples['attr']: examples['attr'] = (elem,name,o,m,ops)
    else:
        c = 'MULTI-ELEMENTO (piu righe/blocchi: riordino o ristrutturazione)'
        if not examples['multi']: examples['multi'] = (elem,name,o,m,ops)
    if blocks > 1 or tag_added > 0:
        multi += 1
    cls[c] += 1
print('=== Localita delle 200 mutazioni realistiche ===')
for k,v in cls.most_common(): print(f'  {v:>4}  {k}')
print(f'\n  -> {multi}/200 toccano PIU di un punto (tag nuovo o piu blocchi) = "tocca il vicinato"')
print(f'  -> {200-multi}/200 restano confinate a un singolo punto')

def show(tag):
    ex = examples[tag]
    if not ex: print(f'(nessun esempio {tag})'); return
    elem,name,o,m,ops = ex
    print(f'\n----- ESEMPIO [{tag}]  file={elem}  label="{name}" -----')
    for op,i1,i2,j1,j2 in ops:
        for ln in o[i1:i2]: print('   -', ln[:110])
        for ln in m[j1:j2]: print('   +', ln[:110])
for t in ('attr','wrapper','multi'): show(t)
