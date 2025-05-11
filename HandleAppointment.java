import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Pos;
import java.sql.*;
import java.util.*;

public class HandleAppointment {

    public void showAdminDashboard() {
        Stage dialog = new Stage();
        dialog.setTitle("Admin Dashboard");

        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        ListView<AppointmentRequest> requestListView = new ListView<>();
        List<AppointmentRequest> pendingRequests = getPendingRequests();

        if (pendingRequests.isEmpty()) {
            System.out.println("No pending requests found.");
        } else {
            System.out.println("Pending requests fetched: " + pendingRequests.size());
        }

        requestListView.getItems().addAll(pendingRequests);

        Button approveButton = new Button("Approve Appointment");
        Button rejectButton = new Button("Reject Appointment");

        approveButton.setOnAction(e -> {
            AppointmentRequest selectedRequest = requestListView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                boolean success = approveAppointmentRequest(selectedRequest.getId());
                if (success) {
                    showAlert("Appointment approved.");
                    requestListView.getItems().setAll(getPendingRequests());
                } else {
                    showAlert("Failed to approve appointment.");
                }
            } else {
                showAlert("No appointment selected.");
            }
        });

        rejectButton.setOnAction(e -> {
            AppointmentRequest selectedRequest = requestListView.getSelectionModel().getSelectedItem();
            if (selectedRequest != null) {
                boolean success = rejectAppointmentRequest(selectedRequest.getId());
                if (success) {
                    showAlert("Appointment rejected.");
                    requestListView.getItems().setAll(getPendingRequests());
                } else {
                    showAlert("Failed to reject appointment.");
                }
            } else {
                showAlert("No appointment selected.");
            }
        });

        layout.getChildren().addAll(new Label("Pending Appointments"), requestListView, approveButton, rejectButton);

        Scene scene = new Scene(layout, 700, 500);
        dialog.setScene(scene);
        dialog.show();
    }

    private List<AppointmentRequest> getPendingRequests() {
        List<AppointmentRequest> requests = new ArrayList<>();
        String query = "SELECT * FROM appointment_requests WHERE `status` = 'Requested'";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                int patientId = rs.getInt("patient_id");
                int doctorId = rs.getInt("doctor_id");
                String requestedDate = rs.getString("requested_date");
                String requestedTime = rs.getString("requested_time");
                String status = rs.getString("status");
                requests.add(new AppointmentRequest(id, patientId, doctorId, requestedDate, requestedTime, status));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }

    private boolean approveAppointmentRequest(int requestId) {
        String selectQuery = "SELECT ar.*, p.email, p.name FROM appointment_requests ar JOIN patients p ON ar.patient_id = p.id WHERE ar.id = ?";
        String insertQuery = "INSERT INTO appointments (patient_id, doctor_id, date, time, `status`) VALUES (?, ?, ?, ?, 'Scheduled')";
        String updateQuery = "UPDATE appointment_requests SET `status` = 'Approved' WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int patientId = rs.getInt("patient_id");
                int doctorId = rs.getInt("doctor_id");
                String requestedDate = rs.getString("requested_date");
                String requestedTime = rs.getString("requested_time");
                String email = rs.getString("email");
                String patientName = rs.getString("name");

                // Insert into appointments table
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, patientId);
                    insertStmt.setInt(2, doctorId);
                    insertStmt.setString(3, requestedDate);
                    insertStmt.setString(4, requestedTime);

                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        // Update request status
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, requestId);
                            int updateRows = updateStmt.executeUpdate();

                            if (updateRows > 0) {
                                // Check if the email exists before sending
                                if (email != null && !email.trim().isEmpty()) {
                                    // Send email
                                    String subject = "Appointment Approved";
                                    String body = "Dear " + patientName + ",\n\nYour appointment has been approved.\n\nDate: " +
                                            requestedDate + "\nTime: " + requestedTime + "\n\nThank you.";
                                    EmailSender.sendEmail(email, patientName, body);
                                } else {
                                    System.out.println("No email provided for patient: " + patientName + " (ID: " + patientId + ")");
                                    // You can log this or notify the admin in some way
                                }

                                return true;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean rejectAppointmentRequest(int requestId) {
        String selectQuery = "SELECT ar.id, p.email, p.name FROM appointment_requests ar JOIN patients p ON ar.patient_id = p.id WHERE ar.id = ?";
        String updateQuery = "UPDATE appointment_requests SET `status` = 'Rejected' WHERE id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            stmt.setInt(1, requestId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String email = rs.getString("email");
                String name = rs.getString("name");

                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setInt(1, requestId);
                    int updateRows = updateStmt.executeUpdate();

                    if (updateRows > 0) {
                        String messageBody = "We regret to inform you that your appointment request has been rejected.\n\nPlease try again or contact support.";
                        EmailSender.sendEmail(email, name, messageBody);
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
