@echo off
title 🚀 Building AlphaWash .exe with PS2EXE

REM === Get absolute path of script folder ===
set "SCRIPT_DIR=%~dp0"
REM === Go to project root (parent of script folder) ===
cd /d "%SCRIPT_DIR%.."

REM Define input/output paths
set "PS_SCRIPT=launch.ps1"
set "EXE_OUTPUT=alpha.exe"

REM --- Set Execution Policy ---
powershell -Command "Try { Set-ExecutionPolicy RemoteSigned -Scope CurrentUser -Force } Catch { Write-Host 'ExecutionPolicy already set or failed.' }"

REM --- Install PS2EXE if not available ---
powershell -Command "if (-not (Get-Module -ListAvailable -Name ps2exe)) { Install-Module -Name ps2exe -Scope CurrentUser -Force }"

REM --- Build the EXE ---
powershell -Command "Import-Module ps2exe; Invoke-ps2exe -inputFile '%PS_SCRIPT%' -outputFile '%EXE_OUTPUT%' -noConsole"

echo.
echo ✅ Build done. Output: %EXE_OUTPUT%
pause
