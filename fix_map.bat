@echo off
echo ========================================
echo Map Functionality Fix Script
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

REM Check for Maven
mvn -version >nul 2>&1
set MAVEN_AVAILABLE=%ERRORLEVEL%

echo.
echo ========================================
echo Fixing Map Issues
echo ========================================
echo.

if %JAVA17_AVAILABLE%==0 (
    echo Java 17+ detected - using full JavaFX support
    echo.
    
    if %MAVEN_AVAILABLE%==0 (
        echo Maven available - building with JavaFX 21
        call mvn clean compile
        if %ERRORLEVEL%==0 (
            echo.
            echo Build successful! Running with full map support...
            call mvn javafx:run
        ) else (
            echo Build failed - trying alternative approach
            goto :java8_fallback
        )
    ) else (
        echo Maven not available - trying direct JavaFX approach
        goto :javafx_direct
    )
) else (
    echo Java 8 detected - using compatibility mode
    goto :java8_fallback
)

goto :end

:javafx_direct
echo.
echo Attempting direct JavaFX approach...
set JAVAFX_SDK=C:\Program Files\Java\javafx-sdk-21.0.7
if exist "%JAVAFX_SDK%" (
    echo JavaFX SDK found - running with module flags
    java --module-path "%JAVAFX_SDK%\lib" --add-modules javafx.controls,javafx.fxml,javafx.web --add-opens javafx.graphics/com.sun.javafx.sg.prism=ALL-UNNAMED --add-opens javafx.web/com.sun.webkit=ALL-UNNAMED -cp "target/classes;mysql-connector-java-8.0.33.jar" com.alerts.system.Main
) else (
    echo JavaFX SDK not found - falling back to Java 8
    goto :java8_fallback
)
goto :end

:java8_fallback
echo.
echo Using Java 8 compatibility mode...
echo This will show a text-based map view instead of interactive map
echo.

REM Compile for Java 8
if %MAVEN_AVAILABLE%==0 (
    echo Compiling with Maven for Java 8...
    call mvn clean compile -Dmaven.compiler.source=8 -Dmaven.compiler.target=8
) else (
    echo Compiling with javac for Java 8...
    if not exist "target\classes" mkdir target\classes
    javac -d target/classes -cp "mysql-connector-java-8.0.33.jar" src/main/java/com/alerts/system/*.java
)

if %ERRORLEVEL%==0 (
    echo.
    echo Compilation successful! Running application...
    java -cp "target/classes;mysql-connector-java-8.0.33.jar" com.alerts.system.Main
) else (
    echo.
    echo Compilation failed!
    echo Please check your Java installation and try again.
)

:end
echo.
echo ========================================
echo Map fix completed
echo ========================================
echo.
echo If you're still having issues:
echo 1. Install Java 17+ from https://adoptium.net/
echo 2. Install JavaFX 21 SDK from https://openjfx.io/
echo 3. Run this script again
echo.
pause 