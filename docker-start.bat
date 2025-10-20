@echo off
REM Docker Quick Start Script for Lost and Found System

echo ================================
echo Lost and Found - Docker Setup
echo ================================
echo.

REM Check if Docker is installed
docker --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker is not installed or not in PATH
    echo Please install Docker Desktop from: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

REM Check if Docker Compose is installed
docker-compose --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker Compose is not installed
    pause
    exit /b 1
)

echo Docker is installed and ready!
echo.

:menu
echo Choose an option:
echo 1. Build and Start Application
echo 2. Stop Application
echo 3. View Logs
echo 4. Rebuild Application
echo 5. Clean Up (Remove containers and volumes)
echo 6. Check Status
echo 7. Exit
echo.
set /p choice="Enter your choice (1-7): "

if "%choice%"=="1" goto start
if "%choice%"=="2" goto stop
if "%choice%"=="3" goto logs
if "%choice%"=="4" goto rebuild
if "%choice%"=="5" goto cleanup
if "%choice%"=="6" goto status
if "%choice%"=="7" goto end
goto menu

:start
echo.
echo [INFO] Building and starting the application...
docker-compose up -d --build
if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] Application started successfully!
    echo Access the application at: http://localhost:8080
    echo Health check: http://localhost:8080/actuator/health
    echo.
    echo Default credentials:
    echo Username: admin
    echo Password: admin123
) else (
    echo [ERROR] Failed to start the application
)
echo.
pause
goto menu

:stop
echo.
echo [INFO] Stopping the application...
docker-compose stop
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Application stopped successfully!
) else (
    echo [ERROR] Failed to stop the application
)
echo.
pause
goto menu

:logs
echo.
echo [INFO] Showing logs (Press Ctrl+C to exit)...
docker-compose logs -f
goto menu

:rebuild
echo.
echo [INFO] Rebuilding the application...
docker-compose down
docker-compose build --no-cache
docker-compose up -d
if %ERRORLEVEL% EQU 0 (
    echo [SUCCESS] Application rebuilt and started successfully!
) else (
    echo [ERROR] Failed to rebuild the application
)
echo.
pause
goto menu

:cleanup
echo.
echo [WARNING] This will remove all containers, volumes, and images
set /p confirm="Are you sure? (y/n): "
if /i "%confirm%"=="y" (
    echo [INFO] Cleaning up...
    docker-compose down -v --rmi all
    echo [SUCCESS] Cleanup completed!
) else (
    echo [INFO] Cleanup cancelled
)
echo.
pause
goto menu

:status
echo.
echo [INFO] Checking application status...
docker-compose ps
echo.
echo [INFO] Container health:
docker inspect --format="{{.State.Health.Status}}" lost-and-found-app 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Container is not running or health check not available
)
echo.
pause
goto menu

:end
echo.
echo Thank you for using Lost and Found System!
exit /b 0
