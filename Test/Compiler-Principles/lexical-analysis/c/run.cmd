@echo off
setlocal
if not "%~2"=="" (
    echo Usage: run.cmd [source.pl0]
    exit /b 1
)
call "%~dp0build.cmd"
if errorlevel 1 exit /b 1
if "%~1"=="" (
    "%~dp0bin\pl0lex.exe" "%~dp0tests\lex_demo.pl0"
) else (
    "%~dp0bin\pl0lex.exe" "%~1"
)
exit /b %ERRORLEVEL%
