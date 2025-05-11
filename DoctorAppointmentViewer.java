import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorAppointmentViewer {

    private int doctorId;

    // Constructor now only takes doctorId, as patientId is not directly used here
    public DoctorAppointmentViewer(int doctorId) {
        this.doctorId = doctorId;
    }

    public void displayAppointments(Pane contentArea) {  // Accepting Pane so it works with both StackPane and VBox
        String query = "SELECT a.patient_id, p.name, a.date, a.time, a.status " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "WHERE a.doctor_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            ListView<String> appointmentListView = new ListView<>();
            while (rs.next()) {
                String appointmentDetails = "Patient: " + rs.getString("name") +
                        ", Date: " + rs.getDate("date") +
                        ", Time: " + rs.getString("time") +
                        ", Status: " + rs.getString("status");
                appointmentListView.getItems().add(appointmentDetails);
            }

            if (appointmentListView.getItems().isEmpty()) {
                showAlert("No appointments found.");
                return;
            }

            // Clear the content area and add new appointment list view
            contentArea.getChildren().clear();

            VBox appointmentLayout = new VBox(20);
            appointmentLayout.getChildren().addAll(new Text("Appointments"), appointmentListView);
            contentArea.getChildren().add(appointmentLayout);

        } catch (SQLException ex) {
            ex.printStackTrace();
            showAlert("Error fetching appointments.");
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
