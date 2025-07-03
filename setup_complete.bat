@echo off
echo ========================================
echo Complete Setup for Missing Person Alert System
echo ========================================
echo.

REM Check current Java version
echo Checking current Java installation...
java -version 2>&1 | findstr "version" | findstr "17\|18\|19\|20\|21" >nul
if %ERRORLEVEL%==0 (
    echo Java 17+ already installed!
    goto :check_javafx
)

echo Java 17+ not found. Setting up complete environment...
echo.

REM Create setup directory
if not exist "setup" mkdir setup
cd setup

echo Downloading Java 17...
echo This may take a few minutes...
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.9%2B9/OpenJDK17U-jdk_x64_windows_hotspot_17.0.9_9.zip' -OutFile 'java17.zip'}"

if not exist "java17.zip" (
    echo Failed to download Java 17. Please download manually from:
    echo https://adoptium.net/
    pause
    exit /b 1
)

echo Extracting Java 17...
powershell -Command "Expand-Archive -Path 'java17.zip' -DestinationPath '.' -Force"

REM Find the extracted directory
for /d %%i in (jdk-17*) do (
    set JAVA17_DIR=%%i
    goto :found_java
)

:found_java
echo Java 17 extracted to: %JAVA17_DIR%

REM Set JAVA_HOME temporarily
set JAVA_HOME=%CD%\%JAVA17_DIR%
set PATH=%JAVA_HOME%\bin;%PATH%

echo.
echo Java 17 setup complete!
echo.

:check_javafx
REM Check for JavaFX SDK
set JAVAFX_SDK=C:\Program Files\Java\javafx-sdk-21.0.7
if exist "%JAVAFX_SDK%" (
    echo JavaFX 21.0.7 SDK already installed!
    goto :build_project
)

echo JavaFX 21.0.7 SDK not found. Downloading...
echo This may take a few minutes...

REM Download JavaFX SDK
powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://download2.gluonhq.com/openjfx/21.0.7/openjfx-21.0.7_windows-x64_bin-sdk.zip' -OutFile 'javafx-sdk.zip'}"

if not exist "javafx-sdk.zip" (
    echo Failed to download JavaFX SDK. Please download manually from:
    echo https://openjfx.io/
    pause
    exit /b 1
)

echo Extracting JavaFX SDK...
powershell -Command "Expand-Archive -Path 'javafx-sdk.zip' -DestinationPath 'C:\Program Files\Java' -Force"

echo JavaFX SDK setup complete!
echo.

:build_project
cd ..

echo Building project with full JavaFX 21 support...
call build_full.bat

if %ERRORLEVEL%==0 (
    echo.
    echo ========================================
    echo Setup Complete!
    echo ========================================
    echo.
    echo You can now run the application with full map functionality using:
    echo run_complete.bat
    echo.
    echo Or directly with:
    echo run_full.bat
    echo.
) else (
    echo.
    echo Build failed. Please check the error messages above.
    echo.
)

pause 