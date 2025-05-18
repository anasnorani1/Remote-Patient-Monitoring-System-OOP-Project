import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.*;
import java.time.LocalDate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ScheduleAppointment {

    public VBox createAppointmentView(int patientId) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("Schedule Appointment");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2a2a2a;");

        ComboBox<DoctorItem> doctorBox = new ComboBox<>();
        DatePicker datePicker = new DatePicker();
        ComboBox<String> timeBox = new ComboBox<>();
        Button submit = new Button("Request Appointment");

        doctorBox.getItems().addAll(getDoctors());

        doctorBox.setOnAction(e -> {
            timeBox.getItems().setAll(getAvailableTimes());
        });

        submit.setOnAction(e -> {
            DoctorItem doctor = doctorBox.getValue();
            LocalDate date = datePicker.getValue();
            String time = timeBox.getValue();

            if (doctor == null || date == null || time == null) {
                showAlert("Please fill all fields.");
            } else {
                boolean success = requestAppointment(patientId, doctor.id, date.toString(), time);
                if (success) {
                    showAlert("Appointment request sent.");
                    // Optionally clear fields
                    doctorBox.setValue(null);
                    doctorBox.setPromptText("Select Doctor");
                    datePicker.setValue(null);
                    datePicker.setPromptText("12-12-2025");
                    timeBox.setValue(null);
                    timeBox.setPromptText("time");
                } else {
                    showAlert("Failed to send the appointment request.");
                }
            }
        });

        layout.getChildren().addAll(
                title,
                new Label("Select Doctor:"), doctorBox,

                new Label("Select Date:"), datePicker,
                new Label("Select Time:"), timeBox,
                submit
        );

        return layout;
    }


    private List<DoctorItem> getDoctors() {
        List<DoctorItem> list = new ArrayList<>();
        String query = "SELECT doctor_id, name FROM doctors";

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int doctorId = rs.getInt("doctor_id");
                String doctorName = rs.getString("name");
                list.add(new DoctorItem(doctorId, doctorName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error fetching doctors from database.");
        }
        return list;
    }

    private List<String> getAvailableTimes() {
        return Arrays.asList("09:00 AM", "10:00 AM", "11:00 AM", "02:00 PM", "03:00 PM");
    }

    private boolean requestAppointment(int patientId, int doctorId, String date, String time) {
        String convertedTime = convertTo24HourFormat(time);
        String query = "INSERT INTO appointment_requests (patient_id, doctor_id, requested_date, requested_time) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, doctorId);
            stmt.setString(3, date);
            stmt.setString(4, convertedTime);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error scheduling appointment.");
            return false;
        }
    }

    private String convertTo24HourFormat(String time12Hour) {
        try {
            SimpleDateFormat sdf12 = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat sdf24 = new SimpleDateFormat("HH:mm:ss");
            Date date = sdf12.parse(time12Hour);
            return sdf24.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Invalid time format.");
            return null;
        }
    }

    private static class DoctorItem {
        int id;
        String name;

        DoctorItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
