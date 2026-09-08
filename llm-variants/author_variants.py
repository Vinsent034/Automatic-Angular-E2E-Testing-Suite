# Authoring helper (NOT part of the measured experiment code).
# Claude authors realistic, localized developer-style edits as (category, find, replace) tuples.
# This script applies each edit to the REAL template and emits ######### START/END ######### blocks.
# The llm-generator ingest mode then validates (parse / no-op / dedupe / x-test) and stores them.

import os

SUITE = os.path.dirname(os.path.abspath(__file__)) + "/.."
SEARCH = os.path.normpath(SUITE + "/../../angular-spotify/libs/web/search/feature/src/lib/search.component.html")
CARD = os.path.normpath(SUITE + "/../../angular-spotify/libs/web/shared/ui/media/src/lib/card.component.html")

# Fall back to absolute paths if the relative resolution misses (OneDrive layout).
if not os.path.exists(SEARCH):
    SEARCH = r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/search/feature/src/lib/search.component.html"
if not os.path.exists(CARD):
    CARD = r"C:/Users/vince/OneDrive/Desktop/Tirocinio/angular-spotify/libs/web/shared/ui/media/src/lib/card.component.html"

OUT_DIR = os.path.dirname(os.path.abspath(__file__))


def read(path):
    with open(path, encoding="utf-8") as f:
        return f.read()


def emit(target, original, edits):
    blocks = []
    n = 0
    skipped = 0
    for cat, find, repl in edits:
        if find not in original:
            skipped += 1
            print(f"  [{target}] SKIP (find not found): {cat} :: {find[:50]!r}")
            continue
        mutated = original.replace(find, repl, 1)
        if mutated == original:
            skipped += 1
            print(f"  [{target}] SKIP (no-op): {cat}")
            continue
        n += 1
        blocks.append(f"######### START {n} - {cat} #########\n{mutated}\n######### END {n} - {cat} #########")
    out = os.path.join(OUT_DIR, target + ".txt")
    with open(out, "w", encoding="utf-8") as f:
        f.write("\n".join(blocks) + "\n")
    print(f"{target}: wrote {n} blocks ({skipped} skipped) -> {out}")


search = read(SEARCH)
card = read(CARD)

# ---------------------------------------------------------------------------
# CardTitle  (card.component.html) — target <h2 class="card-title ...">
# ---------------------------------------------------------------------------
cardtitle_edits = []

# class-rename (rename a single class to a plausible new name)
for old, new in [
    ('card-title font-bold', 'track-title font-bold'),
    ('card-title font-bold', 'media-title font-bold'),
    ('card-title font-bold', 'song-title font-bold'),
    ('card-title font-bold', 'item-title font-bold'),
    ('card-title font-bold', 'tile-title font-bold'),
    ('font-bold text-white', 'font-semibold text-white'),
    ('font-bold text-white', 'fw-bold text-white'),
    ('text-white ellipsis-one-line', 'text-light ellipsis-one-line'),
    ('text-white ellipsis-one-line', 'text-white truncate'),
    ('text-white ellipsis-one-line', 'text-white ellipsis-1'),
    ('class="card-body"', 'class="card-content"'),
    ('class="card-body"', 'class="card-details"'),
    ('class="card-body"', 'class="card-meta"'),
    ('class="card-cover"', 'class="card-image"'),
    ('class="card-cover"', 'class="cover-wrapper"'),
    ('class="card-description text-description"', 'class="card-subtitle text-description"'),
    ('class="card-description text-description"', 'class="card-caption text-description"'),
    ('class="card-description text-description"', 'class="card-description text-muted"'),
    ('<a class="card"', '<a class="media-card"'),
    ('<a class="card"', '<a class="grid-card"'),
    ('class="play-button-overlay"', 'class="play-overlay"'),
    ('class="play-button-overlay"', 'class="overlay-play-button"'),
]:
    cardtitle_edits.append(('class-rename', old, new))

# tag-swap (semantically plausible element change)
cardtitle_edits += [
    ('tag-swap', '<h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>',
                 '<h3 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h3>'),
    ('tag-swap', '<h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>',
                 '<h1 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h1>'),
    ('tag-swap', '<h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>',
                 '<p class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </p>'),
    ('tag-swap', '<div class="card-body">', '<section class="card-body">'),
    ('tag-swap', '<div class="card-description text-description">\n      {{ description }}\n    </div>',
                 '<p class="card-description text-description">\n      {{ description }}\n    </p>'),
    ('tag-swap', '<div class="card-description text-description">\n      {{ description }}\n    </div>',
                 '<span class="card-description text-description">\n      {{ description }}\n    </span>'),
]

