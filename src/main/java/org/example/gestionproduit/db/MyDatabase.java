package org.example.gestionproduit.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    // Nouvelle URL pour la base de données "3A"
    final String URL = "jdbc:mysql://localhost:3306/projetpi2?useSSL=false&serverTimezone=UTC";
    final String USERNAME = "root";
    final String PASSWORD = "";
    Connection connection;

    static MyDatabase instance;

    private MyDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connexion réussie à la base !");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion !");
            e.printStackTrace();
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
