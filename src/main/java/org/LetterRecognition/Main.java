package org.LetterRecognition;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.LetterRecognition.DAO.Database;
import org.LetterRecognition.model.NeuralNetwork;
import org.LetterRecognition.model.Settings;
import org.opencv.core.Core;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class Main extends Application {
    private static final Logger log = LoggerFactory.getLogger(Main.class);


    /** Method sets up MainForm by loading FXML file. */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenuView.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("C964-Capstone");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /** org.LetterRecognition.Main method starts the application.
     * Opens database connection and pull current settings from database.
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException {
        log.info("Application Start");
        Database.openConnection();
        Settings.loadSettings();
        NeuralNetwork.loadModel();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
        Database.closeConnection();
        log.info("Application Stopped");

    }


}