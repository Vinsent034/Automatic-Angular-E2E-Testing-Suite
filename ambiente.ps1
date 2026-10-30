# Imposta, solo per la finestra corrente, gli strumenti portatili scaricati da installa.ps1
# (cartella "strumenti"): Java 25, Maven, Node.js e Python. Viene usato da installa.ps1
# e da terminale.bat; non modifica niente nel sistema.

$strumenti = Join-Path $PSScriptRoot "strumenti"
if (-not (Test-Path $strumenti)) {
    Write-Host "Manca la cartella strumenti: lancia prima installa.bat (oppure installa.ps1)" -ForegroundColor Yellow
    return
}
$env:JAVA_HOME = Join-Path $strumenti "jdk"
$env:MAVEN_HOME = Join-Path $strumenti "maven"
$env:Path = @(
    (Join-Path $env:JAVA_HOME "bin"),
    (Join-Path $env:MAVEN_HOME "bin"),
    (Join-Path $strumenti "node"),
    (Join-Path $strumenti "python"),
    $env:Path) -join ";"

# Variabili usate nei comandi di RIPRODURRE-LE-CAMPAGNE.md
$global:JAVA   = Join-Path $env:JAVA_HOME "bin\java.exe"
$global:LLMJAR = Join-Path $PSScriptRoot "mutation-generator\llm-generator\target\llm-generator-1.0.0-jar-with-dependencies.jar"
