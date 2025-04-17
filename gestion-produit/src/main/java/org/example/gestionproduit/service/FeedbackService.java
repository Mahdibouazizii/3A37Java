package org.example.gestionproduit.service;

import org.example.gestionproduit.db.DBConnection;
import org.example.gestionproduit.entity.Feedback;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/projetpi2?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";       // adjust as needed
    private static final String PASS = "";           // adjust as needed

    public FeedbackService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            // Use "produit_id" for consistency with the French naming.
            String sql = "CREATE TABLE IF NOT EXISTS feedback (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "produit_id INT NOT NULL, " +
                    "commentaire TEXT NOT NULL, " +
                    "rating INT NOT NULL, " +
                    "user_id INT NOT NULL, " +
                    "FOREIGN KEY (produit_id) REFERENCES produit(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Feedback getFeedbackByUserAndProduct(int userId, int productId) {
        // Query the database to check if the user has already given feedback for this product
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM feedback WHERE user_id = ? AND produit_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, userId);
                statement.setInt(2, productId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return new Feedback(
                            resultSet.getInt("produit_id"),
                            resultSet.getString("commentaire"),
                            resultSet.getInt("rating"),
                            resultSet.getInt("user_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void updateFeedback(Feedback feedback) {
        // Update existing feedback in the database
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "UPDATE feedback SET commentaire = ?, rating = ? WHERE user_id = ? AND produit_id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, feedback.getCommentaire());
                statement.setInt(2, feedback.getRating());
                statement.setInt(3, feedback.getUserId());
                statement.setInt(4, feedback.getProductId());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    // Returns all feedback for a given product (by produit_id)
    public List<Feedback> getFeedbacksForProduct(int produitId) {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE produit_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int prodId = rs.getInt("produit_id");
                    String commentaire = rs.getString("commentaire");
                    int rating = rs.getInt("rating");
                    int userId = rs.getInt("user_id");
                    feedbackList.add(new Feedback(id, prodId, commentaire, rating, userId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return feedbackList;
    }

    // Calculates the average rating for a given product (by produit_id)
    public double getAverageRating(int produitId) {
        String sql = "SELECT AVG(rating) AS avgRating FROM feedback WHERE produit_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avgRating");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public void addFeedback(Feedback feedback) {
        String sql = "INSERT INTO feedback (produit_id, commentaire, rating, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, feedback.getProductId());
            pstmt.setString(2, feedback.getCommentaire());
            pstmt.setInt(3, feedback.getRating());
            pstmt.setInt(4, feedback.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
