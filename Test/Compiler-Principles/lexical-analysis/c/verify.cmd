@echo off
setlocal
call "%~dp0build.cmd"
if errorlevel 1 exit /b 1
pushd "%~dp0"
if errorlevel 1 exit /b 1
set "OUT=%TEMP%\compiler_verify_%RANDOM%_%RANDOM%.txt"
bin\pl0lex.exe tests\lex_demo.pl0 > "%OUT%" 2>&1
if errorlevel 1 goto failed
findstr /C:"0 lexical error(s)" "%OUT%" >nul
if errorlevel 1 goto failed
bin\pl0lex.exe tests\lex_boundary.pl0 > "%OUT%" 2>&1
if errorlevel 1 goto failed
findstr /C:"0 lexical error(s)" "%OUT%" >nul
if errorlevel 1 goto failed
bin\pl0lex.exe tests\lex_errors.pl0 > "%OUT%" 2>&1
if not errorlevel 1 goto failed
findstr /C:"6 lexical error(s)" "%OUT%" >nul
if errorlevel 1 goto failed

del "%OUT%" >nul 2>&1
popd
echo [OK] Independent C lexer tests passed.
exit /b 0

:failed
echo [FAILED] Independent C lexer verification failed.
if exist "%OUT%" type "%OUT%"
if exist "%OUT%" del "%OUT%" >nul 2>&1
popd
exit /b 1
