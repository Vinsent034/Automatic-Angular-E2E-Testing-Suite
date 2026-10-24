# Generazione dei mutanti linguistici, una fase alla volta. Si lancia dal terminale aperto con
# terminale.bat, dalla cartella principale del progetto:
#
#   .\mutanti.ps1 <applicazione> prompt      1. prepara i prompt per il modello (nessun mutante ancora)
#   (passo 2: il modello scrive le risposte, vedi GUIDA-RIPRODUZIONE.md)
#   .\mutanti.ps1 <applicazione> completa    3. operatori f/g/h e acquisizione delle risposte
#   .\mutanti.ps1 <applicazione> esporta     4. (facoltativo) copia leggibile dei mutanti, per guardarli
#   .\mutanti.ps1 cookbook escludi-i         5. (solo CookBook, facoltativo) toglie i 10 mutanti i sul bersaglio, come nella tesi
#
# Applicazioni: cinelib, flowboard, cookbook. Ogni fase parte da impostazioni pulite, quindi
# non dipende da cio' che e' rimasto nella finestra dalle volte prima.
# Per rifare una campagna da zero basta cancellare la cartella generazione-<applicazione>.

param(
    [Parameter(Mandatory = $true)][string]$App,
    [Parameter(Mandatory = $true)][ValidateSet("prompt", "completa", "esporta", "escludi-i")][string]$Fase
)

$Radice = $PSScriptRoot
. (Join-Path $Radice "ambiente.ps1")

# applicazione -> file dei bersagli, configurazione, campagna della tesi (da cui si copia il file di istruzioni),
# e numeri attesi (prompt, blocchi, mutanti con TUTTE le risposte del modello)
$app = $App.ToLower()
$attesi = $null
switch ($app) {
    "cinelib"   { $bersagli = "role-targets.json";           $config = "generator-config-cinelib.json";   $tesi = "cinelib-v2-run";   $attesi = @(171, 1057, 944) }
    "flowboard" { $bersagli = "role-targets-flowboard.json"; $config = "generator-config-flowboard.json"; $tesi = "flowboard-v2-run"; $attesi = @(590, 3425, 1559) }
    "cookbook"  { $bersagli = "role-targets-cookbook.json";  $config = "generator-config-cookbook.json";  $tesi = "cookbook-run";     $attesi = @(215, 1426, 1939) }
    default     { $bersagli = "role-targets-$app.json";      $config = "generator-config-$app.json";      $tesi = $null }
}
foreach ($f in @($bersagli, $config)) {
    if (-not (Test-Path (Join-Path $Radice $f))) { Write-Host "Manca il file $f nella cartella principale." -ForegroundColor Red; return }
}
if (-not (Test-Path $global:LLMJAR)) { Write-Host "Manca lo strumento compilato: lancia prima installa.bat" -ForegroundColor Red; return }

$campagna = Join-Path $Radice "generazione-$app"
$db = Join-Path $campagna "mutations.db"
$variabili = "LLM_ROLE", "LLM_ROLE_TARGETS", "LLM_MUTATION_TYPES", "LLM_ROLE_DUMP", "LLM_H_AUTO", "LLM_FG_AUTO", "LLM_ROLE_INGEST_DIR", "LLM_MODEL"

function Esegui($nome, $impostazioni) {
    foreach ($v in $variabili) { Remove-Item "Env:$v" -ErrorAction SilentlyContinue }
    $env:LLM_ROLE = "1"
    $env:LLM_ROLE_TARGETS = Join-Path $Radice $bersagli
    $env:LLM_MUTATION_TYPES = Join-Path $Radice "mutation-types-modello.json"
    foreach ($k in $impostazioni.Keys) { Set-Item "Env:$k" $impostazioni[$k] }
    Write-Host "  $nome ..." -ForegroundColor Cyan
    & $global:JAVA -jar $global:LLMJAR (Join-Path $Radice $config) 2>&1 |
        Where-Object { "$_" -match "^DONE|^ERROR|Exception in|Caused by" } | ForEach-Object { Write-Host "    $_" }
}

function ContaMutanti {
    if (-not (Test-Path $db)) { return 0 }
    return [int](& python (Join-Path $Radice "conta_mutanti.py") $db)
}

