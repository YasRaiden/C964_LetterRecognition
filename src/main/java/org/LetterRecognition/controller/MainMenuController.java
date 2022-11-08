package org.LetterRecognition.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.LetterRecognition.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/** Class controls org.LetterRecognition.Main Menu form window for letter recognition system.*/
public class MainMenuController {
    private static final Logger log = LoggerFactory.getLogger(MainMenuController.class);
    private Stage stage;

    /** Method changes current form to letter recognition form.
     * @param event take action on event
     */
    @FXML
    public void onActionStart(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/RecognitionView.fxml"));
            loader.load();
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent e) {
                    Platform.exit();
                }
            });
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (IOException e) {
            log.error("Unable to load recognition form" + e);
        }
    }

    /** Method changes current form to settings form.
     * @param event take action on event
     */
    @FXML
    public void onActionSettings(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/SettingsView.fxml"));
            loader.load();
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (IOException e) {
            log.error("Unable to load settings form" + e);
        }
    }

    /** Method changes current form to report form.
     * @param event take action on event
     */
    @FXML
    public void onActionReport(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/SessionReportView.fxml"));
            loader.load();
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        } catch (IOException e) {
            log.error("Unable to load session report form" + e);
        }
    }

    /** Method closes application.
     * @param event take action on event
     */
    @FXML
    public void onActionExit(ActionEvent event) {
        log.info("Application stopped and resources were release via system exit.");
        System.exit(0);
    }

    @FXML
    public void onActionAdmin(ActionEvent event) {
        if (User.authUser()) {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/AdminView.fxml"));
                loader.load();
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();
            } catch (IOException e) {
                log.error("Unable to load Admin form" + e);
            }
        }
    }
}