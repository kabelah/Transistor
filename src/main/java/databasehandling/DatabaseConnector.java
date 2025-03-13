package databasehandling;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

// Check whether database has been preprocessed after connecting.
// If not, start the lengthy preprocessing by creating DatabasePreprocessing class
// If so, don't do that

    private static final String localHost = "jdbc:mysql://localhost:3306/gtfs";
    private static final String username = "gtfsuser";
    private static final String password = "Gtfspassword123!";
    
    // Flag to track if we've already shown the connection error message
    private static boolean connectionErrorShown = false;
    
    // Flag to track if we've attempted a connection at least once
    private static boolean connectionAttempted = false;
    
    // Flag to track if the connection was successful
    private static boolean connectionSuccessful = false;

    public static Connection getConnection() throws SQLException {
        try {
            connectionAttempted = true;
            Connection conn = DriverManager.getConnection(localHost, username, password);
            connectionSuccessful = true;
            return conn;
        } catch (SQLException e) {
            if (!connectionErrorShown) {
                System.out.println("Unable to connect to database: " + e.getMessage());
                System.out.println("This is likely because either:");
                System.out.println("1. MySQL is not running on your machine");
                System.out.println("2. The database 'gtfs' doesn't exist");
                System.out.println("3. The username/password credentials are incorrect");
                System.out.println("For migration purposes, we'll continue but database features won't work.");
                connectionErrorShown = true;
            }
            throw e;
        }
    }
    
    // Test if we can connect to the database
    public static boolean canConnectToDatabase() {
        if (connectionAttempted) {
            return connectionSuccessful;
        }
        
        try {
            Connection conn = getConnection();
            conn.close();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
