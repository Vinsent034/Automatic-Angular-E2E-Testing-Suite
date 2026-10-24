# Prepara questo progetto su un PC Windows nuovo.
#
# Scarica nella cartella "strumenti" (dentro il progetto) le versioni PORTATILI di:
#   - Java 25 (Eclipse Temurin 25.0.2, la versione usata negli esperimenti)
#   - Maven 3.9.9
#   - Node.js 22 (per le applicazioni Angular)
#   - Python 3.13 (per il programma di controllo e le misure)
# Non installa niente nel sistema e non chiede permessi di amministratore:
# per togliere tutto basta cancellare la cartella "strumenti".
#
# Poi: aggiorna i percorsi nei file di configurazione, compila lo strumento
# e installa le dipendenze delle tre applicazioni.
#
# Uso: doppio clic su installa.bat (oppure: powershell -ExecutionPolicy Bypass -File installa.ps1)

# I programmi esterni (java, mvn, npm) scrivono messaggi normali sull'uscita degli errori:
# con "Stop" PowerShell 5.1 li tratterebbe come errori. Gli esiti si controllano uno per uno.
$ErrorActionPreference = "Continue"
$ProgressPreference = "SilentlyContinue"
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$Radice    = (Resolve-Path $PSScriptRoot).Path
$Strumenti = Join-Path $Radice "strumenti"
$Download  = Join-Path $Strumenti "_download"

$Pacchetti = @(
    @{ Nome = "Java 25";   Cartella = "jdk";    File = "jdk25.zip";
       Url  = "https://api.adoptium.net/v3/binary/version/jdk-25.0.2%2B10/windows/x64/jdk/hotspot/normal/eclipse?project=jdk" },
    @{ Nome = "Maven";     Cartella = "maven";  File = "maven.zip";
       Url  = "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip" },
    @{ Nome = "Node.js";   Cartella = "node";   File = "node.zip";
       Url  = "https://nodejs.org/dist/v22.23.2/node-v22.23.2-win-x64.zip" },
    @{ Nome = "Python";    Cartella = "python"; File = "python.zip";
       Url  = "https://www.python.org/ftp/python/3.13.7/python-3.13.7-embed-amd64.zip" }
)

function Titolo($testo) { Write-Host ""; Write-Host "=== $testo ===" -ForegroundColor Cyan }
function Ok($testo)     { Write-Host "  OK  $testo" -ForegroundColor Green }
function Avviso($testo) { Write-Host "  !!  $testo" -ForegroundColor Yellow }

function Scarica($url, $dest) {
    if (Get-Command curl.exe -ErrorAction SilentlyContinue) {
        & curl.exe -L --fail --retry 3 --progress-bar -o $dest $url
        if ($LASTEXITCODE -ne 0) { throw "Download non riuscito: $url" }
    } else {
        Invoke-WebRequest -Uri $url -OutFile $dest -UseBasicParsing -ErrorAction Stop
    }
}

function Estrai($zip, $dest) {
    New-Item -ItemType Directory -Force $dest | Out-Null
    if (Get-Command tar.exe -ErrorAction SilentlyContinue) {
        & tar.exe -xf $zip -C $dest
        if ($LASTEXITCODE -ne 0) { throw "Estrazione non riuscita: $zip" }
    } else {
        Expand-Archive -Path $zip -DestinationPath $dest -Force -ErrorAction Stop
    }
}

# --------------------------------------------------------------------------
Titolo "1/5  Scarico gli strumenti portatili in $Strumenti"
New-Item -ItemType Directory -Force $Strumenti, $Download -ErrorAction Stop | Out-Null

