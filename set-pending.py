#!/usr/bin/env python
"""
Attiva/disattiva i mutanti di UNO scenario CineLib per il mutation-tester.

Il tester gira TUTTE le classi in --test-dir contro OGNI mutante PENDING, quindi
va eseguito uno scenario alla volta: prima si mettono a PENDING solo i mutanti di
quello scenario (gli altri restano HELD), poi si lancia il tester con la test-dir
corrispondente.

Uso:
  python set-pending.py moviedetail        # movie-detail + cast-row  -> PENDING (173)
  python set-pending.py movieform          # movie-form               -> PENDING (113)
  python set-pending.py moviedetail --reset # rimette quei mutanti a HELD
  python set-pending.py status             # stampa la distribuzione stati
"""
import sqlite3, sys, os

DB = os.path.join(os.path.dirname(os.path.abspath(__file__)), "mutations.db")
SCENARIOS = {
    "moviedetail": ["movie_detail_component_html", "cast_row_component_html"],
    "movieform":   ["movie_form_component_html"],
    "reviews":     ["reviews_component_html"],
    "stats":       ["stats_component_html"],
}

def status():
    con = sqlite3.connect(DB); cur = con.cursor()
    print("== stato per (element, status) [LLM_ROLE] ==")
    for el, st, n in cur.execute(
        "select element,status,count(*) from mutations where mutation_type='LLM_ROLE' "
        "group by element,status order by element,status"):
        print(f"  {el:34} {st:9} {n}")
    con.close()

def main():
    if len(sys.argv) < 2 or sys.argv[1] == "status":
        status(); return
    sc = sys.argv[1]
    reset = "--reset" in sys.argv
    if sc not in SCENARIOS:
        print("scenario sconosciuto:", sc, "->", list(SCENARIOS)); return
    new = "HELD" if reset else "PENDING"
    frm = "PENDING" if reset else "HELD"
    con = sqlite3.connect(DB); cur = con.cursor()
    q = ("update mutations set status=? where mutation_type='LLM_ROLE' "
         "and status=? and element in (%s)" % ",".join("?" * len(SCENARIOS[sc])))
    cur.execute(q, [new, frm] + SCENARIOS[sc])
    con.commit()
    print(f"{sc}: {cur.rowcount} mutanti {frm} -> {new}")
    con.close()
    status()

if __name__ == "__main__":
    main()
