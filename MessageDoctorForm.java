import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jakarta.mail.internet.MimeMessage;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class MessageDoctorForm {

    public static void showMessageForm(StackPane contentArea, int patientId) {
        contentArea.getChildren().clear();
        Map<String, String> doctorMap = getDoctorsFromDatabase();

        // Doctor selection dropdown
        ComboBox<String> doctorComboBox = createDoctorComboBox(doctorMap);

        // Text area for the message
        TextArea messageArea = createMessageArea();

        // Video call checkbox
        CheckBox videoCallRequestCheckBox = createVideoCallCheckBox();

        // Send button
        Button sendBtn = createSendButton(contentArea, doctorComboBox, messageArea, videoCallRequestCheckBox, patientId, doctorMap);

        // Layout for the form
        VBox layout = createLayout(doctorComboBox, messageArea, videoCallRequestCheckBox, sendBtn);
        layout.setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Add the form to the content area
        contentArea.getChildren().add(layout);
    }

    // Method to create the layout for the message form
    private static VBox createLayout(ComboBox<String> doctorComboBox, TextArea messageArea,
                                     CheckBox videoCallRequestCheckBox, Button sendBtn) {
        Label selectDoctorLabel = new Label("Select Doctor:");
        Label msgLabel = new Label("Your Message:");

        VBox layout = new VBox(10, selectDoctorLabel, doctorComboBox, msgLabel, messageArea, videoCallRequestCheckBox, sendBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);
        return layout;
    }

    // ✅ Create doctor combo box with fetched doctors from DB
    private static ComboBox<String> createDoctorComboBox(Map<String, String> doctorMap) {
        ComboBox<String> doctorComboBox = new ComboBox<>();
        doctorComboBox.getItems().addAll(doctorMap.keySet());
        doctorComboBox.setPromptText("Choose doctor");
        return doctorComboBox;
    }

    // ✅ Create text area for message
    private static TextArea createMessageArea() {
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Type your message here...");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(6);
        return messageArea;
    }

    // ✅ Create checkbox for video call request
    private static CheckBox createVideoCallCheckBox() {
        return new CheckBox("Request Video Call");
    }

    // ✅ Create the send button
    private static Button createSendButton(StackPane contentArea, ComboBox<String> doctorComboBox, TextArea messageArea,
                                           CheckBox videoCallRequestCheckBox, int patientId, Map<String, String> doctorMap) {
        Button sendBtn = new Button("Send Message");
        sendBtn.setStyle("-fx-background-color: #1D3557; -fx-text-fill: white;");
        sendBtn.setOnAction(e -> {
            String selectedDoctor = doctorComboBox.getValue();
            String message = messageArea.getText().trim();

            if (selectedDoctor == null || message.isEmpty()) {
                showAlert("Please select a doctor and write a message.");
                return;
            }

            String toEmail = doctorMap.get(selectedDoctor);
            boolean videoCallRequest = videoCallRequestCheckBox.isSelected();
            String patientName = getPatientName(patientId);

            String subject = "Message from Patient: " + patientName;
            String body = "Patient Name: " + patientName + "\n\n" + message;

            if (videoCallRequest) {
                subject += " (Video Call Requested)";
                body += "\n\nThe patient has requested a video call.";
            }

            boolean success = sendEmailToDoctor(toEmail, subject, body);

            if (success) {
                storeMessageInDatabase(patientId, toEmail, message, videoCallRequest);

                if (videoCallRequest) {
                    showAlert("Video call request sent to Dr. " + selectedDoctor + ". You will receive a link if accepted.");
                } else {
                    showAlert("Message sent successfully to " + selectedDoctor);
                }
            } else {
                showAlert("Failed to send message. Try again.");
            }
        });
        return sendBtn;
    }

    // ✅ Send email to doctor
    private static boolean sendEmailToDoctor(String to, String subject, String body) {
        final String from = "remotepatientmonitoringsystem1@gmail.com";
        final String appPassword = "drfwrwofmevlhgjr";  // Ensure this is a secure password
        final String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(from, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from, "Remote Patient Monitoring System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            message.setHeader("X-Priority", "1");
            message.setHeader("Importance", "High");

            Transport.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error sending email. Please check your SMTP settings.");
            return false;
        }
    }

    // ✅ Show alert dialog
    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ✅ Database method to fetch doctor names and emails
    private static Map<String, String> getDoctorsFromDatabase() {
        Map<String, String> doctorMap = new HashMap<>();

        String url = "jdbc:mysql://localhost:3306/healthcare_system"; // Adjust DB URL
        String user = "root"; // Your DB username
        String password = "Anasnorani@107"; // Your DB password

        String query = "SELECT name, email FROM doctors";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                doctorMap.put(name, email);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error fetching doctors from database.");
        }

        return doctorMap;
    }

    // ✅ Method to get the patient's name from the database
    private static String getPatientName(int patientId) {
        String patientName = null;
        String query = "SELECT name FROM patients WHERE id = ?";

        String url = "jdbc:mysql://localhost:3306/healthcare_system"; // Adjust DB URL
        String user = "root"; // Your DB username
        String password = "Anasnorani@107"; // Your DB password

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                patientName = rs.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error fetching patient name from database.");
        }

        return patientName;
    }

    // ✅ Method to store message in the database
    private static void storeMessageInDatabase(int patientId, String doctorEmail, String messageText, boolean videoCallRequest) {
        String url = "jdbc:mysql://localhost:3306/healthcare_system"; // Adjust DB URL
        String user = "root"; // Your DB username
        String password = "Anasnorani@107"; // Your DB password

        String insertSQL = "INSERT INTO messages (patient_id, doctor_email, message, timestamp, video_call_request) VALUES (?, ?, ?, NOW(), ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            stmt.setInt(1, patientId);
            stmt.setString(2, doctorEmail);
            stmt.setString(3, messageText);
            stmt.setBoolean(4, videoCallRequest);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Message sent but failed to store in database.");
        }
    }
}