# wrapper-add (introduce a layout container)
cardtitle_edits += [
    ('wrapper-add', '<h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>',
                    '<div class="title-wrapper">\n      <h2 class="card-title font-bold text-white ellipsis-one-line">\n        {{ title }}\n      </h2>\n    </div>'),
    ('wrapper-add', '<div class="card-description text-description">\n      {{ description }}\n    </div>',
                    '<div class="desc-wrapper">\n      <div class="card-description text-description">\n        {{ description }}\n      </div>\n    </div>'),
    ('wrapper-add', '<as-media-cover [imageUrl]="imageUrl"\n                    [roundedImage]="roundedImage">\n    </as-media-cover>',
                    '<div class="cover-frame">\n      <as-media-cover [imageUrl]="imageUrl"\n                      [roundedImage]="roundedImage">\n      </as-media-cover>\n    </div>'),
]

# wrapper-remove (drop a redundant wrapper, promoting children)
cardtitle_edits += [
    ('wrapper-remove', '  <div class="card-body">\n    <h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>\n    <div class="card-description text-description">\n      {{ description }}\n    </div>\n  </div>',
                       '  <h2 class="card-title font-bold text-white ellipsis-one-line">\n    {{ title }}\n  </h2>\n  <div class="card-description text-description">\n    {{ description }}\n  </div>'),
]

# reorder (layout tweak)
cardtitle_edits += [
    ('reorder', '<h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>\n    <div class="card-description text-description">\n      {{ description }}\n    </div>',
                '<div class="card-description text-description">\n      {{ description }}\n    </div>\n    <h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>'),
]

# attr-change (non-functional attribute added/changed)
cardtitle_edits += [
    ('attr-change', '<a class="card"\n   [routerLink]="routerUrl">', '<a class="card"\n   title="Open"\n   [routerLink]="routerUrl">'),
    ('attr-change', '<h2 class="card-title font-bold text-white ellipsis-one-line">', '<h2 class="card-title font-bold text-white ellipsis-one-line" aria-label="title">'),
    ('attr-change', '<div class="card-description text-description">', '<div class="card-description text-description" aria-hidden="false">'),
    ('attr-change', '<a class="card"\n   [routerLink]="routerUrl">', '<a class="card"\n   data-role="card"\n   [routerLink]="routerUrl">'),
]

# container-move (move element during a restructuring)
cardtitle_edits += [
    ('container-move', '  <div class="card-body">\n    <h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>\n    <div class="card-description text-description">\n      {{ description }}\n    </div>\n  </div>',
                       '  <div class="card-body">\n    <div class="card-description text-description">\n      {{ description }}\n    </div>\n  </div>\n  <h2 class="card-title font-bold text-white ellipsis-one-line">\n    {{ title }}\n  </h2>'),
]

# more class-renames (additional plausible names) to broaden the set
for old, new in [
    ('card-title font-bold', 'heading-title font-bold'),
    ('card-title font-bold', 'primary-title font-bold'),
    ('card-title font-bold', 'card-name font-bold'),
    ('card-title font-bold', 'cardTitle font-bold'),
    ('class="card-cover"', 'class="cover-area"'),
    ('class="card-cover"', 'class="thumb"'),
    ('class="card-cover"', 'class="media-thumb"'),
    ('class="card-body"', 'class="body"'),
    ('class="card-body"', 'class="card-info"'),
    ('class="card-body"', 'class="card-text"'),
    ('class="card-description text-description"', 'class="card-desc text-description"'),
    ('class="card-description text-description"', 'class="description text-description"'),
    ('<a class="card"', '<a class="card card--media"'),
    ('<a class="card"', '<a class="card-link"'),
    ('class="play-button-overlay"', 'class="play-btn-overlay"'),
    ('class="play-button-overlay"', 'class="overlay-play"'),
    ('font-bold text-white', 'font-bold text-gray-50'),
    ('text-white ellipsis-one-line', 'text-white line-clamp-1'),
]:
    cardtitle_edits.append(('class-rename', old, new))

