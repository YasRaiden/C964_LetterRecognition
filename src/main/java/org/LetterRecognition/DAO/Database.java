package org.LetterRecognition.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/** Class for handling database connection */
public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class);
    private static final String url = "jdbc:mysql://localhost:3306/letter_recognition";
    private static final String username = "c964";
    private static final String password = "password";
    private static final String driver = "com.mysql.cj.jdbc.Driver";
    public static Connection connection;

    /** Starts connection to database */
    public static void openConnection() {

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            log.info("Connection to database was successful!");
        }
        catch (SQLException | ClassNotFoundException se){

            log.error("error:" + se.getMessage());
        }
    }

    /** Closes connection to database */
    public static void closeConnection()  {
        try {
            connection.close();
            log.info("Connection to database was closed!");
        }
        catch (SQLException e) {
            log.error("SQLException" + e);
        }
    }

    /** Prepares simple SQL SELECT statements to run against database.
     * @param query SELECT SQL query to run again database
     * @return results of select statement
     */
    public static ResultSet selectStatement(String query) {
        Statement statement;
        ResultSet resultSet = null;

        try {
            statement = connection.createStatement();
            if (query.toUpperCase().startsWith("SELECT"))
                resultSet = statement.executeQuery(query);
            return resultSet;
        } catch (SQLException e) {
            log.error("SQLException" + e);
        } catch (NullPointerException e) {
            log.error("Connection is not available to database! \n\t" + e);
        }
        return null;
    }

    /** Method determines if database connection is open
     * @return true fals if database connection is open
     */
    public static boolean connectionOpen() {
        if(connection != null) {
            return true;
        }
        return false;
    }

}
