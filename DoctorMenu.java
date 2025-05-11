import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorMenu {

    private String doctorCnic;
    private String doctorName = "Dr. John Doe";
    private int doctorId;
    private String doctorEmail;

    private StackPane contentArea;
    public Scene getScene() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #2A3F54;");

        Label title = new Label("Doctor Menu");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label welcome = new Label("Welcome, " + doctorName);
        welcome.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");
        sidebar.getChildren().addAll(title, welcome);

        String[] menuItems = {
                "View Appointments", "View Patient Vitals", "Provide Patient Feedback", "Prescribe Medicine",
                "View Chat History", "Start Video Call", "Send Patient Reminder", "Change Password", "Exit"
        };

        for (String item : menuItems) {
            Button btn = createMenuButton(item);
            sidebar.getChildren().add(btn);
        }

        // Content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: white;");
        contentArea.setPadding(new Insets(30));

        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Layout
        HBox mainLayout = new HBox(sidebar, contentArea);

        return new Scene(mainLayout, 1000, 600); // Return the scene to be used
    }

    public DoctorMenu(String doctorCnic) {
        this.doctorCnic = doctorCnic;
        this.doctorName = getDoctorNameByCnic(doctorCnic);
        this.doctorEmail = getDoctorEmailByCnic(doctorCnic);
        this.doctorId = getDoctorIdByCnic(doctorCnic);
    }

    public void show(Stage primaryStage) {
        // Sidebar setup
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(220);
        sidebar.setAlignment(Pos.TOP_CENTER);
        sidebar.setStyle("-fx-background-color: #2A3F54;");

        Label title = new Label("Doctor Menu");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label welcome = new Label("Welcome, " + doctorName);
        welcome.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");
        sidebar.getChildren().addAll(title, welcome);

        String[] menuItems = {
                "View Appointments", "View Patient Vitals", "Provide Patient Feedback", "Prescribe Medicine",
                "View Chat History", "Start Video Call", "Send Patient Reminder", "Change Password", "Exit"
        };

        for (String item : menuItems) {
            Button btn = createMenuButton(item);
            sidebar.getChildren().add(btn);
        }

        // Content area
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: white;");
        contentArea.setPadding(new Insets(30));

        HBox.setHgrow(contentArea, Priority.ALWAYS);

        // Layout
        HBox mainLayout = new HBox(sidebar, contentArea);

        Scene scene = new Scene(mainLayout, 1000, 600);
        primaryStage.setTitle("Doctor Menu");
        primaryStage.setScene(scene); // Replace the current scene with the DoctorMenu
        primaryStage.show(); // Show the updated scene in the same window
    }

    private Button createMenuButton(String label) {
        Button btn = new Button(label);
        btn.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 10px; -fx-padding: 10px 15px;");
        btn.setPrefWidth(180);
        btn.setEffect(new DropShadow(2, Color.gray(0.3)));

        btn.setOnAction(e -> handleMenuAction(label));
        return btn;
    }

    private void handleMenuAction(String label) {
        contentArea.getChildren().clear(); // Clear current content

        switch (label) {
            case "View Appointments":
                DoctorAppointmentViewer appointmentViewer = new DoctorAppointmentViewer(doctorId);
                appointmentViewer.displayAppointments(contentArea);  // Add to contentArea instead of opening a new window
                break;


            case "View Patient Vitals":
                DoctorPatientVitalsViewer vitalsViewer = new DoctorPatientVitalsViewer(doctorId, contentArea);
                break;
            case "Provide Patient Feedback":
                ProvideFeedbackWindow feedbackWindow = new ProvideFeedbackWindow(doctorId, contentArea);
                break;

// In your handleMenuAction method, update the "Prescribe Medicine" case:
            case "Prescribe Medicine":
                // Create an instance of the PrescribeMedicineWindow
                PrescribeMedicineWindow prescribeWindow = new PrescribeMedicineWindow(doctorId);

                // Display the prescription window inside contentArea
                VBox prescriptionForm = prescribeWindow.createPrescriptionForm(doctorId);
                contentArea.getChildren().add(prescriptionForm); // Add the form to contentArea instead of opening a new window
                break;


            case "View Chat History":
                contentArea.getChildren().clear();  // Clear previous content
                VBox chatView = DoctorChatViewer.createChatHistoryView(doctorEmail);
                contentArea.getChildren().add(chatView);
                break;


            case "Start Video Call":
                contentArea.getChildren().clear();
                VBox meetingPane = MeetingSchedulerWindow.create(doctorId);
                contentArea.getChildren().add(meetingPane);
                break;

            case "Send Patient Reminder":
                contentArea.getChildren().clear(); // clear previous content

                SendReminderPane reminderPane = new SendReminderPane(doctorId); // pass the correct doctor ID
                contentArea.getChildren().add(reminderPane);
                break;

            case "Change Password":
                contentArea.getChildren().clear();
                DoctorChangePasswordPane changePasswordPane = new DoctorChangePasswordPane(doctorCnic);
                contentArea.getChildren().add(changePasswordPane);
                break;

            case "Exit":
                Stage currentStage = (Stage) contentArea.getScene().getWindow();
                currentStage.close(); // Optional if you want to close first

                Stage loginStage = new Stage();
                new LoginPage().start(loginStage); // Ensure Login has a start(Stage) method
                break;


            default:
                UIUtils.showAlert("Unknown Selection!");
        }
    }

    private String getDoctorNameByCnic(String cnic) {
        String query = "SELECT name FROM doctors WHERE cnic = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cnic);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return "Dr. John Doe";
    }

    private int getDoctorIdByCnic(String cnic) {
        String query = "SELECT doctor_id FROM doctors WHERE cnic = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cnic);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getInt("doctor_id");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private String getDoctorEmailByCnic(String cnic) {
        String query = "SELECT email FROM doctors WHERE cnic = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, cnic);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString("email");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private List<String> fetchAppointments() {
        // Example implementation to fetch appointments
        List<String> appointments = new ArrayList<>();
        return appointments;
    }
}
