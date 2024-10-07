package com.example.atividade;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TeacherListRegController {

    @FXML
    private TableView<Teacher> teacherTable;
    @FXML
    private TableColumn<Teacher, String> idColumn;
    @FXML
    private TableColumn<Teacher, String> raColumn;
    @FXML
    private TableColumn<Teacher, String> usernameColumn;
    @FXML
    private TableColumn<Teacher, String> subjectColumn;
    @FXML
    private Button registerButton;
    @FXML
    private Button backButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button editButton;

    private DatabaseConnection databaseConnection = new DatabaseConnection();
    private ObservableList<Teacher> teacherList = FXCollections.observableArrayList();

    public void initialize() {
        databaseConnection = new DatabaseConnection();
        registerButton.setOnAction(event -> handleRegisterButtonAction());
        backButton.setOnAction(event -> handleBackButtonAction());
        deleteButton.setOnAction(event -> handleDeleteButtonAction());
        editButton.setOnAction(event -> handleEditButtonAction());
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        raColumn.setCellValueFactory(new PropertyValueFactory<>("ra"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        loadTeacherData();
    }

    private void handleRegisterButtonAction() {
        openNewWindow("teacherReg.fxml", "Cadastro de Professores");
    }

    private void handleBackButtonAction() {
        try {
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("menu.fxml"));
            Parent root = loader.load();
            Stage menuStage = new Stage();
            menuStage.setTitle("Menu");
            menuStage.setScene(new Scene(root));
            menuStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível carregar a tela de menu.");
        }
    }

    private void handleDeleteButtonAction() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            showAlert("Aviso", "Nenhum professor selecionado para excluir.");
            return;
        }
        showConfirmationAlert("Confirmação", "Você realmente deseja excluir o professor " + selectedTeacher.getUsername() + "?",
                () -> deleteTeacher(selectedTeacher.getId()));
    }

    private void deleteTeacher(String teacherId) {
        String query = "DELETE FROM Professores WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, teacherId);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                showAlert("Sucesso", "Professor excluído com sucesso!");
                teacherList.removeIf(teacher -> teacher.getId().equals(teacherId));
            } else {
                showAlert("Erro", "Não foi possível excluir o professor.");
            }
        } catch (SQLException e) {
            showAlert("Erro", "Erro ao excluir o professor: " + e.getMessage());
        }
    }

    private void handleEditButtonAction() {
        Teacher selectedTeacher = teacherTable.getSelectionModel().getSelectedItem();
        if (selectedTeacher == null) {
            showAlert("Aviso", "Nenhum professor selecionado para editar.");
            return;
        }
        openEditWindow(selectedTeacher);
    }

    private void openEditWindow(Teacher teacher) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("teacherEdit.fxml"));
            Parent root = loader.load();
            TeacherEditController editController = loader.getController();
            editController.setTeacherData(teacher);
            Stage stage = new Stage();
            stage.setTitle("Editar Professor");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            teacherTable.refresh();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erro", "Não foi possível abrir a janela de edição: " + e.getMessage());
        }
    }

    private void openNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            Stage currentStage = (Stage) registerButton.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            showAlert("ERRO", "Não foi possível abrir a nova janela: " + e.getMessage());
        }
    }

    private void showConfirmationAlert(String title, String message, Runnable onConfirm) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(response -> {
            if (response.getButtonData().isDefaultButton()) {
                onConfirm.run();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadTeacherData() {
        String query = "SELECT * FROM Professores";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String ra = resultSet.getString("ra");
                String username = resultSet.getString("nome");
                String subject = resultSet.getString("disciplina");
                teacherList.add(new Teacher(id, ra, username, subject));
            }
            teacherTable.setItems(teacherList);
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuários: " + e.getMessage());
        }
    }
}
