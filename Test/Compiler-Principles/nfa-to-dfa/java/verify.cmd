@echo off
setlocal
call "%~dp0build.cmd"
if errorlevel 1 exit /b 1
javac -encoding UTF-8 -Xlint:all -cp "%~dp0classes" -d "%~dp0classes" "%~dp0tests\NfaToDfaTest.java"
if errorlevel 1 exit /b 1
java -cp "%~dp0classes" NfaToDfaTest
if errorlevel 1 exit /b 1
java -cp "%~dp0classes" NfaToDfa < "%~dp0tests\basic.txt" >nul
if errorlevel 1 exit /b 1
java -cp "%~dp0classes" NfaToDfa < "%~dp0tests\epsilon_chain.txt" >nul
if errorlevel 1 exit /b 1
java -cp "%~dp0classes" NfaToDfa < "%~dp0tests\epsilon_cycle.txt" >nul
if errorlevel 1 exit /b 1
echo [OK] All three input files passed.
exit /b 0
