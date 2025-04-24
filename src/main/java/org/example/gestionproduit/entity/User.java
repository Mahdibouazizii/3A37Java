package org.example.gestionproduit.entity;

public class User {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String password;
    private String adresse;
    private String profilePicture;
    private String roles; // e.g., "admin", "user"
    private boolean isBanned;


    private String resetKey;

    public String getResetKey() { return resetKey; }
    public void setResetKey(String resetKey) { this.resetKey = resetKey; }

    // Constructors
    public User() {}

    public User(int id, String nom, String prenom, String email, String password, String adresse,
                String profilePicture, String roles, boolean isBanned) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.password = password;
        this.adresse = adresse;
        this.profilePicture = profilePicture;
        this.roles = roles;
        this.isBanned = isBanned;
    }

    // Getters & Setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getProfilePicture() { return profilePicture; }
    public void setProfilePicture(String profilePicture) { this.profilePicture = profilePicture; }

    public String getRoles() { return roles; }
    public void setRoles(String roles) { this.roles = roles; }

    public boolean isBanned() { return isBanned; }
    public void setBanned(boolean isBanned) { this.isBanned = isBanned; }

    // Fixed getRole method, assuming roles is a single role string
    public String getRole() {
        return this.roles;  // Simply return the roles string.
    }
}
