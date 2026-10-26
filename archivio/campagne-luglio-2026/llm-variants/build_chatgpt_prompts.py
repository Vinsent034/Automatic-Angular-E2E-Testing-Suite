# Assembles ready-to-paste prompts for a chat LLM (e.g. ChatGPT).
# Output = the independent system instructions (realistic-prompt.txt) + the user request + the
# REAL template, in one message per template file. Paste a PROMPT-*.txt into the chat as-is.
# The chat's reply (the START/END blocks) goes into the matching <fileSlug>.txt for ingest.

import os

HERE = os.path.dirname(os.path.abspath(__file__))
SUITE = os.path.normpath(HERE + "/..")
PROMPT_FILE = os.path.join(SUITE, "mutation-generator/llm-generator/src/main/resources/realistic-prompt.txt")

TEMPLATES = {
    "search_component_html": r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/search/feature/src/lib/search.component.html",
    "card_component_html":   r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/shared/ui/media/src/lib/card.component.html",
}

N = 20  # variants to ask per message (ask again in the same chat for more; ingest dedupes repeats)


def read(p):
    with open(p, encoding="utf-8") as f:
        return f.read()


system = read(PROMPT_FILE)

for slug, path in TEMPLATES.items():
    template = read(path)
    user = (
        "Here is the complete source of one Angular template.\n"
        f"Produce exactly {N} variants of it, numbered 1..{N}, each in its own delimited block "
        "exactly as specified above. Decide entirely on your own what to change in each variant. "
        "Output ONLY the blocks, nothing else.\n\n"
        "----- ORIGINAL TEMPLATE -----\n"
        + template + "\n"
        "----- END ORIGINAL TEMPLATE -----\n"
    )
    message = system + "\n\n========================================\n\n" + user
    out = os.path.join(HERE, f"PROMPT-{slug}.txt")
    with open(out, "w", encoding="utf-8") as f:
        f.write(message)
    print(f"wrote {out}  (paste into chat; save reply to {slug}.txt)")
