package org.example.gestionproduit.util;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class BadWordsFilter {

    // Vérifie si le texte contient des mots inappropriés via PurgoMalum
    public static boolean containsBadWords(String text) {
        try {
            String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
            String url = "https://www.purgomalum.com/service/containsprofanity?text=" + encodedText;

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // La réponse est soit "true" soit "false"
            return Boolean.parseBoolean(response.body().trim());
        } catch (Exception e) {
            e.printStackTrace();
            // Si erreur => par sécurité considérer que c'est propre
            return false;
        }
    }

    // Méthode pour vérifier les mots inappropriés dans le nom et la description
    public static boolean containsBadWordsInEvent(String nom, String description) {
        return containsBadWords(nom) || containsBadWords(description);
    }
}
