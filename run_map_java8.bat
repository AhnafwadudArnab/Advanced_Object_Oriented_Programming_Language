@echo off
echo ========================================
echo Map View - Java 8 Compatible
echo ========================================
echo.

REM Check for JavaFX SDK
set JAVAFX_SDK=C:\Program Files\Java\javafx-sdk-21.0.7
if not exist "%JAVAFX_SDK%" (
    echo JavaFX SDK not found at: %JAVAFX_SDK%
    echo.
    echo Please download JavaFX 21.0.7 SDK from:
    echo https://openjfx.io/
    echo.
    echo Extract it to: C:\Program Files\Java\javafx-sdk-21.0.7
    echo.
    pause
    exit /b 1
)

echo JavaFX SDK found: %JAVAFX_SDK%
echo.

REM Check if classes are compiled
if not exist "target\classes\com\alerts\system\MapViewApp.class" (
    echo Compiling classes...
    javac -cp "src/main/java" -d target/classes src/main/java/com/alerts/system/*.java
    if %ERRORLEVEL% neq 0 (
        echo Compilation failed!
        pause
        exit /b 1
    )
)

echo Running MapViewApp with Java 8 + JavaFX 21...
echo Note: If WebView fails, you'll see a text-based map view.
echo.

set JAVAFX_LIB=%JAVAFX_SDK%\lib
set CLASSPATH=%JAVAFX_LIB%\javafx.base.jar;%JAVAFX_LIB%\javafx.controls.jar;%JAVAFX_LIB%\javafx.fxml.jar;%JAVAFX_LIB%\javafx.graphics.jar;%JAVAFX_LIB%\javafx.web.jar;target/classes

java -cp "%CLASSPATH%" com.alerts.system.MapViewApp

echo.
echo Map view completed.
pause 