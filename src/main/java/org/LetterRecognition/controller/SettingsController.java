package org.LetterRecognition.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import org.LetterRecognition.model.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Class controls Settings form window for letter recognition system.*/
public class SettingsController implements Initializable {
    private static final Logger log = LoggerFactory.getLogger(SettingsController.class);
    private Stage stage;
    private ToggleButton[] toggleButtons;

    @FXML
    private ToggleButton letter41Tgl;

    @FXML
    private ToggleButton letter42Tgl;

    @FXML
    private ToggleButton letter43Tgl;

    @FXML
    private ToggleButton letter44Tgl;

    @FXML
    private ToggleButton letter45Tgl;

    @FXML
    private ToggleButton letter46Tgl;

    @FXML
    private ToggleButton letter47Tgl;

    @FXML
    private ToggleButton letter48Tgl;

    @FXML
    private ToggleButton letter49Tgl;

    @FXML
    private ToggleButton letter4aTgl;

    @FXML
    private ToggleButton letter4bTgl;

    @FXML
    private ToggleButton letter4cTgl;

    @FXML
    private ToggleButton letter4dTgl;

    @FXML
    private ToggleButton letter4eTgl;

    @FXML
    private ToggleButton letter4fTgl;

    @FXML
    private ToggleButton letter50Tgl;

    @FXML
    private ToggleButton letter51Tgl;

    @FXML
    private ToggleButton letter52Tgl;

    @FXML
    private ToggleButton letter53Tgl;

    @FXML
    private ToggleButton letter54Tgl;

    @FXML
    private ToggleButton letter55Tgl;

    @FXML
    private ToggleButton letter56Tgl;

    @FXML
    private ToggleButton letter57Tgl;

    @FXML
    private ToggleButton letter58Tgl;

    @FXML
    private ToggleButton letter59Tgl;

    @FXML
    private ToggleButton letter5aTgl;

    @FXML
    private ToggleButton letter61Tgl;

    @FXML
    private ToggleButton letter62Tgl;

    @FXML
    private ToggleButton letter63Tgl;

    @FXML
    private ToggleButton letter64Tgl;

    @FXML
    private ToggleButton letter65Tgl;

    @FXML
    private ToggleButton letter66Tgl;

    @FXML
    private ToggleButton letter67Tgl;

    @FXML
    private ToggleButton letter68Tgl;

    @FXML
    private ToggleButton letter69Tgl;

    @FXML
    private ToggleButton letter6aTgl;

    @FXML
    private ToggleButton letter6bTgl;

    @FXML
    private ToggleButton letter6cTgl;

    @FXML
    private ToggleButton letter6dTgl;

    @FXML
    private ToggleButton letter6eTgl;

    @FXML
    private ToggleButton letter6fTgl;

    @FXML
    private ToggleButton letter70Tgl;

    @FXML
    private ToggleButton letter71Tgl;

    @FXML
    private ToggleButton letter72Tgl;

    @FXML
    private ToggleButton letter73Tgl;

    @FXML
    private ToggleButton letter74Tgl;

    @FXML
    private ToggleButton letter75Tgl;

    @FXML
    private ToggleButton letter76Tgl;

    @FXML
    private ToggleButton letter77Tgl;

    @FXML
    private ToggleButton letter78Tgl;

    @FXML
    private ToggleButton letter79Tgl;

    @FXML
    private ToggleButton letter7aTgl;

    @FXML
    private Label totalCntLbl;

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

    /** Updates all uppercase letters to be enabled.
     * @param event take action on event of selecting All On from uppercase tab.
     * @throws IOException handle exceptions
     */
    @FXML
    void onActionAllOnUpperCase(ActionEvent event) {
        Settings.enableAllUpperCase();
        updateAllTglBtn();
    }

    /** Updates all uppercase letters to be disabled
     * @param event take action on event of selecting All Off from uppercase tab
     * @throws IOException handle exceptions
     */
    @FXML
    void onActionAllOffUpperCase(ActionEvent event) {
        Settings.disableAllUpperCase();
        updateAllTglBtn();
    }

    /** Updates all lowercase letters to be enabled
     * @param event take action on event of selecting All On from lowercase tab
     * @throws IOException handle exceptions
     */
    @FXML
    void onActionAllOnLowerCase(ActionEvent event) {
        Settings.enableAllLowerCase();
        updateAllTglBtn();
    }

    /** Updates all lowercase letters to be disabled
     * @param event take action on event of selecting All Off from lowercase tab
     * @throws IOException handle exceptions
     */
    @FXML
    void onActionAllOffLowerCase(ActionEvent event) {
        Settings.disableAllLowerCase();
        updateAllTglBtn();
    }

    /** Updates selected letter to be enabled or disabled
     * @param actionEvent handles any toggle switched for each letter
     */
    @FXML
    void onActionLetterTgl(ActionEvent actionEvent) {
        ToggleButton selectedButton = (ToggleButton) actionEvent.getSource();
        String btnId = parseTglButtonId(selectedButton);
        Settings.setLetterEnabled(btnId, selectedButton.isSelected());
        updateTglBtnLbl(selectedButton);
    }

    /** Updates button label to be On or Off for selected letter
     * @param toggleButton button for selected letter
     */
    private void updateTglBtnLbl(ToggleButton toggleButton){
        if (toggleButton.isSelected()){
            toggleButton.setText("On");
        }
        else {
            toggleButton.setText("Off");
        }
        totalCntLbl.setText(String.valueOf(Settings.getLetterCount()));
    }

    /** Updates all toggle button selection status and label from current list in settings */
    private void updateAllTglBtn(){
        for(int i = 0; i < toggleButtons.length; i++){
            Boolean selected = Settings.getLetterEnabled(Settings.letters.get(i));
            toggleButtons[i].setSelected(selected);
            updateTglBtnLbl(toggleButtons[i]);
        }
    }

    /** Pulls hex letter value from button ID
     * @param toggleButton selected button
     * @return hex letter value of selected button
     */
    private String parseTglButtonId(ToggleButton toggleButton){
        String tglButtonId = toggleButton.getId();
        String testLtr = null;
        String foundLtr = null;
        for(int i = 0; i < Settings.letters.size(); i++) {
            testLtr = Settings.letters.get(i);
            if (tglButtonId.indexOf(testLtr) > -1) {
                foundLtr = testLtr;
            }
        }
        return foundLtr;
    }

    /** Creates an array of all button to handle as a group.
     * Updates all button labels and selection status.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        toggleButtons = new ToggleButton[]{
                letter41Tgl, letter42Tgl, letter43Tgl, letter44Tgl, letter45Tgl, letter46Tgl, letter47Tgl, letter48Tgl,
                letter49Tgl, letter4aTgl, letter4bTgl, letter4cTgl, letter4dTgl, letter4eTgl, letter4fTgl, letter50Tgl,
                letter51Tgl, letter52Tgl, letter53Tgl, letter54Tgl, letter55Tgl, letter56Tgl, letter57Tgl, letter58Tgl,
                letter59Tgl, letter5aTgl,

                letter61Tgl, letter62Tgl, letter63Tgl, letter64Tgl, letter65Tgl, letter66Tgl, letter67Tgl, letter68Tgl,
                letter69Tgl, letter6aTgl, letter6bTgl, letter6cTgl, letter6dTgl, letter6eTgl, letter6fTgl, letter70Tgl,
                letter71Tgl, letter72Tgl, letter73Tgl, letter74Tgl, letter75Tgl, letter76Tgl, letter77Tgl, letter78Tgl,
                letter79Tgl, letter7aTgl};

        updateAllTglBtn();
    }
}

