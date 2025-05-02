package org.example.gestionproduit.service;

import org.example.gestionproduit.entity.Produit;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class ProduitService {
    // Update the DB_URL and credentials as necessary.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/projetpi2?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";       // Change as needed
    private static final String PASS = "";           // Change as needed (or leave empty for passwordless)

    public ProduitService() {
        initializeDatabase();
    }

    // Initialize the database and create the table if it doesn't exist.
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
                    "poids DECIMAL(10,2) NOT NULL" +
                    ")";
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtain a connection to MySQL.
    private Connection getConnection() throws SQLException {
        // Option A: With credentials
        return DriverManager.getConnection(DB_URL, USER, PASS);

        // Option B: Without credentials (if configured accordingly)
        // return DriverManager.getConnection(DB_URL);
    }

    public ObservableList<Produit> getAllProduits() {
        ObservableList<Produit> produits = FXCollections.observableArrayList();
        String sql = "SELECT * FROM produit";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                String description = rs.getString("description");
                String image = rs.getString("image");
                double prix = rs.getDouble("prix");
                int stock = rs.getInt("stock");
                double poids = rs.getDouble("poids");
                produits.add(new Produit(id, nom, description, image, prix, stock, poids));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return produits;
    }

    public void addProduit(Produit produit) {
        String sql = "INSERT INTO produit (nom, description, image, prix, stock, poids) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getDescription());
            pstmt.setString(3, produit.getImage());
            pstmt.setDouble(4, produit.getPrix());
            pstmt.setInt(5, produit.getStock());
            pstmt.setDouble(6, produit.getPoids());
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
        String sql = "UPDATE produit SET nom = ?, description = ?, image = ?, prix = ?, stock = ?, poids = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, produit.getNom());
            pstmt.setString(2, produit.getDescription());
            pstmt.setString(3, produit.getImage());
            pstmt.setDouble(4, produit.getPrix());
            pstmt.setInt(5, produit.getStock());
            pstmt.setDouble(6, produit.getPoids());
            pstmt.setInt(7, produit.getId());
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
}
