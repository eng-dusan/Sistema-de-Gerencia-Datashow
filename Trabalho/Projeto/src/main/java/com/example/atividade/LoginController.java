package com.example.atividade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button forgetPasswordButton;
    @FXML
    private Button exitButton;

    private DatabaseConnection databaseConnection;

    public void resetFields() {
        usernameField.clear();
        passwordField.clear();
    }

    @FXML
    private void initialize() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();
            }
        });
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                loginButton.requestFocus();
            }
        });

        databaseConnection = new DatabaseConnection();

        loginButton.setOnAction(event -> handleLoginButtonAction());
        forgetPasswordButton.setOnAction(event -> handlePasswordButtonAction());
        exitButton.setOnAction(event -> handleExitButtonAction());
    }

    private void handleExitButtonAction() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    private void handleLoginButtonAction() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Erro de Login", "Usuário e senha não podem estar vazios.");
            return;
        }

        if (validateLogin(username, password)) {
            openNewWindow("menu.fxml", "Menu");
        } else {
            showAlert("Login falhou", "Usuário ou senha incorretos.");
        }
    }

    private void handlePasswordButtonAction() {
        openNewWindow("recoverPassword.fxml", "Recuperar Senha");
    }

    private void openNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erro", "Não foi possível abrir a nova janela: " + e.getMessage());
        }
    }

    private boolean validateLogin(String username, String password) {
        String query = "SELECT * FROM Login WHERE nome = ? AND senha = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Erro ao validar o login: " + e.getMessage());
            return false;
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
