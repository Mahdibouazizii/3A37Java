package org.example.gestionproduit.entity;

public class Participation {

    private int id;
    private int idevenement_id;
    private int iduser_id; // Foreign key referencing the User table
    private int age;
    private int nbrplace;
    private String statut;

    public Participation() {
    }

    public Participation(int id, int idevenement_id, int iduser_id, int age, int nbrplace, String statut) {
        this.id = id;
        this.idevenement_id = idevenement_id;
        this.iduser_id = iduser_id;
        this.age = age;
        this.nbrplace = nbrplace;
        this.statut = statut;
    }

    public Participation(int idevenement_id, int iduser_id, int age, int nbrplace, String statut) {
        this.idevenement_id = idevenement_id;
        this.iduser_id = iduser_id;
        this.age = age;
        this.nbrplace = nbrplace;
        this.statut = statut;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdevenement_id() {
        return idevenement_id;
    }

    public void setIdevenement_id(int idevenement_id) {
        this.idevenement_id = idevenement_id;
    }

    public int getIduser_id() {
        return iduser_id;
    }

    public void setIduser_id(int iduser_id) {
        this.iduser_id = iduser_id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getNbrplace() {
        return nbrplace;
    }

    public void setNbrplace(int nbrplace) {
        this.nbrplace = nbrplace;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", idevenement_id=" + idevenement_id +
                ", iduser_id=" + iduser_id +
                ", age=" + age +
                ", nbrplace=" + nbrplace +
                ", statut='" + statut + '\'' +
                '}';
    }
}