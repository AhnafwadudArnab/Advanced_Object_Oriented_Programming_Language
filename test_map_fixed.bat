@echo off
echo Testing Fixed Map View Application...
echo.

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH
    pause
    exit /b 1
)

REM Compile the application
echo Compiling the application...
javac -cp "lib/*" -d target/classes src/main/java/com/alerts/system/*.java
if errorlevel 1 (
    echo ERROR: Compilation failed
    pause
    exit /b 1
)

echo.
echo Running Map View Test...
echo This will open the map view with sample data...
echo.

REM Run the map view application
java -cp "target/classes;lib/*" com.alerts.system.MapViewApp

echo.
echo Map test completed.
pause 