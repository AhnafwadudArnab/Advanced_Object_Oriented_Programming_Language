@echo off
echo ========================================
echo Quick Switch to Port 3306
echo ========================================
echo.

echo Switching database configuration to port 3306...
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH.
    pause
    exit /b 1
)

REM Check if the project is compiled
if not exist "target\classes\com\alerts\system\DatabasePortManager.class" (
    echo Compiling the project first...
    mvn clean compile
    if %errorlevel% neq 0 (
        echo ERROR: Failed to compile the project.
        pause
        exit /b 1
    )
)

echo Running quick switch to port 3306...
echo.

REM Run the quick switch method with command line argument
java -cp "target\classes" com.alerts.system.DatabasePortManager 3306

echo.
echo Successfully switched to port 3306!
echo.
pause 