package com.example.atividade;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EquipmentListRegController {

    @FXML
    private TableView<Equipment> equipmentTable;

    @FXML
    private TableColumn<Equipment, String> idColumn;
    @FXML
    private TableColumn<Equipment, String> nameColumn;
    @FXML
    private TableColumn<Equipment, String> descriptionColumn;

    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;

    private DatabaseConnection databaseConnection = new DatabaseConnection();

    private ObservableList<Equipment> equipmentList = FXCollections.observableArrayList();

    public void initialize() {
        databaseConnection = new DatabaseConnection();


        registerButton.setOnAction(event -> handleRegisterButtonAction());
        backButton.setOnAction(event -> handleBackButtonAction());
        deleteButton.setOnAction(event -> handleDeleteButtonAction());
        editButton.setOnAction(event -> handleEditButtonAction());


        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));


        loadEquipmentData();
    }

    private void handleRegisterButtonAction() {
        openNewWindow("equipmentReg.fxml", "Cadastro de Equipamentos"); // Abre a tela de registro
    }

    private void handleBackButtonAction() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close(); // Fecha a tela atual


            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent root = loader.load();
            Stage menuStage = new Stage();
            menuStage.setTitle("Menu");
            menuStage.setScene(new Scene(root));
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível carregar a tela de login."); // Mostra erro se falhar
        }
    }

    private void handleDeleteButtonAction() {
        Equipment selectedEquipment = equipmentTable.getSelectionModel().getSelectedItem(); // Pega equipamento selecionado
        if (selectedEquipment == null) {
            showAlert("Aviso", "Nenhum equipamento selecionado para excluir."); // Aviso se nada foi selecionado
            return;
        }


        showConfirmationAlert("Confirmação", "Você realmente deseja excluir o equipamento " + selectedEquipment.getName() + "?",
                () -> deleteEquipment(selectedEquipment.getId())); // Executa exclusão
    }

    private void handleEditButtonAction() {
        Equipment selectedEquipment = equipmentTable.getSelectionModel().getSelectedItem();
        if (selectedEquipment == null) {
            showAlert("Aviso", "Nenhum equipamento selecionado para editar.");
            return;
        }

        openEditWindow(selectedEquipment);
    }

    private void openEditWindow(Equipment equipment) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("equipmentEdit.fxml"));
            Parent root = loader.load();
            EquipmentEditController editController = loader.getController();
            editController.setEquipmentData(equipment);

            Stage stage = new Stage();
            stage.setTitle("Editar Equipamento");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


            equipmentTable.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível abrir a janela de edição: " + e.getMessage()); // Mostra erro se falhar
        }
    }

    private void deleteEquipment(String equipmentId) {
        String query = "DELETE FROM Equipamentos WHERE id = ?"; // Comando para deletar
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, equipmentId);
            int rowsAffected = preparedStatement.executeUpdate(); // Executa a atualização

            if (rowsAffected > 0) {
                showAlert("Sucesso", "Equipamento excluído com sucesso!"); // Confirma exclusão
                equipmentList.removeIf(equipment -> equipment.getId().equals(equipmentId)); // Remove da tabela
            } else {
                showAlert("Erro", "Não foi possível excluir o equipamento."); // Erro se falhar
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao excluir o equipamento: " + e.getMessage()); // Mostra erro se falhar
        }
    }

    private void openNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show(); // Abre a nova janela

            // Fecha a janela atual
            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("Erro", "Não foi possível abrir a nova janela: " + e.getMessage()); // Mostra erro se falhar
        }
    }

    private void showConfirmationAlert(String title, String message, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType confirmButton = new ButtonType("Confirmar");
        ButtonType cancelButton = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                onConfirm.run(); // Executa a ação de confirmação
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait(); // Mostra alerta
    }

    private void loadEquipmentData() {
        DatabaseConnection databaseConnection = new DatabaseConnection(); // Conexão com o banco
        String query = "SELECT * FROM Equipamentos"; // Comando para selecionar todos os equipamentos

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("nome");
                String description = resultSet.getString("descricao");
                equipmentList.add(new Equipment(id, name, description)); // Adiciona o equipamento à lista
            }

            equipmentTable.setItems(equipmentList); // Define os dados na tabela

        } catch (SQLException e) {
            System.out.println("Erro ao listar equipamentos: " + e.getMessage()); // Mostra erro se falhar
        }
    }
}
