package org.example.gestionproduit.controller;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import java.nio.file.Files;
import java.nio.file.Path;

import javafx.animation.Animation;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

// JavaScript bridge

// Dialog layout
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

// File access
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gestionproduit.entity.*;
import org.example.gestionproduit.service.*;
import org.example.gestionproduit.session.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.List;
import java.util.ResourceBundle;

public class ProductCardsFrontOfficeController implements Initializable {

    @FXML
    private FlowPane flowPane;

    @FXML
    private ListView<CartItem> cartListView; // Displays products in the shopping cart

    @FXML
    private Button btnConfirmPurchase;      // Global confirm purchase button

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button btnGenerateFacture;
// Displays total price of products in cart

    // In-memory shopping cart (panier)
    private final ObservableList<CartItem> panier = FXCollections.observableArrayList();

    // Services (assume these are implemented as discussed)
    private final ProduitService produitService = new ProduitService();
    private final FeedbackService feedbackService = new FeedbackService();
    private final CommandeService commandeService = new CommandeService();
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cartListView.setItems(panier);
        setupCartListView();
        updateCartTotal();
        // ✅ ADD THIS LINE
        refreshCards(); // ← this line was missing
    }

    @FXML
    private void handleOpenSignaturePad() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/signature_pad.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Signature Pad");
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleUpdateFactureTotal() {
        double total = 0;
        StringBuilder details = new StringBuilder();

        for (CartItem item : panier) {
            // Make sure the total for each item includes the updated quantity.
            double itemTotal = item.getProduit().getPrix() * item.getQuantity();  // Correct multiplication
            total += itemTotal;

            // Update the details to show in the facture
            details.append(item.getProduit().getNom())
                    .append(" x ")
                    .append(item.getQuantity())
                    .append(" = ")
                    .append(String.format("%.2f", itemTotal))
                    .append("\n");
        }

        // Update the total price label in the UI
        totalPriceLabel.setText(String.format("Total: %.2f", total));

        // Now you can also send the total to the Facture and Commande
        // But we will update this below with the correct value
    }






    @FXML
    public void handleAddReclamation() {
        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            showAlert("Error", "You must be logged in to submit a reclamation.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("New Reclamation");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(15));
        grid.setVgap(10);
        grid.setHgap(10);

        Label sujetLabel = new Label("Subject:");
        TextField sujetField = new TextField();
        Label descLabel = new Label("Description:");
        TextField descField = new TextField();

        Button submit = new Button("Submit");
        submit.setOnAction(e -> {
            String sujet = sujetField.getText().trim();
            String desc = descField.getText().trim();

            if (sujet.isEmpty() || desc.isEmpty()) {
                showAlert("Input Error", "All fields are required.");
                return;
            }

            Reclamation rec = new Reclamation(
                    session.getUser().getId(),
                    sujet,
                    desc,
                    new Timestamp(System.currentTimeMillis())
            );

            if (new ReclamationService().addReclamation(rec)) {
                showAlert("Success", "Reclamation submitted.");
                dialog.close();
            } else {
                showAlert("Error", "Failed to submit reclamation.");
            }
        });

        grid.add(sujetLabel, 0, 0);
        grid.add(sujetField, 1, 0);
        grid.add(descLabel, 0, 1);
        grid.add(descField, 1, 1);
        grid.add(submit, 1, 2);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }



    @FXML
    public void handleViewReclamations() {
        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            showAlert("Error", "You must be logged in to view reclamations.");
            return;
        }

        List<Reclamation> list = new ReclamationService().getReclamationsByUser(session.getUser().getId());
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));

        for (Reclamation r : list) {
            Label label = new Label(
                    r.getSujet() + "\n" + r.getDescription() + "\nDate: " + r.getCreatedAt() + "\n"
            );
            label.setStyle("-fx-background-color: #f4f4f4; -fx-padding: 10;");
            box.getChildren().add(label);
        }

        Stage stage = new Stage();
        stage.setTitle("My Reclamations");
        stage.setScene(new Scene(new ScrollPane(box), 400, 300));
        stage.show();
    }




    @FXML
    private void handleGenerateCartFacture() {
        if (panier.isEmpty()) {
            showAlert("Panier vide", "Ajoutez des produits au panier avant de générer la facture.");
            return;
        }

        double total = 0;
        StringBuilder details = new StringBuilder();

        for (CartItem item : panier) {
            double itemTotal = item.getTotalPrice();
            total += itemTotal;
            details.append(item.getProduit().getNom())
                    .append(" x ")
                    .append(item.getQuantity())
                    .append(" = ")
                    .append(String.format("%.2f", itemTotal))
                    .append("\n");
        }

        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.");
            return;
        }

        Commande newCommande = new Commande(total, details.toString(), session.getUser().getId());
        int insertedCommandeId = commandeService.addCommandeAndReturnId(newCommande);

        if (insertedCommandeId == -1) {
            showAlert("Erreur", "Échec de l'enregistrement de la commande.");
            return;
        }

        String userNom = session.getUser().getNom();
        Facture facture = new Facture(insertedCommandeId, total, details.toString(), userNom);
        facture.setIdUser(session.getUser().getId());

        FactureService factureService = new FactureService();
        if (factureService.addFacture(facture)) {
            FacturePDFGenerator.generatePDFWithSignature(facture);
            showAlert("Succès", "Votre commande a été enregistrée et la facture générée.");
            panier.clear();
            updateCartTotal();
        } else {
            showAlert("Erreur", "Échec de la génération de la facture.");
        }
    }






    // Refreshes the product cards in the FlowPane
    public void refreshCards() {
        flowPane.getChildren().clear();  // Clear previous cards
        ObservableList<Produit> produits = produitService.getAllProduits();  // Get all products
        for (Produit produit : produits) {
            VBox card = createProductCard(produit);  // Create product card
            flowPane.getChildren().add(card);  // Add card to the FlowPane
        }
    }

    // Sets up the cart ListView with a custom cell factory to show product name, price, and quantity
    private void setupCartListView() {
        cartListView.setCellFactory(lv -> new ListCell<CartItem>() {
            private final HBox content;
            private final Label nameLabel;
            private final Label priceLabel;
            private final TextField quantityField;
            private final Button btnRemove;
            private final Button btnGenerateFacture;

            {
                nameLabel = new Label();
                priceLabel = new Label();

                quantityField = new TextField();
                quantityField.setPrefWidth(50);

                btnRemove = new Button("Remove");
                btnGenerateFacture = new Button("Generate Facture");

                content = new HBox(10, nameLabel, priceLabel, quantityField, btnRemove, btnGenerateFacture);
                content.setPadding(new Insets(5));

                // Remove item button
                btnRemove.setOnAction(e -> {
                    CartItem item = getItem();
                    if (item != null) {
                        panier.remove(item);
                        updateCartTotal();
                    }
                });

                // Generate facture for individual item
                btnGenerateFacture.setOnAction(e -> {
                    CartItem item = getItem();
                    if (item != null) {
                        generateFactureForCartItem(item);
                    }
                });

                // Update quantity and recalculate total
                quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
                    CartItem item = getItem();
                    if (item != null) {
                        try {
                            int newQuantity = Integer.parseInt(newVal);
                            if (newQuantity > 0) {
                                item.setQuantity(newQuantity);
                                updateCartTotal();
                            }
                        } catch (NumberFormatException ex) {
                            // Optionally reset to old value or ignore
                        }
                    }
                });
            }

            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getProduit().getNom());
                    priceLabel.setText(String.format("Price: %.2f", item.getProduit().getPrix()));
                    quantityField.setText(String.valueOf(item.getQuantity()));
                    setGraphic(content);
                }
            }
        });
    }


    // Generate Facture for a CartItem
    private void generateFactureForCartItem(CartItem item) {
        double total = item.getTotalPrice();
        String details = item.getProduit().getNom() + ": " + item.getQuantity();

        Facture facture = new Facture(item.getProduit().getId(), total, details);
        facture.setIdUser(UserSession.getInstance().getUser().getId()); // ✅ Add user ID

        FactureService factureService = new FactureService();
        if (factureService.addFacture(facture)) {
            FacturePDFGenerator.generatePDFWithSignature(facture);
        }
    }


    // Updates the total price in the cart
    private void updateCartTotal() {
        double total = 0;
        for (CartItem item : panier) {
            total += item.getTotalPrice();  // Get total price from CartItem
        }
        totalPriceLabel.setText(String.format("Total: %.2f", total));  // Update the label with the total price
    }
    @FXML
    private void handlePurchase(Produit produit) {
        // Check if the product is already in the cart
        boolean found = false;
        for (CartItem item : panier) {
            if (item.getProduit().getId() == produit.getId()) {
                item.setQuantity(item.getQuantity() + 1);
                found = true;
                break;
            }
        }

        // If not found, add it as a new item
        if (!found) {
            panier.add(new CartItem(produit, 1));
        }

        updateCartTotal();
    }



    // Creates a styled product card with image, details, and action buttons
    private VBox createProductCard(Produit produit) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setPadding(new Insets(10));
        card.setPrefWidth(220);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);" +
                        "-fx-padding: 10;"
        );

        // Product image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        File file = new File(produit.getImage());
        if (file.exists()) {
            imageView.setImage(new Image(file.toURI().toString(), true));
        }

        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label descLabel = new Label(produit.getDescription());
        descLabel.setWrapText(true);

        // ✅ Display promo price if applicable
        Label priceLabel;
        if (produit.getPromoPercentage() != null && produit.getPromoPercentage() > 0) {
            double discountedPrice = produit.getPrix(); // prix in DB is already discounted
            double originalPrice = discountedPrice / (1 - produit.getPromoPercentage() / 100.0);

            Label originalPriceLabel = new Label(String.format("Original: %.2f €", originalPrice));
            originalPriceLabel.setStyle("-fx-text-fill: #888888; -fx-strikethrough: true; -fx-font-size: 12px;");

            priceLabel = new Label(String.format("Promo: %.2f € (%.0f%% OFF)", discountedPrice, produit.getPromoPercentage()));
            priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");

            card.getChildren().add(originalPriceLabel);
        } else {
            priceLabel = new Label(String.format("Price: %.2f €", produit.getPrix()));
            priceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold;");
        }

        // 🔲 Generate QR code image
        ImageView qrView = new ImageView();
        qrView.setFitWidth(100);
        qrView.setFitHeight(100);
        try {
            String qrData = "Nom: " + produit.getNom() + "\nDescription: " + produit.getDescription() + "\nStock: " + produit.getStock();
            BitMatrix matrix = new MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, 200, 200);
            Path qrPath = Files.createTempFile("qr_" + produit.getId(), ".png");
            MatrixToImageWriter.writeToPath(matrix, "PNG", qrPath);
            qrView.setImage(new Image(qrPath.toUri().toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ✅ Feedback Section
        VBox feedbackBox = new VBox(5);
        feedbackBox.setPadding(new Insets(5, 0, 0, 0));
        feedbackBox.setStyle("-fx-background-color: #f9f9f9; -fx-padding: 5; -fx-border-color: #ddd;");

        Label feedbackHeader = new Label("Avis des clients :");
        feedbackHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        feedbackBox.getChildren().add(feedbackHeader);

        List<Feedback> feedbacks = feedbackService.getFeedbacksForProduct(produit.getId());
        if (feedbacks.isEmpty()) {
            Label noFeedback = new Label("Aucun avis disponible.");
            noFeedback.setStyle("-fx-text-fill: #777;");
            feedbackBox.getChildren().add(noFeedback);
        } else {
            for (Feedback fb : feedbacks) {
                Label fbLabel = new Label("★ " + fb.getRating() + " — " + fb.getCommentaire());
                fbLabel.setWrapText(true);
                fbLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #333;");
                feedbackBox.getChildren().add(fbLabel);
            }
        }

        // Buttons
        Button btnFeedback = new Button("Feedback");
        btnFeedback.setOnAction(e -> handleFeedback(produit));

        Button btnPurchase = new Button("Purchase");
        btnPurchase.setOnAction(e -> handlePurchase(produit));

        HBox buttonBox = new HBox(10, btnFeedback, btnPurchase);

        // Add all components to the card
        card.getChildren().addAll(
                imageView,
                nameLabel,
                descLabel,
                priceLabel,
                qrView,
                feedbackBox,
                buttonBox
        );

        return card;
    }





    // Add the product to the shopping cart (panier)
    @FXML
    public void handleConfirmPurchase() {
        if (panier.isEmpty()) {
            showAlert("Cart Empty", "Your cart is empty.");
            return;
        }

        boolean stockError = false;
        StringBuilder stockWarnings = new StringBuilder();

        for (CartItem item : panier) {
            Produit latestProduit = produitService.getProduitById(item.getProduit().getId());
            int availableStock = latestProduit.getStock();

            if (item.getQuantity() > availableStock) {
                item.setQuantity(availableStock);
                stockError = true;
                stockWarnings.append(" - ")
                        .append(item.getProduit().getNom())
                        .append(": stock disponible = ")
                        .append(availableStock)
                        .append("\n");
            }
        }

        if (stockError) {
            updateCartTotal();
            showAlert("Stock insuffisant", "Certains produits ont été mis à jour à la quantité maximale disponible :\n\n" + stockWarnings);
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirmer la commande");
        dialog.setHeaderText("Choisissez votre adresse sur la carte");

        WebView mapView = new WebView();
        mapView.setPrefSize(600, 360);
        WebEngine webEngine = mapView.getEngine();
        URL mapUrl = getClass().getResource("/org/example/gestionproduit/map.html");
        if (mapUrl == null) {
            showAlert("Erreur", "Impossible de charger la carte.");
            return;
        }
        webEngine.load(mapUrl.toExternalForm());

        TextField addressField = new TextField();
        addressField.setPromptText("Aucune adresse sélectionnée");
        addressField.setEditable(false);

        Timeline poller = new Timeline(new KeyFrame(Duration.millis(200), e -> {
            try {
                String coords = (String) webEngine.executeScript("document.getElementById('coords').value");
                if (coords != null && !coords.isEmpty()) {
                    addressField.setText(coords);
                }
            } catch (Exception ignored) {}
        }));
        poller.setCycleCount(Animation.INDEFINITE);
        poller.play();

        ComboBox<String> paiementCombo = new ComboBox<>(FXCollections.observableArrayList("à la livraison", "en ligne"));
        paiementCombo.setValue("à la livraison");

        GridPane content = new GridPane();
        content.setHgap(10);
        content.setVgap(10);
        content.setPadding(new Insets(10));
        content.add(new Label("Type de Paiement:"), 0, 0);
        content.add(paiementCombo, 1, 0);
        content.add(new Label("Adresse sélectionnée:"), 0, 1);
        content.add(addressField, 1, 1);
        content.add(mapView, 0, 2, 2, 1);

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK ? ButtonType.OK : null);

        dialog.showAndWait().ifPresent(result -> {
            poller.stop();

            String selectedAddress = addressField.getText();
            if (selectedAddress == null || selectedAddress.isBlank()) {
                showAlert("Erreur", "Veuillez cliquer sur la carte pour choisir un point.");
                return;
            }

            double total = panier.stream().mapToDouble(CartItem::getTotalPrice).sum();
            StringBuilder productsList = new StringBuilder();
            panier.forEach(item -> productsList
                    .append(item.getProduit().getId())
                    .append(':')
                    .append(item.getQuantity())
                    .append(", "));
            if (productsList.length() > 0) productsList.setLength(productsList.length() - 2);

            User user = UserSession.getInstance().getUser();
            if (user == null) {
                showAlert("Erreur", "Aucun utilisateur connecté.");
                return;
            }

            String paiementType = paiementCombo.getValue();

            if (paiementType.equals("en ligne")) {
                try {
                    String sessionUrl = StripePaymentService.createStripeSession(panier);
                    Stage stripeStage = new Stage();
                    WebView stripeView = new WebView();
                    WebEngine stripeEngine = stripeView.getEngine();
                    stripeEngine.load(sessionUrl);

                    stripeEngine.locationProperty().addListener((obs, oldLoc, newLoc) -> {
                        if (newLoc.contains("https://example.com/success")) {
                            stripeStage.close();
                            Commande commande = new Commande(
                                    0, total, productsList.toString(), new Timestamp(System.currentTimeMillis()),
                                    user.getId(), selectedAddress, paiementType, "validée", user.getNom()
                            );
                            int commandeId = commandeService.addCommandeAndReturnId(commande);
                            if (commandeId == -1) {
                                showAlert("Erreur", "Échec commande.");
                                return;
                            }

                            Facture facture = new Facture(commandeId, total, productsList.toString(), user.getNom());
                            facture.setIdUser(user.getId());  // ✅ Add user ID

                            if (new FactureService().addFacture(facture)) {
                                FacturePDFGenerator.generatePDFWithSignature(facture);

                                showAlert("Paiement réussi", "Commande validée et facture générée.");
                                finalizeCommande(commande);
                            } else {
                                showAlert("Erreur", "Facture non générée.");
                            }
                        } else if (newLoc.contains("https://example.com/cancel")) {
                            stripeStage.close();
                            showAlert("Paiement annulé", "Le paiement a été annulé.");
                        }
                    });

                    stripeStage.setTitle("Paiement Stripe");
                    stripeStage.setScene(new Scene(stripeView, 800, 600));
                    stripeStage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Stripe Error", "Erreur Stripe : " + e.getMessage());
                }
            } else {
                Commande commande = new Commande(
                        0, total, productsList.toString(), new Timestamp(System.currentTimeMillis()),
                        user.getId(), selectedAddress, paiementType, "en cours", user.getNom()
                );
                int commandeId = commandeService.addCommandeAndReturnId(commande);
                if (commandeId == -1) {
                    showAlert("Erreur", "Échec commande.");
                    return;
                }

                Facture facture = new Facture(commandeId, total, productsList.toString(), user.getNom());
                facture.setIdUser(user.getId());  // ✅ Add user ID

                if (new FactureService().addFacture(facture)) {
                    FacturePDFGenerator.generatePDFWithSignature(facture);

                    showAlert("Succès", "Commande confirmée et facture générée.");
                    finalizeCommande(commande);
                } else {
                    showAlert("Erreur", "Échec de la facture.");
                }
            }
        });
    }



    private void finalizeCommande(Commande commande) {
        ProduitService produitService = new ProduitService();
        for (CartItem item : panier) {
            Produit produit = item.getProduit();
            int newStock = produit.getStock() - item.getQuantity();
            produit.setStock(Math.max(0, newStock));
            produitService.updateProduitStock(produit);
        }
        panier.clear();
        updateCartTotal();
    }

    // Global handler for confirming the purchase (commande)






    // helper: call OSM Nominatim
    @FXML
    private void handleMesCommandes() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/MesCommandes.fxml"));
            ScrollPane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Mes Commandes");
            stage.setScene(new Scene(root, 700, 500));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue des commandes.");
        }
    }


    @FXML
    public void handleLogout() {
        UserSession.getInstance().clearSession();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/Login.fxml"));
            Parent loginRoot = loader.load(); // Use Parent instead of BorderPane
            Stage stage = (Stage) flowPane.getScene().getWindow();
            stage.setScene(new Scene(loginRoot));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Échec du chargement de la page de connexion.");
        }
    }

    @FXML
    private void handleGoToAdmin() {
        if (UserSession.getInstance().getUser().getRoles().equalsIgnoreCase("admin")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/BackofficeDashboard.fxml"));
                Scene dashboardScene = new Scene(loader.load(), 1200, 700); // ✅ Increased window size

                Stage stage = (Stage) flowPane.getScene().getWindow();
                stage.setScene(dashboardScene);
                stage.centerOnScreen(); // Optional: center the window
                stage.setResizable(true); // Optional: allow resizing
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Access Denied", "Only admins can access the dashboard.");
        }
    }








    // Opens a dialog for users to add feedback for a product.
    private void handleFeedback(Produit produit) {
        UserSession userSession = UserSession.getInstance();
        if (userSession == null || userSession.getUser() == null) {
            showAlert("Session Error", "No active user session found.");
            return;
        }

        int userId = userSession.getUser().getId();

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Ajouter un feedback pour : " + produit.getNom());

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        Label labelComment = new Label("Commentaire :");
        TextField tfComment = new TextField();
        grid.add(labelComment, 0, 0);
        grid.add(tfComment, 1, 0);

        Label labelRating = new Label("Note (1-5) :");
        TextField tfRating = new TextField();
        grid.add(labelRating, 0, 1);
        grid.add(tfRating, 1, 1);

        HBox hboxButtons = new HBox(10);
        Button btnSave = new Button("Enregistrer");
        Button btnCancel = new Button("Annuler");
        hboxButtons.getChildren().addAll(btnSave, btnCancel);
        grid.add(hboxButtons, 1, 2);

        btnCancel.setOnAction(e -> dialog.close());

        btnSave.setOnAction(e -> {
            String comment = tfComment.getText().trim();
            String ratingStr = tfRating.getText().trim();

            if (comment.isEmpty()) {
                showAlert("Erreur", "Le commentaire ne peut pas être vide.");
                return;
            }

            // 🔞 Check for bad words via API
            if (containsProfanity(comment)) {
                showAlert("Contenu inapproprié", "Votre commentaire contient des mots interdits.");
                return;
            }

            int rating;
            try {
                rating = Integer.parseInt(ratingStr);
                if (rating < 1 || rating > 5) {
                    showAlert("Erreur", "La note doit être comprise entre 1 et 5.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Erreur", "Veuillez entrer une note valide (1-5).");
                return;
            }

            feedbackService.addFeedback(new Feedback(produit.getId(), comment, rating, userId));
            dialog.close();
            refreshCards(); // Refresh to reflect new feedback
        });

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

///////api badwords
    private boolean containsProfanity(String text) {
        try {
            String encoded = java.net.URLEncoder.encode(text, "UTF-8");
            String url = "https://www.purgomalum.com/service/containsprofanity?text=" + encoded;

            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .build();
            java.net.http.HttpResponse<String> response = client.send(
                    request, java.net.http.HttpResponse.BodyHandlers.ofString());

            return response.body().contains("true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @FXML
    public void handleEvent(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherEvent.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Unable to load the Event view.");
        }
    }
    @FXML
    public void handleProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gestionproduit/ProductCardsFrontOffice.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.setTitle("Catalogue des Produits");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger la vue des produits.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
