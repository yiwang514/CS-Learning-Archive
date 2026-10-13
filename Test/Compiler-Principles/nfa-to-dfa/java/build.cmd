@echo off
setlocal
pushd "%~dp0"
if errorlevel 1 exit /b 1
if not exist classes mkdir classes
javac -encoding UTF-8 -Xlint:all -d classes src\NfaToDfa.java
set "RESULT=%ERRORLEVEL%"
popd
if not "%RESULT%"=="0" exit /b %RESULT%
echo [OK] NFA to DFA build completed.
exit /b 0