try {
    if ($Fase -eq "prompt") {
        New-Item -ItemType Directory -Force $campagna | Out-Null
        Push-Location $campagna
        Esegui "Preparo i prompt" @{ LLM_ROLE_DUMP = "1" }
        Pop-Location
        $istruzioni = if ($tesi) { Join-Path $Radice "$tesi\ISTRUZIONI-OPUS-4.8.md" } else { $null }
        if ($istruzioni -and (Test-Path $istruzioni)) { Copy-Item $istruzioni $campagna -Force; Write-Host "  Copiato ISTRUZIONI-OPUS-4.8.md in $campagna" }
        $n = (Get-ChildItem (Join-Path $campagna "output\mutations\role-prompts") -File -ErrorAction SilentlyContinue).Count
        Write-Host ""
        Write-Host "Fatto: $n prompt in $campagna\output\mutations\role-prompts" -ForegroundColor Green
        if ($attesi) { Write-Host "  Attesi per questa applicazione: $($attesi[0]) prompt e $($attesi[1]) blocchi (riga 'Applicable')." }
        Write-Host "Prossimo passo: far scrivere le risposte al modello (GUIDA-RIPRODUZIONE.md, passo 4)."
    }
    elseif ($Fase -eq "completa") {
        $ingest = Join-Path $campagna "output\mutations\role-ingest"
        $nr = (Get-ChildItem $ingest -File -Filter "*.txt" -ErrorAction SilentlyContinue | Where-Object { $_.Name -ne "_registro.txt" }).Count
        if (-not $nr) { Write-Host "Non ci sono risposte del modello in $ingest. Fai prima il passo del modello." -ForegroundColor Red; return }
        $gia = ContaMutanti
        if ($gia -gt 0) {
            Write-Host "Nel database ci sono gia' $gia mutanti: rilanciare 'completa' ne aggiungerebbe di doppi." -ForegroundColor Red
            Write-Host "Per rifare da zero cancella la cartella $campagna e ricomincia da 'prompt'."
            return
        }
        Write-Host "Trovate $nr risposte del modello."
        Push-Location $campagna
        Esegui "Operatori h (spostamenti fra file)" @{ LLM_H_AUTO = "1" }
        Esegui "Operatori f e g (spostamenti nello stesso file)" @{ LLM_FG_AUTO = "1" }
        Esegui "Acquisizione delle risposte del modello" @{ LLM_ROLE_INGEST_DIR = "output/mutations/role-ingest"; LLM_MODEL = "claude-opus-4-8" }
        Pop-Location
        $tot = ContaMutanti
        Write-Host ""
        Write-Host "Fatto. Mutanti nel database: $tot  ($campagna\mutations.db)" -ForegroundColor Green
        if ($attesi) {
            if ($nr -ge $attesi[0]) { Write-Host "  Con tutte le risposte del modello se ne attendono $($attesi[2]) (tesi, Tabella 4.1)." }
            else { Write-Host "  Risposte del modello: $nr su $($attesi[0]) prompt: e' una prova parziale, i mutanti sono meno dei $($attesi[2]) completi." }
        }
        if ($app -eq "cookbook") { Write-Host "  Per ottenere i 1929 della tesi: .\mutanti.ps1 cookbook escludi-i" }
    }
    elseif ($Fase -eq "escludi-i") {
        if ($app -ne "cookbook") { Write-Host "Questa fase serve solo per CookBook." -ForegroundColor Red; return }
        if (-not (Test-Path $db)) { Write-Host "Non c'e' ancora un database: fai prima 'completa'." -ForegroundColor Red; return }
        Push-Location $campagna
        & python (Join-Path $Radice "escludi_i_su_bersaglio.py")
        Pop-Location
        Write-Host "Mutanti nel database: $(ContaMutanti)"
    }
    else {
        if (-not (Test-Path $db)) { Write-Host "Non c'e' ancora un database in ${campagna}: fai prima 'prompt' e 'completa'." -ForegroundColor Red; return }
        Push-Location $campagna
        $out = Join-Path $campagna "esporta"
        & $global:JAVA -cp $global:LLMJAR org.unina.data.MutationPorter export $out 2>&1 | Where-Object { "$_" -match "^Esportate|^ERROR|Exception in|Caused by" } | ForEach-Object { Write-Host "  $_" }
        Pop-Location
    }
}
finally {
    foreach ($v in $variabili) { Remove-Item "Env:$v" -ErrorAction SilentlyContinue }
    if ((Get-Location).Path -like "*generazione-*") { Set-Location $Radice }
}
