import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorDashboard extends Stage {
    private TextField doctorIdField;
    private PasswordField passwordField;

    private Stage primaryStage; // Reference to the primary stage

    public DoctorDashboard(Stage primaryStage) {
        this.primaryStage = primaryStage;
        setupUI();
    }

    private void setupUI() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("Doctor Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.PRIMARY_COLOR + ";");

        doctorIdField = new TextField();
        doctorIdField.setPromptText("Enter Doctor CNIC");
        styleTextField(doctorIdField);

        passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        styleTextField(passwordField);

        Button loginBtn = UIUtils.createHoverButton("Login", "🔑", UIUtils.SECONDARY_COLOR);
        Button backBtn = UIUtils.createHoverButton("Back", "⬅", UIUtils.ACCENT_COLOR);

        loginBtn.setOnAction(e -> handleLogin());
        backBtn.setOnAction(e -> goBackToLoginPage());

        layout.getChildren().addAll(title, doctorIdField, passwordField, loginBtn, backBtn);
        Scene scene = new Scene(layout, 400, 400);
        this.setScene(scene);
        this.setTitle("Doctor Dashboard");
    }

    private void handleLogin() {
        try {
            String doctorCnic = doctorIdField.getText().trim();
            String password = passwordField.getText().trim();

            if (doctorCnic.isEmpty() || password.isEmpty()) {
                UIUtils.showAlert("All fields are required!");
                return;
            }

            if (authenticate(doctorCnic, password)) {
                // Create the doctor menu and replace the scene on the primary stage
                DoctorMenu doctorMenu = new DoctorMenu(doctorCnic);
                Scene doctorMenuScene = doctorMenu.getScene();

                // Close the current doctor dashboard window
                this.close(); // Close the DoctorDashboard window

                // Replace current scene with the doctor menu scene
                primaryStage.setScene(doctorMenuScene);

            } else {
                UIUtils.showAlert("Invalid Credentials!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            UIUtils.showAlert("Database Error: " + ex.getMessage());
        }
    }

    private boolean authenticate(String cnic, String password) throws SQLException {
        String query = "SELECT password FROM doctors WHERE cnic = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, cnic);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return password.equals(storedPassword);
            }
        }
        return false;
    }

    private void goBackToLoginPage() {
        // Switch back to the login page
        LoginPage loginPage = new LoginPage();
        loginPage.start(primaryStage);
    }

    private void styleTextField(TextField field) {
        field.setStyle("-fx-background-color: white;"
                + "-fx-border-color: #dcdde1;"
                + "-fx-border-radius: 5;"
                + "-fx-padding: 8px;"
                + "-fx-font-size: 14px;"
                + "-fx-prompt-text-fill: #a4a4a4;"
                + "-fx-pref-width: 300;"
                + "-fx-pref-height: 35;");
        field.setEffect(new DropShadow(2, Color.gray(0.5)));
    }
}
