@echo off
setlocal
call "%~dp0pl0-compiler\c\verify.cmd"
if errorlevel 1 exit /b 1
call "%~dp0lexical-analysis\c\verify.cmd"
if errorlevel 1 exit /b 1
call "%~dp0lexical-analysis\c-textbook\verify.cmd"
if errorlevel 1 exit /b 1
call "%~dp0lexical-analysis\java\verify.cmd"
if errorlevel 1 exit /b 1
call "%~dp0nfa-to-dfa\java\verify.cmd"
if errorlevel 1 exit /b 1
echo [OK] All compiler-principles experiments verified.
exit /b 0
