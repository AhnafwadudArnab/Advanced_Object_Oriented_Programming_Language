# Database Port Configuration Guide

This guide explains all the alternative ways to change the database port in the Missing Person Alert System.

## Current Configuration

The system is now configured to use **port 3306** by default (the standard MySQL port).

## Alternative Ways to Change Database Port

### 1. **Direct Configuration File Edit** (Simplest)

Edit the main configuration file:
```bash
# Edit src/main/resources/database.properties
# Change the URL line to:
db.url=jdbc:mysql://localhost:3306/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
```

### 2. **Using Pre-configured Configuration Files**

The system includes multiple configuration files for different ports:

- `src/main/resources/database.properties` - Main configuration (currently set to 3306)
- `src/main/resources/database_3306.properties` - Port 3306 configuration
- `src/main/resources/database_3308.properties` - Port 3308 configuration

### 3. **Using Batch Files** (Windows)

#### Quick Switch to Port 3306:
```bash
# Double-click or run:
quick_switch_3306.bat
```

#### Quick Switch to Port 3308:
```bash
# Double-click or run:
quick_switch_3308.bat
```

#### Interactive Port Configuration:
```bash
# Double-click or run:
switch_to_port_3306.bat
```

### 4. **Using Java Utility Class**

#### Command Line Usage:
```bash
# Compile the project first
mvn clean compile

# Quick switch to port 3306
java -cp "target\classes" com.alerts.system.DatabasePortManager 3306

# Quick switch to port 3308
java -cp "target\classes" com.alerts.system.DatabasePortManager 3308

# Switch to default configuration
java -cp "target\classes" com.alerts.system.DatabasePortManager default

# Test current connection
java -cp "target\classes" com.alerts.system.DatabasePortManager test
```

#### Interactive Mode:
```bash
java -cp "target\classes" com.alerts.system.DatabasePortManager
```

### 5. **Programmatic Port Switching**

You can also switch ports programmatically in your Java code:

```java
// Switch to port 3306
DatabaseManager.switchToPort3306();

// Switch to port 3308
DatabaseManager.switchToPort3308();

// Switch to default configuration
DatabaseManager.switchToDefaultConfig();

// Set custom port
DatabaseManager.setCustomPort(3307);

// Get current configuration info
String currentUrl = DatabaseManager.getCurrentDatabaseUrl();
String configFile = DatabaseManager.getCurrentConfigurationFile();
```

### 6. **Environment Variable Override** (Future Enhancement)

You can set environment variables to override the configuration:
```bash
# Set environment variable (not yet implemented)
set DB_PORT=3306
set DB_HOST=localhost
```

## Port Configuration Methods Summary

| Method | Ease of Use | Flexibility | Description |
|--------|-------------|-------------|-------------|
| Edit Properties File | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | Direct file editing |
| Batch Files | ⭐⭐⭐⭐⭐ | ⭐⭐ | One-click switching |
| Java Utility | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Interactive and command-line |
| Programmatic | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | Full control in code |
| Environment Variables | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | System-wide configuration |

## Testing Your Configuration

After changing the port, test the connection:

```bash
# Using the utility
java -cp "target\classes" com.alerts.system.DatabasePortManager test

# Or programmatically
DatabaseManager dbManager = DatabaseManager.getInstance();
boolean isConnected = dbManager.testConnection();
```

## Troubleshooting

### Common Issues:

1. **Port Already in Use**
   - Check if MySQL is running on the desired port
   - Use `netstat -an | findstr :3306` to check port usage

2. **Connection Refused**
   - Ensure MySQL service is running
   - Verify the port number is correct
   - Check firewall settings

3. **Access Denied**
   - Verify database credentials in the properties file
   - Ensure the user has proper permissions

### MySQL Port Configuration:

To change MySQL's listening port:

1. **Edit MySQL Configuration:**
   ```ini
   # In my.ini or my.cnf
   [mysqld]
   port=3306
   ```

2. **Restart MySQL Service:**
   ```bash
   # Windows
   net stop MySQL80
   net start MySQL80
   
   # Linux/Mac
   sudo systemctl restart mysql
   ```

## Recommended Workflow

1. **For Development:** Use port 3306 (default MySQL port)
2. **For Testing:** Use port 3308 (alternative port)
3. **For Production:** Use the standard port 3306

## Files Created/Modified

- ✅ `src/main/resources/database.properties` - Updated to port 3306
- ✅ `src/main/resources/database_3306.properties` - Port 3306 configuration
- ✅ `src/main/resources/database_3308.properties` - Port 3308 configuration
- ✅ `src/main/java/com/alerts/system/DatabaseManager.java` - Enhanced with port switching methods
- ✅ `src/main/java/com/alerts/system/DatabasePortManager.java` - New utility class
- ✅ `quick_switch_3306.bat` - Quick switch to port 3306
- ✅ `quick_switch_3308.bat` - Quick switch to port 3308
- ✅ `switch_to_port_3306.bat` - Interactive port configuration

## Next Steps

1. Compile the project: `mvn clean compile`
2. Test the connection: `java -cp "target\classes" com.alerts.system.DatabasePortManager test`
3. Run the application: `mvn javafx:run`

The system now provides multiple flexible ways to configure the database port according to your needs! 