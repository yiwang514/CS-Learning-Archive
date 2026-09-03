@echo off
setlocal
if not exist bin\pl0.exe call build.cmd
if not exist bin\pl0.exe (
    echo [ERROR] build failed
    exit /b 1
)
set "SOURCE=examples\multiply.pl0"
if not "%~1"=="" set "SOURCE=%~1"
bin\pl0.exe "%SOURCE%"
exit /b %errorlevel%

