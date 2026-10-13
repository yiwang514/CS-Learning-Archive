@echo off
setlocal
call "%~dp0pl0-compiler\c\build.cmd"
if errorlevel 1 exit /b 1
call "%~dp0lexical-analysis\c\build.cmd"
if errorlevel 1 exit /b 1
call "%~dp0lexical-analysis\c-textbook\build.cmd"
if errorlevel 1 exit /b 1
call "%~dp0lexical-analysis\java\build.cmd"
if errorlevel 1 exit /b 1
call "%~dp0nfa-to-dfa\java\build.cmd"
if errorlevel 1 exit /b 1
echo [OK] All compiler-principles experiments built.
exit /b 0
