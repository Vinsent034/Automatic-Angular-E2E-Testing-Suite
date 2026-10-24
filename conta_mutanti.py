"""Stampa quanti mutanti ci sono in un database mutations.db (0 se il file manca o e' vuoto).
Usato da mutanti.ps1: python conta_mutanti.py <percorso di mutations.db>"""
import sqlite3
import sys

try:
    print(sqlite3.connect(sys.argv[1]).execute("select count(*) from mutations").fetchone()[0])
except Exception:
    print(0)
