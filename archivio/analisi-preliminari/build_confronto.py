# Costruisce il PDF di confronto (3 tabelle) + i prompt LLM sotto le rispettive tabelle.
# Presentazione corretta (feedback prof Q4): Obsolescence e Not-Compiled dipendono dalla
# MUTAZIONE (non dal locatore) -> riportate UNA volta per set; per-locatore solo Success/Fragility.
import html, os

RES = "mutation-generator/llm-generator/src/main/resources"
param_prompt = html.escape(open(f"{RES}/parametric-prompt.txt", encoding="utf-8").read())
real_prompt = html.escape(open(f"{RES}/realistic-prompt.txt", encoding="utf-8").read())

param_user_example = html.escape(
    "Apply ONLY the following mutation type to the template below.\n\n"
    "Mutation type id: k\n"
    "Name: HTML Tag Insertion\n"
    "Definition: Insert a new wrapper/container tag around an existing element (add a hierarchy level).\n"
    "Generic example:\n"
    "  Before: <tag attr=\"value\">text</tag>\n"
    "  After:  <wrapper>\n    <tag attr=\"value\">text</tag>\n  </wrapper>\n\n"
    "Produce exactly N variants, numbered 1..N, each applying THIS mutation type to a DIFFERENT "
    "element you choose. Output ONLY the delimited blocks, with {type_id} = k.\n\n"
    "----- ORIGINAL TEMPLATE -----\n"
    "{contenuto completo del file .html}\n"
    "----- END ORIGINAL TEMPLATE -----"
)
real_user_example = html.escape(
    "Here is the complete source of one Angular template.\n"
    "Produce exactly N variants of it, numbered 1..N, each in its own delimited block exactly as "
    "specified in your instructions. Decide entirely on your own what to change in each variant. "
    "Output ONLY the blocks, nothing else.\n\n"
    "----- ORIGINAL TEMPLATE -----\n"
    "{contenuto completo del file .html}\n"
    "----- END ORIGINAL TEMPLATE -----"
)

CSS = """
  @page { size: A4 portrait; margin: 15mm; }
  html,body{margin:0;background:#fff;font-family:'Segoe UI',Helvetica,Arial,sans-serif;color:#1f1f1f;}
  h1{font-size:18px;font-weight:700;margin:0 0 2px 0;}
  h2{font-size:14px;font-weight:600;margin:20px 0 3px 0;}
  h3{font-size:12px;font-weight:600;margin:14px 0 4px 0;color:#333;}
  p.sub{font-size:11px;color:#555;margin:0 0 10px 0;}
  p.note{font-size:11px;color:#444;margin:6px 0 10px 0;padding:8px 12px;background:#f7f7f2;border-left:3px solid #b9b9a8;}
  p.setline{font-size:11.5px;color:#333;margin:4px 0 8px 0;padding:7px 12px;background:#eef3ff;border-left:3px solid #6f8fd6;}
  table{border-collapse:collapse;font-size:12.5px;width:auto;min-width:340px;}
  th{background:#f2f2f2;text-align:left;padding:9px 22px;font-weight:600;border-bottom:1px solid #e0e0e0;white-space:nowrap;}
  td{padding:9px 22px;border-bottom:1px solid #ededed;white-space:nowrap;}
  .cap{font-size:10.5px;color:#777;margin-top:4px;}
  pre{font-family:Consolas,'Courier New',monospace;font-size:8.4px;line-height:1.35;background:#f6f6f6;border:1px solid #e3e3e3;border-radius:4px;padding:9px 11px;white-space:pre-wrap;word-break:break-word;}
  .promptbox{page-break-inside:avoid;}
"""

def table(rows):
    body = ""
    for r in rows:
        body += "<tr>" + "".join(f"<td>{c}</td>" for c in r) + "</tr>"
    return ("<table><thead><tr><th>Locator</th><th>Success</th><th>Fragility</th>"
            "<th>Obsolescence</th><th>Not Compiled</th></tr></thead><tbody>" + body + "</tbody></table>")

NOTE_OBS = ('<p class="setline"><strong>Obsolescence</strong> e <strong>Not&nbsp;Compiled</strong> dipendono dalla '
            'mutazione (non dal locatore): sono quindi <strong>identiche per tutte le strategie</strong> nella stessa '
            'colonna.</p>')

