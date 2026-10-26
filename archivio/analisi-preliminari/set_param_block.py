import sqlite3, sys

block = int(sys.argv[1]) if len(sys.argv) > 1 else 1
ids = [l.strip() for l in open('param_robustness_target59.txt') if l.strip()]
remaining = ids[20:]              # non testati (target59[20:])
b1, b2 = remaining[:19], remaining[19:]
chosen = b1 if block == 1 else b2

con = sqlite3.connect('mutations.db')
cur = con.cursor()
cur.execute("UPDATE mutations SET status='HELD'")
n = 0
for mid in chosen:
    cur.execute("UPDATE mutations SET status='PENDING' WHERE mutation_id=?", (mid,))
    n += cur.rowcount
con.commit()

print(f"remaining totali: {len(remaining)}")
print(f"BLOCCO {block}: richiesti {len(chosen)}, marcati PENDING {n}")
print("PENDING nel DB:", cur.execute("SELECT COUNT(*) FROM mutations WHERE status='PENDING'").fetchone()[0])
miss = [m for m in chosen if cur.execute("SELECT COUNT(*) FROM mutations WHERE mutation_id=?", (m,)).fetchone()[0] == 0]
if miss:
    print("ATTENZIONE, id non trovati nel DB:", miss)
print("primi:", chosen[:2], "ultimo:", chosen[-1])
