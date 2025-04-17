package org.example.gestionproduit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gestionproduit.entity.*;
import org.example.gestionproduit.service.*;
import org.example.gestionproduit.session.UserSession;
import org.example.gestionproduit.util.FacturePDFGenerator;

import java.io.File;
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
    public void handleQuantityChange(ActionEvent event) {
        TextField source = (TextField) event.getSource();
        CartItem cartItem = (CartItem) source.getUserData();  // Assuming the CartItem is set as userData for the field

        // Parse the new quantity from the text field
        try {
            int newQuantity = Integer.parseInt(source.getText());
            if (newQuantity <= 0) {
                showAlert("Invalid Quantity", "Quantity must be greater than 0.");
                return;
            }
            cartItem.setQuantity(newQuantity);  // Update quantity in CartItem

            // Now update the total after quantity change
            updateCartTotal();  // This will recalculate the total based on the new quantity
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid number for quantity.");
        }
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

        // Calculate total based on current quantities
        for (CartItem item : panier) {
            double itemTotal = item.getTotalPrice();  // Correct multiplication
            total += itemTotal;

            details.append(item.getProduit().getNom())
                    .append(" x ")
                    .append(item.getQuantity())
                    .append(" = ")
                    .append(String.format("%.2f", itemTotal))
                    .append("\n");
        }

        // Proceed to create Commande and Facture
        UserSession session = UserSession.getInstance();
        if (session == null || session.getUser() == null) {
            showAlert("Erreur", "Aucun utilisateur connecté.");
            return;
        }

        // Create and insert Commande
        Commande newCommande = new Commande(total, details.toString(), session.getUser().getId());
        int insertedCommandeId = commandeService.addCommandeAndReturnId(newCommande);

        if (insertedCommandeId == -1) {
            showAlert("Erreur", "Échec de l'enregistrement de la commande.");
            return;
        }

        // Create and insert Facture linked to the commande
        Facture facture = new Facture(insertedCommandeId, total, details.toString());
        FactureService factureService = new FactureService();
        if (factureService.addFacture(facture)) {
            // Generate the PDF for the facture
            FacturePDFGenerator.generatePDF(facture);
            showAlert("Succès", "Votre commande a été enregistrée et la facture générée.");

            panier.clear();  // Clear the cart
            updateCartTotal();  // Update the total display in the UI
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

            @FXML
            private void handleGenerateCartFacture() {
                if (panier.isEmpty()) {
                    showAlert("Panier vide", "Ajoutez des produits au panier avant de générer la facture.");
                    return;
                }

                double total = 0;
                StringBuilder details = new StringBuilder();

                for (CartItem item : panier) {
                    double itemTotal = item.getProduit().getPrix() * item.getQuantity(); // ✅ FIXED
                    total += itemTotal;

                    details.append(item.getProduit().getNom())
                            .append(" x ")
                            .append(item.getQuantity())
                            .append(" = ")
                            .append(String.format("%.2f", itemTotal))
                            .append("\n");
                }


                // Generate dummy facture object (you can link with Commande ID if needed)
                Facture facture = new Facture(0, total, details.toString()); // ProduitId set to 0 if not needed

                FactureService factureService = new FactureService();
                if (factureService.addFacture(facture)) {
                    FacturePDFGenerator.generatePDF(facture);
                    showAlert("Succès", "Facture générée pour le panier.");
                } else {
                    showAlert("Erreur", "Échec de la génération de la facture.");
                }
            }


            {
                nameLabel = new Label();
                priceLabel = new Label();
                quantityField = new TextField();
                quantityField.setPrefWidth(50);
                btnRemove = new Button("Remove");
                btnGenerateFacture = new Button("Generate Facture");

                btnRemove.setOnAction(e -> {
                    CartItem item = getItem();
                    if (item != null) {
                        panier.remove(item);  // Remove item from panier
                        updateCartTotal();  // Update total price
                    }
                });

                // Handle facture generation
                btnGenerateFacture.setOnAction(e -> {
                    CartItem item = getItem();
                    if (item != null) {
                        generateFactureForCartItem(item);  // Generate Facture for this CartItem
                    }
                });

                content = new HBox(10, nameLabel, priceLabel, quantityField, btnRemove, btnGenerateFacture);
                content.setPadding(new Insets(5));
            }

            @Override
            protected void updateItem(CartItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    nameLabel.setText(item.getProduit().getNom());  // Product name
                    priceLabel.setText(String.format("Price: %.2f", item.getProduit().getPrix()));  // Product price
                    quantityField.setText(String.valueOf(item.getQuantity()));  // Product quantity
                    setGraphic(content);  // Set the content for this item
                }
            }
        });
    }

    // Generate Facture for a CartItem
    private void generateFactureForCartItem(CartItem item) {
        double total = item.getTotalPrice(); // Calculate total for the item
        String details = item.getProduit().getNom() + ": " + item.getQuantity();

        // Create the Facture object
        Facture facture = new Facture(item.getProduit().getId(), total, details);

        // Add the facture to the database
        FactureService factureService = new FactureService();
        if (factureService.addFacture(facture)) {
            // Generate the PDF
            FacturePDFGenerator.generatePDF(facture);
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

    // Creates a styled product card with image, details, and action buttons
    private VBox createProductCard(Produit produit) {
        VBox card = new VBox();
        card.setSpacing(5);
        card.setPadding(new Insets(10));
        card.setPrefWidth(220);
        card.setPrefHeight(360);
        card.setStyle(
                "-fx-background-color: #ffffff;" +
                        "-fx-border-color: #cccccc;" +
                        "-fx-border-width: 1;" +
                        "-fx-background-radius: 5;" +
                        "-fx-border-radius: 5;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);" +
                        "-fx-padding: 10;"
        );

        // Uniform product image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        File file = new File(produit.getImage());
        if (file.exists()) {
            Image image = new Image(file.toURI().toString(), true);
            imageView.setImage(image);
        }

        // Product detail labels
        Label nameLabel = new Label(produit.getNom());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        Label descLabel = new Label(produit.getDescription());
        descLabel.setStyle("-fx-text-fill: #666666;");
        Label priceLabel = new Label(String.format("Price: %.2f", produit.getPrix()));
        priceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Existing feedback display (if any)
        StringBuilder feedbackDisplay = new StringBuilder();
        List<Feedback> feedbacks = feedbackService.getFeedbacksForProduct(produit.getId());
        if (feedbacks.isEmpty()) {
            feedbackDisplay.append("No feedback yet.");
        } else {
            for (Feedback feedback : feedbacks) {
                feedbackDisplay.append(feedback.getCommentaire())
                        .append(" (Rating: ").append(feedback.getRating()).append(")\n");
            }
        }

        // Label to display feedback
        Label feedbackLabel = new Label(feedbackDisplay.toString());
        feedbackLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        // "Feedback" button
        Button btnFeedback = new Button("Feedback");
        btnFeedback.setStyle(
                "-fx-background-color: #3498db;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;"
        );
        btnFeedback.setOnAction(e -> handleFeedback(produit));

        // "Purchase" button
        Button btnPurchase = new Button("Purchase");
        btnPurchase.setStyle(
                "-fx-background-color: #27ae60;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 5;"
        );
        btnPurchase.setOnAction(e -> handlePurchase(produit));

        HBox actionButtons = new HBox(10, btnFeedback, btnPurchase);

        // Assemble card
        card.getChildren().addAll(
                imageView,
                nameLabel,
                descLabel,
                priceLabel,
                feedbackLabel,   // Add feedback label
                actionButtons
        );

        return card;
    }

    // Add the product to the shopping cart (panier)
    private void handlePurchase(Produit produit) {
        boolean found = false;
        for (CartItem item : panier) {
            if (item.getProduit().getId() == produit.getId()) {
                item.setQuantity(item.getQuantity() + 1);  // Increase quantity if product already exists
                found = true;
                break;
            }
        }

        if (!found) {
            panier.add(new CartItem(produit, 1));  // Add new product if not already in the cart
        }
        updateCartTotal();
    }

    // Global handler for confirming the purchase (commande)
    @FXML
    public void handleConfirmPurchase() {
        if (panier.isEmpty()) {
            showAlert("Cart Empty", "Your cart is empty.");
            return;
        }

        double total = 0;
        StringBuilder productsList = new StringBuilder();

        for (CartItem item : panier) {
            total += item.getTotalPrice();  // Get total price from CartItem
            productsList.append(item.getProduit().getId())  // productId
                    .append(":")
                    .append(item.getQuantity())  // quantity
                    .append(", ");  // Format: productId:quantity
        }

        // Remove the last comma
        if (productsList.length() > 0) {
            productsList.setLength(productsList.length() - 2);
        }

        // Get the current user ID from the session
        UserSession userSession = UserSession.getInstance();
        if (userSession == null || userSession.getUser() == null) {
            showAlert("Session Error", "No active user session found.");
            return;
        }
        int userId = userSession.getUser().getId();  // Get the logged-in user ID from the session

        // Register the order (commande) using CommandeService and associate it with the user
        Commande commande = new Commande(total, productsList.toString(), userId);  // Pass the user ID here
        int commandeId = commandeService.addCommandeAndReturnId(commande);

        if (commandeId == -1) {
            showAlert("Error", "Failed to register the order.");
            return;
        }

        // Create and insert Facture linked to the commande
        Facture facture = new Facture(commandeId, total, productsList.toString());
        FactureService factureService = new FactureService();
        if (factureService.addFacture(facture)) {
            // Generate the PDF for the facture
            FacturePDFGenerator.generatePDF(facture);
            showAlert("Success", "Your order has been registered and facture generated.");

            // Clear the cart and update the total
            panier.clear();
            updateCartTotal();  // This will update the total displayed in the UI
        } else {
            showAlert("Error", "Failed to generate facture.");
        }
    }


    // Opens a dialog for users to add feedback for a product.
    private void handleFeedback(Produit produit) {
        // Get current user ID from session
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

            // Always add new feedback
            feedbackService.addFeedback(new Feedback(produit.getId(), comment, rating, userId));

            dialog.close();
            refreshCards(); // Refresh cards to reflect new feedback
        });

        Scene scene = new Scene(grid);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
