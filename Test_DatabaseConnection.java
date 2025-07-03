import com.alerts.system.DatabaseManager;

public class Test_DatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        try {
            DatabaseManager dbManager = DatabaseManager.getInstance();
            
            // Test connection
            boolean connectionTest = dbManager.testConnection();
            System.out.println("Connection test result: " + connectionTest);
            
            if (connectionTest) {
                System.out.println("Database connection is working properly!");
            } else {
                System.out.println("Database connection failed!");
            }
            
        } catch (Exception e) {
            System.err.println("Error during database connection test: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 