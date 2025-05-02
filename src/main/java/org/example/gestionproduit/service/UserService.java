package org.example.gestionproduit.service;

import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.db.DBConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register a new user
    public boolean registerUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO user (nom, prenom, email, password, adresse, profile_picture, roles, is_banned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getNom());
                statement.setString(2, user.getPrenom());
                statement.setString(3, user.getEmail());
                statement.setString(4, hashedPassword);
                statement.setString(5, user.getAdresse());
                statement.setString(6, user.getProfilePicture());
                statement.setString(7, user.getRoles());
                statement.setBoolean(8, user.isBanned());
                int rowsAffected = statement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Validate user login and return the User object
    public User validateLogin(String email, String password) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM user WHERE email = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, email);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    String storedPassword = resultSet.getString("password");
                    if (passwordEncoder.matches(password, storedPassword)) {
                        // If login is successful, return the User object
                        User user = new User(
                                resultSet.getInt("id"),
                                resultSet.getString("nom"),
                                resultSet.getString("prenom"),
                                resultSet.getString("email"),
                                resultSet.getString("password"),
                                resultSet.getString("adresse"),
                                resultSet.getString("profile_picture"),
                                resultSet.getString("roles"),
                                resultSet.getBoolean("is_banned")
                        );
                        return user;  // Return the user object if credentials match
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;  // Return null if login failed
    }

    public boolean addUser(User user) {
        String hashedPassword = new BCryptPasswordEncoder().encode(user.getPassword());
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "INSERT INTO user (nom, prenom, email, password, adresse, profile_picture, roles, is_banned) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getNom());
                statement.setString(2, user.getPrenom());
                statement.setString(3, user.getEmail());
                statement.setString(4, hashedPassword);
                statement.setString(5, user.getAdresse());
                statement.setString(6, user.getProfilePicture());
                statement.setString(7, user.getRoles());
                statement.setBoolean(8, user.isBanned());
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update an existing user
    public boolean updateUser(User user) {
        // Implement update logic, similar to the addUser method
        return true;  // Dummy return for now
    }

    // Delete a user
    public boolean deleteUser(User user) {
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "DELETE FROM user WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, user.getId());
                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all users (for the ListView)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "SELECT * FROM user";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    User user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("nom"),
                            resultSet.getString("prenom"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getString("adresse"),
                            resultSet.getString("profile_picture"),
                            resultSet.getString("roles"),
                            resultSet.getBoolean("is_banned")
                    );
                    users.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
