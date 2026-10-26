@echo off
rem Avvia la riesecuzione della Procedura B (Blocco A) in una finestra propria, indipendente da
rem Claude: se la sessione di Claude si chiude, le campagne continuano. Chiudere QUESTA finestra
rem invece ferma il run (i gruppi gia' completati restano salvati e la ripresa li salta).
rem Il percorso non e' scritto qui dentro di proposito: %~dp0 e' la cartella del .bat, cosi' la
rem "a accentata" di Universita' non passa dalla codepage di cmd.
title Blocco A - campagne in esecuzione - NON chiudere
cd /d "%~dp0"
set PYTHONIOENCODING=utf-8

set CAMPAGNE=%*
if "%CAMPAGNE%"=="" set CAMPAGNE=1 2 3 4

echo.
echo  ============================================================
echo    BLOCCO A - riesecuzione Procedura B a 7 strategie
echo    Campagne: %CAMPAGNE%
echo    Avvio:    %date% %time%
echo.
echo    NON chiudere questa finestra finche' non compare FINITO.
echo    L'avanzamento e' in blocco-a.log, in questa cartella.
echo  ============================================================
echo.

echo. >> blocco-a.log
echo ############ AVVIO DA avvia_blocco_a.bat %date% %time% - campagne %CAMPAGNE% ############ >> blocco-a.log
python -u run_blocco_a.py %CAMPAGNE% >> blocco-a.log 2>&1
set ESITO=%ERRORLEVEL%

echo.
echo  ============================================================
if "%ESITO%"=="0" (
  echo    FINITO - tutte le campagne richieste sono complete.
) else (
  echo    FINITO CON PROBLEMI - codice %ESITO%. Vedi le ultime righe di blocco-a.log:
  echo    le campagne incomplete si riprendono rilanciando questo file.
)
echo    Fine: %date% %time%
echo  ============================================================
echo.
pause
