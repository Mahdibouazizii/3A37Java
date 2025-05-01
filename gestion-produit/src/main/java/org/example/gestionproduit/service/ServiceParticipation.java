package org.example.gestionproduit.service;

import org.example.gestionproduit.entity.Participation;
import org.example.gestionproduit.db.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceParticipation {

    private final Connection connection;

    public ServiceParticipation() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public void ajouter(Participation participation) {
        String sql = "INSERT INTO participation (idevenement_id, iduser_id, age, nbrplace, statut) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, participation.getIdevenement_id());
            stmt.setInt(2, participation.getIduser_id());
            stmt.setInt(3, participation.getAge());
            stmt.setInt(4, participation.getNbrplace());
            stmt.setString(5, participation.getStatut());

            stmt.executeUpdate();
            System.out.println("✅ Participation ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de l'ajout de la participation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Participation> getByUserId(int userId) {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT * FROM participation WHERE iduser_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Participation p = new Participation();
                p.setId(rs.getInt("id"));
                p.setIdevenement_id(rs.getInt("idevenement_id"));
                p.setIduser_id(rs.getInt("iduser_id"));
                p.setAge(rs.getInt("age"));
                p.setNbrplace(rs.getInt("nbrplace"));
                p.setStatut(rs.getString("statut"));
                participations.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération des participations par utilisateur : " + e.getMessage());
        }

        return participations;
    }

    public List<Participation> getAll() {
        List<Participation> participations = new ArrayList<>();
        String sql = "SELECT * FROM participation";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Participation p = new Participation();
                p.setId(rs.getInt("id"));
                p.setIdevenement_id(rs.getInt("idevenement_id"));
                p.setIduser_id(rs.getInt("iduser_id"));
                p.setAge(rs.getInt("age"));
                p.setNbrplace(rs.getInt("nbrplace"));
                p.setStatut(rs.getString("statut"));
                participations.add(p);
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la récupération de toutes les participations : " + e.getMessage());
        }

        return participations;
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM participation WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("🗑 Participation supprimée avec succès !");
            } else {
                System.out.println("⚠️ Aucune participation trouvée avec cet ID.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la suppression de la participation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifier(Participation participation) {
        String sql = "UPDATE participation SET idevenement_id = ?, age = ?, nbrplace = ?, statut = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, participation.getIdevenement_id());
            stmt.setInt(2, participation.getAge());
            stmt.setInt(3, participation.getNbrplace());
            stmt.setString(4, participation.getStatut());
            stmt.setInt(5, participation.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("🔄 Participation modifiée avec succès !");
            } else {
                System.out.println("⚠️ Aucune participation trouvée avec cet ID pour la modification.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la modification de la participation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isUserAlreadyParticipating(int userId, int eventId) {
        // Code pour vérifier si une participation existe déjà pour cet utilisateur et cet événement
        // Par exemple, une requête SQL : "SELECT COUNT(*) FROM Participation WHERE iduser_id = ? AND idevenement_id = ?"
        String query = "SELECT COUNT(*) FROM participation WHERE iduser_id = ? AND idevenement_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) { // Utilisation de la connexion déjà existante
            statement.setInt(1, userId);
            statement.setInt(2, eventId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Si le compte est > 0, cela signifie que l'utilisateur a déjà participé
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }





}