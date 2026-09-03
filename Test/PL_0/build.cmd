@echo off
setlocal
if not exist bin mkdir bin
gcc -std=c11 -Wall -Wextra -Iinclude -o bin\pl0.exe src\pl0.c
if errorlevel 1 exit /b 1
echo [OK] build: bin\pl0.exe
endlocal

