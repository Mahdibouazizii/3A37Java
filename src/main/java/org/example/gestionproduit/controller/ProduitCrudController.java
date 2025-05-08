package org.example.gestionproduit.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.gestionproduit.entity.Produit;
import org.example.gestionproduit.service.ProduitService;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ProduitCrudController implements Initializable {

    @FXML
    private ListView<Produit> listView;

    @FXML
    private FlowPane flowPane;

    private Produit selectedProduitFromCard = null;


    private final ProduitService produitService = new ProduitService();

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        if (listView != null) {
            // Ensure the ListView is initialized and populated with products
            listView.setItems(produitService.getAllProduits());
        }
        refreshList();
    }

    private VBox createCard(Produit produit) {
        VBox card = new VBox();
        ImageView imageView = new ImageView();
        Label nameLabel = new Label(produit.getNom());
        Label descLabel = new Label(produit.getDescription());

        Label priceLabel;

        if (produit.getPromoPercentage() != null && produit.getPromoPercentage() > 0) {
            double discountedPrice = produit.getPrix(); // prix is already discounted
            double originalPrice = discountedPrice / (1 - produit.getPromoPercentage() / 100.0); // reverse calculation

            Label originalPriceLabel = new Label(String.format("Original: %.2f €", originalPrice));
            originalPriceLabel.setStyle("-fx-text-fill: #888888; -fx-strikethrough: true; -fx-font-size: 12px;");
            priceLabel = new Label(String.format("Promo: %.2f € (%.0f%% OFF)", discountedPrice, produit.getPromoPercentage()));
            priceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px; -fx-font-weight: bold;");
            card.getChildren().add(originalPriceLabel);
        } else {
            priceLabel = new Label(String.format("Price: %.2f €", produit.getPrix()));
            priceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 14px; -fx-font-weight: bold;");
        }

        // Set image
        File file = new File(produit.getImage());
        if (file.exists()) {
            imageView.setImage(new Image(file.toURI().toString()));
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        // Style card container
        card.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc; " +
                "-fx-border-width: 1; -fx-background-radius: 8; -fx-border-radius: 8; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 2);");

        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333;");
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #555;");

        // Action buttons
        Button btnDelete = new Button("Delete");
        btnDelete.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnDelete.setOnAction(e -> handleDelete(produit));

        Button btnPromo = new Button("Add Promo");
        btnPromo.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white;");
        btnPromo.setOnAction(e -> handlePromo(produit));

        Button btnRemovePromo = new Button("Remove Promo");
        btnRemovePromo.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;");
        btnRemovePromo.setOnAction(e -> handleRemovePromo(produit));

        HBox buttonBox = new HBox(10, btnDelete, btnPromo, btnRemovePromo);
        buttonBox.setPadding(new Insets(5, 0, 0, 0));

        card.setPadding(new Insets(10));
        card.setSpacing(8);
        card.getChildren().addAll(imageView, nameLabel, descLabel, priceLabel, buttonBox);

        return card;
    }



    private void handleRemovePromo(Produit produit) {
        if (produit.getPromoPercentage() == null) {
            showAlert("Info", "Ce produit n'a pas de promotion.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Êtes-vous sûr de vouloir supprimer la promotion pour " + produit.getNom() + " ?",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                produit.setPromoPercentage(null);
                produitService.updateProduit(produit);
                refreshList();
            }
        });
    }

    private void handlePromo(Produit produit) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Ajouter une promotion");
        dialog.setHeaderText("Entrez le pourcentage de promotion (%) pour : " + produit.getNom());
        dialog.setContentText("Promotion (%):");

        dialog.showAndWait().ifPresent(input -> {
            try {
                double promo = Double.parseDouble(input);
                if (promo < 0 || promo > 100) {
                    showAlert("Erreur", "Le pourcentage doit être entre 0 et 100.");
                    return;
                }

                // Apply promo
                double originalPrice = produit.getPrix();
                double discountedPrice = originalPrice - (originalPrice * promo / 100.0);

                produit.setPromoPercentage(promo);
                produit.setPrix(discountedPrice);  // ⚠️ Update actual price!
                produitService.updateProduit(produit);
                refreshList();
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Veuillez entrer un nombre valide.");
            }
        });
    }

    private void handleDelete(Produit produit) {
        // Confirm deletion
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete " + produit.getNom() + "?", ButtonType.YES, ButtonType.NO);
        confirmation.setHeaderText(null);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // Delete the product
                produitService.deleteProduit(produit);

                // Refresh the product list to reflect the deletion
                refreshList();
            }
        });
    }


    private void handleCardSelection(Produit selectedProduct) {
        if (selectedProduct != null) {
            selectedProduitFromCard = selectedProduct;  // Save the selected product
            handleEdit(); // Trigger edit dialog directly
        }
    }


    public void refreshList() {
        flowPane.getChildren().clear();  // Clear existing cards before adding new ones
        ObservableList<Produit> produits = produitService.getAllProduits();

        if (produits.isEmpty()) {
            flowPane.getChildren().add(new Label("No products available."));  // Add a placeholder
        } else {
            for (Produit produit : produits) {
                VBox card = createCard(produit);  // Create the card for each product
                card.setOnMouseClicked(event -> handleCardSelection(produit));  // Handle selection
                flowPane.getChildren().add(card);  // Add card to FlowPane
            }
        }
    }


    @FXML
    public void handleAdd() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Add New Product");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Name
        Label labelNom = new Label("Nom:");
        TextField tfNom = new TextField();
        grid.add(labelNom, 0, 0);
        grid.add(tfNom, 1, 0);

        // Description
        Label labelDesc = new Label("Description:");
        TextField tfDesc = new TextField();
        grid.add(labelDesc, 0, 1);
        grid.add(tfDesc, 1, 1);

        // Photo (using FileChooser)
        Label labelImage = new Label("Photo:");
        TextField tfImage = new TextField();
        tfImage.setEditable(false);
        Button btnBrowse = new Button("Browse");
        HBox hboxImage = new HBox(5, tfImage, btnBrowse);
        grid.add(labelImage, 0, 2);
        grid.add(hboxImage, 1, 2);

        btnBrowse.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Photo");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog);
            if (selectedFile != null) {
                tfImage.setText(selectedFile.getAbsolutePath());
            }
        });

        // Price
        Label labelPrix = new Label("Prix:");
        TextField tfPrix = new TextField();
        grid.add(labelPrix, 0, 3);
        grid.add(tfPrix, 1, 3);

        // Stock
        Label labelStock = new Label("Stock:");
        TextField tfStock = new TextField();
        grid.add(labelStock, 0, 4);
        grid.add(tfStock, 1, 4);

        // Weight (Poids)
        Label labelPoids = new Label("Poids:");
        TextField tfPoids = new TextField();
        grid.add(labelPoids, 0, 5);
        grid.add(tfPoids, 1, 5);

        // Buttons
        HBox hboxButtons = new HBox(10);
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel");
        hboxButtons.getChildren().addAll(btnSave, btnCancel);
        grid.add(hboxButtons, 1, 6);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);

        btnCancel.setOnAction(e -> dialog.close());

        btnSave.setOnAction(e -> {
            String nom = tfNom.getText().trim();
            String desc = tfDesc.getText().trim();
            String image = tfImage.getText().trim();
            String prixStr = tfPrix.getText().trim();
            String stockStr = tfStock.getText().trim();
            String poidsStr = tfPoids.getText().trim();

            if (nom.isEmpty()) {
                showAlert("Input Error", "Product name is required.");
                return;
            }
            if (desc.isEmpty()) {
                showAlert("Input Error", "Product description is required.");
                return;
            }
            if (image.isEmpty()) {
                showAlert("Input Error", "Product photo is required. Please select a file.");
                return;
            }
            if (prixStr.isEmpty()) {
                showAlert("Input Error", "Product price is required.");
                return;
            }
            if (stockStr.isEmpty()) {
                showAlert("Input Error", "Product stock quantity is required.");
                return;
            }
            if (poidsStr.isEmpty()) {
                showAlert("Input Error", "Product weight is required.");
                return;
            }

            double prix;
            try {
                prix = Double.parseDouble(prixStr);
                if (prix <= 0) {
                    showAlert("Input Error", "Price must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid numeric value for Price.");
                return;
            }

            int stock;
            try {
                stock = Integer.parseInt(stockStr);
                if (stock <= 0) {
                    showAlert("Input Error", "Stock must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid numeric value for Stock.");
                return;
            }

            double poids;
            try {
                poids = Double.parseDouble(poidsStr);
                if (poids <= 0) {
                    showAlert("Input Error", "Weight must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid numeric value for Weight.");
                return;
            }

            Produit newProduct = new Produit(0, nom, desc, image, prix, stock, poids);
            produitService.addProduit(newProduct);
            refreshList();
            dialog.close();
        });

        dialog.showAndWait();
    }

    @FXML
    public void handleEdit() {
        // Ensure the product is selected from the card (not ListView)
        if (selectedProduitFromCard == null) {
            showAlert("No Selection", "Please select a product to edit.");
            return;
        }

        Produit selected = selectedProduitFromCard;  // Use the selected product from the card

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Edit Product");

        // GridPane for dialog content
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);

        // Name
        Label labelNom = new Label("Nom:");
        TextField tfNom = new TextField(selected.getNom());
        grid.add(labelNom, 0, 0);
        grid.add(tfNom, 1, 0);

        // Description
        Label labelDesc = new Label("Description:");
        TextField tfDesc = new TextField(selected.getDescription());
        grid.add(labelDesc, 0, 1);
        grid.add(tfDesc, 1, 1);

        // Photo
        Label labelImage = new Label("Photo:");
        TextField tfImage = new TextField(selected.getImage());
        tfImage.setEditable(false);
        Button btnBrowse = new Button("Browse");
        HBox hboxImage = new HBox(5, tfImage, btnBrowse);
        grid.add(labelImage, 0, 2);
        grid.add(hboxImage, 1, 2);

        btnBrowse.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Photo");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(dialog);
            if (selectedFile != null) {
                tfImage.setText(selectedFile.getAbsolutePath());
            }
        });

        // Price
        Label labelPrix = new Label("Prix:");
        TextField tfPrix = new TextField(String.valueOf(selected.getPrix()));
        grid.add(labelPrix, 0, 3);
        grid.add(tfPrix, 1, 3);

        // Stock
        Label labelStock = new Label("Stock:");
        TextField tfStock = new TextField(String.valueOf(selected.getStock()));
        grid.add(labelStock, 0, 4);
        grid.add(tfStock, 1, 4);

        // Weight (Poids)
        Label labelPoids = new Label("Poids:");
        TextField tfPoids = new TextField(String.valueOf(selected.getPoids()));
        grid.add(labelPoids, 0, 5);
        grid.add(tfPoids, 1, 5);

        // Buttons
        HBox hboxButtons = new HBox(10);
        Button btnSave = new Button("Save");
        Button btnCancel = new Button("Cancel");
        hboxButtons.getChildren().addAll(btnSave, btnCancel);
        grid.add(hboxButtons, 1, 6);

        Scene scene = new Scene(grid);
        dialog.setScene(scene);

        btnCancel.setOnAction(e -> dialog.close());

        btnSave.setOnAction(e -> {
            // Retrieve updated values from the form
            String nom = tfNom.getText().trim();
            String desc = tfDesc.getText().trim();
            String image = tfImage.getText().trim();
            String prixStr = tfPrix.getText().trim();
            String stockStr = tfStock.getText().trim();
            String poidsStr = tfPoids.getText().trim();

            // Validate inputs
            if (nom.isEmpty()) {
                showAlert("Input Error", "Product name is required.");
                return;
            }
            if (desc.isEmpty()) {
                showAlert("Input Error", "Product description is required.");
                return;
            }
            if (image.isEmpty()) {
                showAlert("Input Error", "Product photo is required. Please select a file.");
                return;
            }
            if (prixStr.isEmpty()) {
                showAlert("Input Error", "Product price is required.");
                return;
            }
            if (stockStr.isEmpty()) {
                showAlert("Input Error", "Product stock quantity is required.");
                return;
            }
            if (poidsStr.isEmpty()) {
                showAlert("Input Error", "Product weight is required.");
                return;
            }

            // Parse numeric values
            double prix;
            int stock;
            double poids;

            try {
                prix = Double.parseDouble(prixStr);
                if (prix <= 0) {
                    showAlert("Input Error", "Price must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid numeric value for Price.");
                return;
            }

            try {
                stock = Integer.parseInt(stockStr);
                if (stock <= 0) {
                    showAlert("Input Error", "Stock must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid numeric value for Stock.");
                return;
            }

            try {
                poids = Double.parseDouble(poidsStr);
                if (poids <= 0) {
                    showAlert("Input Error", "Weight must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                showAlert("Input Error", "Please enter a valid numeric value for Weight.");
                return;
            }

            // Update the selected product with new values
            selected.setNom(nom);
            selected.setDescription(desc);
            selected.setImage(image);
            selected.setPrix(prix);
            selected.setStock(stock);
            selected.setPoids(poids);

            // Call the service to update the product in the database
            produitService.updateProduit(selected);

            // Refresh the product list to show the updated product
            refreshList();

            // Close the dialog after saving the changes
            dialog.close();
        });

        dialog.showAndWait();
    }




    @FXML
    public void handleDelete() {
        Produit selected = listView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a product to delete.");
            return;
        }
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete " + selected.getNom() + "?", ButtonType.YES, ButtonType.NO);
        confirmation.setHeaderText(null);
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                produitService.deleteProduit(selected);
                refreshList();
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
