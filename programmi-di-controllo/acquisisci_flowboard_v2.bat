@echo off
rem Acquisisce nel database le risposte di Opus 4.8 (FlowBoard v2) e stampa il riepilogo.
cd /d "%~dp0flowboard-v2-run"
set LLM_ROLE=1
set LLM_ROLE_INGEST_DIR=output/mutations/role-ingest
set LLM_ROLE_TARGETS=%~dp0role-targets-flowboard.json
set LLM_MUTATION_TYPES=%~dp0mutation-types-modello.json
set LLM_MODEL=claude-opus-4-8
"C:\Program Files\Eclipse Adoptium\jdk-25.0.2.10-hotspot\bin\java.exe" -jar "%~dp0mutation-generator\llm-generator\target\llm-generator-1.0.0-jar-with-dependencies-eps.jar" "%~dp0generator-config-flowboard.json" > ingest.log 2>&1
findstr /C:"DONE" ingest.log
