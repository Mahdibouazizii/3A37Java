package org.example.gestionproduit.entity;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.example.gestionproduit.util.PathConstants;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Generates a stylish PDF invoice that also embeds the user‘s signature image
 * if it exists inside {@link PathConstants#SIGNATURE_DIR}.
 */
public class FacturePDFGenerator {

    /** Blue used in header banner */
    private static final BaseColor PRIMARY = new BaseColor(41, 128, 185);      // #2980B9
    /** Light grey for table striping */
    private static final BaseColor STRIPE  = new BaseColor(245, 245, 245);     // #F5F5F5
    /** Green badge for paid invoices */
    private static final BaseColor OK      = new BaseColor( 39, 174,  96);     // #27AE60
    /** Red badge for unpaid invoices */
    private static final BaseColor KO      = new BaseColor(192,  57,  43);     // #C0392B

    public static void generatePDFWithSignature(Facture facture) {
        try {
            /* ------------------------------------------------------------------
               1.  Directory & basic document set-up
               ------------------------------------------------------------------ */
            File dir = new File(PathConstants.PDF_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName  = "facture_signed_" + facture.getIdCommande() + ".pdf";
            String fullPath  = PathConstants.PDF_DIR + fileName;

            Document doc = new Document(PageSize.A4, 36, 36, 64, 48);           // margins: L,R,T,B
            PdfWriter.getInstance(doc, new FileOutputStream(fullPath));
            doc.open();

            /* ------------------------------------------------------------------
               2.  Fonts
               ------------------------------------------------------------------ */
            Font fHeader  = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, BaseColor.WHITE);
            Font fSection = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13, BaseColor.BLACK);
            Font fText    = FontFactory.getFont(FontFactory.HELVETICA,       11, BaseColor.DARK_GRAY);
            Font fBadge   = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

            /* ------------------------------------------------------------------
               3.  Banner header  (company / project name could go here)
               ------------------------------------------------------------------ */
            PdfPTable banner = new PdfPTable(1);
            banner.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("FACTURE", fHeader));
            cell.setBackgroundColor(PRIMARY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorder(Rectangle.NO_BORDER);
            cell.setPadding(12);
            banner.addCell(cell);
            doc.add(banner);
            doc.add(Chunk.NEWLINE);

            /* ------------------------------------------------------------------
               4.  Payment-status ribbon
               ------------------------------------------------------------------ */
            String etat = facture.getEtatPaiement() == null ? "non payée" : facture.getEtatPaiement();
            BaseColor badgeColor = "payée".equalsIgnoreCase(etat) ? OK : KO;

            PdfPTable badgeTable = new PdfPTable(1);
            badgeTable.setWidthPercentage(50);
            PdfPCell badge = new PdfPCell(new Phrase("STATUT : " + etat.toUpperCase(), fBadge));
            badge.setBackgroundColor(badgeColor);
            badge.setHorizontalAlignment(Element.ALIGN_CENTER);
            badge.setBorder(Rectangle.NO_BORDER);
            badge.setPadding(6);
            badgeTable.addCell(badge);
            badgeTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            doc.add(badgeTable);
            doc.add(Chunk.NEWLINE);

            /* ------------------------------------------------------------------
               5.  Facts block (client, order, date)
               ------------------------------------------------------------------ */
            PdfPTable facts = new PdfPTable(new float[]{25, 75});
            facts.setWidthPercentage(100);

            addFactRow(facts, "Client",     facture.getUserNom(),  fSection, fText);
            addFactRow(facts, "Commande #", String.valueOf(facture.getIdCommande()), fSection, fText);
            addFactRow(facts, "Date",       String.valueOf(facture.getCreatedAt()),  fSection, fText);

            doc.add(facts);
            doc.add(Chunk.NEWLINE);

            /* ------------------------------------------------------------------
               6.  Item details table with zebra stripes
               ------------------------------------------------------------------ */
            PdfPTable tab = new PdfPTable(new float[]{70, 30});
            tab.setWidthPercentage(100);
            addHeader(tab, "Détail", fSection);
            addHeader(tab, "Montant", fSection);

            // — single cell holding the product list —
            PdfPCell prodCell  = new PdfPCell(new Phrase(facture.getDetails(), fText));
            prodCell.setPadding(6);
            prodCell.setBackgroundColor(STRIPE);
            PdfPCell emptyCell = new PdfPCell();
            emptyCell.setBorder(Rectangle.NO_BORDER);                 // visual spacing row

            PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f €", facture.getTotal()), fText));
            totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            totalCell.setPadding(6);
            totalCell.setBackgroundColor(STRIPE);

            tab.addCell(prodCell);
            tab.addCell(totalCell);
            doc.add(tab);
            doc.add(Chunk.NEWLINE);

            /* ------------------------------------------------------------------
               7.  Signature
               ------------------------------------------------------------------ */
            String sigPath = PathConstants.SIGNATURE_DIR + "signature_user_" + facture.getIdUser() + ".png";
            File sigFile = new File(sigPath);

            if (sigFile.exists()) {
                Image sig = Image.getInstance(sigPath);
                sig.scaleToFit(160, 80);
                sig.setAlignment(Element.ALIGN_LEFT);
                doc.add(new Paragraph("Signature électronique :", fSection));
                doc.add(sig);
            } else {
                Paragraph warn = new Paragraph("⚠ Aucune signature enregistrée.", fText);
                warn.setSpacingAfter(12);
                doc.add(warn);
            }

            /* ------------------------------------------------------------------
               8.  Footer thank-you
               ------------------------------------------------------------------ */
            Paragraph foot = new Paragraph("Merci pour votre confiance !", fText);
            foot.setAlignment(Element.ALIGN_CENTER);
            foot.setSpacingBefore(30);
            doc.add(foot);

            doc.close();
            System.out.println("✅ PDF signé généré avec succès à : " + fullPath);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /* --------------------------------------------------------------------------
       Helper methods
       -------------------------------------------------------------------------- */
    private static void addFactRow(PdfPTable table, String label, String value, Font fLabel, Font fValue) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, fLabel));
        PdfPCell c2 = new PdfPCell(new Phrase(value, fValue));
        c1.setBorder(Rectangle.NO_BORDER);
        c2.setBorder(Rectangle.NO_BORDER);
        table.addCell(c1);
        table.addCell(c2);
    }

    private static void addHeader(PdfPTable table, String text, Font font) {
        PdfPCell h = new PdfPCell(new Phrase(text, font));
        h.setBackgroundColor(new BaseColor(236, 240, 241));           // light blue-grey
        h.setHorizontalAlignment(Element.ALIGN_CENTER);
        h.setPadding(6);
        table.addCell(h);
    }
}
