import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class AppointmentsManager extends VBox {

    private AppointmentViewerAdmin databaseHelper;

    public AppointmentsManager() {
        databaseHelper = new AppointmentViewerAdmin();  // Initialize the database helper
        setupUI();
    }

    private void setupUI() {
        this.setSpacing(20);
        this.setPadding(new Insets(20));
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #f5f6fa;");

        Label title = new Label("Appointment Management");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + UIUtils.PRIMARY_COLOR + ";");

        // Create appointment management buttons
        Button[] appointmentButtons = {
                UIUtils.createHoverButton("View Scheduled Appointments", "✅", UIUtils.PRIMARY_COLOR),
                UIUtils.createHoverButton("View Requested Appointments", "📅", UIUtils.SECONDARY_COLOR),
                UIUtils.createHoverButton("Exit Appointment Manager", "🚪", UIUtils.ACCENT_COLOR)
        };

        // Arrange buttons in a grid
        GridPane buttonGrid = new GridPane();
        buttonGrid.setAlignment(Pos.CENTER);
        buttonGrid.setHgap(20);
        buttonGrid.setVgap(20);
        buttonGrid.setPadding(new Insets(20));

        // Add buttons to grid
        buttonGrid.add(appointmentButtons[0], 0, 0, 2, 1);
        buttonGrid.add(appointmentButtons[1], 0, 1, 2, 1);
        buttonGrid.add(appointmentButtons[2], 0, 2, 2, 1);

        // Set button actions (unchanged)
        appointmentButtons[0].setOnAction(e -> showScheduledAppointments());
        appointmentButtons[1].setOnAction(e -> {
            HandleAppointment handleAppointment = new HandleAppointment();
            handleAppointment.showAdminDashboard();
        });
        appointmentButtons[2].setOnAction(e -> {
            // Since we're in a pane now, no `this.close()` — you can add a back button instead if needed
            this.setVisible(false); // Or remove it from the content area in the dashboard
        });

        this.getChildren().addAll(title, buttonGrid);
    }
    private void showScheduledAppointments() {
        // Fetch all appointments from the database
        ObservableList<AppointmentViewerAdmin.Appointment> appointments = databaseHelper.getScheduledAppointments();

        // Filter out only scheduled appointments
        ObservableList<AppointmentViewerAdmin.Appointment> scheduledAppointments = filterScheduledAppointments(appointments);

        // If no appointments were found, display a message
        if (scheduledAppointments.isEmpty()) {
            System.out.println("No scheduled appointments found.");
            return;
        }

        // Convert the scheduled appointments into a list of strings for display
        ObservableList<String> appointmentStrings = FXCollections.observableArrayList();
        for (AppointmentViewerAdmin.Appointment appointment : scheduledAppointments) {
            String details = appointment.getDoctorName() + " - " + appointment.getPatientName() + " - " +
                    appointment.getDate() + " " + appointment.getTime() + " - Status: " + appointment.getStatus();
            appointmentStrings.add(details);
        }

        VBox appointmentsLayout = new VBox(10);
        appointmentsLayout.setPadding(new Insets(20));

        Label appointmentsTitle = new Label("Scheduled Appointments");
        appointmentsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<String> appointmentsList = new ListView<>(appointmentStrings);
        appointmentsLayout.getChildren().addAll(appointmentsTitle, appointmentsList);

        // Show in a new window (same as before)
        javafx.stage.Stage appointmentsStage = new javafx.stage.Stage();
        appointmentsStage.setScene(new javafx.scene.Scene(appointmentsLayout, 600, 400));
        appointmentsStage.setTitle("Scheduled Appointments");
        appointmentsStage.show();
    }

    private ObservableList<AppointmentViewerAdmin.Appointment> filterScheduledAppointments(ObservableList<AppointmentViewerAdmin.Appointment> appointments) {
        ObservableList<AppointmentViewerAdmin.Appointment> scheduledAppointments = FXCollections.observableArrayList();
        for (AppointmentViewerAdmin.Appointment appointment : appointments) {
            // Check for 'Scheduled' status
            if (appointment.getStatus().equals("Scheduled")) {
                scheduledAppointments.add(appointment);
            }
        }
        return scheduledAppointments;
    }
}
