import sqlite3, sys

idx = int(sys.argv[1]) if len(sys.argv) > 1 else 0  # blocco 0,1,2
SIZE = 19
ids = [l.strip() for l in open('param_untested.txt') if l.strip()]
chunk = ids[idx * SIZE:(idx + 1) * SIZE]

con = sqlite3.connect('mutations.db')
cur = con.cursor()
cur.execute("UPDATE mutations SET status='HELD'")
n = 0
for mid in chunk:
    cur.execute("UPDATE mutations SET status='PENDING' WHERE mutation_id=?", (mid,))
    n += cur.rowcount
con.commit()
print(f"untested totali: {len(ids)}")
print(f"BLOCCO {idx}: {len(chunk)} richiesti, marcati PENDING {n}")
print("PENDING nel DB:", cur.execute("SELECT COUNT(*) FROM mutations WHERE status='PENDING'").fetchone()[0])
print("primi:", chunk[:2], "ultimo:", chunk[-1] if chunk else '')
