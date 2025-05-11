import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Set;

public class ProvideFeedbackWindow {

    private int doctorId;
    private StackPane contentArea;  // To update content in the same window

    public ProvideFeedbackWindow(int doctorId, StackPane contentArea) {
        this.doctorId = doctorId;
        this.contentArea = contentArea;  // Pass the contentArea from the parent view
        setupUI();
    }

    private void setupUI() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));

        // ComboBox to select a patient
        ComboBox<String> patientComboBox = new ComboBox<>();
        loadPatientNames(patientComboBox);

        // Text area for feedback
        TextArea feedbackTextArea = new TextArea();
        feedbackTextArea.setPromptText("Enter feedback for the patient...");
        feedbackTextArea.setPrefHeight(150);

        // Submit button
        Button submitButton = new Button("Submit Feedback");
        submitButton.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        submitButton.setOnAction(e -> handleSubmitFeedback(patientComboBox, feedbackTextArea));

        layout.getChildren().addAll(patientComboBox, feedbackTextArea, submitButton);

        // Clear the content area and add the new layout
        contentArea.getChildren().clear();
        contentArea.getChildren().add(layout);
    }

    private void loadPatientNames(ComboBox<String> patientComboBox) {
        String query = "SELECT DISTINCT p.id, p.name FROM patients p " +
                "JOIN appointments a ON p.id = a.patient_id " +
                "WHERE a.doctor_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            Set<String> uniquePatients = new LinkedHashSet<>();
            while (rs.next()) {
                uniquePatients.add(rs.getInt("id") + " - " + rs.getString("name"));
            }
            patientComboBox.getItems().addAll(uniquePatients);
        } catch (SQLException ex) {
            ex.printStackTrace();
            UIUtils.showAlert("Error loading patient data.");
        }
    }

    private void handleSubmitFeedback(ComboBox<String> patientComboBox, TextArea feedbackTextArea) {
        String selectedPatient = patientComboBox.getValue();
        String feedback = feedbackTextArea.getText();

        if (selectedPatient == null || feedback.isEmpty()) {
            UIUtils.showAlert("Please select a patient and provide feedback.");
            return;
        }

        int patientId = Integer.parseInt(selectedPatient.split(" - ")[0]);  // Extract patient ID

        String query = "INSERT INTO feedback (doctor_id, patient_id, feedback) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            pstmt.setInt(2, patientId);
            pstmt.setString(3, feedback);
            pstmt.executeUpdate();

            UIUtils.showAlert("Feedback submitted successfully!");

            // Clear the content area and close the form after submission
            contentArea.getChildren().clear();
        } catch (SQLException ex) {
            ex.printStackTrace();
            UIUtils.showAlert("Error submitting feedback.");
        }
    }
}