foreach ($p in $Pacchetti) {
    $cartella = Join-Path $Strumenti $p.Cartella
    if (Test-Path $cartella) { Ok "$($p.Nome) gia' presente"; continue }
    Write-Host "  ... scarico $($p.Nome)"
    $zip = Join-Path $Download $p.File
    Scarica $p.Url $zip
    $tmp = Join-Path $Download ("estratto-" + $p.Cartella)
    if (Test-Path $tmp) { Remove-Item $tmp -Recurse -Force }
    Estrai $zip $tmp
    # Java, Maven e Node stanno in una sottocartella; Python no.
    $dentro = @(Get-ChildItem $tmp)
    if ($dentro.Count -eq 1 -and $dentro[0].PSIsContainer) { Move-Item $dentro[0].FullName $cartella -ErrorAction Stop }
    else { Move-Item $tmp $cartella -ErrorAction Stop }
    Ok "$($p.Nome) pronto"
}

# Python portatile: il file ._pth limita i percorsi di ricerca dei moduli; lo si toglie
# perche' gli script del progetto si comportino come con un Python installato.
Get-ChildItem (Join-Path $Strumenti "python") -Filter "python*._pth" | Remove-Item -Force

Remove-Item $Download -Recurse -Force

# Ambiente di questa finestra: gli strumenti portatili vengono prima di quelli del sistema.
. (Join-Path $Radice "ambiente.ps1")

Write-Host ""
& java -version 2>&1 | Select-Object -First 1 | ForEach-Object { Ok "java:   $_" }
& mvn -v 2>&1 | Select-Object -First 1 | ForEach-Object { Ok "maven:  $_" }
Ok "node:   $(& node -v)"
Ok "python: $(& python --version 2>&1)"

# --------------------------------------------------------------------------
Titolo "2/5  Aggiorno i percorsi nei file di configurazione"
& (Join-Path $Radice "configura_percorsi.ps1")
if (-not $?) { throw "Aggiornamento dei percorsi non riuscito." }

# --------------------------------------------------------------------------
Titolo "3/5  Compilo lo strumento (la prima volta Maven scarica le sue librerie)"
Push-Location $Radice
& mvn -q package -DskipTests
$esito = $LASTEXITCODE
Pop-Location
if ($esito -ne 0) { throw "Compilazione non riuscita (vedi i messaggi sopra)." }
$jar = @(
    "mutation-generator\llm-generator\target\llm-generator-1.0.0-jar-with-dependencies.jar",
    "mutation-generator\static-generator\target\static-generator-1.0.0-jar-with-dependencies.jar",
    "mutation-tester\target\mutation-tester-1.0.0-jar-with-dependencies.jar")
foreach ($j in $jar) {
    if (-not (Test-Path (Join-Path $Radice $j))) { throw "Manca $j dopo la compilazione." }
    Ok (Split-Path $j -Leaf)
}

# --------------------------------------------------------------------------
Titolo "4/5  Installo le dipendenze delle tre applicazioni Angular"
foreach ($app in "cinelib", "flowboard", "cookbook") {
    $dir = Join-Path $Radice "applicazioni-soggetto\$app"
    if (Test-Path (Join-Path $dir "node_modules")) { Ok "$app gia' pronta"; continue }
    Write-Host "  ... npm ci in $app (qualche minuto)"
    Push-Location $dir
    & npm ci --no-audit --no-fund --loglevel=error
    $esito = $LASTEXITCODE
    Pop-Location
    if ($esito -ne 0) { throw "npm ci non riuscito in $app" }
    Ok "$app pronta"
}

# --------------------------------------------------------------------------
Titolo "5/5  Controlli finali"
$chrome = @("$env:ProgramFiles\Google\Chrome\Application\chrome.exe",
            "${env:ProgramFiles(x86)}\Google\Chrome\Application\chrome.exe",
            "$env:LOCALAPPDATA\Google\Chrome\Application\chrome.exe") | Where-Object { Test-Path $_ }
if ($chrome) { Ok "Google Chrome trovato (serve solo per eseguire i test sui mutanti)" }
else { Avviso "Google Chrome non trovato: serve solo per eseguire i test sui mutanti. Si scarica da https://www.google.com/chrome/" }

Write-Host ""
Write-Host "Tutto pronto." -ForegroundColor Green
Write-Host "Per lavorare con gli strumenti portatili apri sempre il terminale con terminale.bat"
Write-Host "(imposta Java 25, Maven, Node e Python di questa cartella). Poi segui GUIDA-RIPRODUZIONE.md."
