@echo off
echo ========================================
echo Quick Start - Missing Person Alert System
echo ========================================
echo.

REM Check for JavaFX SDK
set JAVAFX_SDK=C:\Program Files\Java\javafx-sdk-21.0.7
if not exist "%JAVAFX_SDK%" (
    echo JavaFX SDK not found. Please install JavaFX 21.0.7 SDK.
    pause
    exit /b 1
)

echo JavaFX 21.0.7 SDK found: %JAVAFX_SDK%

REM Check if we have compiled classes
if exist "target\classes\com\alerts\system\Main.class" (
    echo Found compiled classes.
    
    REM Try to run with Java 8 and JavaFX 21
    echo Attempting to run with Java 8 + JavaFX 21...
    echo Note: Map may show as text view if WebView fails.
    echo.
    
    set JAVAFX_LIB=%JAVAFX_SDK%\lib
    set CLASSPATH=%JAVAFX_LIB%\javafx.base.jar;%JAVAFX_LIB%\javafx.controls.jar;%JAVAFX_LIB%\javafx.fxml.jar;%JAVAFX_LIB%\javafx.graphics.jar;%JAVAFX_LIB%\javafx.web.jar;mysql-connector-java-8.0.33.jar;target/classes
    
    java -cp "%CLASSPATH%" com.alerts.system.Main
    
) else (
    echo No compiled classes found.
    echo.
    echo To get full functionality, you need Java 17+.
    echo For now, let's try to compile with Java 8...
    echo.
    
    REM Try to compile with Java 8
    set JAVAFX_LIB=%JAVAFX_SDK%\lib
    set CLASSPATH=%JAVAFX_LIB%\javafx.base.jar;%JAVAFX_LIB%\javafx.controls.jar;%JAVAFX_LIB%\javafx.fxml.jar;%JAVAFX_LIB%\javafx.graphics.jar;%JAVAFX_LIB%\javafx.web.jar;mysql-connector-java-8.0.33.jar
    
    if not exist "target\classes" mkdir target\classes
    
    echo Compiling with Java 8 + JavaFX 21...
    javac -cp "%CLASSPATH%" -d target/classes src/main/java/com/alerts/system/*.java
    
    if %ERRORLEVEL%==0 (
        echo Compilation successful!
        echo Copying resources...
        xcopy /E /I /Y src\main\resources target\classes
        
        echo.
        echo Starting application...
        java -cp "%CLASSPATH%;target/classes" com.alerts.system.Main
    ) else (
        echo Compilation failed. The classes require Java 17+.
        echo.
        echo To get full map functionality, please:
        echo 1. Install Java 17+ from https://adoptium.net/
        echo 2. Run build_full.bat
        echo 3. Run run_full.bat
    )
)

echo.
echo ========================================
echo Quick start completed
echo ========================================
pause 