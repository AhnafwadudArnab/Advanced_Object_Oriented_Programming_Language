-- Test Database Connection and Data for Missing Person Alert System
-- Run this script to verify your database setup

USE missing_person_alert_system;

-- Test 1: Check if all tables exist
SELECT 'Checking tables...' as test;
SHOW TABLES;

-- Test 2: Check table structures
SELECT 'Checking table structures...' as test;
DESCRIBE users;
DESCRIBE missing_persons;
DESCRIBE reports;
DESCRIBE notifications;

-- Test 3: Check sample data
SELECT 'Checking sample data...' as test;

-- Users table
SELECT 'Users:' as table_name;
SELECT user_id, username, email, created_at FROM users;

-- Missing persons table
SELECT 'Missing Persons:' as table_name;
SELECT person_id, name, age, gender, last_seen_location, status FROM missing_persons;

-- Reports table
SELECT 'Reports:' as table_name;
SELECT report_id, person_id, report_type, report_date FROM reports;

-- Notifications table
SELECT 'Notifications:' as table_name;
SELECT notification_id, title, notification_type, is_read FROM notifications;

-- Test 4: Check views
SELECT 'Checking views...' as test;
SHOW FULL TABLES WHERE Table_type = 'VIEW';

-- Test active missing persons view
SELECT 'Active Missing Persons View:' as view_name;
SELECT * FROM active_missing_persons;

-- Test recent reports view
SELECT 'Recent Reports View:' as view_name;
SELECT * FROM recent_reports LIMIT 5;

-- Test 5: Check stored procedures
SELECT 'Checking stored procedures...' as test;
SHOW PROCEDURE STATUS WHERE Db = 'missing_person_alert_system';

-- Test 6: Test user authentication (sample)
SELECT 'Testing user authentication...' as test;
SELECT user_id, username, email FROM users WHERE username = 'admin';

-- Test 7: Count records
SELECT 'Record counts:' as test;
SELECT 
    (SELECT COUNT(*) FROM users) as users_count,
    (SELECT COUNT(*) FROM missing_persons) as missing_persons_count,
    (SELECT COUNT(*) FROM reports) as reports_count,
    (SELECT COUNT(*) FROM notifications) as notifications_count;

-- Test 8: Check indexes
SELECT 'Checking indexes...' as test;
SHOW INDEX FROM missing_persons;
SHOW INDEX FROM users;

-- Test 9: Test foreign key relationships
SELECT 'Testing foreign key relationships...' as test;
SELECT 
    mp.person_id,
    mp.name,
    u.username as reported_by_user
FROM missing_persons mp
LEFT JOIN users u ON mp.reported_by = u.user_id;

-- Success message
SELECT 'Database connection and data verification completed successfully!' as status; 