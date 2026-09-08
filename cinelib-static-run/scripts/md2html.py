# -*- coding: utf-8 -*-
import markdown, sys
md_path, html_path = sys.argv[1], sys.argv[2]
body = markdown.markdown(open(md_path, encoding='utf-8').read(), extensions=['tables','fenced_code'])
CSS = """
@page { size: A4; margin: 1.3cm; }
body { font-family:'Segoe UI',Arial,sans-serif; font-size:11px; color:#1a1a1a; line-height:1.45; }
h1 { font-size:20px; border-bottom:2px solid #333; padding-bottom:4px; }
h2 { font-size:15px; margin-top:20px; color:#0b3d91; border-bottom:1px solid #ccd; padding-bottom:2px; }
h3 { font-size:12.5px; margin-top:14px; color:#333; }
table { border-collapse:collapse; margin:6px 0 12px; font-size:9.5px; }
th,td { border:1px solid #bbb; padding:3px 6px; text-align:center; white-space:nowrap; }
th { background:#e8eef7; font-weight:600; }
td:first-child, th:first-child { text-align:left; }
tr:nth-child(even) td { background:#fafbfd; }
blockquote { background:#f3f7ec; border-left:4px solid #6aa84f; padding:6px 12px; margin:8px 0; font-size:11px; }
code { background:#eef; padding:1px 3px; border-radius:3px; font-family:Consolas,monospace; }
strong { color:#000; }
hr { border:none; border-top:1px solid #ccc; margin:14px 0; }
"""
html = f'<!doctype html><html><head><meta charset="utf-8"><style>{CSS}</style></head><body>{body}</body></html>'
open(html_path, 'w', encoding='utf-8').write(html)
print("html scritto:", html_path)
