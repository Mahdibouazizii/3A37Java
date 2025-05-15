# GreenTech – JavaFX Application (Frontend)

This is the desktop frontend for the GreenTech project built with JavaFX. It allows users to browse, buy, and interact with eco-responsible electronics.

## 💻 Features

- 🛒 Product browsing (from `produit`)
- 🧾 Order creation (linked to `commande`, `commande_produit`)
- ⭐ Rate & comment products (`feedback`, `comments`)
- 🧑‍🤝‍🧑 Join events and challenges (`event`, `challenge`, `participation`)
- 📑 View past orders and invoices (`facture`)

## 🧰 Technologies

- Java 17+
- JavaFX 17
- JDBC (MySQL)
- MVC Architecture
- FXML for UI layout

## ⚙️ Setup Instructions

### 1. Requirements
- Java JDK 17+
- SceneBuilder (for editing FXML)
- MySQL server running `projetpi2`

### 2. Clone the repo
```bash
git clone https://github.com/your-org/greentech-javafx.git
cd greentech-javafx
```

### 3. Configure DB Connection
Edit the database credentials in your `DBConnection.java` or equivalent:
```java
String url = "jdbc:mysql://localhost:3306/projetpi2";
String user = "root";
String password = "yourpassword";
```

### 4. Run the application
Use your IDE or:
```bash
javac -cp "lib/*" src/*.java
java -cp "lib/*:." Main
```

## 🔄 Sync with Symfony

> Both apps use the same DB `projetpi2`, but work independently.  
> You can manage data from Symfony or interact directly via JavaFX.

---

## 📦 Sample Screens
- Product list
- Product detail with rating
- Order cart view
- Invoice PDF export
- Event participation form

---

## ✅ To-Do
- Add map integration for eco-events
- Improve invoice export styling
- Optional: Add API client instead of direct DB access
