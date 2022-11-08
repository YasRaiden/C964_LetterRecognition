package org.LetterRecognition.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Class controls sessions.*/
public class Session {
    private int sessionId;
    private int totalCount;
    private int currentCount;
    private int letterCorrect;
    private int letterIncorrect;
    private boolean sessionComplete;
    private LocalDateTime sessionDateTime;
    private ObservableList<Letter> associatedLetters = FXCollections.observableArrayList();
    private List<String> letterQueue = new ArrayList<>(Arrays.asList());

    /** Constructor method for sessions.
     * @param letterQueue queue of letters for active session
     */
    public Session(List<String> letterQueue) {
        this.letterQueue = letterQueue;
        this.currentCount = 0;
        this.letterCorrect = 0;
        this.letterIncorrect = 0;
        this.totalCount = letterQueue.size();
        this.sessionComplete = false;
    }

    /** Constructor method for session used for reporting.
     * @param sessionId identifier from database
     * @param sessionDateTime date time when session took place
     * @param totalCount total count of letters in session
     * @param letterCorrect total amount of correct letters in session
     * @param letterIncorrect total amount of incorrect letters in session
     */
    public Session(int sessionId, LocalDateTime sessionDateTime, int totalCount, int letterCorrect, int letterIncorrect) {
        this.sessionId = sessionId;
        this.sessionDateTime = sessionDateTime;
        this.totalCount = totalCount;
        this.letterCorrect = letterCorrect;
        this.letterIncorrect = letterIncorrect;
    }

    /**
     * @return session ID from database
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * @return date time when session took place
     */
    public String getSessionDateTime() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM-dd-yyyy -- hh:mm:ss a");
        String formatLocalDateTime = sessionDateTime.format(dateFormatter);
        return formatLocalDateTime;
    }

    /**
     * @return status of session
     */
    public boolean isSessionComplete() {
        return sessionComplete;
    }

    /**
     * @param sessionComplete status of session
     */
    public void setSessionComplete(boolean sessionComplete) {
        this.sessionComplete = sessionComplete;
    }


    /**
     * @return total count of letters
     */
    public int getTotalCount() {
        return totalCount;
    }

    /**
     * @return total count of correct letters
     */
    public int getLetterCorrect() {
        return letterCorrect;
    }

    /**
     * @return total count of incorrect letters
     */
    public int getLetterIncorrect() {
        return letterIncorrect;
    }

    /**
     * @return current count in session
     */
    public int getCurrentCount() {
        return currentCount;
    }

    /**
     * @param letter specified letter in session
     */
    public void addAssociatedLetter(Letter letter) {
        associatedLetters.add(letter);
    }

    /**
     * @return all associated letters in session
     */
    public ObservableList<Letter> getAssociatedLetters() {
        return associatedLetters;
    }

    /** Increment the amount of correct letters. */
    public void setLetterCorrect() {
        this.letterCorrect = letterCorrect + 1;
    }

    /** Increment the amount of incorrect letters. */
    public void setLetterIncorrect() {
        this.letterIncorrect = letterIncorrect + 1;
    }

    /**
     * @return random letter from letter queue
     */
    public String getRandomLetter() {
        String currentLetter = "List is Empty!";
        if(letterQueue.size() > 0) {
            Random rand = new Random();
            int letterIndex = rand.nextInt(letterQueue.size());
            currentLetter = letterQueue.get(letterIndex);
            letterQueue.remove(letterIndex);
            currentCount = totalCount - letterQueue.size();
        }
        else {
            currentLetter = "List is Empty!";
        }
        return currentLetter;
    }

    /** Method updates total correct and incorrect values and updates associated letter
     * @param letterCorrect number of letter correct
     * @param letterIncorrect number of letters incorrect
     */
    public void updateSessionCounts(int letterCorrect, int letterIncorrect){
        this.letterCorrect = letterCorrect;
        this.letterIncorrect = letterIncorrect;
    }


}
