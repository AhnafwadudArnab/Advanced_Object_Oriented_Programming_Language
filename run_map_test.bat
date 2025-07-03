@echo off
echo Testing Map Functionality...
echo.

REM Check if classes exist
if exist "target\classes\com\alerts\system\MapViewApp.class" (
    echo MapViewApp class found - testing...
    java -cp "target/classes;mysql-connector-java-8.0.33.jar" com.alerts.system.MapViewApp
) else (
    echo MapViewApp class not found
    echo Please compile the project first
)

echo.
echo Test completed
pause 