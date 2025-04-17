package green.green.tests;

import green.green.Entity.event; // ✅ Import corrigé
import green.green.services.ServiceEvent;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ServiceEvent serviceEvent = new ServiceEvent();

        // Création d'un événement à ajouter
        event e1 = new event("Hackathon", "Compétition de codage",
                new Date(), new Date(), "image.jpg", 100);

        try {
            // 1. Ajout de l'événement
            System.out.println("Ajout de l'événement...");
            serviceEvent.ajouter(e1);

            // 2. Mise à jour de l'événement
            e1.setNom("Hackathon 2025");
            e1.setDescription("Mise à jour de l'événement");
            e1.setNbrplacetottale(150);
            serviceEvent.modifier(e1);

            // 3. Affichage de tous les événements
            System.out.println("Affichage des événements :");
            List<event> events = serviceEvent.afficher();
            for (event ev : events) {
                System.out.println(ev);
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }
}
