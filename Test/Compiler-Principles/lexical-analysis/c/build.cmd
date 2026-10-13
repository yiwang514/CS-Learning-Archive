@echo off
setlocal
pushd "%~dp0"
if errorlevel 1 exit /b 1
if not exist bin mkdir bin
gcc -std=c11 -Wall -Wextra -Werror -o "bin\pl0lex.exe" "src\pl0lex.c"
set "RESULT=%ERRORLEVEL%"
popd
exit /b %RESULT%
