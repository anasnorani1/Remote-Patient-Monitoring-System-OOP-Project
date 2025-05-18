import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryViewer {

    public static void showChatHistory(StackPane contentArea, int patientId) {
        // Clear the content area before displaying new content
        contentArea.getChildren().clear();

        // Fetch the chat messages from the database
        List<String> chatMessages = fetchMessagesFromDB(patientId);

        // Create a ListView to display the messages
        ListView<String> chatListView = new ListView<>();
        chatListView.getItems().addAll(chatMessages);

        // Create a label for the chat history section
        Label chatHistoryLabel = new Label("Messages Sent to Doctors:");

        // Create a VBox to hold the label and the ListView
        VBox layout = new VBox(10, chatHistoryLabel, chatListView);
        layout.setPadding(new Insets(20));

        // Set the layout style for a clean, white background
        layout.setStyle("-fx-background-color: white; -fx-padding: 20px;");

        // Add the layout to the content area
        contentArea.getChildren().add(layout);
    }

    private static List<String> fetchMessagesFromDB(int patientId) {
        List<String> messages = new ArrayList<>();

        String url = "jdbc:mysql://localhost:3306/healthcare_system";
        String user = "root";
        String password = "Anasnorani@107"; // your DB password
        String query = "SELECT doctor_email, message, timestamp FROM messages WHERE patient_id = ? ORDER BY timestamp DESC";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String doctor = rs.getString("doctor_email");
                String msg = rs.getString("message");
                Timestamp time = rs.getTimestamp("timestamp");

                // Format the message to display
                messages.add("To: " + doctor + "\n" + msg + "\n[" + time.toString() + "]\n");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            messages.add("Failed to fetch messages.");
        }

        return messages;
    }
}
