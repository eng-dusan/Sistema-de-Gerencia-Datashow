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

public class EquipmentRegController {
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private Button backButton;
    @FXML
    private Button registerButton;

    private DatabaseConnection databaseConnection = new DatabaseConnection();

    @FXML
    private void initialize() {

        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                descriptionField.requestFocus();
            }
        });
        descriptionField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                registerButton.requestFocus();
            }
        });


        registerButton.setOnAction(event -> handleRegisterButtonAction());
        backButton.setOnAction(event -> handleBackButtonAction());
    }

    @FXML
    private void handleRegisterButtonAction() {
        String name = nameField.getText();
        String description = descriptionField.getText();


        if (name.isEmpty() || description.isEmpty()) {
            showAlert("Erro de cadastro", "Nome e descrição não podem estar vazios.");
            return;
        }


        if (equipmentExists(name)) {
            showAlert("Erro", "Equipamento já cadastrado com esse nome.");
            return;
        }


        String query = "INSERT INTO Equipamentos (nome, descricao) VALUES (?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {


            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.executeUpdate();
            showAlert("Sucesso", "Equipamento registrado com sucesso!");
            handleBackButtonAction();
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao registrar o equipamento: " + e.getMessage()); // Mostra erro se falhar
        }
    }


    private boolean equipmentExists(String name) {
        String query = "SELECT * FROM Equipamentos WHERE nome = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, name);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            showAlert("Erro", "Erro ao verificar equipamento: " + e.getMessage());
            return true; // Assume que o equipamento já existe em caso de erro
        }
    }

    @FXML
    private void handleBackButtonAction() {
        try {
            // Fecha a tela de registro atual
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();

            // Carrega a tela de lista de equipamentos
            FXMLLoader loader = new FXMLLoader(getClass().getResource("equipmentListReg.fxml"));
            Parent root = loader.load();

            Stage equipmentListRegStage = new Stage();
            equipmentListRegStage.setTitle("Lista de Equipamentos");
            equipmentListRegStage.setScene(new Scene(root));

            equipmentListRegStage.show(); // Mostra a nova tela

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível carregar a tela de lista de equipamentos."); // Mostra erro se falhar
        }
    }

    // Mostra um alerta
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Mostra alerta
    }
}
