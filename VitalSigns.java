import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.layout.StackPane;

public class VitalSigns {

    public void showVitalSignInput(StackPane contentArea, int patientId) {
        // Root white pane
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: white; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label title = new Label("Upload Vital Signs");
        title.setFont(Font.font("Arial", 24));
        title.setStyle("-fx-font-weight: bold;");
        title.setPadding(new Insets(0, 0, 10, 0));

        Text instructions = new Text(
                "Upload a CSV file containing the patient's vital signs:\n\n" +
                        "Required columns: heartRate, oxygenLevel, temperature, bloodPressure\n" +
                        "Example row: 72,98,98.6,120/80"
        );
        instructions.setTextAlignment(TextAlignment.CENTER);
        instructions.setWrappingWidth(400);

        // Doctor Dropdown
        ComboBox<String> doctorDropdown = new ComboBox<>();
        doctorDropdown.setPrefWidth(300);
        List<String> doctorList = fetchDoctorsForPatient(patientId);
        if (doctorList.isEmpty()) {
            UIUtils.showAlert("No associated doctors found for this patient.");
            return;
        }
        doctorDropdown.getItems().addAll(doctorList);
        doctorDropdown.setPromptText("Select your doctor");

        // Buttons
        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(Pos.CENTER);

        Button downloadSampleCSVButton = new Button("Download Sample CSV");
        Button uploadFileButton = new Button("Upload CSV");

        downloadSampleCSVButton.setOnAction(e -> downloadSampleCSV());

        uploadFileButton.setOnAction(e -> {
            if (doctorDropdown.getValue() == null) {
                UIUtils.showAlert("Please select a doctor.");
                return;
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("CSV Files", "*.csv")
            );
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                parseFileAndStoreVitals(file, doctorDropdown.getValue(), patientId);
            }
        });

        buttonRow.getChildren().addAll(downloadSampleCSVButton, uploadFileButton);

        // Add to layout
        root.getChildren().addAll(title, instructions, doctorDropdown, buttonRow);

        // Set content area
        contentArea.getChildren().clear();
        contentArea.getChildren().add(root);
    }

    private List<String> fetchDoctorsForPatient(int patientId) {
        Set<String> uniqueDoctors = new LinkedHashSet<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthcare_system", "root", "Anasnorani@107");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT DISTINCT d.doctor_id, d.name " +
                             "FROM appointments a JOIN doctors d ON a.doctor_id = d.doctor_id " +
                             "WHERE a.patient_id = ?")) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                uniqueDoctors.add(rs.getString("name") + " (ID: " + rs.getInt("doctor_id") + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            UIUtils.showAlert("Database error: " + e.getMessage());
        }
        return new ArrayList<>(uniqueDoctors);
    }

    private void downloadSampleCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Sample CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        fileChooser.setInitialFileName("sample_vitals.csv");

        File saveFile = fileChooser.showSaveDialog(null);
        if (saveFile != null) {
            try (FileWriter writer = new FileWriter(saveFile)) {
                writer.write("heartRate,oxygenLevel,temperature,bloodPressure\n");
                writer.write("72,98,98.6,120/80\n");
                writer.write("75,99,98.5,120/80\n");
                UIUtils.showAlert("Sample CSV exported successfully to:\n" + saveFile.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
                UIUtils.showAlert("Error exporting sample CSV: " + e.getMessage());
            }
        }
    }

    private void parseFileAndStoreVitals(File file, String doctorInfo, int patientId) {
        String doctorId = extractDoctorId(doctorInfo);
        String doctorName = doctorInfo;
        String patientName = PatientLogin.getLoggedInPatientName();

        if (file.getName().endsWith(".csv")) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.trim().startsWith("heartRate")) continue;
                    String[] data = line.split(",");
                    if (data.length == 4) {
                        storeVitalsInDatabase(data[0], data[1], data[2], data[3], doctorId, doctorName, patientId, patientName);
                    }
                }
                UIUtils.showAlert("Vitals uploaded successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                UIUtils.showAlert("Error reading file: " + e.getMessage());
            }
        } else {
            UIUtils.showAlert("Excel support not yet implemented.");
        }
    }

    private String extractDoctorId(String selectedDoctor) {
        try {
            int idStart = selectedDoctor.lastIndexOf("(") + 1;
            int idEnd = selectedDoctor.indexOf(")", idStart);
            if (idStart != -1 && idEnd != -1) {
                return selectedDoctor.substring(idStart, idEnd).replace("ID:", "").trim();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void storeVitalsInDatabase(String heartRate, String oxygenLevel, String temperature, String bloodPressure,
                                       String doctorId, String doctorName, int patientId, String patientName) {
        String query = "INSERT INTO vital_signs (patient_id, heart_rate, oxygen_level, temperature, blood_pressure, doctor_id, patient_name, doctor_name, timestamp) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthcare_system", "root", "Anasnorani@107");
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, patientId);
            stmt.setInt(2, Integer.parseInt(heartRate));
            stmt.setInt(3, Integer.parseInt(oxygenLevel));
            stmt.setDouble(4, Double.parseDouble(temperature));
            stmt.setString(5, bloodPressure);
            stmt.setInt(6, Integer.parseInt(doctorId));
            stmt.setString(7, patientName);
            stmt.setString(8, doctorName);
            stmt.executeUpdate();
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            UIUtils.showAlert("Error storing vitals: " + e.getMessage());
        }
    }
}
