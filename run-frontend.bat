@echo off
echo ========================================
echo Lost and Found - Starting Frontend (Swing)
echo ========================================
echo.
echo Make sure the backend is running on port 8080!
echo.
timeout /t 3
echo Starting Swing application...
echo.
mvn exec:java -Dexec.mainClass="com.lostandfound.swing.SwingApplication"
