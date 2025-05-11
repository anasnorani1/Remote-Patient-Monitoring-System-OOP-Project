import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String URL = "jdbc:mysql://localhost:3306/healthcare_system?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "Anasnorani@107";

    public static Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Database connection successful!");
            return connection;
        } catch (SQLException e) {
            System.err.println("❌ Connection failed: " + e.getMessage());
            throw e;  // Rethrow for further handling
        }
    }

    // Test connection method
    public static void testConnection() {
        try (Connection conn = getConnection()) {
            // Connection successful, no need to do anything
        } catch (SQLException e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
        }
    }
}
