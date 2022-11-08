package org.LetterRecognition.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.LetterRecognition.model.Letter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Class for getting letter information from letters table in database */
public class LettersDaoImpl {
    private static final Logger log = LoggerFactory.getLogger(LettersDaoImpl.class);

    /** Used for adding letter record in letters table from database
     * @param letter selected letter object
     * @return amount of rows updated in database indicating add was successful
     */
    public static int insertLetter(Letter letter, int sessionID){
        List<Double> letterAccuracyList = letter.getLetterAccuracyList();
        int rowsAffected = 0;

        String statement = "INSERT INTO letters (" +
                "session_ID, current_letter, predicted_letter, status, " +
                "41_accuracy, 42_accuracy, 43_accuracy, 44_accuracy, 45_accuracy, 46_accuracy, 47_accuracy, " +
                "48_accuracy, 49_accuracy, 4a_accuracy, 4b_accuracy, 4c_accuracy, 4d_accuracy, 4e_accuracy, " +
                "4f_accuracy, 50_accuracy, 51_accuracy, 52_accuracy, 53_accuracy, 54_accuracy, 55_accuracy, " +
                "56_accuracy, 57_accuracy, 58_accuracy, 59_accuracy, 5a_accuracy, " +
                "61_accuracy, 62_accuracy, 63_accuracy, 64_accuracy, 65_accuracy, 66_accuracy, 67_accuracy, " +
                "68_accuracy, 69_accuracy, 6a_accuracy, 6b_accuracy, 6c_accuracy, 6d_accuracy, 6e_accuracy, " +
                "6f_accuracy, 70_accuracy, 71_accuracy, 72_accuracy, 73_accuracy, 74_accuracy, 75_accuracy, " +
                "76_accuracy, 77_accuracy, 78_accuracy, 79_accuracy, 7a_accuracy, " +
                "Create_Date, Last_Update) " +
                "VALUES(?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?," +
                "NOW(),NOW())";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Database.connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, sessionID);
            preparedStatement.setString(2, letter.getCurrentLetter());
            preparedStatement.setString(3, letter.getPredictedLetter());
            preparedStatement.setString(4, letter.getStatus());
            int parameterIndex;
            for (int i = 0; i < letterAccuracyList.size(); i++) {
                parameterIndex = i + 5;
                preparedStatement.setDouble(parameterIndex, letterAccuracyList.get(i));
            }

            rowsAffected = preparedStatement.executeUpdate();
            ResultSet generateKeys = preparedStatement.getGeneratedKeys();
            if (generateKeys.next()){
                rowsAffected = (int) generateKeys.getLong(1);
            }
        } catch (SQLException e) {
            log.error("SQLException" + e);
        }
        return rowsAffected;
    }

    /** Used for retrieving all letter records in letters table from database
     * @return ObservableList of all letter objects pulled from database
     */
    public static ObservableList<Letter> getAllLetters() {
        ObservableList<Letter> allLetters = FXCollections.observableArrayList();
        ResultSet lettersList = Database.selectStatement("SELECT * FROM letters");
        Letter selectedLetter;
        try {
            while (lettersList.next()) {
                int letterID = lettersList.getInt("letter_ID");
                int sessionID = lettersList.getInt("session_ID");
                String currentLetter = lettersList.getString("current_letter");
                String predictedLetter = lettersList.getString("predicted_letter");
                String status = lettersList.getString("status");

                List<Double> letterAccuracyList = new ArrayList<>(Arrays.asList());;
                for (int index = 6; index < 58; index++){
                    letterAccuracyList.add(lettersList.getDouble(index));
                }

                selectedLetter = new Letter(letterID, sessionID, currentLetter, predictedLetter, letterAccuracyList, status);
                allLetters.add(selectedLetter);
            }
        } catch (SQLException e) {
            log.error("SQLException" + e);
        }

        return allLetters;
    }

    /** Used for updating letter record in letters table from database
     * @param letter record to be updated
     * @return amount of rows updated in database indicating update was successful
     */
    public static int updateLetter(Letter letter) {
        int rowsAffected = 0;
        String statement = "UPDATE letters SET predicted_letter = ?, status = ?, Last_Update = NOW() WHERE letter_ID = ?";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = Database.connection.prepareStatement(statement);
            preparedStatement.setString(1, letter.getPredictedLetter());
            preparedStatement.setString(2, letter.getStatus());
            preparedStatement.setInt(3, letter.getLetterId());
            rowsAffected = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            log.error("SQLException" + e);
        }
        return rowsAffected;
    }

}
