import java.sql.*;
import java.time.LocalDate;

public class TestDatabaseFix {
    public static void main(String[] args) {
        System.out.println("Testing Database Fix for Missing Person Alert System");
        System.out.println("==================================================");
        
        try {
            // Test database connection
            System.out.println("1. Testing database connection...");
            String url = "jdbc:mysql://localhost:3306/missing_person_alert_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
            String user = "missing_person_user";
            String password = "your_secure_password_123";
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✓ Database connection successful!");
            
            // Test the new INSERT approach
            System.out.println("\n2. Testing INSERT approach (replacing stored procedure)...");
            
            String sql = "INSERT INTO missing_persons (name, age, gender, last_seen_location, last_seen_date, " +
                        "contact_person, contact_number, description, photo_path, reported_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, "Test Person");
                stmt.setInt(2, 25);
                stmt.setString(3, "Male");
                stmt.setString(4, "Test Location");
                stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
                stmt.setString(6, "Test Contact");
                stmt.setString(7, "123-456-7890");
                stmt.setString(8, "Test description");
                stmt.setString(9, "/test/path.jpg");
                stmt.setInt(10, 1);
                
                System.out.println("Executing INSERT for test person...");
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    ResultSet rs = stmt.getGeneratedKeys();
                    if (rs.next()) {
                        int personId = rs.getInt(1);
                        System.out.println("✓ Successfully added test person with ID: " + personId);
                        
                        // Clean up - delete the test record
                        String deleteSql = "DELETE FROM missing_persons WHERE person_id = ?";
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                            deleteStmt.setInt(1, personId);
                            deleteStmt.executeUpdate();
                            System.out.println("✓ Test record cleaned up successfully");
                        }
                    }
                }
            }
            
            // Test creating a report entry
            System.out.println("\n3. Testing report entry creation...");
            String reportSql = "INSERT INTO reports (person_id, reported_by, report_type, report_details) VALUES (?, ?, 'Initial', ?)";
            try (PreparedStatement stmt = conn.prepareStatement(reportSql)) {
                stmt.setInt(1, 1); // Use existing person ID
                stmt.setInt(2, 1); // Use existing user ID
                stmt.setString(3, "Test report entry");
                stmt.executeUpdate();
                System.out.println("✓ Report entry created successfully");
                
                // Clean up the test report
                String deleteReportSql = "DELETE FROM reports WHERE report_details = 'Test report entry'";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteReportSql)) {
                    deleteStmt.executeUpdate();
                    System.out.println("✓ Test report cleaned up successfully");
                }
            }
            
            conn.close();
            System.out.println("\n✓ All tests passed! The database fix should work correctly.");
            System.out.println("\nThe application should now be able to add missing persons without the stored procedure error.");
            
        } catch (SQLException e) {
            System.err.println("✗ Database error: " + e.getMessage());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("✗ Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 