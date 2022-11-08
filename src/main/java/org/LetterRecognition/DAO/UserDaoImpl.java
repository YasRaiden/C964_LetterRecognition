package org.LetterRecognition.DAO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Class for getting user information from users table in database */
public class UserDaoImpl {
    private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);

    /** Used for receiving user from users table from database
     * @return determine if user is authorized
     */
    public static boolean authUser(String password) {
        Boolean authUser = false;
        String statement = "SELECT count(*) FROM users WHERE Password = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = Database.connection.prepareStatement(statement);
            preparedStatement.setString(1, password);
            resultSet = preparedStatement.executeQuery();
            resultSet.next();

            if(resultSet.getInt(1) > 0){
                authUser = true;
            }
        } catch (SQLException e) {
            log.error("SQLException" + e);
        }
        return authUser;
    }

    /** Used for setting user password from users table from database
     * @return determine if user password was set
     */
    public static boolean setPassword(String password) {
        int rowsAffected = 0;
        String statement = "UPDATE users SET Password = ? WHERE User_ID = 1";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = Database.connection.prepareStatement(statement);
            preparedStatement.setString(1, password);
            rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected > 0){
                return true;
            }
        } catch (SQLException e) {
            log.error("SQLException" + e);
        }
        return false;
    }

}
