import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.util.*;

public class EmergencyAlertHandler {

    public static VBox createEmergencyAlertForm(int patientId) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: #ffffff;");
        layout.setPrefWidth(400);
        layout.setPrefHeight(300);
        layout.setSpacing(10);
        layout.setAlignment(Pos.CENTER);

        // Label for selecting doctor
        Label label = new Label("Select the doctor with whom your appointment is scheduled:");
        label.setStyle("-fx-font-size: 14px; -fx-text-fill: red; -fx-font-weight: bold;");

        // ComboBox to list doctors
        ComboBox<String> doctorComboBox = new ComboBox<>();
        doctorComboBox.setPromptText("Select Doctor");

        // Fetch the doctors with scheduled appointments
        List<String> doctorNames = getScheduledDoctors(patientId);
        doctorComboBox.getItems().addAll(doctorNames);

        // TextArea for patient to describe the situation
        Label detailsLabel = new Label("Describe what happened:");
        TextArea detailsTextArea = new TextArea();
        detailsTextArea.setPromptText("Enter details of your emergency situation...");
        detailsTextArea.setWrapText(true);
        detailsTextArea.setPrefHeight(100);

        // Confirm button for triggering the alert
        Button confirmBtn = new Button("Yes, Trigger Alert");
        confirmBtn.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 14px;");
        confirmBtn.setOnAction(e -> {
            String selectedDoctorName = doctorComboBox.getValue();
            String patientDetails = detailsTextArea.getText();
            if (selectedDoctorName != null && !selectedDoctorName.isEmpty() && !patientDetails.isEmpty()) {
                validateDoctorName(patientId, selectedDoctorName, patientDetails);
            } else {
                UIUtils.showAlert("Please select a valid doctor and provide details of the emergency.");
            }
        });

        // Cancel button to exit the form
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color: gray; -fx-text-fill: white; -fx-font-size: 14px;");
        cancelBtn.setOnAction(e -> layout.getChildren().clear());

        // Adding elements to layout
        layout.getChildren().addAll(label, doctorComboBox, detailsLabel, detailsTextArea, confirmBtn, cancelBtn);

