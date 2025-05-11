import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatientDashboard extends Stage {
    private TextField usernameField;
    private PasswordField passwordField;

    private Stage primaryStage;

    public PatientDashboard(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setupUI();
    }


    private void setupUI() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("Patient Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.PRIMARY_COLOR + ";");

        usernameField = new TextField();
        usernameField.setPromptText("Enter Patient CNIC");
        styleTextField(usernameField);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        styleTextField(passwordField);

        Button loginBtn = UIUtils.createHoverButton("Login", "🔑", UIUtils.SECONDARY_COLOR);
        Button backBtn = UIUtils.createHoverButton("Back", "⬅", UIUtils.ACCENT_COLOR);

        loginBtn.setOnAction(e -> handleLogin());
        backBtn.setOnAction(e -> this.close());

        layout.getChildren().addAll(title, usernameField, passwordField, loginBtn, backBtn);
        Scene scene = new Scene(layout, 400, 400);
        this.setScene(scene);
        this.setTitle("Patient Dashboard");
    }

    private void handleLogin() {
        try {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                UIUtils.showAlert("All fields are required!");
                return;
            }

            String query = "SELECT id, name, password FROM patients WHERE username = ?";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    if (storedPassword != null && password.equals(storedPassword.trim())) {
                        int patientId = rs.getInt("id");
                        String patientName = rs.getString("name");

                        PatientLogin.setLoggedInPatient(patientId, patientName);
                        System.out.println("[DEBUG] Logged in as: " + patientName + " (ID: " + patientId + ")");

                        this.close();
                        new PatientMenu().show(primaryStage, patientId);
                        return;
                    }
                }

                UIUtils.showAlert("Invalid Credentials!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            UIUtils.showAlert("Database Error: " + ex.getMessage());
        }
    }

    private boolean authenticate(String username, String password) throws SQLException {
        String query = "SELECT id, name, password FROM patients WHERE username = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (storedPassword == null) return false;

                storedPassword = storedPassword.trim();

                if (password.equals(storedPassword)) {
                    int patientId = rs.getInt("id");
                    String patientName = rs.getString("name");
                    PatientLogin.setLoggedInPatient(patientId, patientName);
                    System.out.println("[DEBUG] Logged in as: " + patientName + " (ID: " + patientId + ")");
                    return true;
                }
            }

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
