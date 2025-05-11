import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class PatientMenu {
    private int patientId;
    private StackPane contentArea;

    public void show(Stage primaryStage, int patientId) {
        this.patientId = patientId;

        // Sidebar (menu)
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2A3F54;");
        sidebar.setPrefWidth(220);
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Patient Menu");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        sidebar.getChildren().add(title);

        // Menu buttons
        String[] menuLabels = {
                "Upload Vital Signs", "View Doctor Feedback", "Schedule Appointment",
                "View My Appointments", "Emergency Panic Button", "Send Message to Doctor",
                "View Chat History", "Change Password", "Generate Report", "Exit"
        };

        for (String label : menuLabels) {
            Button btn = createMenuButton(label);
            sidebar.getChildren().add(btn);
        }

        // Main content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: #ffffff;");
        contentArea.setPadding(new Insets(30));

        // Let content area grow to take all remaining space
        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Combine sidebar and content area
        HBox mainLayout = new HBox(sidebar, contentArea);

        Scene scene = new Scene(mainLayout, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Patient Menu");
        primaryStage.show();
    }

    private Button createMenuButton(String label) {
        Button btn = new Button(label);
        btn.setPrefWidth(180);
        btn.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-background-color: #3E8ACC;" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-padding: 10px 15px;"
        );
        btn.setEffect(new DropShadow(2, Color.gray(0.5)));

        btn.setOnAction(e -> handleMenuSelection(label));
        return btn;
    }

    private void handleMenuSelection(String label) {
        contentArea.getChildren().clear();

        Label placeholder = new Label("You selected: " + label);
        placeholder.setStyle("-fx-font-size: 18px; -fx-text-fill: #333333;");
        contentArea.getChildren().add(placeholder);

        switch (label) {
            case "Upload Vital Signs":
                VitalSigns vitalSigns = new VitalSigns();
                vitalSigns.showVitalSignInput(contentArea, patientId);
                break;

            case "View Doctor Feedback":
                DoctorFeedbackViewer feedbackViewer = new DoctorFeedbackViewer();
                VBox feedbackView = feedbackViewer.createFeedbackView(patientId);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(feedbackView);

                break;
            case "Schedule Appointment":
                ScheduleAppointment scheduler = new ScheduleAppointment();
                VBox appointmentView = scheduler.createAppointmentView(patientId);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(appointmentView);
                break;
            case "View My Appointments":
                AppointmentViewer viewer = new AppointmentViewer();
                VBox appointmentViewer = viewer.createAppointmentsView(patientId);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(appointmentViewer);
                break;
            case "Emergency Panic Button":
                VBox emergencyAlertForm = EmergencyAlertHandler.createEmergencyAlertForm(patientId);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(emergencyAlertForm);

                break;
            case "Send Message to Doctor":
                // Make sure the contentArea is passed as the mainLayout for MessageDoctorForm
                MessageDoctorForm.showMessageForm(contentArea, patientId);
                break;

            case "View Chat History":
                ChatHistoryViewer.showChatHistory(contentArea, patientId);
                break;
            case "Change Password":
                ChangePatientPassword.show(contentArea, patientId);
                break;
            case "Generate Report":
                PrescriptionReport reportGenerator = new PrescriptionReport();
                VBox reportView = reportGenerator.createReportView(patientId);
                contentArea.getChildren().clear();
                contentArea.getChildren().add(reportView);

                break;
            case "Exit":
                Stage currentStage = (Stage) contentArea.getScene().getWindow();
                currentStage.close(); // Optional if you want to close first

                Stage loginStage = new Stage();
                new LoginPage().start(loginStage); // Ensure Login has a start(Stage) method
                break;
            default:
                UIUtils.showAlert("Unknown Option Selected!");
        }
    }

    public StackPane getContentArea() {
        return contentArea;
    }

    public Stage getStage() {
        return (Stage) contentArea.getScene().getWindow();
    }
}