        return layout;
    }

    private static List<String> getScheduledDoctors(int patientId) {
        Set<String> doctorNames = new LinkedHashSet<>(); // preserves order and ensures uniqueness
        try (Connection conn = DatabaseConnector.getConnection()) {
            String getDoctorsQuery = "SELECT d.name FROM appointments a " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "WHERE a.patient_id = ? AND a.status = 'Scheduled' " +
                    "ORDER BY a.date DESC";
            try (PreparedStatement stmt = conn.prepareStatement(getDoctorsQuery)) {
                stmt.setInt(1, patientId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        doctorNames.add(rs.getString("name")); // only unique names will be added
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert("Error: Could not fetch scheduled doctors.\n" + e.getMessage());
        }
        return new ArrayList<>(doctorNames); // convert back to list for ComboBox
    }

    private static void validateDoctorName(int patientId, String enteredDoctorName, String patientDetails) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String scheduledDoctorName = getScheduledDoctorName(patientId, conn);
            if (scheduledDoctorName == null) {
                UIUtils.showAlert("No scheduled appointment found for this patient.");
                return;
            }

            if (scheduledDoctorName.equalsIgnoreCase(enteredDoctorName)) {
                triggerAlert(patientId, enteredDoctorName, patientDetails);
            } else {
                UIUtils.showAlert("The doctor's name does not match the scheduled appointment. Please verify.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert("Error: Could not validate doctor name.\n" + e.getMessage());
        }
    }

    private static String getScheduledDoctorName(int patientId, Connection conn) throws Exception {
        String scheduledDoctorName = null;
        String getDoctorQuery = "SELECT d.name FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.patient_id = ? AND a.status = 'Scheduled' " +
                "ORDER BY a.date DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(getDoctorQuery)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    scheduledDoctorName = rs.getString("name");
                }
            }
        }
        return scheduledDoctorName;
    }

    private static void triggerAlert(int patientId, String enteredDoctorName, String patientDetails) {
        try (Connection conn = DatabaseConnector.getConnection()) {
            String patientName = getPatientName(patientId, conn);
            String doctorName = getDoctorName(patientId, conn);
            String doctorEmail = getDoctorEmail(patientId, conn);

            if (patientName == null || doctorName == null || doctorEmail == null) {
                UIUtils.showAlert("Error retrieving patient or doctor information.");
                return;
            }

            insertEmergencyAlert(patientId, doctorName, doctorEmail, patientName, patientDetails, conn);

            String message = buildAlertMessage(patientName, doctorName, doctorEmail, patientDetails);

            // Show confirmation popup
            showAlertPopup(message);

            // Send Email to Doctor
            sendEmailToDoctor(doctorEmail, message);

        } catch (Exception e) {
            e.printStackTrace();
            UIUtils.showAlert("Error: Could not trigger emergency alert.\n" + e.getMessage());
        }
    }

    private static String getPatientName(int patientId, Connection conn) throws Exception {
        String patientName = null;
        String getPatientQuery = "SELECT name FROM patients WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(getPatientQuery)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    patientName = rs.getString("name");
                }
            }
        }
        return patientName;
    }

    private static String getDoctorName(int patientId, Connection conn) throws Exception {
        String doctorName = null;
        String getDoctorQuery = "SELECT d.name FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.patient_id = ? AND a.status = 'Scheduled' " +
                "ORDER BY a.date DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(getDoctorQuery)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    doctorName = rs.getString("name");
                }
            }
        }
        return doctorName;
    }

    private static String getDoctorEmail(int patientId, Connection conn) throws Exception {
        String doctorEmail = null;
        String getDoctorEmailQuery = "SELECT d.email FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.patient_id = ? AND a.status = 'Scheduled' " +
                "ORDER BY a.date DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(getDoctorEmailQuery)) {
            stmt.setInt(1, patientId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    doctorEmail = rs.getString("email");
                }
            }
        }
        return doctorEmail;
    }

    private static void insertEmergencyAlert(int patientId, String doctorName, String doctorEmail, String patientName, String patientDetails, Connection conn) throws Exception {
        String insertAlertQuery = "INSERT INTO emergency_alerts (patient_id, doctor_id, patient_name, doctor_name, doctor_email, details, alert_time) " +
                "VALUES (?, (SELECT doctor_id FROM appointments WHERE patient_id = ? LIMIT 1), ?, ?, ?, ?, NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(insertAlertQuery)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, patientId);
            stmt.setString(3, patientName);
            stmt.setString(4, doctorName);
            stmt.setString(5, doctorEmail);
            stmt.setString(6, patientDetails);
            stmt.executeUpdate();
        }
    }

    private static String buildAlertMessage(String patientName, String doctorName, String doctorEmail, String patientDetails) {
        return "Emergency Alert:\n" +
                "Patient: " + patientName + "\n" +
                "Doctor: " + doctorName + "\n" +
                "Email: " + doctorEmail + "\n" +
                "Details: " + patientDetails + "\n" +
                "Please respond immediately.";
    }

    private static void showAlertPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Emergency Alert");
        alert.setHeaderText("An emergency alert has been triggered.");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static void sendEmailToDoctor(String to, String messageBody) {
        final String from = "remotepatientmonitoringsystem1@gmail.com"; // Replace with sender's email
        final String password = "drfwrwofmevlhgjr";      // Replace with sender's email password

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Emergency Alert - Immediate Attention Needed");
            message.setText(messageBody);

            Transport.send(message);

            System.out.println("Emergency alert email sent successfully to " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
            UIUtils.showAlert("Failed to send email to doctor: " + e.getMessage());
        }
    }

}
