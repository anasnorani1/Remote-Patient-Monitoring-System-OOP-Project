import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.sql.*;

public class AppointmentViewerAdmin extends Application {

    private static final String URL = "jdbc:mysql://localhost:3306/healthcare_system";  // Database URL
    private static final String USER = "root";  // Database username
    private static final String PASSWORD = "Anasnorani@107";  // Database password

    private Connection connection;

    // TableView for displaying the appointments
    private TableView<Appointment> tableView;

    public AppointmentViewerAdmin() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to connect to the database");
        }
    }

    public ObservableList<Appointment> getScheduledAppointments() {
        ObservableList<Appointment> appointments = FXCollections.observableArrayList();

        try {
            String sql = "SELECT a.date, a.time, a.status, d.name AS doctor_name, p.name AS patient_name " +
                    "FROM appointments a " +
                    "JOIN doctors d ON a.doctor_id = d.doctor_id " +
                    "JOIN patients p ON a.patient_id = p.id " +  // Fixed the JOIN condition for patient
                    "WHERE a.status = 'Scheduled' " +  // Only get appointments with status 'Scheduled'
                    "ORDER BY a.date, a.time";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String doctorName = resultSet.getString("doctor_name");
                String patientName = resultSet.getString("patient_name");
                Date date = resultSet.getDate("date");
                String time = resultSet.getString("time");
                String status = resultSet.getString("status");

                appointments.add(new Appointment(doctorName, patientName, date, time, status));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error fetching appointments");
        }

        return appointments;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create TableView and define columns
        tableView = new TableView<>();
        TableColumn<Appointment, String> doctorNameCol = new TableColumn<>("Doctor Name");
        TableColumn<Appointment, String> patientNameCol = new TableColumn<>("Patient Name");
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");

        // Define cell value factories (how to extract data for each column)
        doctorNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDoctorName()));
        patientNameCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPatientName()));
        dateCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().toString()));
        timeCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
        statusCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Add columns to TableView
        tableView.getColumns().addAll(doctorNameCol, patientNameCol, dateCol, timeCol, statusCol);

        // Populate the TableView with data
        tableView.setItems(getScheduledAppointments());

        // Layout and Scene setup
        VBox vbox = new VBox(tableView);
        Scene scene = new Scene(vbox, 800, 600);

        // Setup the stage
        primaryStage.setTitle("Scheduled Appointments");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Appointment class to hold appointment data
    public static class Appointment {
        private final SimpleStringProperty doctorName;
        private final SimpleStringProperty patientName;
        private final SimpleObjectProperty<Date> date;
        private final SimpleStringProperty time;
        private final SimpleStringProperty status;

        public Appointment(String doctorName, String patientName, Date date, String time, String status) {
            this.doctorName = new SimpleStringProperty(doctorName);
            this.patientName = new SimpleStringProperty(patientName);
            this.date = new SimpleObjectProperty<>(date);
            this.time = new SimpleStringProperty(time);
            this.status = new SimpleStringProperty(status);
        }

        public String getDoctorName() {
            return doctorName.get();
        }

        public String getPatientName() {
            return patientName.get();
        }

        public Date getDate() {
            return date.get();
        }

        public String getTime() {
            return time.get();
        }

        public String getStatus() {
            return status.get();
        }
    }
}
