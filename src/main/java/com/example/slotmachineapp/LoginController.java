package com.example.slotmachineapp;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private UserManager userManager = UserManager.getInstance();

    @FXML
    public void initialize() {
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.contains(" ")) {
                usernameField.setText(newValue.replace(" ", ""));
            }
        });


        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.contains(" ")) {
                passwordField.setText(newValue.replace(" ", ""));
            }
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty()) {
            errorLabel.setText("Username cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            errorLabel.setText("Password cannot be empty");
            return;
        }

        if (userManager.loginUser(username, password)) {
            try {

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/SlotMachineView.fxml"));
                Scene scene = new Scene(fxmlLoader.load());
                

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                errorLabel.setText("Error loading game: " + e.getMessage());
            }
        } else {
            errorLabel.setText("Invalid username or password");
            passwordField.clear();
        }
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty()) {
            errorLabel.setText("Username cannot be empty");
            return;
        }

        if (password.isEmpty()) {
            errorLabel.setText("Password cannot be empty");
            return;
        }

        if (username.length() < 3) {
            errorLabel.setText("Username must be at least 3 characters");
            return;
        }

        if (password.length() < 4) {
            errorLabel.setText("Password must be at least 4 characters");
            return;
        }

        if (userManager.registerUser(username, password)) {
            errorLabel.setText("Registration successful! You can now login.");
            usernameField.clear();
            passwordField.clear();
        } else {
            errorLabel.setText("Username already exists");
            passwordField.clear();
        }
    }
} 