@echo off
setlocal
pushd "%~dp0"
if errorlevel 1 exit /b 1
if not exist classes mkdir classes
javac -encoding UTF-8 -Xlint:all -d classes src\Pl0Lexer.java
set "RESULT=%ERRORLEVEL%"
popd
exit /b %RESULT%

