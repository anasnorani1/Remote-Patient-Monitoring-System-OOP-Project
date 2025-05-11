import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminLoginPopup {

    public void show(Stage ownerStage) {
        Stage loginStage = new Stage();
        loginStage.setTitle("Admin Login");
        loginStage.initModality(Modality.WINDOW_MODAL);
        loginStage.initOwner(ownerStage);

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("Admin Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.PRIMARY_COLOR + ";");

        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();

        usernameField.setPromptText("Enter Admin CNIC");
        passwordField.setPromptText("Enter Password");

        styleTextField(usernameField);
        styleTextField(passwordField);

        Button loginBtn = UIUtils.createHoverButton("Login", "🔑", UIUtils.SECONDARY_COLOR);
        Button backBtn = UIUtils.createHoverButton("Back", "⬅", UIUtils.ACCENT_COLOR);

        Label message = new Label();
        message.setStyle("-fx-text-fill: red;");

        loginBtn.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                message.setText("All fields are required!");
                return;
            }

            if (authenticateAdmin(username, password)) {
                loginStage.close();
                new AdminDashboard(ownerStage).show();
            } else {
                message.setText("Invalid username or password.");
            }
        });

        backBtn.setOnAction(e -> loginStage.close());

        layout.getChildren().addAll(title, usernameField, passwordField, loginBtn, backBtn, message);

        Scene scene = new Scene(layout, 400, 400);
        loginStage.setScene(scene);
        loginStage.show();
    }

    private boolean authenticateAdmin(String username, String password) {
        String query = "SELECT * FROM admins WHERE cnic = ? AND password = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next(); // Login successful if a record is found

        } catch (SQLException e) {
            System.err.println("Database error during admin login: " + e.getMessage());
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
