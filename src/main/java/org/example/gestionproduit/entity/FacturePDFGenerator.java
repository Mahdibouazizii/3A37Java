package org.example.gestionproduit.entity;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.example.gestionproduit.entity.Facture;
import org.example.gestionproduit.util.PathConstants;

import java.io.File;
import java.io.FileOutputStream;

public class FacturePDFGenerator {

    public static void generatePDFWithSignature(Facture facture) {
        try {
            // Ensure the PDF directory exists
            File pdfDirectory = new File(PathConstants.PDF_DIR);
            if (!pdfDirectory.exists()) {
                pdfDirectory.mkdirs();
            }

            String fileName = "facture_signed_" + facture.getIdCommande() + ".pdf";
            String fullPath = PathConstants.PDF_DIR + fileName;

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(fullPath));
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

            Paragraph title = new Paragraph("FACTURE SIGNÉE", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            document.add(new Paragraph("👤 Client: " + facture.getUserNom(), bodyFont));
            document.add(new Paragraph("🧾 Commande ID: " + facture.getIdCommande(), bodyFont));
            document.add(new Paragraph("📅 Date: " + facture.getCreatedAt(), bodyFont));
            document.add(Chunk.NEWLINE);

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

            // Signature section
            String signaturePath = PathConstants.SIGNATURE_DIR + "signature_user_" + facture.getIdUser() + ".png";
            System.out.println("Looking for signature at: " + signaturePath);

            File signatureFile = new File(signaturePath);
            if (signatureFile.exists()) {
                Image signatureImage = Image.getInstance(signaturePath);
                signatureImage.scaleToFit(150, 75);
                signatureImage.setAlignment(Element.ALIGN_CENTER);

                document.add(new Paragraph("Signature électronique :", bodyFont));
                document.add(signatureImage);
            } else {
                document.add(new Paragraph("⚠ Aucune signature enregistrée.", bodyFont));
            }

            Paragraph footer = new Paragraph("Merci pour votre confiance !", bodyFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(30);
            document.add(footer);

            document.close();

            System.out.println("✅ PDF signé généré avec succès à : " + fullPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
