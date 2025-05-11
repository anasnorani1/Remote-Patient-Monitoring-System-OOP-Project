import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentViewer {

    public VBox createAppointmentsView(int patientId) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("Scheduled Appointments");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2a2a2a;");

        ListView<String> listView = new ListView<>();
        List<String> appointments = fetchAppointments(patientId);
        listView.getItems().addAll(appointments);

        layout.getChildren().addAll(title, listView);
        return layout;
    }

    private List<String> fetchAppointments(int patientId) {
        List<String> appointmentList = new ArrayList<>();

        String query = "SELECT a.date, a.time, d.name AS doctor_name " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                "WHERE a.patient_id = ? AND a.status = 'Scheduled' " +
                "ORDER BY a.date, a.time";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String date = rs.getString("date");
                String time = rs.getString("time");
                String doctorName = rs.getString("doctor_name");

                appointmentList.add("Doctor: " + doctorName + " | Date: " + date + " | Time: " + time);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return appointmentList;
    }
}
