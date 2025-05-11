import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DoctorChangePasswordPane extends VBox {

    private final String doctorCnic;

    public DoctorChangePasswordPane(String doctorCnic) {
        this.doctorCnic = doctorCnic;
        setupUI();
    }

    private void setupUI() {
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(30));
        this.setSpacing(15);
        this.setStyle("-fx-background-color: white;");

        Label title = new Label("Change Your Password");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        PasswordField currentPassword = new PasswordField();
        currentPassword.setPromptText("Current Password");

        PasswordField newPassword = new PasswordField();
        newPassword.setPromptText("New Password");

        PasswordField confirmPassword = new PasswordField();
        confirmPassword.setPromptText("Confirm New Password");

        Button changeButton = new Button("Change Password");
        changeButton.setOnAction(e -> handleChangePassword(
                currentPassword.getText(),
                newPassword.getText(),
                confirmPassword.getText()
        ));

        this.getChildren().addAll(title, currentPassword, newPassword, confirmPassword, changeButton);
    }

    private void handleChangePassword(String current, String newPass, String confirm) {
        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "All fields are required.");
            return;
        }

        if (!newPass.equals(confirm)) {
            showAlert(Alert.AlertType.WARNING, "New passwords do not match.");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String checkQuery = "SELECT password FROM doctors WHERE cnic = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, doctorCnic);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (!storedPassword.equals(current)) {
                    showAlert(Alert.AlertType.ERROR, "Current password is incorrect.");
                    return;
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Doctor not found.");
                return;
            }

            String updateQuery = "UPDATE doctors SET password = ? WHERE cnic = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, newPass);
            updateStmt.setString(2, doctorCnic);

            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Password changed successfully.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to update password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "An error occurred: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type, msg);
        alert.show();
    }
}
