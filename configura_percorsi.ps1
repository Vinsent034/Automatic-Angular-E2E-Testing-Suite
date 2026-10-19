# Aggiorna i percorsi delle applicazioni nei file di configurazione dello strumento,
# in modo che puntino alle applicazioni contenute in questo repository
# (cartella applicazioni-soggetto), ovunque il repository sia stato copiato.
#
# Uso, dalla radice del repository:
#   powershell -ExecutionPolicy Bypass -File .\configura_percorsi.ps1
#
# Si puo' rilanciare quante volte si vuole: ogni volta riscrive i percorsi
# in base alla posizione attuale del repository.

$ErrorActionPreference = "Stop"
$root = (Resolve-Path $PSScriptRoot).Path -replace '\\', '/'
$apps = "$root/applicazioni-soggetto"

# Nome della cartella negli esperimenti -> nome della cartella nel repository
$mappa = @{ "cinelib" = "cinelib"; "cookbook" = "cookbook"; "Kanban board" = "flowboard"; "flowboard" = "flowboard" }

# Un percorso assoluto che termina con la cartella di una delle tre applicazioni
$regex = '[A-Za-z]:/[^"]*?/(cinelib|cookbook|Kanban board|flowboard)(?=/|")'

$utf8 = New-Object System.Text.UTF8Encoding($false)   # UTF-8 senza BOM
$file = Get-ChildItem -Path $PSScriptRoot -File |
    Where-Object { $_.Name -like "generator-config-*.json" -or $_.Name -like "role-targets*.json" }

$modificati = 0
foreach ($f in $file) {
    $testo = [System.IO.File]::ReadAllText($f.FullName, $utf8)
    $nuovo = [regex]::Replace($testo, $regex, { param($m) "$apps/" + $mappa[$m.Groups[1].Value] })
    if ($nuovo -ne $testo) {
        [System.IO.File]::WriteAllText($f.FullName, $nuovo, $utf8)
        $modificati++
        Write-Host "aggiornato  $($f.Name)"
    } else {
        Write-Host "invariato   $($f.Name)"
    }
}

foreach ($a in "cinelib", "flowboard", "cookbook") {
    if (-not (Test-Path "$apps/$a/src/app")) { throw "Manca l'applicazione $apps/$a" }
}
Write-Host ""
Write-Host "Fatto: $modificati file aggiornati. Le applicazioni ora sono cercate in $apps"
