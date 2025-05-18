import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

public class SendReminderPane extends VBox {

    private final int doctorId;
    private final ComboBox<String> patientComboBox = new ComboBox<>();
    private final TextArea messageBox = new TextArea();
    private final HashMap<String, String> patientEmailMap = new HashMap<>();

    public SendReminderPane(int doctorId) {
        this.doctorId = doctorId;
        setSpacing(15);
        setPadding(new Insets(30));
        setAlignment(Pos.CENTER);
        setStyle("-fx-background-color: white;");

        setupUI();
        loadPatients();
    }

    private void setupUI() {
        Label label = new Label("Select a patient to send a reminder:");
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        patientComboBox.setPrefWidth(300);

        Label messageLabel = new Label("Custom Reminder Message:");
        messageBox.setPromptText("Enter a custom message here...");
        messageBox.setWrapText(true);
        messageBox.setPrefHeight(100);
        messageBox.setPrefWidth(300);

        Button sendButton = new Button("Send Reminder");
        sendButton.setOnAction(e -> sendReminder());

        this.getChildren().addAll(label, patientComboBox, messageLabel, messageBox, sendButton);
    }

    private void loadPatients() {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT DISTINCT p.name, p.email FROM appointments a " +
                    "JOIN patients p ON a.patient_id = p.id " +
                    "WHERE a.doctor_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");

                if (email != null && !email.isEmpty()) {
                    patientComboBox.getItems().add(name);
                    patientEmailMap.put(name, email);
                }
            }

            patientComboBox.setPromptText(patientComboBox.getItems().isEmpty() ? "No patients found" : "Select Patient");

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to load patients.").show();
        }
    }

    private void sendReminder() {
        String selectedPatient = patientComboBox.getValue();
        String customMessage = messageBox.getText().trim();

        if (selectedPatient == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a patient.").show();
            return;
        }

        if (customMessage.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Please enter a reminder message.").show();
            return;
        }

        String email = patientEmailMap.get(selectedPatient);
        if (email != null) {
            String fullMessage = PatientReminderService.composeBody(selectedPatient)
                    + "\n\n---\nDoctor's Note:\n" + customMessage;
            PatientReminderService.sendEmail(email,
                    "Appointment Reminder - Hospital Management System",
                    fullMessage);
            new Alert(Alert.AlertType.INFORMATION, "Reminder sent to " + selectedPatient + " at " + email).show();
        } else {
            new Alert(Alert.AlertType.ERROR, "No email found for selected patient.").show();
        }
    }
}
