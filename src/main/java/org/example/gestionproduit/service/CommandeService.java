package org.example.gestionproduit.service;

import org.example.gestionproduit.db.DBConnection;
import org.example.gestionproduit.entity.Commande;
import java.sql.*;

public class CommandeService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/projetpi2?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";      // Adjust as needed
    private static final String PASS = "";          // Adjust as needed

    public CommandeService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS commande (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "total DECIMAL(10,2) NOT NULL, " +
                    "details TEXT NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public boolean addCommande(Commande commande) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO commande (total, details, created_at, id_user) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setDouble(1, commande.getTotal());
                statement.setString(2, commande.getDetails());
                statement.setTimestamp(3, commande.getCreatedAt());
                statement.setInt(4, commande.getIdUser());  // Store the user ID
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int addCommandeAndReturnId(Commande commande) {
        String sql = "INSERT INTO commande (total, details, created_at, id_user) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, commande.getTotal());
            stmt.setString(2, commande.getDetails());
            stmt.setTimestamp(3, commande.getCreatedAt());
            stmt.setInt(4, commande.getIdUser());
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) return -1;

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getLastCommandeIdByUser(int userId) {
        String query = "SELECT id FROM commande WHERE id_user = ? ORDER BY created_at DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }




}
