package org.example.gestionproduit.service;

import org.example.gestionproduit.entity.event;
import org.example.gestionproduit.db.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceEvent implements IService<event> {

    private final Connection connection;

    public ServiceEvent() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(event e) throws SQLException {
        String sql = "INSERT INTO event (nom, description, heur_debut, heur_fin, image, nbrplacetottale) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNom());
            ps.setString(2, e.getDescription());
            ps.setTimestamp(3, new Timestamp(e.getHeur_debut().getTime()));
            ps.setTimestamp(4, new Timestamp(e.getHeur_fin().getTime()));
            ps.setString(5, e.getImage());
            ps.setInt(6, e.getNbrplacetottale());

            ps.executeUpdate();
            System.out.println("✅ Événement ajouté !");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            // Handle exception with more detailed information
            System.err.println("❌ Erreur lors de l'ajout de l'événement : " + ex.getMessage());
            throw ex; // Optionally rethrow the exception
        }
    }

    @Override
    public void modifier(event updatedEvent) throws SQLException {
        String sql = "UPDATE event SET nom = ?, description = ?, heur_debut = ?, heur_fin = ?, nbrplacetottale = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, updatedEvent.getNom());
            stmt.setString(2, updatedEvent.getDescription());
            stmt.setTimestamp(3, new Timestamp(updatedEvent.getHeur_debut().getTime()));
            stmt.setTimestamp(4, new Timestamp(updatedEvent.getHeur_fin().getTime()));
            stmt.setInt(5, updatedEvent.getNbrplacetottale());
            stmt.setInt(6, updatedEvent.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucune ligne mise à jour. Vérifiez l'ID.");
            }
            System.out.println("✅ Événement mis à jour !");
        } catch (SQLException ex) {
            // Handle exception
            System.err.println("❌ Erreur lors de la mise à jour de l'événement : " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM event WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Aucun événement trouvé à supprimer avec cet ID.");
            }
            System.out.println("🗑️ Événement supprimé !");
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la suppression de l'événement : " + ex.getMessage());
            throw ex;
        }
    }

    @Override
    public List<event> afficher() throws SQLException {
        List<event> events = new ArrayList<>();
        String sql = "SELECT * FROM event";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                event e = new event(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getTimestamp("heur_debut"),
                        rs.getTimestamp("heur_fin"),
                        rs.getString("image"),
                        rs.getInt("nbrplacetottale")
                );
                events.add(e);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de l'affichage des événements : " + ex.getMessage());
            throw ex;
        }

        return events;
    }

    public event getEventById(int eventId) {
        event e = null;
        String sql = "SELECT * FROM event WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    e = new event();
                    e.setId(rs.getInt("id"));
                    e.setNom(rs.getString("nom"));
                    e.setDescription(rs.getString("description"));
                    e.setHeur_debut(rs.getTimestamp("heur_debut"));
                    e.setHeur_fin(rs.getTimestamp("heur_fin"));
                    e.setImage(rs.getString("image"));
                    e.setNbrplacetottale(rs.getInt("nbrplacetottale"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération de l'événement : " + ex.getMessage());
        }

        return e;
    }

    public void decrementerPlacesDisponibles(int eventId, int nbrPlaces) {
        String query = "UPDATE event SET nbrplacetottale = nbrplacetottale - ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nbrPlaces);
            stmt.setInt(2, eventId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la mise à jour des places disponibles : " + e.getMessage());
        }
    }



}
