package com.alerts.system;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Database manager for MySQL operations in the Missing Person Alert System
 */
public class DatabaseManager {
    private static final String PROPERTIES_FILE = "database.properties";
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASSWORD;
    
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        loadDatabaseProperties();
    }
    
    private void loadDatabaseProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {
            if (input == null) {
                System.err.println("Unable to find " + PROPERTIES_FILE + " in classpath");
                // Try alternative paths
                String[] alternativePaths = {
                    "database_3306.properties",
                    "database_3308.properties",
                    "com/alerts/system/database.properties"
                };
                
                for (String path : alternativePaths) {
                    try (InputStream altInput = getClass().getClassLoader().getResourceAsStream(path)) {
                        if (altInput != null) {
                            System.out.println("Found database properties at: " + path);
                            Properties prop = new Properties();
                            prop.load(altInput);
                            
                            DB_URL = prop.getProperty("db.url", "jdbc:mysql://localhost:3306/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
                            DB_USER = prop.getProperty("db.username", "missing_person_user");
                            DB_PASSWORD = prop.getProperty("db.password", "your_secure_password_123");
                            return;
                        }
                    } catch (IOException e) {
                        System.err.println("Error loading " + path + ": " + e.getMessage());
                    }
                }
                
                // Fallback to default values
                System.out.println("Using default database configuration");
                DB_URL = "jdbc:mysql://localhost:3306/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
                DB_USER = "missing_person_user";
                DB_PASSWORD = "your_secure_password_123";
                return;
            }
            
            Properties prop = new Properties();
            prop.load(input);
            
            DB_URL = prop.getProperty("db.url", "jdbc:mysql://localhost:3306/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
            DB_USER = prop.getProperty("db.username", "missing_person_user");
            DB_PASSWORD = prop.getProperty("db.password", "your_secure_password_123");
            
        } catch (IOException e) {
            System.err.println("Error loading database properties: " + e.getMessage());
            // Fallback to default values
            DB_URL = "jdbc:mysql://localhost:3306/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            DB_USER = "missing_person_user";
            DB_PASSWORD = "your_secure_password_123";
        }
    }
    
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Get database connection
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                System.out.println("Attempting to connect to database: " + DB_URL);
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                connection.setAutoCommit(true);
                System.out.println("Database connection established successfully");
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                System.err.println("URL: " + DB_URL);
                System.err.println("User: " + DB_USER);
                System.err.println("Error Code: " + e.getErrorCode());
                System.err.println("SQL State: " + e.getSQLState());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Close database connection
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed successfully");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean isValid = conn != null && !conn.isClosed();
            if (isValid) {
                System.out.println("Database connection test successful");
            } else {
                System.err.println("Database connection test failed: connection is null or closed");
            }
            return isValid;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            return false;
        }
    }
    
    /**
     * Authenticate user
     */
    public Optional<Integer> authenticateUser(String username, String password) {
        String sql = "SELECT user_id FROM users WHERE username = ? AND password_hash = ? AND is_active = TRUE";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password); // In production, use proper password hashing
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                updateLastLogin(userId);
                return Optional.of(userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    /**
     * Update user's last login time
     */
    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get all missing persons
     */
    public List<MissingPerson> getAllMissingPersons() {
        List<MissingPerson> persons = new ArrayList<>();
        String sql = "SELECT * FROM missing_persons ORDER BY last_seen_date DESC";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                MissingPerson person = mapResultSetToMissingPerson(rs);
                persons.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persons;
    }
    
    /**
     * Get missing persons by status
     */
    public List<MissingPerson> getMissingPersonsByStatus(String status) {
        List<MissingPerson> persons = new ArrayList<>();
        String sql = "SELECT * FROM missing_persons WHERE status = ? ORDER BY last_seen_date DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                MissingPerson person = mapResultSetToMissingPerson(rs);
                persons.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persons;
    }
    
    /**
     * Search missing persons
     */
    public List<MissingPerson> searchMissingPersons(String query, int userId) {
        List<MissingPerson> persons = new ArrayList<>();
        String sql = "CALL SearchMissingPersons(?, ?)";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, query);
            stmt.setInt(2, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                MissingPerson person = mapResultSetToMissingPerson(rs);
                persons.add(person);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return persons;
    }
    
    /**
     * Add new missing person
     */
    public int addMissingPerson(MissingPerson person, int reportedBy) {
        String sql = "CALL AddMissingPerson(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setString(1, person.getName());
            stmt.setInt(2, person.getAge());
            stmt.setString(3, person.getGender());
            stmt.setString(4, person.getLastSeenLocation());
            stmt.setDate(5, java.sql.Date.valueOf(person.getLastSeenDate()));
            stmt.setString(6, person.getContactPerson());
            stmt.setString(7, person.getContactNumber());
            stmt.setString(8, person.getDescription());
            stmt.setString(9, person.getPhotoPath());
            stmt.setInt(10, reportedBy);
            
            System.out.println("Executing AddMissingPerson stored procedure for: " + person.getName());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int personId = rs.getInt("person_id");
                System.out.println("Successfully added missing person with ID: " + personId);
                return personId;
            } else {
                System.err.println("AddMissingPerson stored procedure did not return a person_id");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Error adding missing person: " + e.getMessage());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Person details: " + person.getName() + ", Age: " + person.getAge());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error adding missing person: " + e.getMessage());
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Update missing person status
     */
    public boolean updatePersonStatus(int personId, String status, LocalDate foundDate, 
                                    String foundLocation, String notes, int updatedBy) {
        String sql = "CALL UpdatePersonStatus(?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            
            stmt.setInt(1, personId);
            stmt.setString(2, status);
            stmt.setDate(3, foundDate != null ? java.sql.Date.valueOf(foundDate) : null);
            stmt.setString(4, foundLocation);
            stmt.setString(5, notes);
            stmt.setInt(6, updatedBy);
            
            stmt.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Remove missing person (soft delete by setting status to 'Deceased' or hard delete)
     */
    public boolean removeMissingPerson(MissingPerson person) {
        // First try to find the person by name and other details
        String findSql = "SELECT person_id FROM missing_persons WHERE name = ? AND age = ? AND last_seen_location = ? LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement findStmt = conn.prepareStatement(findSql)) {
            
            findStmt.setString(1, person.getName());
            findStmt.setInt(2, person.getAge());
            findStmt.setString(3, person.getLastSeenLocation());
            
            ResultSet rs = findStmt.executeQuery();
            if (rs.next()) {
                int personId = rs.getInt("person_id");
                
                // Delete the person (this will cascade to reports table)
                String deleteSql = "DELETE FROM missing_persons WHERE person_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                    deleteStmt.setInt(1, personId);
                    int rowsAffected = deleteStmt.executeUpdate();
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update missing person details
     */
    public boolean updateMissingPerson(MissingPerson person, int personId) {
        String sql = "UPDATE missing_persons SET name = ?, age = ?, gender = ?, last_seen_location = ?, " +
                    "last_seen_date = ?, contact_person = ?, contact_number = ?, description = ?, " +
                    "photo_path = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE person_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, person.getName());
            stmt.setInt(2, person.getAge());
            stmt.setString(3, person.getGender());
            stmt.setString(4, person.getLastSeenLocation());
            stmt.setDate(5, java.sql.Date.valueOf(person.getLastSeenDate()));
            stmt.setString(6, person.getContactPerson());
            stmt.setString(7, person.getContactNumber());
            stmt.setString(8, person.getDescription());
            stmt.setString(9, person.getPhotoPath());
            stmt.setString(10, person.getStatus());
            stmt.setInt(11, personId); // For WHERE clause
            
            System.out.println("Executing UPDATE missing_persons for person_id: " + personId);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected by update: " + rowsAffected);
            
            if (rowsAffected > 0) {
                System.out.println("Successfully updated missing person with person_id: " + personId);
                return true;
            } else {
                System.err.println("No rows were updated for person_id: " + personId);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Error updating missing person: " + e.getMessage());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Person ID: " + personId);
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            System.err.println("Unexpected error updating missing person: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user profile
     */
    public Optional<UserProfile> getUserProfile(int userId) {
        String sql = "SELECT username, email, phone, avatar_path FROM users WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                UserProfile profile = new UserProfile(
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("avatar_path")
                );
                return Optional.of(profile);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    /**
     * Update user profile
     */
    public boolean updateUserProfile(int userId, String email, String phone, String avatarPath) {
        String sql = "UPDATE users SET email = ?, phone = ?, avatar_path = ?, updated_at = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, phone);
            stmt.setString(3, avatarPath);
            stmt.setInt(4, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get user notifications
     */
    public List<SystemNotification> getUserNotifications(int userId) {
        List<SystemNotification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notifications WHERE user_id = ? ORDER BY created_at DESC";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                SystemNotification notification = new SystemNotification(
                    rs.getInt("notification_id"),
                    rs.getString("title"),
                    rs.getString("message"),
                    rs.getString("notification_type"),
                    rs.getBoolean("is_read"),
                    rs.getTimestamp("created_at").toLocalDateTime()
                );
                notifications.add(notification);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    
    /**
     * Mark notification as read
     */
    public boolean markNotificationAsRead(int notificationId) {
        String sql = "UPDATE notifications SET is_read = TRUE WHERE notification_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, notificationId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get dashboard statistics
     */
    public DashboardStats getDashboardStats(int userId) {
        String sql = "SELECT * FROM user_dashboard_stats WHERE user_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new DashboardStats(
                    rs.getInt("total_reports"),
                    rs.getInt("active_cases"),
                    rs.getInt("resolved_cases"),
                    rs.getInt("unread_notifications")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new DashboardStats(0, 0, 0, 0);
    }
    
    /**
     * Map ResultSet to MissingPerson object
     */
    private MissingPerson mapResultSetToMissingPerson(ResultSet rs) throws SQLException {
        return new MissingPerson(
            rs.getInt("person_id"),
            rs.getString("name"),
            rs.getInt("age"),
            rs.getString("gender"),
            rs.getString("last_seen_location"),
            rs.getDate("last_seen_date").toLocalDate(),
            rs.getString("contact_person"),
            rs.getString("contact_number"),
            rs.getString("description"),
            rs.getString("photo_path"),
            rs.getString("status")
        );
    }
    
    /**
     * Register a new user
     */
    public boolean registerUser(String username, String email, String phone, String password) {
        String sql = "INSERT INTO users (username, email, phone, password_hash) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, password); // In production, hash the password!
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            // Likely duplicate username/email or other constraint violation
            System.err.println("Registration failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add missing person using direct SQL INSERT (fallback method)
     */
    public int addMissingPersonDirect(MissingPerson person, int reportedBy) {
        String sql = "INSERT INTO missing_persons (name, age, gender, last_seen_location, last_seen_date, " +
                    "contact_person, contact_number, description, photo_path, status, reported_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, person.getName());
            stmt.setInt(2, person.getAge());
            stmt.setString(3, person.getGender());
            stmt.setString(4, person.getLastSeenLocation());
            stmt.setDate(5, java.sql.Date.valueOf(person.getLastSeenDate()));
            stmt.setString(6, person.getContactPerson());
            stmt.setString(7, person.getContactNumber());
            stmt.setString(8, person.getDescription());
            stmt.setString(9, person.getPhotoPath());
            stmt.setString(10, person.getStatus());
            stmt.setInt(11, reportedBy);
            
            System.out.println("Executing direct INSERT for: " + person.getName());
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    int personId = rs.getInt(1);
                    System.out.println("Successfully added missing person with ID: " + personId);
                    
                    // Also add to reports table
                    addReportRecord(personId, reportedBy, "Initial", "Initial missing person report");
                    
                    return personId;
                }
            }
            
            System.err.println("Direct INSERT failed - no rows affected");
            return -1;
            
        } catch (SQLException e) {
            System.err.println("Error in direct INSERT: " + e.getMessage());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return -1;
        } catch (Exception e) {
            System.err.println("Unexpected error in direct INSERT: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Add a report record
     */
    private void addReportRecord(int personId, int reportedBy, String reportType, String reportDetails) {
        String sql = "INSERT INTO reports (person_id, reported_by, report_type, report_details) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, personId);
            stmt.setInt(2, reportedBy);
            stmt.setString(3, reportType);
            stmt.setString(4, reportDetails);
            
            stmt.executeUpdate();
            System.out.println("Added report record for person ID: " + personId);
            
        } catch (SQLException e) {
            System.err.println("Error adding report record: " + e.getMessage());
        }
    }
    
    /**
     * Find person ID by name
     */
    public int findPersonIdByName(String name) {
        String sql = "SELECT person_id FROM missing_persons WHERE name = ? LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("person_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // Inner classes for data transfer objects
    
    public static class UserProfile {
        private String username;
        private String email;
        private String phone;
        private String avatarPath;
        
        public UserProfile(String username, String email, String phone, String avatarPath) {
            this.username = username;
            this.email = email;
            this.phone = phone;
            this.avatarPath = avatarPath;
        }
        
        // Getters and setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAvatarPath() { return avatarPath; }
        public void setAvatarPath(String avatarPath) { this.avatarPath = avatarPath; }
    }
    
    public static class SystemNotification {
        private int notificationId;
        private String title;
        private String message;
        private String notificationType;
        private boolean isRead;
        private java.time.LocalDateTime createdAt;
        
        public SystemNotification(int notificationId, String title, String message, 
                                String notificationType, boolean isRead, java.time.LocalDateTime createdAt) {
            this.notificationId = notificationId;
            this.title = title;
            this.message = message;
            this.notificationType = notificationType;
            this.isRead = isRead;
            this.createdAt = createdAt;
        }
        
        // Getters and setters
        public int getNotificationId() { return notificationId; }
        public void setNotificationId(int notificationId) { this.notificationId = notificationId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getNotificationType() { return notificationType; }
        public void setNotificationType(String notificationType) { this.notificationType = notificationType; }
        public boolean isRead() { return isRead; }
        public void setRead(boolean read) { isRead = read; }
        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
    
    public static class DashboardStats {
        private int totalReports;
        private int activeCases;
        private int resolvedCases;
        private int unreadNotifications;
        
        public DashboardStats(int totalReports, int activeCases, int resolvedCases, int unreadNotifications) {
            this.totalReports = totalReports;
            this.activeCases = activeCases;
            this.resolvedCases = resolvedCases;
            this.unreadNotifications = unreadNotifications;
        }
        
        // Getters and setters
        public int getTotalReports() { return totalReports; }
        public void setTotalReports(int totalReports) { this.totalReports = totalReports; }
        public int getActiveCases() { return activeCases; }
        public void setActiveCases(int activeCases) { this.activeCases = activeCases; }
        public int getResolvedCases() { return resolvedCases; }
        public void setResolvedCases(int resolvedCases) { this.resolvedCases = resolvedCases; }
        public int getUnreadNotifications() { return unreadNotifications; }
        public void setUnreadNotifications(int unreadNotifications) { this.unreadNotifications = unreadNotifications; }
    }
} 