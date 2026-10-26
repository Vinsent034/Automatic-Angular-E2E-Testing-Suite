# Generazione dei mutanti linguistici, una fase alla volta. Si lancia dal terminale aperto con
# terminale.bat, dalla cartella principale del progetto:
#
#   .\mutanti.ps1 <applicazione> prompt      1. prepara i prompt per il modello (nessun mutante ancora)
#   (il modello scrive le risposte ai prompt: vedi il passo 6 di GUIDA-RIPRODUZIONE.md)
#   .\mutanti.ps1 <applicazione> completa    3. operatori f/g/h e acquisizione delle risposte
#   .\mutanti.ps1 <applicazione> esporta     4. (facoltativo) copia leggibile dei mutanti, per guardarli
#   .\mutanti.ps1 cookbook escludi-i         5. (solo CookBook, facoltativo) toglie i 10 mutanti i sul bersaglio, come nella tesi
#   .\mutanti.ps1 <applicazione> esegui 20   6. fa girare i test su 20 mutanti (campione) e mostra i risultati
#
# Per il generatore STATICO (nessun modello, nessuna risposta da scrivere):
#   .\mutanti.ps1 <applicazione> statico            7. genera i mutanti statici (CineLib e FlowBoard: toglie i duplicati)
#   .\mutanti.ps1 <applicazione> esegui 20 statico  8. fa girare i test su 20 mutanti statici
#   .\mutanti.ps1 <applicazione> confronta          9. mette a confronto le due tecniche (serve almeno una delle due generate)
#
# Applicazioni: cinelib, flowboard, cookbook. Ogni fase parte da impostazioni pulite, quindi
# non dipende da cio' che e' rimasto nella finestra dalle volte prima.
# Per rifare una campagna da zero basta cancellare la cartella generazione-<applicazione> (o generazione-<applicazione>-statico).

param(
    [Parameter(Mandatory = $true, Position = 0)][string]$App,
    [Parameter(Mandatory = $true, Position = 1)][ValidateSet("prompt", "completa", "esporta", "escludi-i", "esegui", "statico", "confronta")][string]$Fase,
    [Parameter(Position = 2)][int]$Quanti = 20,
    [Parameter(Position = 3)][ValidateSet("llm", "statico")][string]$Tecnica = "llm"
)

$Radice = $PSScriptRoot
. (Join-Path $Radice "ambiente.ps1")

# applicazione -> file dei bersagli, configurazione, campagna della tesi (da cui si copia il file di istruzioni),
# e numeri attesi (prompt, blocchi, mutanti con TUTTE le risposte del modello)
$app = $App.ToLower()
$attesi = $null
$attesiStatici = $null   # generati, distinti (tesi, Tabella 4.1)
switch ($app) {
    "cinelib"   { $bersagli = "role-targets.json";           $config = "generator-config-cinelib.json";   $tesi = "cinelib-v2-run";   $attesi = @(171, 1057, 944);   $attesiStatici = @(1254, 944)  }
    "flowboard" { $bersagli = "role-targets-flowboard.json"; $config = "generator-config-flowboard.json"; $tesi = "flowboard-v2-run"; $attesi = @(590, 3425, 1559);  $attesiStatici = @(4263, 2148) }
    "cookbook"  { $bersagli = "role-targets-cookbook.json";  $config = "generator-config-cookbook.json";  $tesi = "cookbook-run";     $attesi = @(215, 1426, 1939);  $attesiStatici = @(1503, 1454) }
    default     { $bersagli = "role-targets-$app.json";      $config = "generator-config-$app.json";      $tesi = $null }
}
foreach ($f in @($bersagli, $config)) {
    if (-not (Test-Path (Join-Path $Radice $f))) { Write-Host "Manca il file $f nella cartella principale." -ForegroundColor Red; return }
}
if (-not (Test-Path $global:LLMJAR)) { Write-Host "Manca lo strumento compilato: lancia prima installa.bat" -ForegroundColor Red; return }

