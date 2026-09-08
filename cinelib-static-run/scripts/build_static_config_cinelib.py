# -*- coding: utf-8 -*-
# Costruisce generator-config-cinelib-static.json dai target di role-targets.json (gli STESSI
# 45 target usati dal run LLM), rilevando per ognuno come l'attributo compare nel template:
# forma semplice (x-test-foo) oppure binding Angular ([attr.x-test-foo]).
import json, os, re, collections

SUITE = "C:/Users/vince/OneDrive/Desktop/Tirocinio/progetto/Automatic-Angular-E2E-Testing-Suite"
RT    = SUITE + "/role-targets.json"
OUT   = SUITE + "/generator-config-cinelib-static.json"

rt = json.load(open(RT, encoding="utf-8"))
targets = rt["targets"]

cache = {}
def tpl(path):
    if path not in cache:
        cache[path] = open(path, encoding="utf-8").read()
    return cache[path]

mutations = []
report = collections.defaultdict(list)
seen = set()
for t in targets:
    tid, attr, path = t["id"], t["targetAttr"], t["componentHtml"]
    if not os.path.exists(path):
        report["FILE MANCANTE"].append(f"{tid} -> {path}"); continue
    src = tpl(path)
    bound = f"[attr.{attr}]"
    if re.search(r"(?<![\w.\-])" + re.escape(attr) + r"(?=[\s=>\]])", src.replace(bound, "")):
        key, kind = attr, "semplice"
    elif bound in src:
        key, kind = bound, "binding"
    else:
        report["NON TROVATO NEL TEMPLATE"].append(f"{tid} ({attr}) in {os.path.basename(path)}")
        continue
    if (path, key) in seen:
        report["DUPLICATO (saltato)"].append(f"{tid} ({key})"); continue
    seen.add((path, key))
    report[kind].append(f"{tid} -> {key}")
    mutations.append({
        "name": tid,
        "file_path": path,
        "target_matcher": {"type": "attribute", "key": key, "value": ""},
    })

cfg = {
    "seed": "1234",
    "repositoryRootPath": rt["appRoot"],
    "npmRunCommand": "npm start -- --port 4300",
    "mutations": mutations,
}
json.dump(cfg, open(OUT, "w", encoding="utf-8"), indent=1, ensure_ascii=False)

print(f"scritto {OUT}\ntarget in config: {len(mutations)} (su {len(targets)} in role-targets)\n")
for k in ("semplice", "binding", "DUPLICATO (saltato)", "NON TROVATO NEL TEMPLATE", "FILE MANCANTE"):
    if report[k]:
        print(f"--- {k}: {len(report[k])}")
        for line in report[k]:
            print("   ", line)
