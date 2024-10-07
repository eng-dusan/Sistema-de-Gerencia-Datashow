package com.example.atividade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RecoverPasswordController {
    @FXML
    private TextField usernameField;
    @FXML
    private Button backButton;
    @FXML
    private Button recoverButton;

    private DatabaseConnection databaseConnection = new DatabaseConnection();

    @FXML
    private void initialize() {
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                recoverButton.requestFocus();
            }
        });

        recoverButton.setOnAction(event -> handleRecoverButtonAction());
        backButton.setOnAction(event -> handleBackButtonAction());
    }

    @FXML
    private void handleRecoverButtonAction() {
        String username = usernameField.getText();

        if (username.isEmpty()) {
            showAlert("Erro de Recuperação", "Campo Usuário não pode estar vazio.");
            return;
        }

        String recoveredPassword = recoverPassword(username);

        if (recoveredPassword != null) {
            showAlert("Recuperação de Senha", "A senha para o usuário " + username + " é: " + recoveredPassword);
        } else {
            showAlert("Erro", "Usuário não cadastrado!");
        }
    }

    private String recoverPassword(String username) {
        String query = "SELECT senha FROM Login WHERE nome = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getString("senha");
            } else {
                return null;
            }

        } catch (SQLException e) {
            showAlert("Erro", "Erro ao recuperar a senha: " + e.getMessage());
            return null;
        }
    }

    @FXML
    private void handleBackButtonAction() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setTitle("Login");
            loginStage.setScene(new Scene(root));

            LoginController loginController = loader.getController();
            loginController.resetFields();
            loginStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível carregar a tela de login.");
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
