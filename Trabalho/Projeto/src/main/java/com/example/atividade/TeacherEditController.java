package com.example.atividade;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TeacherEditController {

    @FXML
    private TextField raField;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField subjectField;

    private Teacher teacher;
    private DatabaseConnection databaseConnection;

    public void initialize() {
        databaseConnection = new DatabaseConnection();
    }

    public void setTeacherData(Teacher teacher) {
        this.teacher = teacher;
        raField.setText(teacher.getRa());
        usernameField.setText(teacher.getUsername());
        subjectField.setText(teacher.getSubject());
    }

    @FXML
    private void handleSaveButtonAction() {
        String ra = raField.getText();
        String username = usernameField.getText();
        String subject = subjectField.getText();

        if (ra.isEmpty() || username.isEmpty() || subject.isEmpty()) {
            showAlert("Erro de validação", "Todos os campos devem ser preenchidos.");
            return;
        }

        teacher.setRa(ra);
        teacher.setUsername(username);
        teacher.setSubject(subject);
        updateTeacherInDatabase();

        Stage stage = (Stage) raField.getScene().getWindow();
        stage.close();
    }

    private void updateTeacherInDatabase() {
        String query = "UPDATE Professores SET ra = ?, nome = ?, disciplina = ? WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, teacher.getRa());
            preparedStatement.setString(2, teacher.getUsername());
            preparedStatement.setString(3, teacher.getSubject());
            preparedStatement.setString(4, teacher.getId());
            preparedStatement.executeUpdate();

            showAlert("Sucesso", "Professor atualizado com sucesso!");

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erro", "Erro ao atualizar o professor: " + e.getMessage());
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
