package org.example.gestionproduit.service;

import org.example.gestionproduit.entity.Facture;
import org.example.gestionproduit.db.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class FactureService {


    public boolean updateFactureByCommandeId(Facture facture) {
        String query = "UPDATE facture SET total = ?, details = ? WHERE id_commande = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDouble(1, facture.getTotal());
            stmt.setString(2, facture.getDetails());
            stmt.setInt(3, facture.getIdCommande());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addFacture(Facture facture) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO facture (id_commande, total, details, created_at) VALUES (?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, facture.getIdCommande());
                statement.setDouble(2, facture.getTotal());
                statement.setString(3, facture.getDetails());
                statement.setTimestamp(4, facture.getCreatedAt());
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
