package org.LetterRecognition.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.LetterRecognition.DAO.Database;
import org.LetterRecognition.DAO.LettersDaoImpl;
import org.LetterRecognition.DAO.SessionsDaoImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/** Class for getting letter and session information from letters table and session table in database */
public class Report {
    private static final Logger log = LoggerFactory.getLogger(Report.class);
    private static List<String> letterLabels = Arrays.asList(
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L",
            "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
            "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z");

    private static ObservableList<Session> allSessions = FXCollections.observableArrayList();
    private static ObservableList<Letter> allLetters = FXCollections.observableArrayList();
    private static int[][] letterStatus = new int[2][52];
    private static int[] sessionStatus = new int[2];

    /** Pulls session information from database, stores in ObservableList and calculates totals from data. */
    private static void setSessionReport() {
        allSessions.clear();
        sessionStatus = new int[2];
        allSessions = SessionsDaoImpl.getAllSessions();
        for (Session session : allSessions) {
            sessionStatus[0] += session.getLetterCorrect();
            sessionStatus[1] += session.getLetterIncorrect();
        }
    }

    /** Pulls letter information from database, stores in ObservableList and calculates totals from data. */
    private static void setLetterReport() {
        allLetters.clear();
        letterStatus = new int[2][52];
        allLetters = LettersDaoImpl.getAllLetters();
        int index;
        for (Letter letter : allLetters) {
            index = letterLabels.indexOf(letter.getCurrentLetter());
            if(letter.getCurrentLetter().compareTo(letter.getPredictedLetter()) == 0) {
                letterStatus[0][index] += 1;
            }
            else {
                letterStatus[1][index] += 1;
            }
        }

    }

    /**
     * @return ObservableList for ALL sessions collected from database
     */
    public static ObservableList<Session> getAllSessions() {
        return allSessions;
    }

    /**
     * @return ObservableList for ALL letters collected from database
     */
    public static ObservableList<Letter> getAllLetters() {
        return allLetters;
    }

    /**
     * @return Calculated totals for letters from data pulled from database
     */
    public static int[][] getLetterStatus() {
        return letterStatus;
    }

    /**
     * @return Calculated totals for sessions from data pulled from database
     */
    public static int[] getSessionStatus() {
        return sessionStatus;
    }

    /**
     * @return letter labels used for category list in charts.
     */
    public static List<String> getLetterLabels() {
        return letterLabels;
    }

    /**
     * @return letter index for specified letter.
     */
    public static int getLetterIndex(String letter) {
        return letterLabels.indexOf(letter);
    }

    /** Get all letters for associated with specific session
     * @param sessionID to pull subset of associated letters
     * @return subset of associated letters for specified session
     */
    public static ObservableList<Letter> getSessionLetters(int sessionID) {
        ObservableList<Letter> sessionLetters = FXCollections.observableArrayList();
        for(Letter letter : allLetters){
            if(letter.getSessionID() == sessionID){
                sessionLetters.add(letter);
            }
        }
        return sessionLetters;
    }

    /** Get all letters marked for review
     * @return subset of letters marked for review
     */
    public static ObservableList<Letter> getReviewLetters() {
        ObservableList<Letter> reviewLetters = FXCollections.observableArrayList();
        for(Letter letter : allLetters){
            if(letter.getStatus().compareTo("Review") == 0){
                reviewLetters.add(letter);
            }
        }
        return reviewLetters;
    }

    /** Get specific session object from sessionID
     * @param sessionID to specific session from ObservableList
     * @return specific session
     */
    public static Session getSession(int sessionID) {
        Session selectedSession = null;
        for(Session session : allSessions){
            if(session.getSessionId() == sessionID) {
                selectedSession = session;
                break;
            }
        }
        return selectedSession;
    }

    /** Method is used to refresh ObservableLists with data in database. */
    public static void refreshReport() {
        if(Database.connectionOpen()) {
            setLetterReport();
            setSessionReport();
        }
        else {
            log.error("Connection to database is closed!!!");
        }
    }

    /** Method marks letter to be correct in session and letter objects and updates database
     * @param letter specified letter to be corrected
     */
    public static void markLetterCorrect(Letter letter){
        Session selectedSession = getSession(letter.getSessionID());
        ObservableList<Letter> sessionLetters = getSessionLetters(selectedSession.getSessionId());
        letter.markLetterCorrect();
        int[] sessionCounts = new int[2];
        for(Letter selectedLetter : sessionLetters) {
            if(selectedLetter.getCurrentLetter().compareTo(selectedLetter.getPredictedLetter()) == 0) {
                sessionCounts[0] += 1;
            }
            else{
                sessionCounts[1] += 1;
            }
        }
        selectedSession.updateSessionCounts(sessionCounts[0], sessionCounts[1]);
        SessionsDaoImpl.updateSession(selectedSession);
        LettersDaoImpl.updateLetter(letter);
    }

    /** Method marks letter to be incorrect in session and letter objects and updates database
     * @param letter specified letter to be marked incorrect
     * @param reviewedLetter letter prediction should have made
     */
    public static void markLetterIncorrect(Letter letter, String reviewedLetter){
        Session selectedSession = getSession(letter.getSessionID());
        ObservableList<Letter> sessionLetters = getSessionLetters(selectedSession.getSessionId());
        letter.markLetterIncorrect(reviewedLetter);
        int[] sessionCounts = new int[2];
        for(Letter selectedLetter : sessionLetters) {
            if(selectedLetter.getCurrentLetter().compareTo(selectedLetter.getPredictedLetter()) == 0) {
                sessionCounts[0] += 1;
            }
            else{
                sessionCounts[1] += 1;
            }
        }
        selectedSession.updateSessionCounts(sessionCounts[0], sessionCounts[1]);
        SessionsDaoImpl.updateSession(selectedSession);
        LettersDaoImpl.updateLetter(letter);
    }
}
