@echo off
echo Setting up JavaFX for Missing Person Alert System...

REM Create lib directory if it doesn't exist
if not exist "lib" mkdir lib

REM Download JavaFX 8 if not already present
if not exist "lib\jfxrt.jar" (
    echo Downloading JavaFX 8...
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/openjfx/javafx-controls/8.0.202/javafx-controls-8.0.202.jar' -OutFile 'lib\javafx-controls.jar'}"
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/openjfx/javafx-fxml/8.0.202/javafx-fxml-8.0.202.jar' -OutFile 'lib\javafx-fxml.jar'}"
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/openjfx/javafx-graphics/8.0.202/javafx-graphics-8.0.202.jar' -OutFile 'lib\javafx-graphics.jar'}"
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/openjfx/javafx-base/8.0.202/javafx-base-8.0.202.jar' -OutFile 'lib\javafx-base.jar'}"
    echo JavaFX downloaded successfully!
) else (
    echo JavaFX already exists in lib directory.
)

REM Download MySQL connector if not present
if not exist "lib\mysql-connector-java-8.0.33.jar" (
    echo Downloading MySQL Connector...
    powershell -Command "& {Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.33/mysql-connector-java-8.0.33.jar' -OutFile 'lib\mysql-connector-java-8.0.33.jar'}"
    echo MySQL Connector downloaded successfully!
) else (
    echo MySQL Connector already exists in lib directory.
)

echo Setup complete! You can now run the application using run_fixed.bat
pause 