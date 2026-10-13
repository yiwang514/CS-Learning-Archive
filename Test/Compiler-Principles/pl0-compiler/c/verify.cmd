@echo off
setlocal
call "%~dp0build.cmd"
if errorlevel 1 exit /b 1
pushd "%~dp0"
if errorlevel 1 exit /b 1
set "OUT=%TEMP%\compiler_verify_%RANDOM%_%RANDOM%.txt"
bin\pl0.exe examples\multiply.pl0 > "%OUT%" 2>&1
if errorlevel 1 goto failed
findstr /C:"end PL/0" "%OUT%" >nul
if errorlevel 1 goto failed

del "%OUT%" >nul 2>&1
popd
echo [OK] PL/0 compiler tests passed.
exit /b 0

:failed
echo [FAILED] PL/0 compiler verification failed.
if exist "%OUT%" type "%OUT%"
if exist "%OUT%" del "%OUT%" >nul 2>&1
popd
exit /b 1