cardtitle_edits += [
    ('attr-change', '<div class="card-cover"', '<div class="card-cover" role="img"'),
    ('attr-change', '<h2 class="card-title font-bold text-white ellipsis-one-line">', '<h2 class="card-title font-bold text-white ellipsis-one-line" title="title">'),
    ('attr-change', '<div class="card-body">', '<div class="card-body" data-section="body">'),
    ('tag-swap', '<div class="card-cover"', '<figure class="card-cover"'),
    ('tag-swap', '<h2 class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </h2>',
                 '<span class="card-title font-bold text-white ellipsis-one-line">\n      {{ title }}\n    </span>'),
]

emit("CardTitle", card, cardtitle_edits)


# ---------------------------------------------------------------------------
# InputField  (search.component.html) — target <as-input class="search-control" ...>
# ---------------------------------------------------------------------------
inputfield_edits = []

for old, new in [
    ('class="search-control"', 'class="search-field"'),
    ('class="search-control"', 'class="search-box"'),
    ('class="search-control"', 'class="search-input"'),
    ('class="search-control"', 'class="query-control"'),
    ('class="search-control"', 'class="search-control form-control"'),
    ('class="search-input-container"', 'class="search-wrapper"'),
    ('class="search-input-container"', 'class="search-bar"'),
    ('class="search-input-container"', 'class="search-field-container"'),
    ('<div class="mb-6 content-spacing">', '<div class="mb-8 content-spacing">'),
    ('<div class="mb-6 content-spacing">', '<div class="mb-6 page-spacing">'),
    ('<div class="mb-6 content-spacing">', '<div class="search-page content-spacing">'),
]:
    inputfield_edits.append(('class-rename', old, new))

# attr-change on the input (non-functional values)
inputfield_edits += [
    ('attr-change', 'placeholder="Artists, songs, albums, or playlists"', 'placeholder="Search music"'),
    ('attr-change', 'placeholder="Artists, songs, albums, or playlists"', 'placeholder="Search Spotify"'),
    ('attr-change', 'placeholder="Artists, songs, albums, or playlists"', 'placeholder="What do you want to listen to?"'),
    ('attr-change', 'icon="search"', 'icon="magnifier"'),
    ('attr-change', "[iconSize]=\"'lg'\"", "[iconSize]=\"'md'\""),
    ('attr-change', '<as-input class="search-control"', '<as-input class="search-control" title="Search"'),
    ('attr-change', '<as-input class="search-control"', '<as-input class="search-control" data-role="search"'),
    ('attr-change', '<as-input class="search-control"', '<as-input class="search-control" aria-label="Search"'),
]

# tag-swap on surrounding containers
inputfield_edits += [
    ('tag-swap', '<div class="mb-6 content-spacing">', '<section class="mb-6 content-spacing">'),
    ('tag-swap', '<div class="search-input-container">', '<section class="search-input-container">'),
    ('tag-swap', '<div class="search-input-container">', '<form class="search-input-container">'),
]

# wrapper-add around the input
inputfield_edits += [
    ('wrapper-add', '<as-input class="search-control" [control]="searchControl" icon="search" [iconSize]="\'lg\'" placeholder="Artists, songs, albums, or playlists" [autoFocus]="true" [rounded]="true" [enableClearButton]="true">\n    </as-input>',
                    '<div class="input-group">\n      <as-input class="search-control" [control]="searchControl" icon="search" [iconSize]="\'lg\'" placeholder="Artists, songs, albums, or playlists" [autoFocus]="true" [rounded]="true" [enableClearButton]="true">\n      </as-input>\n    </div>'),
]

# wrapper-remove around the input
inputfield_edits += [
    ('wrapper-remove', '  <div class="search-input-container">\n    <as-input class="search-control" [control]="searchControl" icon="search" [iconSize]="\'lg\'" placeholder="Artists, songs, albums, or playlists" [autoFocus]="true" [rounded]="true" [enableClearButton]="true">\n    </as-input>',
                       '  <as-input class="search-control" [control]="searchControl" icon="search" [iconSize]="\'lg\'" placeholder="Artists, songs, albums, or playlists" [autoFocus]="true" [rounded]="true" [enableClearButton]="true">\n    </as-input>'),
]

