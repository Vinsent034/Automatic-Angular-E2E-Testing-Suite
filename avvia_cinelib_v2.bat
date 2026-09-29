@echo off
rem Avvia la Procedura A e B di CineLib v2 in una finestra propria, indipendente da
rem Claude: se la sessione di Claude si chiude, le campagne continuano. Chiudere QUESTA finestra
rem invece ferma il run (i gruppi gia' completati restano salvati e la ripresa li salta).
rem Il percorso non e' scritto qui dentro di proposito: %~dp0 e' la cartella del .bat, cosi' la
rem "a accentata" di Universita' non passa dalla codepage di cmd.
title CineLib v2 - campagne in esecuzione - NON chiudere
cd /d "%~dp0"
set PYTHONIOENCODING=utf-8

set CAMPAGNE=%*
if "%CAMPAGNE%"=="" set CAMPAGNE=1 2

echo.
echo  ============================================================
echo    CINELIB v2 - campagne linguistica e statica, 7 strategie
echo    Campagne: %CAMPAGNE%
echo    Avvio:    %date% %time%
echo.
echo    NON chiudere questa finestra finche' non compare FINITO.
echo    L'avanzamento e' in cinelib-v2.log, in questa cartella.
echo  ============================================================
echo.

echo. >> cinelib-v2.log
echo ############ AVVIO DA avvia_cinelib_v2.bat %date% %time% - campagne %CAMPAGNE% ############ >> cinelib-v2.log
python -u run_campagne_cinelib_v2.py %CAMPAGNE% >> cinelib-v2.log 2>&1
set ESITO=%ERRORLEVEL%

echo.
echo  ============================================================
if "%ESITO%"=="0" (
  echo    FINITO - tutte le campagne richieste sono complete.
) else (
  echo    FINITO CON PROBLEMI - codice %ESITO%. Vedi le ultime righe di cinelib-v2.log:
  echo    le campagne incomplete si riprendono rilanciando questo file.
)
echo    Fine: %date% %time%
echo  ============================================================
echo.
pause
