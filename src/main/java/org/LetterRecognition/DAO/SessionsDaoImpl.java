package org.LetterRecognition.DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.LetterRecognition.model.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;

/** Class for getting session information from sessions table in database */
public class SessionsDaoImpl {
    private static final Logger log = LoggerFactory.getLogger(SessionsDaoImpl.class);

    /** Used for adding session record in sessions table from database
     * @param session selected session object
     * @return amount of rows updated in database indicating add was successful
     */
    public static int[] insertSession(Session session){

        int[] generatedRow = new int[]{0,0};

        String statement = "INSERT INTO sessions (letter_count, letter_correct, letter_incorrect, " +
                "Create_Date, Last_Update) VALUES(?, ?, ?, NOW(), NOW())";
        PreparedStatement preparedStatement = null;

        try {
            preparedStatement = Database.connection.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, session.getTotalCount());
            preparedStatement.setInt(2, session.getLetterCorrect());
            preparedStatement.setInt(3, session.getLetterIncorrect());
            generatedRow[0] = preparedStatement.executeUpdate();
            ResultSet generateKeys = preparedStatement.getGeneratedKeys();
            if (generateKeys.next()){
                generatedRow[1] = (int) generateKeys.getLong(1);
            }
        } catch (SQLException e) {
            log.error("SQLException" + e);
        }

        return generatedRow;
    }

    /** Used for retrieving all session records in sessions table from database
     * @return ObservableList of all session objects pulled from database
     */
    public static ObservableList<Session> getAllSessions() {
        ObservableList<Session> allSessions = FXCollections.observableArrayList();
        ResultSet sessionList = Database.selectStatement("SELECT * FROM sessions");
        Session selectedSession;
        try {
            while (sessionList.next()) {
                int sessionId = sessionList.getInt("session_ID");
                Timestamp sessionTimeStamp = sessionList.getTimestamp("Create_Date");
                int totalLetterCount = sessionList.getInt("letter_count");
                int letterCorrect = sessionList.getInt("letter_correct");
                int letterIncorrect = sessionList.getInt("letter_incorrect");
                LocalDateTime sessionDate = sessionTimeStamp.toLocalDateTime();
                selectedSession = new Session(sessionId, sessionDate, totalLetterCount, letterCorrect, letterIncorrect);
                allSessions.add(selectedSession);
            }
        } catch (SQLException e) {
            log.error("SQLException" + e);
        }
        return allSessions;
    }

    /** Used for updating Session record in sessions table from database
     * @param session record to be updated
     * @return amount of rows updated in database indicating update was successful
     */
    public static int updateSession(Session session) {
        int rowsAffected = 0;
        String statement = "UPDATE sessions SET letter_correct = ?, letter_incorrect = ?, Last_Update = NOW() WHERE session_ID = ?";
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = Database.connection.prepareStatement(statement);
            preparedStatement.setInt(1, session.getLetterCorrect());
            preparedStatement.setInt(2, session.getLetterIncorrect());
            preparedStatement.setInt(3, session.getSessionId());
            rowsAffected = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            log.error("SQLException" + e);
        }
        return rowsAffected;
    }

}
