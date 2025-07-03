@echo off
echo ========================================
echo Switching to Port 3306 Configuration
echo ========================================
echo.

echo This script will switch the database configuration to use port 3306.
echo.

REM Check if Java is available
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH.
    echo Please install Java and add it to your system PATH.
    echo.
    pause
    exit /b 1
)

REM Check if the project is compiled
if not exist "target\classes\com\alerts\system\DatabasePortManager.class" (
    echo Compiling the project first...
    mvn clean compile
    if %errorlevel% neq 0 (
        echo ERROR: Failed to compile the project.
        echo Please make sure Maven is installed and the project structure is correct.
        echo.
        pause
        exit /b 1
    )
)

echo Running port configuration utility...
echo.

REM Run the port configuration utility
java -cp "target\classes" com.alerts.system.DatabasePortManager

echo.
echo Port configuration completed.
echo.
pause 