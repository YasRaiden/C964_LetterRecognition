package org.LetterRecognition.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.LetterRecognition.model.Report;
import org.LetterRecognition.model.Session;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Class controls Session Report form window for letter recognition system.*/
public class SessionReportController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(SessionReportController.class);
    Stage stage;

    private XYChart.Series<String, Number> correctTotalSeries = new XYChart.Series<String, Number>();
    private XYChart.Series<String, Number> incorrectTotalSeries = new XYChart.Series<String, Number>();

    @FXML
    private CategoryAxis categoryAxis;

    @FXML
    private NumberAxis numberAxis;

    @FXML
    private TableView<Session> sessionTableView;

    @FXML
    private TableColumn<Session, Integer> sessionIdCol;

    @FXML
    private TableColumn<Session, DateTime> sessionDateCol;

    @FXML
    private TableColumn<Session, Integer> sessionCorrectCol;

    @FXML
    private TableColumn<Session, Integer> sessionIncorrectCol;

    @FXML
    private TableColumn<Session, Integer> sessionTotalCol;

    @FXML
    private StackedBarChart<String, Number> sessionBarChart;

    @FXML
    private PieChart sessionPieChart;

    /** Method opens letters report with all associated letters of session.
     * @param event take action on event of selecting Details button.
     */
    @FXML
    void onActionSessionDetails(ActionEvent event) {
        Session selectedSession = sessionTableView.getSelectionModel().getSelectedItem();
        if(selectedSession != null) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/LetterReportView.fxml"));
                loader.load();
                LetterReportController ADMController = loader.getController();
                ADMController.sendSession(selectedSession);
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();

            } catch (IOException e) {
                log.error("Unable to load letter report form" + e);
            }
        }
    }

    /** Method opens letters report with all letters marked for review.
     * @param event take action on event of selecting Show Review button.
     */
    @FXML
    void onActionReview(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/LetterReportView.fxml"));
                loader.load();
                LetterReportController ADMController = loader.getController();
                ADMController.sendReview(Report.getReviewLetters());
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();

            } catch (IOException e) {
                log.error("Unable to load letter report form" + e);
            }
    }

    /** Method changes current form to org.LetterRecognition.Main Menu Form.
     * @param event take action on event of selecting back button.
     */
    @FXML
    void onActionBack(ActionEvent event) {
        try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/MainMenuView.fxml"));
        loader.load();
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Parent scene = loader.getRoot();
        stage.setScene(new Scene(scene));
        stage.show();
        } catch (IOException e) {
            log.error("Unable to load main form" + e);
        }
    }

    /** Sets up sessions tableview with information from database. */
    private void addSessionsTableView() {
        sessionTableView.setItems(Report.getAllSessions());
        sessionIdCol.setCellValueFactory(new PropertyValueFactory<>("sessionId"));
        sessionDateCol.setCellValueFactory(new PropertyValueFactory<>("sessionDateTime"));
        sessionCorrectCol.setCellValueFactory(new PropertyValueFactory<>("letterCorrect"));
        sessionIncorrectCol.setCellValueFactory(new PropertyValueFactory<>("letterIncorrect"));
        sessionTotalCol.setCellValueFactory(new PropertyValueFactory<>("totalCount"));
    }

    /** Sets up session pie chart with information from database. */
    private void addSessionPieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Correct - " + Report.getSessionStatus()[0], Report.getSessionStatus()[0]),
                new PieChart.Data("Incorrect - " + Report.getSessionStatus()[1], Report.getSessionStatus()[1]));
        sessionPieChart.setData(pieChartData);
    }

    /** Sets up letter bar graph with information from database. */
    private void addLetterBarGraph() {
        numberAxis.setLabel("Totals");
        numberAxis.setTickUnit(1);
        correctTotalSeries.setName("Correct");
        incorrectTotalSeries.setName("Incorrect");
        categoryAxis.setCategories(FXCollections.<String>observableArrayList(Report.getLetterLabels()));
        for(int index = 0; index < Report.getLetterLabels().size(); index++){
            Integer totalCorrect = Report.getLetterStatus()[0][index];
            Integer totalIncorrect = Report.getLetterStatus()[1][index];

            String selectedLetter = Report.getLetterLabels().get(index);
            correctTotalSeries.getData().add(new XYChart.Data<String, Number>(selectedLetter, totalCorrect));
            incorrectTotalSeries.getData().add(new XYChart.Data<String, Number>(selectedLetter, totalIncorrect));
        }
        sessionBarChart.getData().addAll(correctTotalSeries,incorrectTotalSeries);
    }

    /** Initializes data for sessions table view, barchart, and circle graph with data existing in database
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Report.refreshReport();
        addSessionsTableView();
        addSessionPieChart();
        addLetterBarGraph();
    }
}