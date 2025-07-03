# Missing Person Alert System - Database Integration

This document explains how to set up and use the database-integrated Missing Person Alert System.

## Overview

The Missing Person Alert System has been updated to use a MySQL database for persistent data storage. All user data, missing person reports, and system information are now stored in the database.

## Database Setup

### Prerequisites

1. **MySQL Server** (version 5.7 or higher) running on port 3308
2. **Java 8 or higher**
3. **Maven** (for building the project)

### Installation Steps

1. **Install MySQL Server**
   - Download and install MySQL Server from the official website
   - Configure MySQL to run on port 3308 (or update the configuration files to match your port)
   - Make sure MySQL service is running
   - Note down your root password

2. **Set up the Database**
   ```bash
   # Run the database setup script
   setup_database.bat
   ```
   
   Or manually:
   ```bash
   mysql -h localhost -P 3308 -u root -p < database_schema.sql
   ```

3. **Configure Database Connection**
   - Edit `src/main/resources/database.properties`
   - Update the following values:
     ```properties
     db.url=jdbc:mysql://localhost:3308/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
     db.username=missing_person_user
     db.password=your_secure_password_123
     ```

4. **Test Database Connection**
   ```bash
   # Run the database connection test
   test_db.bat
   ```

5. **Build and Run**
   ```bash
   mvn clean compile
   mvn exec:java -Dexec.mainClass="com.alerts.system.Main"
   ```

## Database Schema

The system uses the following main tables:

### Users Table
- Stores user authentication and profile information
- Includes username, email, password hash, phone, avatar path

### Missing Persons Table
- Stores all missing person reports
- Includes personal details, last seen information, contact details
- Tracks status (Missing, Found, Deceased)

### Reports Table
- Tracks who reported each missing person
- Includes report types and details

### Notifications Table
- Stores system notifications for users
- Includes notification types and read status

## Features

### User Authentication
- Database-based user authentication
- Secure password storage (hashed)
- User session management

### Missing Person Management
- **Add New Reports**: Create new missing person alerts
- **Edit Reports**: Update existing missing person information
- **Delete Reports**: Remove missing person records
- **Status Updates**: Change status (Missing → Found/Deceased)
- **Search**: Search through missing person records

### User Profiles
- **View Profile**: Display user information
- **Edit Profile**: Update email, phone, avatar
- **Password Change**: Update user password (TODO)

### Dashboard
- **Statistics**: View summary of missing persons by status
- **Recent Reports**: Display latest missing person reports
- **Quick Actions**: Access to common functions

## Database Operations

### Adding a Missing Person
1. Navigate to "New Alert" from dashboard
2. Fill in all required fields
3. Upload photo (optional)
4. Submit report
5. Data is saved to `missing_persons` table

### Updating Status
1. View person details
2. Select new status from dropdown
3. Confirm status change
4. Database is updated with new status and timestamp

### User Profile Updates
1. Navigate to Profile page
2. Edit information
3. Save changes
4. Database is updated with new profile information

## Error Handling

The system includes comprehensive error handling:

- **Database Connection Errors**: Graceful fallback with user notifications
- **Authentication Failures**: Clear error messages for login issues
- **Data Validation**: Form validation before database operations
- **Transaction Management**: Proper rollback on database errors

## Security Features

- **Password Hashing**: Passwords are hashed before storage
- **SQL Injection Prevention**: Prepared statements for all database queries
- **User Session Management**: Proper session tracking
- **Input Validation**: Client and server-side validation

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Check if MySQL service is running
   - Verify database credentials in `database.properties`
   - Ensure database exists: `missing_person_alert_system`

2. **Authentication Errors**
   - Verify user exists in database
   - Check password is correct
   - Ensure user account is active

3. **Data Not Loading**
   - Check database connection
   - Verify tables exist and have data
   - Check application logs for errors

### Database Maintenance

```sql
-- Check database status
SHOW DATABASES;
USE missing_person_alert_system;
SHOW TABLES;

-- Check user permissions
SHOW GRANTS FOR 'missing_person_user'@'localhost';

-- Backup database
mysqldump -u root -p missing_person_alert_system > backup.sql

-- Restore database
mysql -u root -p missing_person_alert_system < backup.sql
```

## Performance Considerations

- **Connection Pooling**: Database connections are managed efficiently
- **Indexed Queries**: Database indexes on frequently searched fields
- **Asynchronous Operations**: Database operations run in background threads
- **Caching**: Frequently accessed data is cached where appropriate

## Future Enhancements

- [ ] User preferences storage in database
- [ ] Advanced search with filters
- [ ] Report analytics and statistics
- [ ] Email notifications
- [ ] Mobile app integration
- [ ] Real-time updates via WebSocket

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review application logs
3. Verify database configuration
4. Contact system administrator

---

**Note**: This system is designed for educational and demonstration purposes. For production use, additional security measures and proper deployment procedures should be implemented. 