-- MySQL Database Setup for Missing Person Alert System
-- This script will create the database, user, and import the complete schema

-- Step 1: Create the database
CREATE DATABASE IF NOT EXISTS missing_person_alert_system
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

-- Step 2: Create a dedicated user for the application
CREATE USER IF NOT EXISTS 'missing_person_user'@'localhost' IDENTIFIED BY 'your_secure_password_123';

-- Step 3: Grant necessary permissions
GRANT SELECT, INSERT, UPDATE, DELETE, EXECUTE, CREATE, ALTER, INDEX, REFERENCES 
ON missing_person_alert_system.* TO 'missing_person_user'@'localhost';

-- Step 4: Apply privilege changes
FLUSH PRIVILEGES;

-- Step 5: Use the database
USE missing_person_alert_system;

-- Step 6: Import the complete schema from database_schema.sql
-- (The rest of the schema will be imported from the existing database_schema.sql file)

-- Display success message
SELECT 'Database setup completed successfully!' as status;
SELECT 'Database: missing_person_alert_system' as database_name;
SELECT 'User: missing_person_user' as username;
SELECT 'Please update the password in database.properties file' as reminder; 