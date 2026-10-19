"""Toglie da mutations.db i mutanti dell'operatore i (eliminazione del tag) applicato al bersaglio.

Eliminare il tag del bersaglio elimina anche il suo attributo di test, quindi l'acquisizione
scarta questa combinazione. Se pero' l'attributo e' scritto in forma dinamica
(es. [attr.x-test-nome]="..."), il controllo non lo riconosce e il mutante viene salvato:
nella tesi e' successo con 10 mutanti di CookBook, tolti dalla campagna principale (par. 4.1).

Uso, dalla cartella della campagna:  python ..\\escludi_i_su_bersaglio.py
Prima di togliere qualcosa salva una copia in mutations.db.bak-prima-esclusione.
"""
import os, shutil, sqlite3, sys

db = sys.argv[1] if len(sys.argv) > 1 else "mutations.db"
con = sqlite3.connect(db)
rows = con.execute("select uuid, mutation_id from mutations "
                   "where mutation_name = 'i' and mutation_id like '%\\_alf\\_i' escape '\\'").fetchall()
if not rows:
    print("Nessun mutante i sul bersaglio: niente da togliere.")
    sys.exit(0)

copia = db + ".bak-prima-esclusione"
if not os.path.exists(copia):
    con.close(); shutil.copy2(db, copia); con = sqlite3.connect(db)
for uuid, mid in rows:
    print("tolto", mid)
    con.execute("delete from mutated_files where mutation_uuid = ?", (uuid,))
    con.execute("delete from mutations where uuid = ?", (uuid,))
con.commit()
print(f"Tolti {len(rows)} mutanti. Ora nel database: "
      f"{con.execute('select count(*) from mutations').fetchone()[0]}")
