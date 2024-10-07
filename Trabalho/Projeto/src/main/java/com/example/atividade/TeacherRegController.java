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

public class TeacherRegController {
    @FXML
    private TextField raField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField subjectField;
    @FXML
    private Button backButton;
    @FXML
    private Button registerButton;

    private DatabaseConnection databaseConnection = new DatabaseConnection();

    @FXML
    private void initialize() {
        raField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                usernameField.requestFocus();
            }
        });
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                subjectField.requestFocus();
            }
        });
        subjectField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                registerButton.requestFocus();
            }
        });

        registerButton.setOnAction(event -> handleRegisterButtonAction());
        backButton.setOnAction(event -> handleBackButtonAction());
    }

    @FXML
    private void handleRegisterButtonAction() {
        String ra = raField.getText();
        String username = usernameField.getText();
        String subject = subjectField.getText();

        if (ra.isEmpty() || username.isEmpty() || subject.isEmpty()) {
            showAlert("Erro de cadastro", "RA, nome e disciplina nao podem estar vazios.");
            return;
        }

        if (userExists(ra)) {
            showAlert("Erro", "Professor ja cadastrado com esse RA.");
            return;
        }

        String query = "INSERT INTO Professores (ra, nome, disciplina) VALUES (?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, ra);
            preparedStatement.setString(2, username);
            preparedStatement.setString(3, subject);
            preparedStatement.executeUpdate();
            showAlert("Sucesso", "Professor registrado com sucesso!");
            handleBackButtonAction();
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao registrar o professor: " + e.getMessage());
        }
    }

    private boolean userExists(String ra) {
        String query = "SELECT * FROM Professores WHERE ra = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, ra);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao verificar professor: " + e.getMessage());
            return true;
        }
    }

    @FXML
    private void handleBackButtonAction() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("teacherListReg.fxml"));
            Parent root = loader.load();

            Stage teacherListRegStage = new Stage();
            teacherListRegStage.setTitle("Lista de Professores");
            teacherListRegStage.setScene(new Scene(root));

            teacherListRegStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Nao foi possivel carregar a tela de lista de professores.");
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
