module com.example.atividade {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.atividade to javafx.fxml;
    exports com.example.atividade;
}