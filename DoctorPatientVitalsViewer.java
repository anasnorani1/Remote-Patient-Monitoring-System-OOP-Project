import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.chart.*;
import java.sql.*;

public class DoctorPatientVitalsViewer {

    private int doctorId;
    private StackPane contentArea;
    private ComboBox<String> patientDropdown;
    private TableView<VitalSign> table;
    private VBox layout;

    public DoctorPatientVitalsViewer(int doctorId, StackPane contentArea) {
        this.doctorId = doctorId;
        this.contentArea = contentArea;
        setupUI();
    }

    private void setupUI() {
        layout = new VBox(20);

        // Dropdown to select patient
        patientDropdown = new ComboBox<>();
        patientDropdown.setPromptText("Select a patient");
        loadPatientsFromDatabase();

        patientDropdown.setOnAction(event -> {
            String selectedPatient = patientDropdown.getValue();
            if (selectedPatient != null && !selectedPatient.isEmpty()) {
                loadVitalsForSelectedPatient(selectedPatient);
            }
        });

        // Vitals Table
        table = new TableView<>();
        table.setPrefHeight(200);
        setupVitalsTableColumns();

        layout.getChildren().addAll(new Label("Vitals Viewer"), patientDropdown, table);
        contentArea.getChildren().clear();
        contentArea.getChildren().add(layout);
    }

    private void setupVitalsTableColumns() {
        TableColumn<VitalSign, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<VitalSign, String> heartCol = new TableColumn<>("Heart Rate");
        heartCol.setCellValueFactory(new PropertyValueFactory<>("heartRate"));

        TableColumn<VitalSign, String> oxygenCol = new TableColumn<>("Oxygen Level");
        oxygenCol.setCellValueFactory(new PropertyValueFactory<>("oxygenLevel"));

        TableColumn<VitalSign, String> tempCol = new TableColumn<>("Temperature");
        tempCol.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        TableColumn<VitalSign, String> bpCol = new TableColumn<>("Blood Pressure");
        bpCol.setCellValueFactory(new PropertyValueFactory<>("bloodPressure"));

        table.getColumns().addAll(dateCol, heartCol, oxygenCol, tempCol, bpCol);
    }

    private void loadPatientsFromDatabase() {
        String query = "SELECT DISTINCT patient_name FROM vital_signs WHERE doctor_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                patientDropdown.getItems().add(rs.getString("patient_name"));
            }
        } catch (SQLException e) {
            showAlert("Error loading patients: " + e.getMessage());
        }
    }

    private void loadVitalsForSelectedPatient(String patientName) {
        table.getItems().clear();
        layout.getChildren().removeIf(node -> node instanceof LineChart); // Remove old chart

        XYChart.Series<String, Number> heartSeries = new XYChart.Series<>();
        heartSeries.setName("Heart Rate");

        XYChart.Series<String, Number> oxygenSeries = new XYChart.Series<>();
        oxygenSeries.setName("Oxygen Level");

        XYChart.Series<String, Number> tempSeries = new XYChart.Series<>();
        tempSeries.setName("Temperature");

        XYChart.Series<String, Number> bpSysSeries = new XYChart.Series<>();
        bpSysSeries.setName("Systolic BP");

        XYChart.Series<String, Number> bpDiaSeries = new XYChart.Series<>();
        bpDiaSeries.setName("Diastolic BP");

        String query = "SELECT heart_rate, oxygen_level, temperature, blood_pressure, timestamp " +
                "FROM vital_signs WHERE doctor_id = ? AND patient_name = ? ORDER BY timestamp ASC";

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, doctorId);
            stmt.setString(2, patientName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String date = rs.getString("timestamp").split(" ")[0];
                String heart = rs.getString("heart_rate");
                String oxygen = rs.getString("oxygen_level");
                String temp = rs.getString("temperature");
                String bp = rs.getString("blood_pressure");

                table.getItems().add(new VitalSign(date, heart, oxygen, temp, bp));

                // Add data to chart
                try {
                    int heartVal = Integer.parseInt(heart);
                    int oxygenVal = Integer.parseInt(oxygen);
                    double tempVal = Double.parseDouble(temp);
                    String[] bpParts = bp.split("/");
                    int sys = Integer.parseInt(bpParts[0]);
                    int dia = Integer.parseInt(bpParts[1]);

                    heartSeries.getData().add(new XYChart.Data<>(date, heartVal));
                    oxygenSeries.getData().add(new XYChart.Data<>(date, oxygenVal));
                    tempSeries.getData().add(new XYChart.Data<>(date, tempVal));
                    bpSysSeries.getData().add(new XYChart.Data<>(date, sys));
                    bpDiaSeries.getData().add(new XYChart.Data<>(date, dia));
                } catch (Exception e) {
                    // Handle parsing issues
                    System.out.println("Skipping invalid vital data for chart");
                }
            }
        } catch (SQLException e) {
            showAlert("Error loading vitals: " + e.getMessage());
        }

        LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
        chart.setTitle("Vital Sign Trends");
        chart.getData().addAll(heartSeries, oxygenSeries, tempSeries, bpSysSeries, bpDiaSeries);

        layout.getChildren().add(chart);
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
