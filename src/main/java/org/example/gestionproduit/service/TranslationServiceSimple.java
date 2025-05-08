package org.example.gestionproduit.service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TranslationServiceSimple {

    public static String traduire(String texte, String targetLang) {
        try {
            // Encoder le texte pour l'URL
            String encodedText = URLEncoder.encode(texte, "UTF-8");
            String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=auto&tl="
                    + targetLang + "&dt=t&q=" + encodedText;

            // Créer une connexion HTTP GET
            URL url = new URL(urlStr);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Lire la réponse
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String ligne;
            while ((ligne = in.readLine()) != null) {
                response.append(ligne);
            }
            in.close();

            // Parsing simple du JSON retourné
            String json = response.toString();
            int start = json.indexOf("\"");
            int end = json.indexOf("\"", start + 1);

            if (start != -1 && end != -1) {
                return json.substring(start + 1, end);
            } else {
                return "❌ Traduction introuvable.";
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Erreur de traduction.";
        }
    }
}


