@echo off
setlocal
pushd "%~dp0"
if errorlevel 1 exit /b 1
if not exist bin mkdir bin
gcc -std=c11 -Wall -Wextra -Werror -Iinclude -o "bin\pl0_textbook.exe" "src\pl0_lexer.c"
set "RESULT=%ERRORLEVEL%"
popd
exit /b %RESULT%
