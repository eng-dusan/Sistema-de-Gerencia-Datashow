package com.example.atividade;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    private Button teacherButton;
    @FXML
    private Button equipmentButton;
    @FXML
    private Button backButton;

    @FXML
    private void initialize() {
        teacherButton.setOnAction(event -> handleTeacherButtonAction());
        equipmentButton.setOnAction(event -> handleEquipmentButtonAction());
        backButton.setOnAction(event -> handleBackButtonAction());
    }

    private void handleTeacherButtonAction() {
        openNewWindow("teacherListReg.fxml", "Lista de Professores");
    }

    private void handleEquipmentButtonAction() {
        openNewWindow("equipmentListReg.fxml", "Lista de Equipamentos");
    }

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

    private void openNewWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();

            Stage currentStage = (Stage) teacherButton.getScene().getWindow();
            Stage currentStage1 = (Stage) equipmentButton.getScene().getWindow();

            currentStage.close();
            currentStage1.close();
        } catch (IOException e) {
            showAlert("Erro", "Não foi possível abrir a nova janela: " + e.getMessage());
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