for old, new in [
    ('class="search-control"', 'class="query-input"'),
    ('class="search-control"', 'class="search-control-lg"'),
    ('class="search-control"', 'class="lookup-control"'),
    ('class="search-control"', 'class="searchControl"'),
    ('class="search-control"', 'class="search-control rounded"'),
    ('class="search-input-container"', 'class="search-field-wrapper"'),
    ('class="search-input-container"', 'class="searchInputContainer"'),
    ('class="search-input-container"', 'class="input-container"'),
    ('<div class="mb-6 content-spacing">', '<div class="mb-4 content-spacing">'),
    ('<div class="mb-6 content-spacing">', '<div class="mb-6 content-area">'),
    ('<div class="mb-6 content-spacing">', '<div class="mb-6 content-spacing search-view">'),
]:
    inputfield_edits.append(('class-rename', old, new))

inputfield_edits += [
    ('attr-change', 'placeholder="Artists, songs, albums, or playlists"', 'placeholder="Search artists and songs"'),
    ('attr-change', 'placeholder="Artists, songs, albums, or playlists"', 'placeholder="Find music"'),
    ('attr-change', 'placeholder="Artists, songs, albums, or playlists"', 'placeholder="Type to search"'),
    ('attr-change', 'icon="search"', 'icon="search-icon"'),
    ('attr-change', 'icon="search"', 'icon="lens"'),
    ('attr-change', '<as-input class="search-control"', '<as-input class="search-control" role="searchbox"'),
    ('attr-change', '<as-input class="search-control"', '<as-input class="search-control" data-testid="search"'),
    ('attr-change', '<as-input class="search-control"', '<as-input class="search-control" id="searchField"'),
    ('tag-swap', '<div class="mb-6 content-spacing">', '<main class="mb-6 content-spacing">'),
    ('tag-swap', '<div class="mb-6 content-spacing">', '<header class="mb-6 content-spacing">'),
]

emit("InputField", search, inputfield_edits)


# ---------------------------------------------------------------------------
# ArtistCard  (search.component.html) — target the Artists-section <as-card ... [roundedImage]="true">
# ---------------------------------------------------------------------------
ARTIST_GRID = 'Artists</h2>\n        <div class="common-grid">'
ARTIST_CARD = '<as-card [title]="item.name" [uri]="item.uri" [imageUrl]="item.images?.[0]?.url" [routerUrl]="\'/artist/\' + item.id" [description]="item.type | titlecase" [roundedImage]="true" (togglePlay)="togglePlay($event, item.uri)">'

artistcard_edits = []

# class-rename on the artists grid container (contextual: only the Artists one)
artistcard_edits += [
    ('class-rename', ARTIST_GRID, 'Artists</h2>\n        <div class="artist-grid">'),
    ('class-rename', ARTIST_GRID, 'Artists</h2>\n        <div class="common-grid artists">'),
    ('class-rename', ARTIST_GRID, 'Artists</h2>\n        <div class="cards-grid">'),
]

# tag-swap on the artists grid / heading
artistcard_edits += [
    ('tag-swap', ARTIST_GRID, 'Artists</h2>\n        <section class="common-grid">'),
    ('tag-swap', '<h2 class="mt-8 mb-4 text-heading">Artists</h2>', '<h3 class="mt-8 mb-4 text-heading">Artists</h3>'),
]

# attr-change on the as-card (add non-functional attrs / reorder bindings)
artistcard_edits += [
    ('attr-change', ARTIST_CARD, ARTIST_CARD[:-1] + ' title="Artist">'),
    ('attr-change', ARTIST_CARD, ARTIST_CARD[:-1] + ' data-kind="artist">'),
    ('attr-change', ARTIST_CARD, ARTIST_CARD[:-1] + ' aria-label="Artist card">'),
    # binding reorder (realistic, no behavioural change)
    ('reorder', ARTIST_CARD,
     '<as-card [uri]="item.uri" [title]="item.name" [imageUrl]="item.images?.[0]?.url" [routerUrl]="\'/artist/\' + item.id" [description]="item.type | titlecase" [roundedImage]="true" (togglePlay)="togglePlay($event, item.uri)">'),
    ('reorder', ARTIST_CARD,
     '<as-card [title]="item.name" [uri]="item.uri" [routerUrl]="\'/artist/\' + item.id" [imageUrl]="item.images?.[0]?.url" [description]="item.type | titlecase" [roundedImage]="true" (togglePlay)="togglePlay($event, item.uri)">'),
]

# wrapper-add around the as-card inside the @for
artistcard_edits += [
    ('wrapper-add', ARTIST_CARD + '\n            </as-card>',
                    '<div class="card-cell">\n              ' + ARTIST_CARD + '\n              </as-card>\n            </div>'),
]

emit("ArtistCard", search, artistcard_edits)