# Per-locatore: Success / Fragility / Obsolescence / Not Compiled.
# Static: Obs/NC sono proprieta del set (266) -> uguali per tutti, Katalon incluso.
# Katalon ha Success/Fragility su 198 (escluso da un test case per il prefisso xpath=).
static_rows = [
    ["Relative", "117/266", "23/266", "68/266", "58/266"],
    ["Absolute", "105/266", "35/266", "68/266", "58/266"],
    ["Robula", "128/266", "12/266", "68/266", "58/266"],
    ["Katalon", "95/198 *", "2/198 *", "68/266", "58/266"],
    ["Selenium", "133/266", "7/266", "68/266", "58/266"],
    ["Robula+", "131/266", "9/266", "68/266", "58/266"],
]
param_rows = [
    ["Relative", "173/256", "14/256", "15/256", "54/256"],
    ["Absolute", "172/256", "15/256", "15/256", "54/256"],
    ["Robula", "183/256", "4/256", "15/256", "54/256"],
    ["Katalon", "183/256", "4/256", "15/256", "54/256"],
    ["Selenium", "177/256", "10/256", "15/256", "54/256"],
    ["Robula+", "177/256", "10/256", "15/256", "54/256"],
]
real_rows = [
    ["Relative", "178/200", "4/200", "3/200", "15/200"],
    ["Absolute", "176/200", "6/200", "3/200", "15/200"],
    ["Robula", "182/200", "0/200", "3/200", "15/200"],
    ["Katalon", "181/200", "1/200", "3/200", "15/200"],
    ["Selenium", "175/200", "7/200", "3/200", "15/200"],
    ["Robula+", "170/200", "12/200", "3/200", "15/200"],
]

htmlout = f"""<!doctype html><html><head><meta charset="utf-8"><style>{CSS}</style></head><body>
<h1>Confronto di robustezza delle strategie di localizzazione</h1>
<p class="sub">App: angular-spotify. Valori espressi come <em>mutazioni effettuate / mutazioni totali</em>.</p>
{NOTE_OBS}

<h2>1. Mutazioni generate tramite analisi statica</h2>
<p class="sub">Tecnica della tesi precedente (operatori meccanici). 266 mutazioni.</p>
{table(static_rows)}
<p class="cap">* Katalon ha Success/Fragility su <strong>198</strong>: escluso da un test case (68 mutazioni) per un problema
di formato pre-esistente del suo locatore (prefisso <code>xpath=</code>), non legato alle mutazioni. Obsolescence e
Not-Compiled dipendono dalla mutazione (non dal locatore): restano quelle del set (68/266, 58/266) anche per Katalon.</p>

<h2>2. Mutazioni generate da LLM secondo il modello statico (parametrico)</h2>
<p class="sub">L'LLM applica gli 11 tipi di mutazione del modello statico (a&ndash;k). 256 mutazioni testate.</p>
{table(param_rows)}
<p class="cap">Serie ampliata da 161 a 256 mutazioni per equiparare il numero a quello dello static (266): il confronto e ora bilanciato (256 vs 266). La classifica di fragilita resta invariata (Absolute &gt; Relative &gt; Selenium/Robula+ &gt; Robula/Katalon).</p>
<div class="promptbox">
<h3>Prompt usato (parametrico) &mdash; system</h3>
<pre>{param_prompt}</pre>
<h3>Messaggio utente (esempio, tipo k) &mdash; la definizione del tipo a&ndash;k e il template vengono iniettati</h3>
<pre>{param_user_example}</pre>
</div>

<h2>3. Modifiche reali generate da LLM</h2>
<p class="note"><strong>Nota:</strong> questa tabella e un'aggiunta sperimentale, inserita per osservare quali risultati produce la generazione di <em>modifiche realistiche e libere</em> da parte dell'LLM (non vincolate al modello di mutazioni statico). 200 mutazioni.</p>
{table(real_rows)}
<div class="promptbox">
<h3>Prompt usato (realistico) &mdash; system</h3>
<pre>{real_prompt}</pre>
<h3>Messaggio utente &mdash; viene iniettato il template</h3>
<pre>{real_user_example}</pre>
</div>
</body></html>"""

os.makedirs("output", exist_ok=True)
open("output/confronto_robustezza.html", "w", encoding="utf-8").write(htmlout)
print("HTML scritto: output/confronto_robustezza.html")
