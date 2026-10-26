# -*- coding: utf-8 -*-
"""Toglie dal database dei mutanti statici quelli identici a uno precedente (stesso codice mutato negli stessi file).

Il generatore statico produce anche mutanti identici fra loro (§ 3.6.2 della tesi). Si tiene il primo di ogni gruppo di identici:
è la stessa regola usata per i risultati della tesi (analisi-risultati/analizza_blocco_a.py, funzione duplicati_da_db).
Prima di toccare il database ne salva una copia, mutations-completo-con-duplicati.db, nella stessa cartella.

Uso: python deduplica_statici.py <cartella con mutations.db> [--solo-conta]
(con --solo-conta non toglie niente: dice solo quanti sono i distinti; si usa per CookBook, dove nella tesi si sono provati tutti)"""
import collections, hashlib, os, shutil, sqlite3, sys

if len(sys.argv) not in (2, 3) or (len(sys.argv) == 3 and sys.argv[2] != "--solo-conta"):
    sys.exit("uso: python deduplica_statici.py <cartella con mutations.db> [--solo-conta]")
cartella = sys.argv[1]
solo_conta = len(sys.argv) == 3
db = os.path.join(cartella, "mutations.db")
copia = os.path.join(cartella, "mutations-completo-con-duplicati.db")
if not os.path.exists(db):
    sys.exit(f"non trovo {db}")
if not solo_conta and not os.path.exists(copia):
    shutil.copyfile(db, copia)

c = sqlite3.connect(db)
files = collections.defaultdict(list)
for u, p, code in c.execute("select mutation_uuid, target_file_path, mutated_code from mutated_files"):
    files[u].append((p, hashlib.md5(code.encode()).hexdigest()))
visti, dup = set(), []
totale = 0
for (u,) in c.execute("select uuid from mutations order by rowid"):
    totale += 1
    k = tuple(sorted(files[u]))
    if k in visti:
        dup.append(u)
    else:
        visti.add(k)
if solo_conta:
    print(f"Mutanti statici generati: {totale} | distinti: {totale - len(dup)} | identici a uno precedente: {len(dup)} (lasciati nel database)")
    sys.exit(0)
c.executemany("delete from mutated_files where mutation_uuid=?", [(u,) for u in dup])
c.executemany("delete from mutations where uuid=?", [(u,) for u in dup])
c.commit()
c.execute("vacuum")
c.close()
print(f"Mutanti statici generati: {totale} | distinti: {totale - len(dup)} | duplicati tolti: {len(dup)}")
print(f"(copia completa con i duplicati: {copia})")
