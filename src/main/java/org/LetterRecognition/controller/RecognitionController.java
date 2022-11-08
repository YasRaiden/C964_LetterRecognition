package org.LetterRecognition.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.LetterRecognition.DAO.Database;
import org.LetterRecognition.DAO.LettersDaoImpl;
import org.LetterRecognition.DAO.SessionsDaoImpl;
import org.LetterRecognition.model.*;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



/** Class controls Recognition form window for letter recognition system.*/
public class RecognitionController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(RecognitionController.class);
    private Stage stage;
    private Session currentSession;
    private Letter currentLetter;
    private VideoCapture camera = new VideoCapture();
    private int cameraId = 0;
    private ScheduledExecutorService scheduler;
    private Mat currentFrame;
    private boolean sessionStarted = false;
    BufferedImage processedImage;

    @FXML
    private Button startBtn;

    @FXML
    private ImageView imageView;

    @FXML
    private Label totalCntLbl;

    @FXML
    private Label currentCntLbl;

    @FXML
    private Label currentLetterLbl;

    @FXML
    private Button nextCheckBtn;

    @FXML
    private Button markReviewBtn;

    /** Method changes current form to org.LetterRecognition.Main Menu Form.
     * @param event take action on event of selecting back button.
     */
    @FXML
    void onActionBack(ActionEvent event) {
        boolean hardStop = true;
        if(!currentSession.isSessionComplete()){
            Alert alert = new Alert(Alert.AlertType.WARNING, "Session is currently in progress. " +
                    "Any progress will not be saved! Are you sure you would like to CLOSE?",
                    ButtonType.CLOSE, ButtonType.CANCEL);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.CANCEL) {
                hardStop = true;
            }
            if (result.isPresent() && result.get() == ButtonType.CLOSE) {
                hardStop = false;
            }
        }
        else{
            hardStop = false;
        }
        if(!hardStop) {
            if (this.camera.isOpened()) {
                stopCamera();
            }
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/MainMenuView.fxml"));
                loader.load();
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();
            } catch (IOException e) {
                log.error("Unable to load main form" + e);
            }
        }
    }

    /** Method to start and stop camera capture on a thread
     * @param event take action on event of selecting Start/Stop button.
     */
    @FXML
    void onActionStartStopCam(ActionEvent event) {
        if(!sessionStarted){
            setupNextLetter();
            sessionStarted = true;
        }
        startBtn.setDisable(true);
        if (!this.camera.isOpened()) {
            startCamera();
            nextCheckBtn.setDisable(false);

        }else {
            stopCamera();
            nextCheckBtn.setDisable(true);
        }
        startBtn.setDisable(false);
    }

    /** Method to check letter against machine learning model and creates next letter.
     * @param event take action on event of selecting Check/Next button.
     */
    @FXML
    void onActionCheckNext(ActionEvent event) {
        if(!currentSession.isSessionComplete()){
            if (currentLetter.getPredictedLetter() != null) {
                nextCheckBtn.setText("Check");
                currentLetterLbl.setTextFill(Paint.valueOf("BLACK"));
                setupNextLetter();
            } else {
                INDArray array = getCurrentImagePrediction();
                currentLetter.setLetterAccuracyList(array);
                if (currentLetter.getCurrentLetter().compareTo(currentLetter.getPredictedLetter()) == 0) {
                    currentLetterLbl.setText("Correct");
                    currentLetterLbl.setTextFill(Paint.valueOf("GREEN"));
                    currentSession.setLetterCorrect();
                    currentLetter.setStatus("Correct");
                } else {
                    currentLetterLbl.setText("Incorrect");
                    currentLetterLbl.setTextFill(Paint.valueOf("RED"));
                    currentSession.setLetterIncorrect();
                    currentLetter.setStatus("Incorrect");
                    markReviewBtn.setDisable(false);
                }
                currentSession.addAssociatedLetter(currentLetter);
                currentLetter.setProcessedImage(processedImage);
                if (currentSession.getCurrentCount() < currentSession.getTotalCount()) {
                    nextCheckBtn.setText("Next");
                } else {
                    nextCheckBtn.setText("Finish");
                    currentSession.setSessionComplete(true);
                }
            }
        }
        else{
            if (this.camera.isOpened()) {
                stopCamera();
            }
            addSession2Database();
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/MainMenuView.fxml"));
                loader.load();
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();
            } catch (IOException e) {
                log.error("Unable to load main form" + e);
            }
        }
    }

    /** Method to mark current letter to be reviewed if marked incorrect.
     * @param event take cation on event of selecting Review button.
     */
    @FXML
    void onActionMarkReview(ActionEvent event) {
        currentLetter.setStatus("Review");
        markReviewBtn.setDisable(true);
    }

    /** Method to add current session and associated letters to connected database. */
    private void addSession2Database() {
        if(Database.connectionOpen()) {
            int[] sessionID = SessionsDaoImpl.insertSession(currentSession);
            ObservableList<Letter> associatedLetters = currentSession.getAssociatedLetters();
            for (Letter letter : associatedLetters) {
                int letterRecordId = LettersDaoImpl.insertLetter(letter, sessionID[1]);
                File userImageDir = new File("user_images/");
                File outputFile = new File("user_images/" + letterRecordId + ".png");
                if (!userImageDir.exists()){
                    userImageDir.mkdir();
                }
                try {
                    ImageIO.write(letter.getProcessedImage(), "png", outputFile);
                } catch (IOException e) {
                    log.error("Error saving image to file: " + e);
                }

            }
        }
        else {
            log.error("Connection to database is closed!!!");
        }


    }

    /** Method to crop image and get letter prediction from machine learning algorithm.
     * @return INDArray of accuracy list from machine learning algorithm.
     */
    private INDArray getCurrentImagePrediction(){
        BufferedImage bufferedImage = SelectedImage.cropObject(currentFrame);
        INDArray output = NeuralNetwork.model.output(NeuralNetwork.normalizeImage(SelectedImage.blackWhite(bufferedImage, 50)));
        processedImage = SelectedImage.blackWhite(bufferedImage,50);
        return output;
    }

    /** Method to grab next random letter and create new letter object for current session. */
    private void setupNextLetter() {
        String selectedLetter = currentSession.getRandomLetter();
        currentLetter = new Letter(selectedLetter);
        currentLetterLbl.setText(selectedLetter);
        currentCntLbl.setText(String.valueOf(currentSession.getCurrentCount()));
    }

    /** Method to start camera on new thread. */
    private void startCamera() {
        this.camera.open(cameraId);
        Runnable cameraRunnable = new Runnable() {
            @Override
            public void run() {
                Mat frame = grabFrame();
                Image image = SelectedImage.mat2Image(frame);
                updateImageView(imageView, image);
            }
        };
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.scheduler.scheduleAtFixedRate(cameraRunnable, 0, 33, TimeUnit.MILLISECONDS);
        startBtn.setText("Stop");
    }

    /** Method to stop camera and stop thread. */
    private void stopCamera() {
        this.scheduler.shutdown();
        try {
            this.scheduler.awaitTermination(20, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.camera.release();
        startBtn.setText("Start");
    }

    /** Method to grab current frame from camera.
     * @return frame from camera.
     */
    private Mat grabFrame() {
        Mat frame = new Mat();
        this.camera.read(frame);
        Mat processed = SelectedImage.processImage(frame);
        SelectedImage.markOutline(processed,frame, NeuralNetwork.model);
        //frame = ImageProcessor.cropImage;
        currentFrame = frame;
        return frame;
    }

    /** Method to update ImageView with current frame.
     * @param imageView object from current frame
     * @param image frame to be placed on imageView
     */
    private void updateImageView(ImageView imageView, Image image) {
        SelectedImage.onFXThread(imageView.imageProperty(), image);
    }

    /** Method to initialize form with new session object and update total count and current count from settings. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentSession = new Session(Settings.generateLetterQueue());
        totalCntLbl.setText("of    " + String.valueOf(currentSession.getTotalCount()));
        currentCntLbl.setText(String.valueOf(currentSession.getCurrentCount()));
    }
}