$STATJAR = Join-Path $Radice "mutation-generator\static-generator\target\static-generator-1.0.0-jar-with-dependencies.jar"
$statico = ($Fase -eq "statico") -or ($Fase -eq "esegui" -and $Tecnica -eq "statico")
$campagna = Join-Path $Radice $(if ($statico) { "generazione-$app-statico" } else { "generazione-$app" })
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
        Write-Host "Prossimo passo: far scrivere le risposte al modello (GUIDA-RIPRODUZIONE.md, passo 6)."
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
    elseif ($Fase -eq "statico") {
        $cfgStatico = "generator-config-$app-static.json"
        if (-not (Test-Path (Join-Path $Radice $cfgStatico))) { Write-Host "Per $app non c'e' il file $cfgStatico." -ForegroundColor Red; return }
        if (-not (Test-Path $STATJAR)) { Write-Host "Manca il generatore statico compilato: lancia prima installa.bat" -ForegroundColor Red; return }
        $gia = ContaMutanti
        if ($gia -gt 0) {
            Write-Host "Nel database ci sono gia' $gia mutanti statici: rilanciare il generatore ne aggiungerebbe di doppi." -ForegroundColor Red
            Write-Host "Per rifare da zero cancella la cartella $campagna e rilancia."
            return
        }
        New-Item -ItemType Directory -Force $campagna | Out-Null
        Push-Location $campagna
        foreach ($v in $variabili) { Remove-Item "Env:$v" -ErrorAction SilentlyContinue }
        Write-Host "  Genero i mutanti statici (qualche minuto) ..." -ForegroundColor Cyan
        & $global:JAVA -jar $STATJAR (Join-Path $Radice $cfgStatico) 2>&1 | Out-File (Join-Path $campagna "gen-static.log") -Encoding utf8
        Pop-Location
        if ((ContaMutanti) -eq 0) { Write-Host "Il generatore non ha prodotto mutanti: guarda $campagna\gen-static.log" -ForegroundColor Red; return }
        if ($app -eq "cookbook") {
            & python (Join-Path $Radice "deduplica_statici.py") $campagna --solo-conta
        } else {
            & python (Join-Path $Radice "deduplica_statici.py") $campagna
        }
        Write-Host ""
        Write-Host "Fatto. Mutanti statici nel database: $(ContaMutanti)  ($campagna\mutations.db)" -ForegroundColor Green
        if ($attesiStatici) {
            if ($app -eq "cookbook") {
                Write-Host "  Tesi (Tabella 4.1): $($attesiStatici[0]) generati, di cui $($attesiStatici[1]) distinti; su CookBook sono stati provati tutti, quindi qui non si toglie niente."
                Write-Host "  Su CookBook la rigenerazione puo' differire di poco dalla tesi (1 mutante in piu' e 152 dal codice diverso, per lo piu' spostamenti verso un altro file):"
                Write-Host "  l'insieme esatto della tesi e' in cookbook-static-run\mutations.db."
            }
            else { Write-Host "  Attesi (tesi, Tabella 4.1): $($attesiStatici[0]) generati, $($attesiStatici[1]) distinti dopo aver tolto i duplicati." }
        }
        Write-Host "Prossimo passo: .\mutanti.ps1 $app esegui 20 statico"
    }
    elseif ($Fase -eq "confronta") {
        & python (Join-Path $Radice "confronta_tecniche.py") $app
    }
    elseif ($Fase -eq "esegui") {
        if (-not (Test-Path $db) -or (ContaMutanti) -eq 0) {
            if ($statico) { Write-Host "Non ci sono mutanti statici in ${campagna}: fai prima .\mutanti.ps1 $app statico" -ForegroundColor Red }
            else { Write-Host "Non ci sono mutanti in ${campagna}: fai prima 'prompt', il modello e 'completa'." -ForegroundColor Red }
            return
        }
        $chrome = @("$env:ProgramFiles\Google\Chrome\Application\chrome.exe", "${env:ProgramFiles(x86)}\Google\Chrome\Application\chrome.exe", "$env:LOCALAPPDATA\Google\Chrome\Application\chrome.exe") | Where-Object { Test-Path $_ }
        if (-not $chrome) { Write-Host "Google Chrome non trovato: serve per eseguire i test. Si scarica da https://www.google.com/chrome/" -ForegroundColor Red; return }
        & python (Join-Path $Radice "esegui_mutanti.py") $app --quanti $Quanti --tecnica $Tecnica
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
