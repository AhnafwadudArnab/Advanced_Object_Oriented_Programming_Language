# MySQL Database Setup Guide for Missing Person Alert System

## Prerequisites
- Windows 10/11
- MySQL Server 8.0 or higher
- Java 11 or higher
- Maven (for building the project)

## Step 1: Install MySQL Server

### Option A: Download MySQL Installer (Recommended)
1. Go to [MySQL Downloads](https://dev.mysql.com/downloads/installer/)
2. Download "MySQL Installer for Windows"
3. Run the installer as Administrator
4. Choose "Developer Default" or "Server only" installation
5. Follow the installation wizard
6. **Important**: Remember the root password you set during installation

### Option B: Using Chocolatey (if you have it installed)
```powershell
choco install mysql
```

## Step 2: Start MySQL Service

### Check if MySQL is running:
```powershell
# Check MySQL service status
Get-Service -Name "MySQL*"

# Start MySQL service if not running
Start-Service -Name "MySQL80"  # or "MySQL" depending on your version
```

### Alternative: Using Services.msc
1. Press `Win + R`, type `services.msc`, press Enter
2. Find "MySQL80" or "MySQL" service
3. Right-click and select "Start"

## Step 3: Access MySQL Command Line

### Option A: Using MySQL Command Line Client
1. Press `Win + R`, type `cmd`, press Enter
2. Navigate to MySQL bin directory (usually `C:\Program Files\MySQL\MySQL Server 8.0\bin`)
3. Run: `mysql -u root -p`
4. Enter your root password when prompted

### Option B: Using MySQL Workbench (GUI)
1. Open MySQL Workbench
2. Create a new connection to `localhost:3306`
3. Connect using root credentials

## Step 4: Set Up Database and User

### Run the setup script:
```sql
-- Copy and paste this into your MySQL client
SOURCE C:/Users/ahana/Downloads/AOOP Final Update/Missing_Person_Alert_System/setup_mysql_database.sql;
```

### Or run manually:
```sql
-- Create the database
CREATE DATABASE IF NOT EXISTS missing_person_alert_system
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Create a dedicated user
CREATE USER IF NOT EXISTS 'missing_person_user'@'localhost' IDENTIFIED BY 'your_secure_password_123';

-- Grant permissions
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE, CREATE, ALTER, INDEX, REFERENCES 
ON missing_person_alert_system.* TO 'missing_person_user'@'localhost';

-- Apply changes
FLUSH PRIVILEGES;
```

## Step 5: Import Database Schema

### Option A: Using command line
```powershell
# Navigate to your project directory
cd "C:\Users\ahana\Downloads\AOOP Final Update\Missing_Person_Alert_System"

# Import schema using the new user
mysql -u missing_person_user -p missing_person_alert_system < database_schema.sql
```

### Option B: Using MySQL client
```sql
-- Switch to the database
USE missing_person_alert_system;

-- Import the schema (copy and paste the contents of database_schema.sql)
SOURCE database_schema.sql;
```

## Step 6: Update Application Configuration

### Update database.properties:
Edit `src/main/resources/database.properties`:

```properties
# Database URL
db.url=jdbc:mysql://localhost:3306/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# Database credentials
db.username=missing_person_user
db.password=your_secure_password_123

# Connection pool settings
db.initial.pool.size=5
db.max.pool.size=20
db.min.pool.size=5

# Connection timeout settings
db.connection.timeout=30000
db.idle.timeout=600000
db.max.lifetime=1800000

# Database driver
db.driver=com.mysql.cj.jdbc.Driver
```

## Step 7: Verify Database Setup

### Test the connection:
```sql
-- Connect as the application user
mysql -u missing_person_user -p missing_person_alert_system

-- Check tables
SHOW TABLES;

-- Check sample data
SELECT * FROM users;
SELECT * FROM missing_persons;
SELECT * FROM notifications;

-- Check views
SHOW FULL TABLES WHERE Table_type = 'VIEW';

-- Check stored procedures
SHOW PROCEDURE STATUS WHERE Db = 'missing_person_alert_system';
```

## Step 8: Build and Run the Application

### Build the project:
```powershell
# Navigate to project directory
cd "C:\Users\ahana\Downloads\AOOP Final Update\Missing_Person_Alert_System"

# Clean and compile
mvn clean compile

# Run the application
mvn javafx:run
```

## Troubleshooting

### Common Issues:

1. **"Access denied for user" error**
   - Check if the user exists: `SELECT User, Host FROM mysql.user;`
   - Recreate user if needed: `DROP USER 'missing_person_user'@'localhost';` then recreate

2. **"Connection refused" error**
   - Check if MySQL service is running
   - Verify port 3306 is not blocked by firewall

3. **"Driver not found" error**
   - Ensure MySQL Connector/J is in your classpath
   - Check pom.xml has the correct dependency

4. **"Unknown database" error**
   - Verify database exists: `SHOW DATABASES;`
   - Create if missing: `CREATE DATABASE missing_person_alert_system;`

### Useful MySQL Commands:
```sql
-- Show all databases
SHOW DATABASES;

-- Show all users
SELECT User, Host FROM mysql.user;

-- Show user privileges
SHOW GRANTS FOR 'missing_person_user'@'localhost';

-- Show table structure
DESCRIBE users;
DESCRIBE missing_persons;

-- Show table data
SELECT * FROM users LIMIT 5;
SELECT * FROM missing_persons LIMIT 5;
```

## Security Notes

1. **Change the default password** in production
2. **Use environment variables** for database credentials
3. **Enable SSL** for production connections
4. **Restrict user permissions** to only necessary operations
5. **Regular backups** of your database

## Database Schema Overview

The database includes:
- **6 main tables**: users, missing_persons, reports, notifications, search_history, user_sessions
- **3 views**: active_missing_persons, recent_reports, user_dashboard_stats
- **3 stored procedures**: AddMissingPerson, UpdatePersonStatus, SearchMissingPersons
- **Sample data** for testing

## Next Steps

1. Test the application login with sample credentials:
   - Username: `admin`, Password: `admin`
   - Username: `user`, Password: `user`

2. Explore the dashboard and features

3. Add real missing person data

4. Customize the application as needed 