@echo off
rem Doppio clic: scarica gli strumenti portatili, compila lo strumento e prepara le applicazioni.
rem Se il file e' stato aperto da dentro uno ZIP, Windows lo ha estratto in una cartella temporanea: ci si ferma.
echo %~dp0 | findstr /i /c:".zip" >nul && (
    echo.
    echo ATTENZIONE: stai avviando installa.bat da dentro un file ZIP.
    echo Chiudi questa finestra, estrai prima lo ZIP ^(tasto destro, "Estrai tutto"^)
    echo e avvia installa.bat dalla cartella estratta.
    echo.
    pause
    exit /b 1
)
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0installa.ps1"
echo.
pause
