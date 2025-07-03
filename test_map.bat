@echo off
echo ========================================
echo Testing Map Functionality
echo ========================================
echo.

REM Check if compiled classes exist
if exist "target\classes\com\alerts\system\MapViewApp.class" (
    echo MapViewApp class found
) else (
    echo MapViewApp class not found - compiling...
    call mvn compile
    if %ERRORLEVEL% neq 0 (
        echo Compilation failed
        pause
        exit /b 1
    )
)

echo.
echo Testing map functionality...
echo.

REM Test with Java 8 compatibility
java -cp "target/classes;mysql-connector-java-8.0.33.jar" com.alerts.system.MapViewApp

echo.
echo Map test completed
pause 