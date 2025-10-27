package com.mycompany.mini_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class DBConnection {
    // CHANGE THESE VALUES TO YOUR ACTUAL MySQL CREDENTIALS
    private static final String URL = "jdbc:mysql://localhost:3306/inventory"; 
    private static final String USER = "root"; 
    private static final String PASSWORD = "Nox@123456789"; 

    /**
     * Establishes a connection to the MySQL database.
     * @return Connection object or null if connection fails.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Get a connection
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Connection Failed: " + e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Closes the database connection.
     * @param conn The Connection object to close.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}