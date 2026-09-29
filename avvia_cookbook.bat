@echo off
rem Avvia la Procedura A e B di CookBook (Blocco B, passo 10) in una finestra propria, indipendente da
rem Claude: se la sessione di Claude si chiude, le campagne continuano. Chiudere QUESTA finestra
rem invece ferma il run (i gruppi gia' completati restano salvati e la ripresa li salta).
rem Il percorso non e' scritto qui dentro di proposito: %~dp0 e' la cartella del .bat, cosi' la
rem "a accentata" di Universita' non passa dalla codepage di cmd.
title CookBook - campagne in esecuzione - NON chiudere
cd /d "%~dp0"
set PYTHONIOENCODING=utf-8

set CAMPAGNE=%*
if "%CAMPAGNE%"=="" set CAMPAGNE=1 2

echo.
echo  ============================================================
echo    COOKBOOK - campagne linguistica e statica, 7 strategie
echo    Campagne: %CAMPAGNE%
echo    Avvio:    %date% %time%
echo.
echo    NON chiudere questa finestra finche' non compare FINITO.
echo    L'avanzamento e' in cookbook.log, in questa cartella.
echo  ============================================================
echo.

echo. >> cookbook.log
echo ############ AVVIO DA avvia_cookbook.bat %date% %time% - campagne %CAMPAGNE% ############ >> cookbook.log
python -u run_campagne_cookbook.py %CAMPAGNE% >> cookbook.log 2>&1
set ESITO=%ERRORLEVEL%

echo.
echo  ============================================================
if "%ESITO%"=="0" (
  echo    FINITO - tutte le campagne richieste sono complete.
) else (
  echo    FINITO CON PROBLEMI - codice %ESITO%. Vedi le ultime righe di cookbook.log:
  echo    le campagne incomplete si riprendono rilanciando questo file.
)
echo    Fine: %date% %time%
echo  ============================================================
echo.
pause
