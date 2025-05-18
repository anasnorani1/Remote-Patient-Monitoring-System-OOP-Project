import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionReport {

    public Button createReportButton(int patientId) {
        Button reportButton = new Button("📄 Generate Report");
        reportButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-padding: 8px 16px;");
        reportButton.setOnAction(e -> showAppointmentSelectionWindow(patientId));
        return reportButton;
    }

    public VBox createReportView(int patientId) {
        VBox reportLayout = new VBox(20);
        reportLayout.setPadding(new Insets(30));
        reportLayout.setStyle("-fx-background-color: #f9f9f9;");

        Label heading = new Label("📋 Prescription Report");
        heading.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        heading.setStyle("-fx-text-fill: #2c3e50;");

        Label patientInfo = new Label("Patient ID: " + patientId);
        patientInfo.setFont(Font.font("Arial", 14));
        patientInfo.setStyle("-fx-text-fill: #555;");

        Button generateReportButton = createReportButton(patientId);

        reportLayout.getChildren().addAll(heading, patientInfo, generateReportButton);
        return reportLayout;
    }

    public void showAppointmentSelectionWindow(int patientId) {
        Stage stage = new Stage();
        StackPane contentArea = new StackPane();
        VBox selectionView = createAppointmentSelectionView(patientId, contentArea);
        contentArea.getChildren().add(selectionView);

        Scene scene = new Scene(contentArea, 500, 400);
        stage.setTitle("Select Appointment");
        stage.setScene(scene);
        stage.show();
    }

    public VBox createAppointmentSelectionView(int patientId, StackPane contentArea) {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(25));
        layout.setStyle("-fx-background-color: white;");

        Label title = new Label("🗓️ Select Appointment to Generate Report");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        ComboBox<String> appointmentComboBox = new ComboBox<>();
        appointmentComboBox.setPromptText("Select Appointment");
        appointmentComboBox.setPrefWidth(300);

        List<Integer> appointmentIds = new ArrayList<>();

        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "SELECT id, date FROM appointments WHERE patient_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, patientId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String date = rs.getString("date");
                appointmentComboBox.getItems().add("Appointment ID: " + id + " - " + date);
                appointmentIds.add(id);
            }

            if (appointmentIds.isEmpty()) {
                layout.getChildren().add(new Label("❌ No appointments found."));
                return layout;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            layout.getChildren().add(new Label("❌ Error loading appointments."));
            return layout;
        }

        Button generateReportBtn = new Button("✔ Generate Report");
        generateReportBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8px 16px;");
        generateReportBtn.setOnAction(e -> {
            int selectedIndex = appointmentComboBox.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                int appointmentId = appointmentIds.get(selectedIndex);
                String reportText = generateReportForAppointment(patientId, appointmentId);
                showReportWindow(reportText);
            } else {
                UIUtils.showAlert("⚠ Please select an appointment.");
            }
        });

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(title, appointmentComboBox, generateReportBtn);
        return layout;
    }

    // Method to generate the report for the selected appointment
    public String generateReportForAppointment(int patientId, int appointmentId) {
        StringBuilder report = new StringBuilder();

        try (Connection conn = DatabaseConnector.getConnection()) {
            // Patient info section
            String patientQuery = """
                        SELECT name, gender, age, contact, email, address, cnic, date_of_birth 
                        FROM patients 
                        WHERE id = ?
                    """;
            try (PreparedStatement pst = conn.prepareStatement(patientQuery)) {
                pst.setInt(1, patientId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    report.append("-------------------------------------------------\n");
                    report.append("                Remote Patient Monitoring System\n");
                    report.append("                     Patient Report\n");
                    report.append("-------------------------------------------------\n\n");

                    report.append("Patient Name      : ").append(rs.getString("name")).append("\n");
                    report.append("Gender            : ").append(rs.getString("gender")).append("\n");
                    report.append("Age               : ").append(rs.getInt("age")).append("\n");
                    report.append("Contact           : ").append(rs.getString("contact")).append("\n");
                    report.append("Email             : ").append(rs.getString("email")).append("\n");
                    report.append("Address           : ").append(rs.getString("address")).append("\n");
                    report.append("CNIC              : ").append(rs.getString("cnic")).append("\n");
                    report.append("Date of Birth     : ").append(rs.getDate("date_of_birth")).append("\n\n");
                } else {
                    report.append("Patient not found.\n");
                    return report.toString();
                }
            }

            // Vital Signs section
            String vitalSignsQuery = """
                        SELECT heart_rate, oxygen_level, temperature, blood_pressure, doctor_name, timestamp 
                        FROM vital_signs 
                        WHERE patient_id = ? 
                        ORDER BY timestamp DESC 
                        LIMIT 1
                    """;
            try (PreparedStatement pst = conn.prepareStatement(vitalSignsQuery)) {
                pst.setInt(1, patientId);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    report.append("-------------------------------------------------\n");
                    report.append("               Vital Signs (Latest)\n");
                    report.append("-------------------------------------------------\n");
                    report.append("Heart Rate       : ").append(rs.getInt("heart_rate")).append(" bpm\n");
                    report.append("Oxygen Level     : ").append(rs.getInt("oxygen_level")).append(" %\n");
                    report.append("Temperature      : ").append(rs.getDouble("temperature")).append(" °C\n");
                    report.append("Blood Pressure   : ").append(rs.getString("blood_pressure")).append("\n");
                    report.append("Doctor           : ").append(rs.getString("doctor_name")).append("\n");
                    report.append("Timestamp        : ").append(rs.getTimestamp("timestamp")).append("\n\n");
                } else {
                    report.append("No vital signs recorded.\n\n");
                }
            }

            // Prescription section
            String prescriptionQuery = """
                        SELECT medicine_name, when_to_take, before_after, quantity, times_per_day, note
                        FROM prescriptions
                        WHERE patient_id = ? AND appointment_id = ?
                    """;
            try (PreparedStatement pst = conn.prepareStatement(prescriptionQuery)) {
                pst.setInt(1, patientId);
                pst.setInt(2, appointmentId);
                ResultSet rs = pst.executeQuery();

                int count = 1;
                if (!rs.isBeforeFirst()) {
                    report.append("No prescriptions found for this appointment.\n\n");
                } else {
                    report.append("-------------------------------------------------\n");
                    report.append("               Prescriptions\n");
                    report.append("-------------------------------------------------\n");
                    while (rs.next()) {
                        report.append("Prescription #").append(count++).append("\n");
                        report.append("Medicine         : ").append(rs.getString("medicine_name")).append("\n");
                        report.append("When to Take     : ").append(rs.getString("when_to_take")).append("\n");
                        report.append("Before/After     : ").append(rs.getString("before_after")).append("\n");
                        report.append("Quantity         : ").append(rs.getString("quantity")).append("\n");
                        report.append("Times per Day    : ").append(rs.getString("times_per_day")).append("\n");
                        report.append("Note             : ").append(rs.getString("note")).append("\n\n");
                    }
                }
            }

        } catch (SQLException e) {
            report.append("Error fetching data: ").append(e.getMessage());
        }

        // Add footer
        report.append("\n-------------------------------------------------\n");
        report.append("           End of Report\n");
        report.append("-------------------------------------------------\n");

        return report.toString();
    }

    public void showReportWindow(String reportText) {
        Stage stage = new Stage();
        stage.setTitle("📄 Patient Report");

        TextArea reportArea = new TextArea(reportText);
        reportArea.setFont(Font.font("Courier New", 13));
        reportArea.setEditable(false);
        reportArea.setWrapText(true);
        reportArea.setStyle("-fx-control-inner-background: #fefefe;");

        Button saveButton = new Button("💾 Save Report");
        saveButton.setStyle("-fx-font-size: 14px; -fx-padding: 8px 20px; -fx-background-color: #27ae60; -fx-text-fill: white;");
        saveButton.setOnAction(e -> saveReportToFile(reportText));

        VBox root = new VBox(155, reportArea, saveButton);
        root.setPadding(new Insets(25));

        Scene scene = new Scene(root, 700, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void saveReportToFile(String reportText) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        fileChooser.setInitialFileName("patient_report.txt");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(reportText);
                UIUtils.showAlert("✅ Report saved successfully!");
            } catch (IOException e) {
                UIUtils.showAlert("❌ Error saving report.");
            }
        }
    }
}