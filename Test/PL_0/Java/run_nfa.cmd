@echo off
setlocal
if not "%~2"=="" (
    echo Usage: run_nfa.cmd [input.txt]
    exit /b 1
)
call "%~dp0build_nfa.cmd"
if errorlevel 1 exit /b 1
if "%~1"=="" (
    java -cp "%~dp0classes" NfaToDfa
) else (
    java -cp "%~dp0classes" NfaToDfa < "%~1"
)
exit /b %ERRORLEVEL%
