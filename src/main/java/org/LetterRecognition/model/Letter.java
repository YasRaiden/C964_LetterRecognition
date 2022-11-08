package org.LetterRecognition.model;

import org.nd4j.linalg.api.ndarray.INDArray;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Class for getting letter information from letters table in database */
public class Letter {

    private int letterId;
    private int sessionId;
    private String currentLetter;
    private String predictedLetter;
    private String status;
    private List<Double> letterAccuracyList = new ArrayList<>(Arrays.asList());
    BufferedImage processedImage;

    /** Constructor method for letters.
     * @param currentLetter current letter to be written
     */
    public Letter(String currentLetter) {
        this.currentLetter = currentLetter;
    }

    /** Constructor method for letters used for reporting.
     * @param letterId identifier from database
     * @param sessionId session letter was identified
     * @param currentLetter letter to be identified
     * @param predictedLetter letter predicted from handwriting
     * @param letterAccuracyList letter accuracy list
     * @param status status of letter
     */
    public Letter(int letterId, int sessionId, String currentLetter, String predictedLetter,
                  List<Double> letterAccuracyList, String status) {
        this.letterId = letterId;
        this.sessionId = sessionId;
        this.currentLetter = currentLetter;
        this.predictedLetter = predictedLetter;
        this.letterAccuracyList = letterAccuracyList;
        this.status = status;

    }

    /**
     * @return letter identifier from database
     */
    public int getLetterId() {
        return letterId;
    }

    /**
     * @return session letter was identified
     */
    public int getSessionID() {
        return sessionId;
    }

    /**
     * @return current letter to be written
     */
    public String getCurrentLetter() {
        return currentLetter;
    }

    /**
     * @return letter predicted
     */
    public String getPredictedLetter() {
        return predictedLetter;
    }

    /**
     * @return status of letter
     */
    public String getStatus() {
        return status;
    }

    /**
     * @return letter accuracy list
     */
    public List<Double> getLetterAccuracyList() {
        return letterAccuracyList;
    }

    /**
     * @param accuracyList accuracy for each letter
     */
    public void setLetterAccuracyList(INDArray accuracyList) {
        List<Double> letterAccuracy = new ArrayList<>(Arrays.asList());
        for(int index = 0; index < accuracyList.length(); index++){
            letterAccuracy.add(accuracyList.getDouble(index));
        }
        setPredictedLetter(letterAccuracy);
        this.letterAccuracyList = letterAccuracy;
    }

    /**
     * @param accuracyList accuracy for each letter
     */
    private void setPredictedLetter(List<Double> accuracyList) {
        int maxIndex = 0;
        for (int i = 0; i < 52; i++){
            if (accuracyList.get(i) > accuracyList.get(maxIndex)){
                maxIndex = i;
            }
        }
        this.predictedLetter = Settings.getLetter(maxIndex);
    }

    /**
     * @param status the status of current letter
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @param predictedLetter the predicted letter from Neural Network
     */
    public void setPredictedLetter(String predictedLetter) {
        this.predictedLetter = predictedLetter;
    }

    /**
     * @return bufferedImage of writtenLetter
     */
    public BufferedImage getProcessedImage() {
        return processedImage;
    }

    /** Set handwritten letter image to object
     * @param processedImage bufferedImage of predicted crop from frame
     */
    public void setProcessedImage(BufferedImage processedImage) {
        this.processedImage = processedImage;
    }

    /** Method marks letter status as correct and updates prediction. */
    public void markLetterCorrect(){
        this.predictedLetter = this.currentLetter;
        this.status = "Correct";
    }

    /** Method marks letter status as incorrect and updates predicted letter to correct value. */
    public void markLetterIncorrect(String reviewedLetter){
        this.predictedLetter = reviewedLetter;
        this.status = "Incorrect";
    }

    /** Method is used to compare current letter and predicted letter
     * @return true or false
     */
    public boolean comparePrediction(){
        if (predictedLetter.compareTo(currentLetter) == 0){
            return true;
        }
        return false;
    }
}
