@echo off
echo ========================================
echo Lost and Found - Building Project
echo ========================================
echo.
echo This will download dependencies and compile the project...
echo This may take a few minutes on first run.
echo.
pause
echo.
echo Building...
mvn clean install -DskipTests
echo.
if %ERRORLEVEL% EQU 0 (
    echo ========================================
    echo BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo You can now run:
    echo 1. run-backend.bat  - to start the server
    echo 2. run-frontend.bat - to start the Swing UI
    echo.
) else (
    echo ========================================
    echo BUILD FAILED!
    echo ========================================
    echo.
    echo Please check:
    echo 1. Java 17+ is installed
    echo 2. Maven is installed and in PATH
    echo 3. Internet connection for downloading dependencies
    echo.
)
pause
