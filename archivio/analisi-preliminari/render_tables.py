# Renderizza tabelle dei risultati come PNG puliti (stile header grigio / righe bianche).
import subprocess, os, shutil

CHROME = r"C:/Program Files/Google/Chrome/Application/chrome.exe"
TMP = os.environ.get("TEMP", "C:/Users/vince/AppData/Local/Temp")
OUTDIR = os.path.abspath("output")

CSS = """
<style>
  html,body{margin:0;background:#ffffff;}
  .wrap{padding:24px;display:inline-block;}
  table{border-collapse:collapse;font-family:'Segoe UI',Helvetica,Arial,sans-serif;font-size:15px;color:#1f1f1f;}
  th{background:#f2f2f2;text-align:left;padding:13px 22px;font-weight:600;border-bottom:1px solid #e0e0e0;white-space:nowrap;}
  td{padding:13px 22px;border-bottom:1px solid #ededed;text-align:left;white-space:nowrap;}
  caption{caption-side:top;text-align:left;font-family:'Segoe UI',Helvetica,Arial,sans-serif;font-size:16px;font-weight:600;padding:0 0 12px 2px;color:#222;}
</style>
"""

def render(name, headers, rows, caption=None, width=1100, height=None):
    if height is None:
        height = 70 + (len(rows) + 1) * 47 + (30 if caption else 0)
    thead = "".join(f"<th>{h}</th>" for h in headers)
    body = ""
    for r in rows:
        body += "<tr>" + "".join(f"<td>{c}</td>" for c in r) + "</tr>"
    cap = f"<caption>{caption}</caption>" if caption else ""
    html = f"<!doctype html><html><head><meta charset='utf-8'>{CSS}</head><body><div class='wrap'><table>{cap}<thead><tr>{thead}</tr></thead><tbody>{body}</tbody></table></div></body></html>"
    htmlpath = os.path.join(TMP, name + ".html")
    open(htmlpath, "w", encoding="utf-8").write(html)
    pngtmp = os.path.join(TMP, name + ".png")
    subprocess.run([CHROME, "--headless", "--disable-gpu", "--default-background-color=FFFFFFFF",
                    f"--screenshot={pngtmp}", f"--window-size={width},{height}",
                    "file:///" + htmlpath.replace("\\", "/")],
                   stdout=subprocess.DEVNULL, stderr=subprocess.DEVNULL)
    dst = os.path.join(OUTDIR, name + ".png")
    shutil.copy(pngtmp, dst)
    print("creato", dst)

# 1) Tabella robustezza (set indipendente, 200 mutanti)
render(
    "tabella_robustezza_llm",
    ["Locator", "Success", "Fragility", "Obsolescence", "Not Compiled"],
    [
        ["Relative", "178/200", "4/200", "3/200", "15/200"],
        ["Absolute", "176/200", "6/200", "3/200", "15/200"],
        ["Robula",   "182/200", "0/200", "3/200", "15/200"],
        ["Katalon",  "181/200", "1/200", "3/200", "15/200"],
        ["Selenium", "175/200", "7/200", "3/200", "15/200"],
        ["Robula+",  "170/200", "12/200", "3/200", "15/200"],
    ],
    width=660,
)

# 2) Tabella validita 1.2 (parametrico, per tipo)
render(
    "tabella_validita_1_2",
    ["Tipo", "Coerenti", "Compila", "Validi (entrambi)"],
    [
        ["a — Attribute Value Modification", "19/20", "10/20", "9/20"],
        ["b — Attribute Removal", "15/20", "19/20", "14/20"],
        ["c — Attribute Identifier Modification", "18/19", "2/19", "1/19"],
        ["d — Text Content Modification", "6/10", "9/10", "6/10"],
        ["e — Text Content Removal", "6/10", "9/10", "6/10"],
        ["f — Tag Movement (within container)", "6/10", "9/10", "6/10"],
        ["g — Tag Movement (any point)", "6/10", "10/10", "6/10"],
        ["h — Tag Movement (between templates)", "5/10", "9/10", "4/10"],
        ["i — Tag Removal", "9/10", "9/10", "8/10"],
        ["j — Tag Type Modification", "12/12", "10/12", "10/12"],
        ["k — Tag Insertion", "9/10", "10/10", "9/10"],
        ["TOTALE", "111/141", "106/141", "79/141"],
    ],
    width=760,
)
