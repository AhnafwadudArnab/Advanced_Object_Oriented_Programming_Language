@echo off
echo ========================================
echo Missing Person Alert System - Complete Launcher
echo ========================================
echo.

REM Check Java version
echo Checking Java version...
java -version 2>&1 | findstr "version" > temp_java_version.txt
set /p JAVA_VERSION=<temp_java_version.txt
del temp_java_version.txt

echo Current Java: %JAVA_VERSION%

REM Check for Java 17+
java -version 2>&1 | findstr "version" | findstr "17\|18\|19\|20\|21" >nul
set JAVA17_AVAILABLE=%ERRORLEVEL%

REM Check for JavaFX SDK
set JAVAFX_SDK=C:\Program Files\Java\javafx-sdk-21.0.7
if exist "%JAVAFX_SDK%" (
    set JAVAFX_AVAILABLE=0
    echo JavaFX 21.0.7 SDK found: %JAVAFX_SDK%
) else (
    set JAVAFX_AVAILABLE=1
    echo JavaFX 21.0.7 SDK not found
)

REM Check for compiled classes
if exist "target\classes\com\alerts\system\Main.class" (
    set CLASSES_AVAILABLE=0
    echo Compiled classes found
) else (
    set CLASSES_AVAILABLE=1
    echo No compiled classes found
)

echo.

REM Decision logic
if %JAVA17_AVAILABLE%==0 (
    if %JAVAFX_AVAILABLE%==0 (
        if %CLASSES_AVAILABLE%==0 (
            echo [OPTION 1] Full JavaFX 21 with Interactive Map
            echo Using Java 17+ with JavaFX 21.0.7 SDK
            echo.
            echo Starting with full map functionality...
            call run_full.bat
            goto :end
        ) else (
            echo [OPTION 2] Build and Run with Full JavaFX 21
            echo Java 17+ and JavaFX 21.0.7 available, but classes need to be built
            echo.
            echo Building with full JavaFX 21 support...
            call build_full.bat
            if %ERRORLEVEL%==0 (
                echo.
                echo Starting with full map functionality...
                call run_full.bat
            )
            goto :end
        )
    ) else (
        echo [OPTION 3] Java 17+ Available but No JavaFX SDK
        echo.
        echo To get full map functionality, you need to:
        echo 1. Download JavaFX 21.0.7 SDK from: https://openjfx.io/
        echo 2. Extract to C:\Program Files\Java\javafx-sdk-21.0.7
        echo 3. Run this script again
        echo.
        echo For now, trying Maven approach...
        call run_with_javafx.bat
        goto :end
    )
) else (
    echo [OPTION 4] Java 8 Environment
    echo.
    echo Java 17+ not found. To get full map functionality:
    echo 1. Install Java 17+ from: https://adoptium.net/
    echo 2. Install JavaFX 21.0.7 SDK from: https://openjfx.io/
    echo 3. Run this script again
    echo.
    echo For now, trying Java 8 compatibility...
    if %CLASSES_AVAILABLE%==0 (
        echo Attempting to run with existing classes...
        java -cp "target/classes;mysql-connector-java-8.0.33.jar" com.alerts.system.Main
    ) else (
        echo Building for Java 8 compatibility...
        call build_java8.bat
        if %ERRORLEVEL%==0 (
            echo.
            echo Starting with Java 8 compatibility...
            call run_java8.bat
        )
    )
)

:end
echo.
echo ========================================
echo Launcher completed
echo ========================================
pause 