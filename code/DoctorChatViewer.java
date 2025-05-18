import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorChatViewer {

    // Method that returns a VBox containing chat history
    public static VBox createChatHistoryView(String doctorEmail) {
        // Retrieve the chat history
        List<String> chatMessages = fetchMessagesFromDB(doctorEmail);

        // Create ListView to display chat messages
        ListView<String> chatListView = new ListView<>();
        chatListView.getItems().addAll(chatMessages);
        chatListView.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5px;");

        // Create a VBox layout to add all the components
        VBox layout = new VBox(10, new Label("Messages From Patients:"), chatListView);
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");  // Set the background to white for the pane

        return layout;  // Return instead of replacing the scene
    }

    private static List<String> fetchMessagesFromDB(String doctorEmail) {
        List<String> messages = new ArrayList<>();

        String query = "SELECT m.patient_id, p.name AS patient_name, m.message, m.timestamp " +
                "FROM messages m " +
                "JOIN patients p ON m.patient_id = p.id " +
                "WHERE m.doctor_email = ? " +
                "ORDER BY m.timestamp DESC";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthcare_system", "root", "Anasnorani@107");
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, doctorEmail);
            ResultSet rs = stmt.executeQuery();

            System.out.println("Fetching messages for doctor: " + doctorEmail);

            while (rs.next()) {
                int patientId = rs.getInt("patient_id");
                String patientName = rs.getString("patient_name");
                String msg = rs.getString("message");
                Timestamp time = rs.getTimestamp("timestamp");

                System.out.println("Message: " + patientName + " (" + patientId + "): " + msg);

                messages.add("From Patient: " + patientName + " (ID: " + patientId + ")\n" +
                        msg + "\n[" + time.toString() + "]\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            messages.add("Failed to fetch messages.");
        }

        return messages;
    }
}
