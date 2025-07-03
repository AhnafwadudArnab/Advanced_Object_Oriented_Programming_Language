-- Missing Person Alert System Database Schema
-- MySQL Database for Missing Person Alert System

-- Create the database
CREATE DATABASE IF NOT EXISTS missing_person_alert_system;
USE missing_person_alert_system;

-- Users table for authentication and profiles
CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_path VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL
);

-- Missing persons table (main data)
CREATE TABLE missing_persons (
    person_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    age INT NOT NULL,
    gender ENUM('Male', 'Female', 'Other') NOT NULL,
    last_seen_location VARCHAR(255) NOT NULL,
    last_seen_date DATE NOT NULL,
    contact_person VARCHAR(100) NOT NULL,
    contact_number VARCHAR(20) NOT NULL,
    description TEXT,
    photo_path VARCHAR(255),
    status ENUM('Missing', 'Found', 'Deceased') DEFAULT 'Missing',
    reported_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    found_date DATE NULL,
    found_location VARCHAR(255) NULL,
    notes TEXT,
    FOREIGN KEY (reported_by) REFERENCES users(user_id) ON DELETE SET NULL
);

-- Reports table to track who reported missing persons
CREATE TABLE reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    person_id INT NOT NULL,
    reported_by INT NOT NULL,
    report_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    report_type ENUM('Initial', 'Update', 'Found') DEFAULT 'Initial',
    report_details TEXT,
    contact_info VARCHAR(255),
    is_anonymous BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (person_id) REFERENCES missing_persons(person_id) ON DELETE CASCADE,
    FOREIGN KEY (reported_by) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Notifications table for system notifications
CREATE TABLE notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    title VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    notification_type ENUM('INFO', 'SUCCESS', 'WARNING', 'ERROR') DEFAULT 'INFO',
    is_read BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Search history table to track user searches
