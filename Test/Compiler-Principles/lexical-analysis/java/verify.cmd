@echo off
setlocal
call "%~dp0build.cmd"
if errorlevel 1 exit /b 1
pushd "%~dp0"
if errorlevel 1 exit /b 1
set "OUT=%TEMP%\compiler_verify_%RANDOM%_%RANDOM%.txt"
java -cp classes Pl0Lexer tests\basic.pl0 > "%OUT%" 2>&1
if errorlevel 1 goto failed
findstr /C:"CONSTSYM" "%OUT%" >nul
if errorlevel 1 goto failed
java -cp classes Pl0Lexer tests\read_write.pl0 > "%OUT%" 2>&1
if errorlevel 1 goto failed
findstr /C:"READSYM" "%OUT%" >nul
if errorlevel 1 goto failed
findstr /C:"WRITESYM" "%OUT%" >nul
if errorlevel 1 goto failed
findstr /C:"NEQ" "%OUT%" >nul
if errorlevel 1 goto failed
java -cp classes Pl0Lexer tests\errors.pl0 > "%OUT%" 2>&1
if not errorlevel 1 goto failed
findstr /C:"!39" "%OUT%" >nul
if errorlevel 1 goto failed
findstr /C:"!30" "%OUT%" >nul
if errorlevel 1 goto failed
findstr /C:"!37" "%OUT%" >nul
if errorlevel 1 goto failed
findstr /C:"!19" "%OUT%" >nul
if errorlevel 1 goto failed
findstr /C:"!36" "%OUT%" >nul
if errorlevel 1 goto failed
findstr /C:"!38" "%OUT%" >nul
if errorlevel 1 goto failed

del "%OUT%" >nul 2>&1
popd
echo [OK] lexical-analysis/java tests passed.
exit /b 0

:failed
echo [FAILED] lexical-analysis/java verification failed.
if exist "%OUT%" type "%OUT%"
if exist "%OUT%" del "%OUT%" >nul 2>&1
popd
exit /b 1

