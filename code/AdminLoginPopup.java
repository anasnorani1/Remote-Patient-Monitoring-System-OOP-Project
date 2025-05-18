import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminLoginPopup implements Dashboard {

    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;

    @Override
    public void show(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("Admin Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.PRIMARY_COLOR + ";");

        usernameField = new TextField();
        usernameField.setPromptText("Enter Admin CNIC");
        styleTextField(usernameField);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        styleTextField(passwordField);

        Button loginBtn = UIUtils.createHoverButton("Login", "🔑", UIUtils.SECONDARY_COLOR);
        Button backBtn = UIUtils.createHoverButton("Back", "⬅", UIUtils.ACCENT_COLOR);
        Button addAdminBtn = UIUtils.createHoverButton("Add New Admin", "➕", UIUtils.PRIMARY_COLOR);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        loginBtn.setOnAction(e -> handleLogin(primaryStage));
        backBtn.setOnAction(e -> {
            LoginPage loginPage = new LoginPage();
            loginPage.start(primaryStage);
        });

        addAdminBtn.setOnAction(e -> {
            AdminSignUpPage signUpPage = new AdminSignUpPage();
            signUpPage.show(primaryStage);
        });

        layout.getChildren().addAll(title, usernameField, passwordField, loginBtn, addAdminBtn, backBtn, messageLabel);

        Scene scene = new Scene(layout, 400, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Login");
        primaryStage.show();
    }

    private void handleLogin(Stage primaryStage) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("All fields are required!");
            return;
        }

        if (authenticateAdmin(username, password)) {
            System.out.println("[DEBUG] Admin login successful.");
            new AdminDashboard(primaryStage).show();
        } else {
            messageLabel.setText("Invalid username or password.");
        }
    }

    private boolean authenticateAdmin(String username, String password) {
        String query = "SELECT * FROM admins WHERE cnic = ? AND password = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            return stmt.executeQuery().next();

        } catch (Exception e) {
            System.err.println("Database error during admin login: " + e.getMessage());
            messageLabel.setText("Database Error: " + e.getMessage());
            return false;
        }
    }

    private void styleTextField(TextField field) {
        field.setStyle("-fx-background-color: white;" +
                "-fx-border-color: #dcdde1;" +
                "-fx-border-radius: 5;" +
                "-fx-padding: 8px;" +
                "-fx-font-size: 14px;" +
                "-fx-prompt-text-fill: #a4a4a4;" +
                "-fx-pref-width: 300;" +
                "-fx-pref-height: 35;");
        field.setEffect(new DropShadow(2, Color.gray(0.5)));
    }
}
