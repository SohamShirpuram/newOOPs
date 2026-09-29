@echo off
title College Admission Portal - OOPS Project
color 0b
echo ====================================================================
echo             COLLEGE ADMISSION PORTAL - OOPS PROJECT
echo ====================================================================
echo.
echo [1/3] Navigating to backend folder...
cd /d "%~dp0backend"

echo [2/3] Compiling Java backend classes...
javac *.java
if %errorlevel% neq 0 (
    echo.
    echo [ERROR] Compilation failed! Please check if JDK is installed and on PATH.
    pause
    exit /b %errorlevel%
)

echo [3/3] Launching Web Server...
echo.
echo ====================================================================
echo Server is running at: http://localhost:8080/
echo Opening default web browser automatically...
echo To stop the server at any time, press Ctrl+C in this window.
echo ====================================================================
echo.

:: Open default browser to the home page
start http://localhost:8080/

:: Start the Java HTTP server
java Main
pause

