package org.example.gestionproduit.service;

import org.example.gestionproduit.db.DBConnection;
import org.example.gestionproduit.entity.Commande;

import java.sql.*;

public class CommandeService {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/projetpi2?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    public CommandeService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = """
                CREATE TABLE IF NOT EXISTS commande (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    total DECIMAL(10,2) NOT NULL,
                    details TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    id_user INT,
                    adresse VARCHAR(255),
                    type_paiement VARCHAR(100),
                    status VARCHAR(100),
                    FOREIGN KEY (id_user) REFERENCES user(id)
                )
                """;
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean validateCommande(int commandeId) {
        String sql = "UPDATE commande SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "confirmé");
            stmt.setInt(2, commandeId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public boolean addCommande(Commande commande) {
        String sql = """
            INSERT INTO commande (total, details, created_at, id_user, adresse, type_paiement, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setDouble(1, commande.getTotal());
            statement.setString(2, commande.getDetails());
            statement.setTimestamp(3, commande.getCreatedAt());
            statement.setInt(4, commande.getIdUser());
            statement.setString(5, commande.getAdresse());
            statement.setString(6, commande.getTypePaiement());
            statement.setString(7, commande.getStatus());

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int addCommandeAndReturnId(Commande commande) {
        String sql = """
            INSERT INTO commande (total, details, created_at, id_user, adresse, type_paiement, status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDouble(1, commande.getTotal());
            stmt.setString(2, commande.getDetails());
            stmt.setTimestamp(3, commande.getCreatedAt());
            stmt.setInt(4, commande.getIdUser());
            stmt.setString(5, commande.getAdresse());
            stmt.setString(6, commande.getTypePaiement());
            stmt.setString(7, commande.getStatus());

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