CREATE TABLE search_history (
    search_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    search_query VARCHAR(255) NOT NULL,
    search_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    results_count INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- User sessions table for tracking active sessions
CREATE TABLE user_sessions (
    session_id VARCHAR(255) PRIMARY KEY,
    user_id INT NOT NULL,
    login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    logout_time TIMESTAMP NULL,
    ip_address VARCHAR(45),
    user_agent TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Create indexes for better performance
CREATE INDEX idx_missing_persons_status ON missing_persons(status);
CREATE INDEX idx_missing_persons_last_seen_date ON missing_persons(last_seen_date);
CREATE INDEX idx_missing_persons_location ON missing_persons(last_seen_location);
CREATE INDEX idx_reports_person_id ON reports(person_id);
CREATE INDEX idx_reports_date ON reports(report_date);
CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_read ON notifications(is_read);
CREATE INDEX idx_search_history_user_id ON search_history(user_id);
CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);

-- Insert sample data

-- Sample users
INSERT INTO users (username, email, password_hash, phone) VALUES
('admin', 'admin@missingperson.com', '$2a$10$example_hash_admin', '+1234567890'),
('user', 'user@email.com', '$2a$10$example_hash_user', '+8801000000000'),
('reporter1', 'reporter1@email.com', '$2a$10$example_hash_reporter1', '+1555123456');

-- Sample missing persons (matching the data from MissingPersonService)
USE missing_person_alert_system;
INSERT INTO missing_persons (name, age, gender, last_seen_location, last_seen_date, contact_person, contact_number, description, photo_path, status, reported_by) VALUES
('Alice Wonderland', 10, 'Female', 'Wonderland Park', '2023-10-20', 'White Rabbit', '555-111-2222', 'Wearing a blue dress and a white apron. Has blonde hair.', '/com/alerts/system/placeholder_profile.jpg', 'Missing', 1),
('Bob The Builder', 45, 'Male', 'Construction Site, Downtown', '2024-01-05', 'Wendy', '555-333-4444', 'Wearing a yellow hard hat and orange overalls.', '/com/alerts/system/placeholder_profile.jpg', 'Missing', 1),
('Charlie Chaplin', 88, 'Male', 'Old Film Studio', '2022-07-01', 'Usher', '555-555-6666', 'Known for his small mustache, bowler hat, and cane.', '/com/alerts/system/placeholder_profile.jpg', 'Found', 1),

('Riya Das', 12, 'Female', 'Dhanmondi Lake, Dhaka', '2024-03-10', 'Soma Das', '01711112222', 'Wearing a red frock, short hair.', '/com/alerts/system/placeholder_profile.jpg', 'Found', 2),
('Arif Hossain', 9, 'Male', 'Mirpur Zoo, Dhaka', '2024-02-18', 'Jamal Hossain', '01722223333', 'Wearing blue jeans and a white t-shirt.', '/com/alerts/system/placeholder_profile.jpg', 'Missing', 2),
('Mitu Akter', 11, 'Female', 'Chittagong Railway Station', '2024-01-25', 'Shirin Akter', '01888889999', 'Last seen with a pink school bag.', '/com/alerts/system/placeholder_profile.jpg', 'Found', 3),
('Sabbir Rahman', 13, 'Male', 'Rajshahi College Gate', '2024-04-02', 'Nazmul Rahman', '01999990000', 'Wearing a green shirt and black pants.', '/com/alerts/system/placeholder_profile.jpg', 'Missing', 1),
('Priya Saha', 8, 'Female', 'Sylhet Tea Garden', '2024-03-15', 'Rina Saha', '01755556666', 'Wearing a yellow frock, has a mole on left cheek.', '/com/alerts/system/placeholder_profile.jpg', 'Found', 2),
('Tanvir Alam', 10, 'Male', 'Khulna Bus Stand', '2024-02-28', 'Shafiqul Alam', '01812345678', 'Wearing a blue school uniform.', '/com/alerts/system/placeholder_profile.jpg', 'Missing', 3),
('Mousumi Khatun', 14, 'Female', 'Barisal Ferry Ghat', '2024-01-12', 'Hasina Khatun', '01787654321', 'Wearing a purple salwar kameez.', '/com/alerts/system/placeholder_profile.jpg', 'Found', 1),
('Rashedul Islam', 12, 'Male', 'Comilla Victoria Park', '2024-03-22', 'Farida Islam', '01912349876', 'Wearing a white panjabi.', '/com/alerts/system/placeholder_profile.jpg', 'Missing', 2),
('Sumaiya Akter', 13, 'Female', 'Jessore Railway Station', '2024-02-10', 'Jahanara Begum', '01876543210', 'Wearing a blue scarf.', '/com/alerts/system/placeholder_profile.jpg', 'Found', 3),
('Shuvo Roy', 9, 'Male', 'Rangpur Central Park', '2024-03-05', 'Biplob Roy', '01723456789', 'Wearing a red t-shirt and shorts.', '/com/alerts/system/placeholder_profile.jpg', 'Missing', 1),
('Nusrat Jahan', 11, 'Female', 'Mymensingh Bridge', '2024-04-01', 'Salma Jahan', '01987654321', 'Wearing a pink dress.', '/com/alerts/system/placeholder_profile.jpg', 'Found', 2),
('Fahim Ahmed', 10, 'Male', 'Gazipur Safari Park', '2024-03-18', 'Rubel Ahmed', '01823456789', 'Wearing a yellow cap.', '/com/alerts/system/placeholder_profile.jpg', 'Missing', 3),
('Laboni Das', 12, 'Female', 'Pabna Town Hall', '2024-02-20', 'Kamal Das', '01734567891', 'Wearing a green frock.', '/com/alerts/system/placeholder_profile.jpg', 'Found', 1);

-- Sample reports
INSERT INTO reports (person_id, reported_by, report_type, report_details) VALUES
(1, 1, 'Initial', 'Initial report of missing child'),
(2, 1, 'Initial', 'Initial report of missing construction worker'),
(3, 1, 'Initial', 'Initial report of missing elderly person'),
(3, 1, 'Found', 'Person found safe and sound');

-- Sample notifications
INSERT INTO notifications (user_id, title, message, notification_type) VALUES
(1, 'Welcome', 'Welcome to Missing Person Alert System!', 'INFO'),
(2, 'Profile Updated', 'Your profile has been updated successfully!', 'SUCCESS'),
(1, 'New Report', 'A new missing person has been reported in your area.', 'WARNING');

-- Create views for common queries

-- View for active missing persons
CREATE VIEW active_missing_persons AS
SELECT * FROM missing_persons 
WHERE status = 'Missing' 
ORDER BY last_seen_date DESC;

-- View for recent reports
CREATE VIEW recent_reports AS
SELECT r.*, mp.name as person_name, u.username as reporter_name
FROM reports r
JOIN missing_persons mp ON r.person_id = mp.person_id
JOIN users u ON r.reported_by = u.user_id
ORDER BY r.report_date DESC;

-- View for user dashboard statistics
CREATE VIEW user_dashboard_stats AS
SELECT 
    u.user_id,
    u.username,
    COUNT(DISTINCT mp.person_id) as total_reports,
    COUNT(DISTINCT CASE WHEN mp.status = 'Missing' THEN mp.person_id END) as active_cases,
    COUNT(DISTINCT CASE WHEN mp.status = 'Found' THEN mp.person_id END) as resolved_cases,
    COUNT(DISTINCT n.notification_id) as unread_notifications
FROM users u
LEFT JOIN missing_persons mp ON u.user_id = mp.reported_by
LEFT JOIN notifications n ON u.user_id = n.user_id AND n.is_read = FALSE
GROUP BY u.user_id, u.username;

-- Stored procedures for common operations

-- Procedure to add a new missing person
DELIMITER //
CREATE PROCEDURE AddMissingPerson(
    IN p_name VARCHAR(100),
    IN p_age INT,
    IN p_gender VARCHAR(10),
    IN p_last_seen_location VARCHAR(255),
    IN p_last_seen_date DATE,
    IN p_contact_person VARCHAR(100),
    IN p_contact_number VARCHAR(20),
    IN p_description TEXT,
    IN p_photo_path VARCHAR(255),
    IN p_reported_by INT
)
BEGIN
    -- Validate gender
    IF p_gender NOT IN ('Male', 'Female', 'Other') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid gender';
    END IF;

    DECLARE new_person_id INT;

    INSERT INTO missing_persons (
        name, age, gender, last_seen_location, last_seen_date,
        contact_person, contact_number, description, photo_path, reported_by
    )
    VALUES (
        p_name, p_age, p_gender, p_last_seen_location, p_last_seen_date,
        p_contact_person, p_contact_number, p_description, p_photo_path, p_reported_by
    );

    SET new_person_id = LAST_INSERT_ID();

    INSERT INTO reports (
        person_id, reported_by, report_type, report_details
    )
    VALUES (
        new_person_id, p_reported_by, 'Initial', 'Initial missing person report'
    );

    INSERT INTO notifications (
        user_id, title, message, notification_type
    )
    VALUES (
        1, 'New Missing Person Report',
        CONCAT('A new missing person has been reported: ', p_name), 'WARNING'
    );

    SELECT new_person_id AS person_id;
END //

CREATE PROCEDURE UpdatePersonStatus(
    IN p_person_id INT,
    IN p_status VARCHAR(20),
    IN p_found_date DATE,
    IN p_found_location VARCHAR(255),
    IN p_notes TEXT,
    IN p_updated_by INT
)
BEGIN
    IF p_status NOT IN ('Missing', 'Found', 'Deceased') THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid status';
    END IF;

    UPDATE missing_persons
    SET
        status = p_status,
        found_date = p_found_date,
        found_location = p_found_location,
        notes = p_notes,
        updated_at = CURRENT_TIMESTAMP
    WHERE person_id = p_person_id;

    INSERT INTO reports (
        person_id, reported_by, report_type, report_details
    )
    VALUES (
        p_person_id, p_updated_by,
        IF(p_status = 'Found', 'Found', 'Update'),
        CONCAT('Status updated to: ', p_status, IF(p_notes IS NOT NULL, CONCAT(' - Notes: ', p_notes), ''))
    );

    INSERT INTO notifications (
        user_id, title, message, notification_type
    )
    SELECT
        reported_by,
        'Status Update',
        CONCAT('Status updated for ', name, ' to: ', p_status),
        'INFO'
    FROM missing_persons
    WHERE person_id = p_person_id;
END //

CREATE PROCEDURE SearchMissingPersons(
    IN p_search_query VARCHAR(255),
    IN p_user_id INT
)
BEGIN
    INSERT INTO search_history (
        user_id, search_query, results_count
    )
    SELECT
        p_user_id, p_search_query, COUNT(*)
    FROM missing_persons
    WHERE
        name LIKE CONCAT('%', p_search_query, '%')
        OR last_seen_location LIKE CONCAT('%', p_search_query, '%')
        OR description LIKE CONCAT('%', p_search_query, '%');

    SELECT *
    FROM missing_persons
    WHERE
        name LIKE CONCAT('%', p_search_query, '%')
        OR last_seen_location LIKE CONCAT('%', p_search_query, '%')
        OR description LIKE CONCAT('%', p_search_query, '%')
    ORDER BY last_seen_date DESC;
END //

DELIMITER ;

-- Grant permissions (adjust as needed for your setup)
-- CREATE USER 'missing_person_user'@'localhost' IDENTIFIED BY 'your_secure_password';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON missing_person_alert_system.* TO 'missing_person_user'@'localhost';
-- FLUSH PRIVILEGES; 