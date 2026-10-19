@echo off
rem Apre PowerShell nella cartella del progetto con Java 25, Maven, Node e Python portatili.
cd /d "%~dp0"
powershell -NoExit -NoProfile -ExecutionPolicy Bypass -Command ". '%~dp0ambiente.ps1'; Write-Host 'Strumenti portatili attivi (Java 25, Maven, Node, Python).' -ForegroundColor Green"
