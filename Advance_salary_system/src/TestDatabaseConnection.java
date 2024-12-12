import java.sql.*;

public class TestDatabaseConnection {

    public static void main(String[] args) {
        // Get a connection to the database
        Connection connection = DBConnection.getConnection();

        // If the connection is successful, print a success message
        if (connection != null) {
            System.out.println("Connection to the database was successful!");
        }

    }
}

