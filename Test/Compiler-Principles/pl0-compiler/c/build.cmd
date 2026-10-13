@echo off
setlocal
pushd "%~dp0"
if errorlevel 1 exit /b 1
if not exist bin mkdir bin
gcc -std=c11 -Wall -Wextra -Iinclude -o "bin\pl0.exe" "src\pl0.c"
set "RESULT=%ERRORLEVEL%"
popd
exit /b %RESULT%
