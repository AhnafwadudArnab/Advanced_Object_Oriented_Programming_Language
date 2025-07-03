# Missing Person Alert System - Database Setup Guide

## Overview
This guide will help you set up the MySQL database for the Missing Person Alert System Java application.

## Prerequisites
- MySQL Server 8.0 or higher
- Java 8 or higher
- MySQL Connector/J (JDBC driver)

## Database Setup Instructions

### 1. Install MySQL Server
If you haven't installed MySQL Server yet:
- **Windows**: Download and install MySQL Server from [MySQL Downloads](https://dev.mysql.com/downloads/mysql/)
- **macOS**: Use Homebrew: `brew install mysql`
- **Linux**: `sudo apt-get install mysql-server` (Ubuntu/Debian) or `sudo yum install mysql-server` (CentOS/RHEL)

### 2. Start MySQL Service
```bash
# Windows
net start mysql

# macOS/Linux
sudo systemctl start mysql
# or
sudo service mysql start
```

### 3. Access MySQL Command Line
```bash
mysql -u root -p
```

### 4. Create Database and User
Run the following SQL commands in MySQL:

```sql
-- Create the database
CREATE DATABASE missing_person_alert_system;

-- Create a dedicated user for the application
CREATE USER 'missing_person_user'@'localhost' IDENTIFIED BY 'your_secure_password';

-- Grant necessary permissions
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE ON missing_person_alert_system.* TO 'missing_person_user'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;
```

### 5. Import Database Schema
Execute the database schema file:

```bash
mysql -u missing_person_user -p missing_person_alert_system < database_schema.sql
```

Or copy and paste the contents of `database_schema.sql` into your MySQL client.

### 6. Verify Database Setup
```sql
USE missing_person_alert_system;

-- Check tables
SHOW TABLES;

-- Check sample data
SELECT * FROM users;
SELECT * FROM missing_persons;
```

## Java Application Configuration

### 1. Add MySQL Connector Dependency
Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
```

### 2. Update Database Configuration
Edit `src/main/resources/database.properties` with your actual database credentials:

```properties
db.url=jdbc:mysql://localhost:3306/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=missing_person_user
db.password=your_actual_secure_password
```

### 3. Update DatabaseManager Class
If needed, update the connection details in `DatabaseManager.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/missing_person_alert_system";
private static final String DB_USER = "missing_person_user";
private static final String DB_PASSWORD = "your_actual_secure_password";
```

## Database Schema Details

### Tables Overview

1. **users** - User authentication and profiles
   - `user_id` (Primary Key)
   - `username`, `email`, `password_hash`
   - `phone`, `avatar_path`
   - `created_at`, `updated_at`, `last_login`

2. **missing_persons** - Main missing person data
   - `person_id` (Primary Key)
   - `name`, `age`, `gender`
   - `last_seen_location`, `last_seen_date`
   - `contact_person`, `contact_number`
   - `description`, `photo_path`, `status`
   - `reported_by` (Foreign Key to users)

3. **reports** - Track who reported missing persons
   - `report_id` (Primary Key)
   - `person_id`, `reported_by` (Foreign Keys)
   - `report_date`, `report_type`, `report_details`

4. **notifications** - System notifications
   - `notification_id` (Primary Key)
   - `user_id` (Foreign Key)
   - `title`, `message`, `notification_type`
   - `is_read`, `created_at`

5. **search_history** - Track user searches
   - `search_id` (Primary Key)
   - `user_id` (Foreign Key)
   - `search_query`, `search_date`, `results_count`

6. **user_sessions** - Track active sessions
   - `session_id` (Primary Key)
   - `user_id` (Foreign Key)
   - `login_time`, `logout_time`, `ip_address`

### Views
- `active_missing_persons` - Shows only missing persons
- `recent_reports` - Shows recent reports with person and reporter names
- `user_dashboard_stats` - Dashboard statistics for users

### Stored Procedures
- `AddMissingPerson()` - Adds new missing person with automatic report creation
- `UpdatePersonStatus()` - Updates person status with notification
- `SearchMissingPersons()` - Searches with logging

## Integration with Existing Code

### 1. Update MissingPersonService
Replace the static data with database calls:

```java
public class MissingPersonService {
    private static DatabaseManager dbManager = DatabaseManager.getInstance();
    
    public static ObservableList<MissingPerson> getAllMissingPersons() {
        List<MissingPerson> persons = dbManager.getAllMissingPersons();
        return FXCollections.observableArrayList(persons);
    }
    
    public static void addMissingPerson(MissingPerson person) {
        // Assuming current user ID is 1 for demo
        int personId = dbManager.addMissingPerson(person, 1);
        if (personId > 0) {
            // Success
        }
    }
}
```

### 2. Update LoginController
Replace hardcoded authentication:

```java
@FXML
private void handleLogin(ActionEvent event) {
    String username = usernameField.getText();
    String password = passwordField.getText();
    
    DatabaseManager dbManager = DatabaseManager.getInstance();
    Optional<Integer> userId = dbManager.authenticateUser(username, password);
    
    if (userId.isPresent()) {
        Main.currentUserId = userId.get();
        // Proceed with login
    } else {
        notificationManager.showNotification("Invalid credentials", NotificationManager.NotificationType.ERROR);
    }
}
```

## Security Considerations

### 1. Password Hashing
In production, use proper password hashing:
```java
// Use BCrypt or similar
String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
```

### 2. Connection Security
- Use SSL connections in production
- Store credentials securely (environment variables, encrypted config files)
- Use connection pooling for better performance

### 3. SQL Injection Prevention
The `DatabaseManager` class uses prepared statements to prevent SQL injection.

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Check if MySQL service is running
   - Verify port 3306 is not blocked
   - Check firewall settings

2. **Access Denied**
   - Verify username and password
   - Check user permissions
   - Ensure user can connect from localhost

3. **Driver Not Found**
   - Add MySQL Connector/J to classpath
   - Check Maven dependencies

4. **Timezone Issues**
   - Add `serverTimezone=UTC` to connection URL
   - Set MySQL timezone: `SET GLOBAL time_zone = '+00:00';`

### Testing Database Connection
Create a simple test class:

```java
public class DatabaseTest {
    public static void main(String[] args) {
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            Connection conn = dbManager.getConnection();
            System.out.println("Database connection successful!");
            conn.close();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

## Performance Optimization

### 1. Indexes
The schema includes indexes on frequently queried columns:
- `status` on missing_persons
- `last_seen_date` on missing_persons
- `user_id` on notifications

### 2. Connection Pooling
Consider using HikariCP or similar for connection pooling:

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>4.0.3</version>
</dependency>
```

### 3. Query Optimization
- Use LIMIT for large result sets
- Implement pagination for search results
- Use appropriate WHERE clauses

## Backup and Maintenance

### 1. Regular Backups
```bash
mysqldump -u root -p missing_person_alert_system > backup_$(date +%Y%m%d).sql
```

### 2. Database Maintenance
```sql
-- Analyze tables for better query performance
ANALYZE TABLE missing_persons, users, reports;

-- Check table status
CHECK TABLE missing_persons;
```

## Support
For database-related issues:
1. Check MySQL error logs
2. Verify connection settings
3. Test with simple queries first
4. Ensure all dependencies are properly configured 