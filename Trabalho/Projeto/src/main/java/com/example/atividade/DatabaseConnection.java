package com.example.atividade;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL = "jdbc:sqlite:E:\\banco.db";

    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL);
        } catch (SQLException e){
            System.err.println("Erro ao conectar com o banco de dados: " + e.getMessage());
        }
        return connection;
    }
}
