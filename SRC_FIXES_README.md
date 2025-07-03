# Missing Person Alert System - Source Code Fixes

## Issues Fixed

### 1. JavaFX Configuration
- **Problem**: JavaFX dependencies were not properly configured for Java 8
- **Fix**: Updated `pom.xml` to use Java 8 compatible configuration
- **Alternative**: Created `build.bat` and `run.bat` scripts that work with Java 8 + JavaFX

### 2. Database Properties Loading
- **Problem**: DatabaseManager couldn't find database properties file
- **Fix**: Enhanced `DatabaseManager.java` to try multiple paths for database properties:
  - `database.properties`
  - `database_3306.properties` 
  - `database_3308.properties`
  - `com/alerts/system/database.properties`
- **Fallback**: Uses default database configuration if no properties file is found

### 3. FXML Resource Loading
- **Problem**: Main class couldn't load FXML files reliably
- **Fix**: Enhanced `Main.java` to try multiple paths for FXML files:
  - Direct filename (e.g., "LoginPage.fxml")
  - With package path (e.g., "/com/alerts/system/LoginPage.fxml")
  - With relative package path (e.g., "com/alerts/system/LoginPage.fxml")

### 4. Missing FXML Elements
- **Problem**: DashboardController referenced `totalDeceasedLabel` that doesn't exist in FXML
- **Fix**: Removed the reference to the non-existent label from `DashboardController.java`

## How to Run the Application

### Option 1: Using Batch Files (Recommended for Java 8)
1. Ensure Java 8 with JavaFX is installed
2. Run `build.bat` to compile the project
3. Run `run.bat` to start the application

### Option 2: Using Maven (Requires Java 17+)
1. Install Maven
2. Run `mvn clean compile` to build
3. Run `mvn javafx:run` to start the application

### Option 3: Manual Compilation
```bash
# Set JavaFX classpath
set JAVAFX_PATH=%JAVA_HOME%\lib\ext\jfxrt.jar

# Compile
javac -cp "%JAVAFX_PATH%;mysql-connector-java-8.0.33.jar" -d target/classes src/main/java/com/alerts/system/*.java

# Copy resources
xcopy /E /I /Y src\main\resources target\classes

# Run
java -cp "%JAVAFX_PATH%;mysql-connector-java-8.0.33.jar;target/classes" com.alerts.system.Main
```

## Database Setup
1. Ensure MySQL is running on port 3306 or 3308
2. Create database: `missing_person_alert_system`
3. Create user: `missing_person_user` with password: `your_secure_password_123`
4. Run the SQL scripts in the root directory to create tables

## File Structure
```
src/
├── main/
│   ├── java/com/alerts/system/
│   │   ├── Main.java                    # Application entry point
│   │   ├── DatabaseManager.java         # Database operations
│   │   ├── LoginController.java         # Login page controller
│   │   ├── DashboardController.java     # Dashboard controller
│   │   ├── MissingPerson.java           # Data model
│   │   └── ...                          # Other controllers
│   └── resources/com/alerts/system/
│       ├── LoginPage.fxml               # Login UI
│       ├── Dashboard.fxml               # Dashboard UI
│       ├── styles.css                   # Styling
│       └── ...                          # Other FXML files
```

## Troubleshooting

### JavaFX Not Found
- Ensure Java 8 with JavaFX is installed
- Check that `%JAVA_HOME%\lib\ext\jfxrt.jar` exists

### Database Connection Issues
- Verify MySQL is running
- Check database credentials in properties files
- Ensure database and tables exist

### FXML Loading Issues
- Verify FXML files are in the correct location
- Check that controller classes match FXML declarations

### Compilation Errors
- Ensure all required JAR files are in classpath
- Check Java version compatibility
- Verify all source files are in correct package structure 