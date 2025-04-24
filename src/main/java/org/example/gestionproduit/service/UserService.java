package org.example.gestionproduit.service;
import org.example.gestionproduit.entity.User;
import org.example.gestionproduit.db.DBConnection;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class UserService {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    public boolean sendResetEmail(String to, String generatedKey) {
        final String from = "mailpisymfony@gmail.com"; // Replace with your sender Gmail
        final String password = "yyatxiaanrqasuag"; // Replace with your Gmail App Password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // ✅ Create mail session with correct Authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("🔐 Réinitialisation de mot de passe");
            message.setText("Bonjour,\n\nVoici votre clé de réinitialisation : " + generatedKey +
                    "\n\nVeuillez entrer cette clé dans l'application pour réinitialiser votre mot de passe." +
                    "\n\nMerci,\nSupport Technique");

            Transport.send(message);
            System.out.println("✅ Email envoyé à : " + to);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
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
        try (Connection connection = DBConnection.getConnection()) {
            String sql = "UPDATE user SET nom = ?, prenom = ?, email = ?, adresse = ?, profile_picture = ?, roles = ?, is_banned = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getNom());
                statement.setString(2, user.getPrenom());
                statement.setString(3, user.getEmail());
                statement.setString(4, user.getAdresse());
                statement.setString(5, user.getProfilePicture());
                statement.setString(6, user.getRoles());
                statement.setBoolean(7, user.isBanned());
                statement.setInt(8, user.getId());

                return statement.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateUserRole(int userId, String newRole) {
        String sql = "UPDATE user SET roles = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, newRole);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User findUserByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("adresse"),
                        rs.getString("profile_picture"), // ✅ corrected field name
                        rs.getString("roles"),
                        rs.getBoolean("is_banned")       // ✅ corrected field name
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }



    public boolean updatePassword(int userId, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // 🔐 Encrypt the password before storing
            String hashedPassword = passwordEncoder.encode(newPassword);

            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
