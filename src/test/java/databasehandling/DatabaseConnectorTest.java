package databasehandling;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseConnectorTest {

    private static final String localHost = "jdbc:mysql://localhost:3306/gtfs";
    private static final String username = "Project12";
    private static final String password = "Projec1234!";

    @Test
    void testGetConnectionSuccess() {
        try (Connection connection = DatabaseConnector.getConnection()) {
            assertNotNull(connection, "Connection should not be null");
            assertTrue(connection.isValid(2), "Connection should be valid");
        } catch (SQLException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    void testGetConnectionFailure() {
        String invalidLocalHost = "jdbc:mysql://invalidhost:3306/gtfs";
        try {
            DriverManager.getConnection(invalidLocalHost, "invalidUser", "invalidPassword");
            fail("Should have thrown an SQLException");
        } catch (SQLException e) {
            assertNotNull(e, "Exception should not be null");
            assertTrue(e.getMessage().contains("Communications link failure") || e.getMessage().contains("Unknown database"),
                    "Exception message should indicate a connection failure");
        }
    }

    @Test
    void testGetConnectionWithNullValues() {
        try {
            DriverManager.getConnection(null, null, null);
            fail("Should have thrown an exception for null values");
        } catch (SQLException e) {
            assertNotNull(e, "Exception should not be null");
            assertTrue(e.getMessage().contains("The url cannot be null"),
                    "Exception message should indicate invalid URL");
        }

        try {
            DriverManager.getConnection("", "", "");
            fail("Should have thrown an exception for empty values");
        } catch (SQLException e) {
            assertNotNull(e, "Exception should not be null");
            assertTrue(e.getMessage().contains("The url can not be null") || e.getMessage().contains("The url cannot be null") || e.getMessage().contains("No suitable driver"),
                    "Exception message should indicate invalid URL or null values");
        }
    }

    @Test
    void testGetConnectionWithInvalidUrlFormat() {
        String invalidUrl = "invalidurl://localhost:3306/gtfs";
        try {
            DriverManager.getConnection(invalidUrl, username, password);
            fail("Should have thrown an exception for invalid URL format");
        } catch (SQLException e) {
            assertNotNull(e, "Exception should not be null");
            assertTrue(e.getMessage().contains("No suitable driver found"),
                    "Exception message should indicate invalid URL format");
        }
    }

    @Test
    void testGetConnectionWithInvalidCredentials() {
        try {
            DriverManager.getConnection(localHost, "invalidUsername", password);
            fail("Should have thrown an exception for invalid username");
        } catch (SQLException e) {
            assertNotNull(e, "Exception should not be null");
            assertTrue(e.getMessage().contains("Access denied"),
                    "Exception message should indicate invalid username");
        }

        try {
            DriverManager.getConnection(localHost, username, "invalidPassword");
            fail("Should have thrown an exception for invalid password");
        } catch (SQLException e) {
            assertNotNull(e, "Exception should not be null");
            assertTrue(e.getMessage().contains("Access denied"),
                    "Exception message should indicate invalid password");
        }
    }

    @Test
    void testGetConnectionWithDatabaseServerNotRunning() {
        try {
            DriverManager.getConnection(localHost, username, password);
            fail("Should have thrown an exception with database server not running");
        } catch (SQLException e) {
            assertNotNull(e, "Exception should not be null");
            assertTrue(e.getMessage().contains("Communications link failure") || e.getMessage().contains("Could not create connection") || e.getMessage().contains("Access denied for user"),
                    "Exception message should indicate a connection failure or server not running");
        }
    }
}

//TODO: Fix dataprocessing TestCase - Either ExcelReader or ExcelWriter. Delete's the .xlsx file.