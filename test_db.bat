@echo off
echo ========================================
echo Database Connection Test
echo ========================================
echo.

echo Compiling test script...
javac -cp ".;mysql-connector-java-8.0.33.jar" TestDatabaseConnection.java

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed. Please check if MySQL JDBC driver is available.
    echo.
    pause
    exit /b 1
)

echo.
echo Running database connection test...
echo.

java -cp ".;mysql-connector-java-8.0.33.jar" TestDatabaseConnection

echo.
echo Test completed.
pause 