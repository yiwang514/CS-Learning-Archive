@echo off
setlocal
if not "%~2"=="" (
    echo Usage: run.cmd [source.pl0]
    exit /b 1
)
call "%~dp0build.cmd"
if errorlevel 1 exit /b 1
java -cp "%~dp0classes" Pl0Lexer %*
exit /b %ERRORLEVEL%
