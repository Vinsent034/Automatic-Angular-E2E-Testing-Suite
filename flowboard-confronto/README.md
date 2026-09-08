# FlowBoard — Confronto LLM vs Static (tesi: robustezza locatori sotto mutazione)

- **FlowBoard-Confronto-LLM-vs-Static.pdf** — documento finale (tabelle RQ1/RQ2 + confronto)
- FlowBoard-Confronto-LLM-vs-Static.md / .html — sorgenti
- RISULTATI-LLM-1225.md — risultati LLM completi (6 scenari, 1225 mutanti)
- RISULTATI-STATIC-2840.md — risultati Static completi (6 scenari, 2840 mutanti)

Scope: tutti e 6 gli scenari (S1-S6, 9 componenti). Mutanti: LLM=1225, Static=2840.

## Risultati
- **RQ1 (validità generazione): LLM 95.2% vs Static 41.5%** (+53.7). L'LLM, comprendendo
  Angular, genera mutanti validi; lo Static meccanico rompe binding/@for → maggioranza non compila.
- **RQ2 (robustezza strategie):** Absolute il più fragile in entrambe (LLM 85.4%, Static 56.4%);
  strategie attributo/classe/testo (Relative/Robula/Robula+/Selenium) le più robuste. Firma coerente.
