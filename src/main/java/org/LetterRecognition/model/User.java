package org.LetterRecognition.model;

import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.LetterRecognition.DAO.UserDaoImpl;
import org.nd4j.shade.guava.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/** Class controls user authentication via password. */
public class User {

    /** Method prompts password dialog box and checks the hash value against database
     * @return true or false if user password is correct
     */
    private static Boolean checkPassword() {
        Dialog passwordDialog = new Dialog<>();
        passwordDialog.setTitle("Authorize Access!");
        passwordDialog.setHeaderText("Enter Password");
        passwordDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        PasswordField passwordField = new PasswordField();
        HBox hBox = new HBox();
        hBox.getChildren().add(passwordField);
        passwordDialog.getDialogPane().setContent(hBox);
        passwordDialog.showAndWait();

        return UserDaoImpl.authUser(Hashing.sha256().hashString(passwordField.getText().toString(),
                StandardCharsets.UTF_8).toString());
    }

    /** Method returns prompt indicating password is invalid */
    private static void invalidEntry() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Invalid password entered try again!"
                , ButtonType.OK);
        alert.showAndWait();
    }

    /** Method is used to retrieve user password and provide prompt for invalid entry
     * @return true or false if user password is correct
     */
    public static Boolean authUser() {
        Boolean authUser = false;
        if(checkPassword()){
            authUser = true;
        }
        else {
            invalidEntry();
        }
        return authUser;
    }

    /** Method is used to reset user password by displaying prompt
     * @return if password was reset successfully
     */
    public static Boolean resetPassword() {
        Boolean resetPass = false;
        Dialog passwordDialog = new Dialog<>();
        passwordDialog.setTitle("Reset Password");
        passwordDialog.setHeaderText("Enter Password");
        passwordDialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField = new TextField();
        HBox hBox = new HBox();
        hBox.getChildren().add(textField);
        passwordDialog.getDialogPane().setContent(hBox);
        passwordDialog.showAndWait();
        if(resetConfirm()) {
            resetPass = UserDaoImpl.setPassword(Hashing.sha256().hashString(textField.getText().toString(),
                    StandardCharsets.UTF_8).toString());
            if (!resetPass) {
                resetFailed();
            }
            else {
                resetSuccess();
            }
        }
        return resetPass;
    }

    /** Method returns prompt indicating password was unable to be set */
    private static void resetFailed() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Unable to set password try again!"
                , ButtonType.OK);
        alert.showAndWait();
    }

    /** Method returns prompt indicating password was reset successfully */
    private static void resetSuccess() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password was reset successfully!!!"
                , ButtonType.OK);
        alert.showAndWait();
    }

    /** Method returns prompt confirming password reset */
    private static Boolean resetConfirm() {
        Alert alert = new Alert(Alert.AlertType.WARNING, "Are you sure you would like to reset your password! " +
                "If password is lost settings can not be altered."
                , ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            return true;
        }
        return false;
    }
}
