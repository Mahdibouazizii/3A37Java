package org.example.gestionproduit.service;

import org.example.gestionproduit.db.DBConnection;
import org.example.gestionproduit.entity.Reclamation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {

    public boolean addReclamation(Reclamation reclamation) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO reclamation (user_id, sujet, description, created_at) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, reclamation.getUserId());
            stmt.setString(2, reclamation.getSujet());
            stmt.setString(3, reclamation.getDescription());
            stmt.setTimestamp(4, reclamation.getCreatedAt());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reclamation> getReclamationsByUser(int userId) {
        List<Reclamation> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM reclamation WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("sujet"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Reclamation> getAllReclamations() {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reclamation";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("sujet"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

}
