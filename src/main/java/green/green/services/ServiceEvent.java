package green.green.services;

import green.green.Entity.event;
import green.green.outils.MyDatabase;

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
        }
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM event WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("🗑️ Événement supprimé !");
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
        }

        return events;
    }

    public event getEventById(int eventId) {
        event e = null;
        String sql = "SELECT * FROM event WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                e = new event();
                e.setId(rs.getInt("id"));
                e.setNom(rs.getString("nom"));
                // Récupère les autres champs nécessaires
            }
        } catch (SQLException ex) {
            System.err.println("❌ Erreur lors de la récupération de l'événement : " + ex.getMessage());
            ex.printStackTrace();
        }
        return e;
    }
}
