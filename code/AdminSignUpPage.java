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
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminSignUpPage {

    private TextField cnicField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label messageLabel;

    public void show(Stage stage) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("Admin Sign Up");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.PRIMARY_COLOR + ";");

        cnicField = new TextField();
        cnicField.setPromptText("Enter CNIC (e.g. 36601-4673227-1)");
        styleTextField(cnicField);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        styleTextField(passwordField);

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        styleTextField(confirmPasswordField);

        Button signUpBtn = UIUtils.createHoverButton("Register", "✅", UIUtils.SECONDARY_COLOR);
        Button backBtn = UIUtils.createHoverButton("Back to Login", "⬅", UIUtils.ACCENT_COLOR);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        signUpBtn.setOnAction(e -> handleSignUp(stage));
        backBtn.setOnAction(e -> {
            AdminLoginPopup loginPopup = new AdminLoginPopup();
            loginPopup.show(stage);
        });

        layout.getChildren().addAll(title, cnicField, passwordField, confirmPasswordField, signUpBtn, backBtn, messageLabel);

        Scene scene = new Scene(layout, 400, 450);
        stage.setScene(scene);
        stage.setTitle("Admin Sign Up");
        stage.show();
    }

    private void handleSignUp(Stage stage) {
        String cnic = cnicField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();

        if (cnic.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            messageLabel.setText("All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Passwords do not match.");
            return;
        }

        // Validate CNIC format: 5 digits - 7 digits - 1 digit
        if (!cnic.matches("^\\d{5}-\\d{7}-\\d{1}$")) {
            messageLabel.setText("CNIC format must be like 36601-4673227-1.");
            return;
        }

        if (isCnicExists(cnic)) {
            messageLabel.setText("This CNIC is already registered.");
            return;
        }

        if (addAdminToDatabase(cnic, password)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Admin registered successfully!");
            alert.showAndWait();

            new AdminLoginPopup().show(stage);
        } else {
            messageLabel.setText("Failed to register. Please try again.");
        }
    }

    private boolean isCnicExists(String cnic) {
        String query = "SELECT COUNT(*) FROM admins WHERE cnic = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cnic);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking CNIC existence: " + e.getMessage());
        }
        return false;
    }

    private boolean addAdminToDatabase(String cnic, String password) {
        String insertQuery = "INSERT INTO admins (cnic, password) VALUES (?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, cnic);
            stmt.setString(2, password);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error inserting admin: " + e.getMessage());
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
