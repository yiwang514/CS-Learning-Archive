@echo off
setlocal
call build.cmd
if errorlevel 1 exit /b 1
bin\pl0.exe examples\multiply.pl0 | findstr /C:"end PL/0"
if errorlevel 1 (
    echo [ERROR] verification failed
    exit /b 1
)
echo [OK] PL/0 environment verified
endlocal

