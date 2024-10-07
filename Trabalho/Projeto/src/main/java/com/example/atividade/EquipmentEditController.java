package com.example.atividade;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EquipmentEditController {

    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;

    private Equipment equipment;
    private DatabaseConnection databaseConnection;


    public void initialize() {
        databaseConnection = new DatabaseConnection();
    }


    public void setEquipmentData(Equipment equipment) {
        this.equipment = equipment;
        nameField.setText(equipment.getName());
        descriptionField.setText(equipment.getDescription());
    }


    @FXML
    private void handleSaveButtonAction() {
        String name = nameField.getText();
        String description = descriptionField.getText();


        if (name.isEmpty() || description.isEmpty()) {
            showAlert("Erro de validação", "Todos os campos devem ser preenchidos.");
            return;
        }


        equipment.setName(name);
        equipment.setDescription(description);
        updateEquipmentInDatabase();


        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }


    private void updateEquipmentInDatabase() {
        String query = "UPDATE Equipamentos SET nome = ?, descricao = ? WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, equipment.getName());
            preparedStatement.setString(2, equipment.getDescription());
            preparedStatement.setString(3, equipment.getId());
            preparedStatement.executeUpdate();

            showAlert("Sucesso", "Equipamento atualizado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao atualizar o equipamento: " + e.getMessage());
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
