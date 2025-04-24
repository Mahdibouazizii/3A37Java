
package org.example.gestionproduit.service;

import org.example.gestionproduit.entity.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ProduitService {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/projetpi2?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "";

    public ProduitService() {
        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sql = "CREATE TABLE IF NOT EXISTS produit (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom VARCHAR(255) NOT NULL, " +
                    "description TEXT NOT NULL, " +
                    "image VARCHAR(255), " +
                    "prix DECIMAL(10,2) NOT NULL, " +
                    "stock INT NOT NULL, " +
                    "poids DECIMAL(10,2) NOT NULL, " +
                    "promo_percentage DOUBLE" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public ObservableList<Produit> getAllProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList();
        String sql = "SELECT * FROM produit";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getDouble("poids")
                );
                Double promo = rs.getObject("promo_percentage") != null ? rs.getDouble("promo_percentage") : null;
                produit.setPromoPercentage(promo);
                produits.add(produit);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public void addProduit(Produit produit) {
        String sql = "INSERT INTO produit (nom, description, image, prix, stock, poids, promo_percentage) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getDescription());
            pstmt.setString(3, produit.getImage());
            pstmt.setDouble(4, produit.getPrix());
            pstmt.setInt(5, produit.getStock());
            pstmt.setDouble(6, produit.getPoids());
            if (produit.getPromoPercentage() != null) {
                pstmt.setDouble(7, produit.getPromoPercentage());
            } else {
                pstmt.setNull(7, Types.DOUBLE);
            }
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Adding product failed, no rows affected.");
            }
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProduit(Produit produit) {
        String sql = "UPDATE produit SET nom = ?, description = ?, image = ?, prix = ?, stock = ?, poids = ?, promo_percentage = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getDescription());
            pstmt.setString(3, produit.getImage());
            pstmt.setDouble(4, produit.getPrix());
            pstmt.setInt(5, produit.getStock());
            pstmt.setDouble(6, produit.getPoids());
            if (produit.getPromoPercentage() != null) {
                pstmt.setDouble(7, produit.getPromoPercentage());
            } else {
                pstmt.setNull(7, Types.DOUBLE);
            }
            pstmt.setInt(8, produit.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteProduit(Produit produit) {
        String sql = "DELETE FROM produit WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produit.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateProduitStock(Produit produit) {
        String sql = "UPDATE produit SET stock = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, produit.getStock());
            pstmt.setInt(2, produit.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Produit getProduitById(int id) {
        String sql = "SELECT * FROM produit WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Produit produit = new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getString("image"),
                        rs.getDouble("prix"),
                        rs.getInt("stock"),
                        rs.getDouble("poids")
                );
                Double promo = rs.getObject("promo_percentage") != null ? rs.getDouble("promo_percentage") : null;
                produit.setPromoPercentage(promo);
                return produit;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
