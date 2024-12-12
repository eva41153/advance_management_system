import java.sql.*;

public class DBConnection {

    // MySQL database credential
    private static final String URL = "jdbc:mysql://localhost:3306/advancesalarypaymentsystem";  // Updated to your DB name
    private static final String USER = "root";  // Change to your MySQL username (e.g., 'root')
    private static final String PASSWORD = "password";  // Change to your MySQL password

    // This method returns a connection to the MySQL database
    public static Connection getConnection() {
        try {
            // Load and register MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Create a connection to the database
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to load JDBC driver.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Database connection failed.");
        }

        return null; // Return null if connection failed
    }
}




