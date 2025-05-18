import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.sql.*;

public class ChangePatientPassword {

    public static void show(StackPane contentArea, int patientId) {
        // Clear the content area before displaying new content
        contentArea.getChildren().clear();

        // Create password fields and button for password change
        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm New Password");

        Button changePasswordBtn = new Button("Change Password");
        changePasswordBtn.setOnAction(e -> {
            String currentPassword = currentPasswordField.getText().trim();
            String newPassword = newPasswordField.getText().trim();
            String confirmPassword = confirmPasswordField.getText().trim();

            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                UIUtils.showAlert("Please fill in all fields.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                UIUtils.showAlert("New passwords do not match.");
                return;
            }

            if (!validateCurrentPassword(patientId, currentPassword)) {
                UIUtils.showAlert("Current password is incorrect.");
                return;
            }

            if (updatePassword(patientId, newPassword)) {
                UIUtils.showAlert("Password changed successfully.");
            } else {
                UIUtils.showAlert("Failed to change password.");
            }
        });

        // Layout for the password change form
        VBox layout = new VBox(10,
                new Label("Enter Current Password:"), currentPasswordField,
                new Label("Enter New Password:"), newPasswordField,
                new Label("Confirm New Password:"), confirmPasswordField,
                changePasswordBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        // Set the layout style for a clean, white background
        layout.setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Add the layout to the content area
        contentArea.getChildren().add(layout);
    }

    private static boolean validateCurrentPassword(int patientId, String currentPassword) {
        String query = "SELECT password FROM patients WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthcare_system", "root", "Anasnorani@107");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(currentPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean updatePassword(int patientId, String newPassword) {
        String updateSQL = "UPDATE patients SET password = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthcare_system", "root", "Anasnorani@107");
             PreparedStatement stmt = conn.prepareStatement(updateSQL)) {
            stmt.setString(1, newPassword);
            stmt.setInt(2, patientId);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
