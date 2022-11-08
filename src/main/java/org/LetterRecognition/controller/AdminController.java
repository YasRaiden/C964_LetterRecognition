package org.LetterRecognition.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.LetterRecognition.model.NeuralNetwork;
import org.LetterRecognition.model.Settings;
import org.LetterRecognition.model.User;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;


/** Class controls Admin form window for letter recognition system.*/
public class AdminController {
    private Stage stage;
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private Boolean validFile = false;
    private DirectoryChooser directoryChooser = new DirectoryChooser();
    private FileChooser fileChooser = new FileChooser();
    private MultiLayerNetwork model = NeuralNetwork.buildModel();


    @FXML
    private TextField filePathTxtBx;

    @FXML
    private Label isInvalidDirLbl;

    @FXML
    private Label loadModelLbl;

    @FXML
    private Label resetPassLbl;

    @FXML
    private Label saveModelLbl;

    @FXML
    private Button trainModelBtn;

    @FXML
    private Button saveModelBtn;

    @FXML
    private Label trainModelLbl;

    /** Method changes current form to org.LetterRecognition.Main Menu Form.
     * @param event take action on event of selecting back button.
     */
    @FXML
    void onActionBack(ActionEvent event) {
        try {
            Settings.saveSettings();
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

    /** Method opens a letter dataset to learn to model
     * @param event take action on event of selecting Open File button
     */
    @FXML
    void onActionOpenFile(ActionEvent event) {
        File file = directoryChooser.showDialog(stage);
        if (file != null) {
            if (validDirectory(file)) {
                isInvalidDirLbl.setText("Directory loaded successfully");
                isInvalidDirLbl.setVisible(true);
                filePathTxtBx.setText(file.getPath());
                validFile = true;
                trainModelBtn.setDisable(false);
                saveModelBtn.setDisable(true);
            } else {
                isInvalidDirLbl.setText("Invalid Directory!");
                isInvalidDirLbl.setVisible(true);
                filePathTxtBx.setText(" ");
                validFile = false;
                trainModelBtn.setDisable(true);
                saveModelBtn.setDisable(true);
            }
        }
    }

    /** Method trains dateset to NEW model
     * @param event take action on event of selecting Train Model button
     */
    @FXML
    void onActionTrainModel(ActionEvent event) {
        trainModelLbl.setVisible(true);
        trainModelLbl.setText("Dataset training to model...");
        if(validFile){
            File file = new File(filePathTxtBx.getText());
            model.fit(NeuralNetwork.parseDirectory(file));
            trainModelLbl.setText("Model training complete!!!");
            saveModelBtn.setDisable(false);
        }
    }

    /** Method saves model to root directory as default name of Saved_Model.zip
     * @param event take action on event of selecting Save Model button
     */
    @FXML
    void onActionSaveModel(ActionEvent event) {
        NeuralNetwork.saveModel(model);
        saveModelLbl.setVisible(true);
        saveModelLbl.setText("Model saved to root directory!");
    }

    /** Method loads model using file chooser and pulls alert if any issues occur with loading file
     * @param event take action on event of selecting Save Model button
     * @throws IOException
     */
    @FXML
    void onActionLoadModel(ActionEvent event)  {
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                model = ModelSerializer.restoreMultiLayerNetwork(new File(file.getPath()));
                loadModelLbl.setVisible(true);
                loadModelLbl.setText("Model successfully loaded.");
            } catch (RuntimeException e) {
                loadModelLbl.setVisible(true);
                loadModelLbl.setText("Error opening model!");
            } catch (IOException e) {
                log.error("Error loading Model Serialization: " + e);
            }
        }
    }


    /** Method resets admin password for system and pull appropriate alerts based on status
     * @param event take action on event of selecting Reset Password button
     */
    @FXML
    void onActionResetPass(ActionEvent event) {
        User.resetPassword();
    }

    /** Method determine if a default directory is valid based on the amount of directories and directory names
     * @param source file source of dataset
     * @return true false if valid directory
     */
    private static boolean validDirectory(File source) {
        try {
            List<File> files = Files.list(Paths.get(source.getPath()))
                    .map(Path::toFile)
                    .filter(File::isDirectory)
                    .collect(Collectors.toList());

            if(files.size() != Settings.letters.size()){
                return false;
            }
            for (int i = 0; i < files.size(); i++){
                if(!Settings.letters.contains(files.get(i).getName())){
                    return false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}
