package org.LetterRecognition.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.LetterRecognition.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;


/** Class controls Letter Report form window for letter recognition system.*/
public class LetterReportController {
    private static final Logger log = LoggerFactory.getLogger(LetterReportController.class);
    Stage stage;
    int sessionID;

    @FXML
    private TableView<Letter> letterTableView;

    @FXML
    private TableColumn<?, ?> letterIdCol;

    @FXML
    private TableColumn<?, ?> letterPredictedCol;

    @FXML
    private TableColumn<?, ?> letterStatusCol;

    @FXML
    private TableColumn<?, ?> letterWriteCol;

    @FXML
    private LineChart<?, ?> upperCaseLineChart;

    @FXML
    private LineChart<?, ?> lowerCaseLineChart;

    @FXML
    private ImageView writtenLetterImageView;

    @FXML
    private Label selectedLetterLbl;

    @FXML
    private Button markCorrectBtn;

    @FXML
    private Button markIncorrectBtn;

    @FXML
    private Button learnModelBtn;

    /** Method changes current form to Session Report Form.
     * @param event take action on event of selecting back button.
     */
    @FXML
    void onActionBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/SessionReportView.fxml"));
            loader.load();
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (IOException e) {
            log.error("Unable to load main form" + e);
        }
    }

    /** Method to mark letter as correct in database and update report
     * @param event take action on event of selecting Mark Correct button.
     */
    @FXML
    void onActionMarkCorrect(ActionEvent event) {
        Letter selectedLetter = letterTableView.getSelectionModel().getSelectedItem();
        if (selectedLetter != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Action will mark currently selected letter (id:" +
                    selectedLetter.getLetterId() + ") as correct. Status and prediction values will be updated. " +
                    "Are you sure you would like to continue?",
                    ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                if(User.authUser()) {
                    Report.markLetterCorrect(selectedLetter);
                    Report.refreshReport();
                    sendSession(Report.getSession(selectedLetter.getSessionID()));
                }
            }
        }
    }

    /** Method to mark letter as incorrect in database and update report
     * @param event  take action on event of selecting Mark Incorrect button.
     */
    @FXML
    void onActionMarkIncorrect(ActionEvent event) {
        Letter selectedLetter = letterTableView.getSelectionModel().getSelectedItem();
        if (selectedLetter != null) {

            Alert alert = new Alert(Alert.AlertType.WARNING, "Action will mark currently selected letter (id:" +
                    selectedLetter.getLetterId() + ") as incorrect. Status and prediction values will be updated. " +
                    "Are you sure you would like to continue?",
                    ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                if (User.authUser()) {
                    TextInputDialog invalidLetterInput = new TextInputDialog();
                    invalidLetterInput.setTitle("Letter Entry");
                    invalidLetterInput.setHeaderText("Input letter identified in image.");
                    invalidLetterInput.getDialogPane().setContentText("Enter letter:");
                    Optional<String> invalidLetter = invalidLetterInput.showAndWait();
                    String letter = invalidLetterInput.getEditor().getText().toString();

                    if (Report.getLetterIndex(letter) != -1) {
                        Report.markLetterIncorrect(selectedLetter, letter);
                        Report.refreshReport();
                        sendSession(Report.getSession(selectedLetter.getSessionID()));
                    } else {
                        Alert invalidEntry = new Alert(Alert.AlertType.WARNING, "Invalid entry try again with a " +
                                "single letter!", ButtonType.OK);
                        invalidEntry.showAndWait();
                    }
                }
            }
        }
    }

    /** Method takes currently selected letter and learns it to the model based on predicted letter
     * @param event take action on event of selecting Learn button
     */
    @FXML
    void onActionLearnModel(ActionEvent event){
        Letter selectedLetter = letterTableView.getSelectionModel().getSelectedItem();
        if (selectedLetter != null) {
            int index = Report.getLetterIndex(selectedLetter.getPredictedLetter());
            Alert alert = new Alert(Alert.AlertType.WARNING, "Action will Learn currently selected letter (id:" +
                    selectedLetter.getLetterId() + "). This will allow current model to predict similar letters " +
                    "more accurately. Are you sure you would like to continue?",
                    ButtonType.YES, ButtonType.NO);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                if(User.authUser()) {
                    File inputFile = new File("user_images/" + selectedLetter.getLetterId() + ".png");
                    try {
                        NeuralNetwork.learnItem(inputFile, new int[]{index}, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    /** Updates line chart, predicted letter and written letter on information for selected letter.
     * @param mouseEvent action on mouse selection of table view
     */
    @FXML
    public void onMouseClickedUpdateTable(MouseEvent mouseEvent) {
        Letter selectedLetter = letterTableView.getSelectionModel().getSelectedItem();
        if (selectedLetter != null) {
            if(selectedLetter.comparePrediction()){
                markCorrectBtn.setDisable(true);
                markIncorrectBtn.setDisable(false);
                learnModelBtn.setDisable(false);
            } else if (selectedLetter.getStatus().compareTo("Review") == 0) {
                markCorrectBtn.setDisable(false);
                markIncorrectBtn.setDisable(false);
                learnModelBtn.setDisable(true);
            } else {
                markCorrectBtn.setDisable(false);
                markIncorrectBtn.setDisable(true);
                learnModelBtn.setDisable(true);
            }
            upperCaseLineChart.getData().clear();
            lowerCaseLineChart.getData().clear();
            selectedLetterLbl.setText(selectedLetter.getCurrentLetter());
            File file = new File("user_images/" + selectedLetter.getLetterId() + ".png");
            Image writtenLetter = new Image(file.toURI().toString());
            writtenLetterImageView.setImage(writtenLetter);
            XYChart.Series upperCaseSeries = new XYChart.Series<>();
            XYChart.Series lowerCaseSeries = new XYChart.Series<>();
            List<Double> letterPredictionList = selectedLetter.getLetterAccuracyList();
            for (int i = 0; i < 26; i++) {
                upperCaseSeries.getData().add(new XYChart.Data<>(Settings.getLetter(i), (int) (letterPredictionList.get(i) * 100)));
            }
            for (int i = 26; i < 52; i++) {
                lowerCaseSeries.getData().add(new XYChart.Data<>(Settings.getLetter(i), (int) (letterPredictionList.get(i) * 100)));
            }
            upperCaseLineChart.getData().add(upperCaseSeries);
            lowerCaseLineChart.getData().add(lowerCaseSeries);
        }
    }

    /** Used to transfer session information to current form to pre-populate letter table view.
     * @param session object received from session report form.
     */
    public void sendSession(Session session) {
        letterTableView.setItems(Report.getSessionLetters(session.getSessionId()));
        letterIdCol.setCellValueFactory(new PropertyValueFactory<>("letterId"));
        letterWriteCol.setCellValueFactory(new PropertyValueFactory<>("currentLetter"));
        letterPredictedCol.setCellValueFactory(new PropertyValueFactory<>("predictedLetter"));
        letterStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        sessionID = session.getSessionId();
    }

    /** Used to transfer all letters marked for review to the letter table view.
     * @param reviewLetters all letter marked for review
     */
    public void sendReview(ObservableList<Letter> reviewLetters) {
        letterTableView.setItems(reviewLetters);
        letterIdCol.setCellValueFactory(new PropertyValueFactory<>("letterId"));
        letterWriteCol.setCellValueFactory(new PropertyValueFactory<>("currentLetter"));
        letterPredictedCol.setCellValueFactory(new PropertyValueFactory<>("predictedLetter"));
        letterStatusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        sessionID = -1;
    }


}
