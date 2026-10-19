@echo off
rem Doppio clic: scarica gli strumenti portatili, compila lo strumento e prepara le applicazioni.
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0installa.ps1"
echo.
pause
