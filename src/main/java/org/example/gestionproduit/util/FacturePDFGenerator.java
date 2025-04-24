package org.example.gestionproduit.util;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.gestionproduit.entity.Facture;

import java.io.FileOutputStream;

public class FacturePDFGenerator {

    public static void generatePDF(Facture facture) {
        try {
            // Path where the PDF will be saved
            String directoryPath = "C:\\Users\\USER\\Desktop\\heni\\gestion-produit\\src\\main\\java\\org\\example\\gestionproduit\\pdf";
            String fileName = "facture_" + facture.getIdCommande() + ".pdf"; // Updated filename using Commande ID
            String fullPath = directoryPath + "\\" + fileName;

            // Create Document and PdfWriter
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fullPath));
            document.open();

            // Fonts
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            // Title
            Paragraph title = new Paragraph("FACTURE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Client Info Section
            document.add(new Paragraph("👤 Client: " + facture.getUserNom(), bodyFont));
            document.add(new Paragraph("🧾 Commande ID: " + facture.getIdCommande(), bodyFont));
            document.add(new Paragraph("📅 Date: " + facture.getCreatedAt(), bodyFont));
            document.add(Chunk.NEWLINE);

            // Detail Table
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            PdfPCell c1 = new PdfPCell(new Phrase("Détail", sectionFont));
            PdfPCell c2 = new PdfPCell(new Phrase("Valeur", sectionFont));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            c2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            table.addCell(c2);

            table.addCell(new Phrase("Produits", bodyFont));
            table.addCell(new Phrase(facture.getDetails(), bodyFont));

            table.addCell(new Phrase("Total (€)", bodyFont));
            table.addCell(new Phrase(String.format("%.2f", facture.getTotal()), bodyFont));

            document.add(table);

            // Footer
            Paragraph footer = new Paragraph("Merci pour votre confiance !", bodyFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();

            System.out.println("✅ PDF généré avec succès à : " + fullPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
