import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;

import java.awt.Desktop;
import java.net.URI;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MeetingSchedulerWindow {

    public static VBox create(int doctorId) {
        ComboBox<String> patientCombo = new ComboBox<>();
        DatePicker datePicker = new DatePicker();

        Spinner<LocalTime> timeSpinner = new Spinner<>();
        timeSpinner.setValueFactory(new SpinnerValueFactory<LocalTime>() {
            {
                setConverter(new LocalTimeStringConverter(DateTimeFormatter.ofPattern("hh:mm a"), null));
                setValue(LocalTime.of(9, 0));
            }

            @Override
            public void decrement(int steps) {
                setValue(getValue().minusMinutes(steps * 30));
            }

            @Override
            public void increment(int steps) {
                setValue(getValue().plusMinutes(steps * 30));
            }
        });

        TextField linkField = new TextField();

        List<PatientAppointment> appointments = getAppointmentsByDoctorId(doctorId);
        for (PatientAppointment appt : appointments) {
            patientCombo.getItems().add(appt.name + " (" + appt.email + ")");
        }

        Button createMeetBtn = new Button("Create Meet Link");
        createMeetBtn.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://meet.google.com/landing"));
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("Failed to open browser.");
            }
        });

        Button sendBtn = new Button("Send Invite");
        sendBtn.setOnAction(e -> {
            if (patientCombo.getValue() == null || datePicker.getValue() == null ||
                    timeSpinner.getValue() == null || linkField.getText().isEmpty()) {
                showAlert("All fields are required!");
                return;
            }

            PatientAppointment selectedAppt = appointments.get(patientCombo.getSelectionModel().getSelectedIndex());
            String email = selectedAppt.email;

            String doctorName = getDoctorNameById(doctorId);
            String subjectPatient = "Online Appointment Scheduled with Dr. " + doctorName + " for " + selectedAppt.name;
            String subjectDoctor = "Online Appointment Scheduled with " + selectedAppt.name + " for Dr. " + doctorName;

            String body = "Dear " + selectedAppt.name + ",\n\n" +
                    "Your online appointment is scheduled as follows:\n" +
                    "Date: " + datePicker.getValue().toString() + "\n" +
                    "Time: " + timeSpinner.getValue().format(DateTimeFormatter.ofPattern("hh:mm a")) + "\n" +
                    "Google Meet Link: " + linkField.getText() + "\n\n" +
                    "Please be on time.\n\nBest regards,\nRemote Patient Monitoring System";

            EmailUtil.sendEmail(email, subjectPatient, body);

            String doctorEmail = getDoctorEmailById(doctorId);
            String doctorBody = "Dear Dr. " + doctorName + ",\n\n" +
                    "A new online appointment has been scheduled with the following details:\n\n" +
                    "Patient: " + selectedAppt.name + "\n" +
                    "Date: " + datePicker.getValue().toString() + "\n" +
                    "Time: " + timeSpinner.getValue().format(DateTimeFormatter.ofPattern("hh:mm a")) + "\n" +
                    "Google Meet Link: " + linkField.getText() + "\n\n" +
                    "Please be on time.\n\nBest regards,\nRemote Patient Monitoring System";

            EmailUtil.sendEmail(doctorEmail, subjectDoctor, doctorBody);

            Alert confirm = new Alert(Alert.AlertType.INFORMATION);
            confirm.setTitle("Invite Sent");
            confirm.setHeaderText("Meeting Invite Sent");
            confirm.setContentText("Meeting invite sent to: " + selectedAppt.name + " (" + email + ") and Dr. " + doctorName + " (" + doctorEmail + ")");
            confirm.showAndWait();
        });

        VBox layout = new VBox(10,
                new Label("Select Patient:"), patientCombo,
                new Label("Date:"), datePicker,
                new Label("Time:"), timeSpinner,
                new Label("Google Meet Link:"), linkField,
                createMeetBtn,
                sendBtn
        );
        layout.setPadding(new Insets(20));
        layout.setStyle("-fx-background-color: white;");

        return layout;
    }

    private static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private static List<PatientAppointment> getAppointmentsByDoctorId(int doctorId) {
        List<PatientAppointment> list = new ArrayList<>();
        String query = "SELECT DISTINCT p.name, p.email FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "WHERE a.doctor_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            // Use a set to avoid duplicates in case they slip through the SQL
            Set<String> seen = new HashSet<>();
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String key = name + "|" + email;
                if (seen.add(key)) {
                    list.add(new PatientAppointment(name, email));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return list;
    }

    private static String getDoctorNameById(int doctorId) {
        String name = "Doctor";
        String query = "SELECT name FROM doctors WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return name;
    }

    private static String getDoctorEmailById(int doctorId) {
        String email = "";
        String query = "SELECT email FROM doctors WHERE doctor_id = ?";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                email = rs.getString("email");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return email;
    }

    private static class PatientAppointment {
        String name, email;

        public PatientAppointment(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}
