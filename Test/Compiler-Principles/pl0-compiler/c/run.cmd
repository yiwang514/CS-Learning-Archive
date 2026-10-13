@echo off
setlocal
if not "%~2"=="" (
    echo Usage: run.cmd [source.pl0]
    exit /b 1
)
call "%~dp0build.cmd"
if errorlevel 1 exit /b 1
if "%~1"=="" (
    "%~dp0bin\pl0.exe" "%~dp0examples\multiply.pl0"
) else (
    "%~dp0bin\pl0.exe" "%~1"
)
exit /b %ERRORLEVEL%

