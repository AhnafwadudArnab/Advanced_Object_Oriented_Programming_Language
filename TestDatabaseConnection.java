import java.sql.*;
import java.util.Properties;
import java.io.InputStream;

/**
 * Simple test script to verify database connection and basic operations
 */
public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("Testing Database Connection...");
        
        try {
            // Load database properties
            Properties props = new Properties();
            InputStream input = TestDatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties");
            
            if (input == null) {
                System.err.println("Could not find database.properties file");
                return;
            }
            
            props.load(input);
            
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            
            System.out.println("Connecting to: " + url);
            System.out.println("Username: " + user);
            
            // Test connection
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("✓ Database connection successful!");
                
                // Test basic queries
                testBasicQueries(conn);
                
            } catch (SQLException e) {
                System.err.println("✗ Database connection failed: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.err.println("✗ Error loading properties: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBasicQueries(Connection conn) throws SQLException {
        System.out.println("\nTesting basic queries...");
        
        // Test 1: Check if tables exist
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SHOW TABLES");
            System.out.println("✓ Tables found:");
            while (rs.next()) {
                System.out.println("  - " + rs.getString(1));
            }
        }
        
        // Test 2: Count users
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users");
            if (rs.next()) {
                System.out.println("✓ Users count: " + rs.getInt(1));
            }
        }
        
        // Test 3: Count missing persons
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM missing_persons");
            if (rs.next()) {
                System.out.println("✓ Missing persons count: " + rs.getInt(1));
            }
        }
        
        // Test 4: Test authentication
        try (PreparedStatement stmt = conn.prepareStatement("SELECT user_id FROM users WHERE username = ? AND password_hash = ?")) {
            stmt.setString(1, "admin");
            stmt.setString(2, "$2a$10$example_hash_admin");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("✓ Authentication test passed");
            } else {
                System.out.println("⚠ Authentication test: No matching user found");
            }
        }
        
        System.out.println("\n✓ All database tests completed successfully!");
    }
} 