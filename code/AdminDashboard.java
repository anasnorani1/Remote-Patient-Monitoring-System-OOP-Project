import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AdminDashboard {

    private StackPane contentArea;
    private Stage primaryStage; // Declare primaryStage as a member of the class

    // Constructor to initialize AdminDashboard with primaryStage
    public AdminDashboard(Stage primaryStage) {
        this.primaryStage = primaryStage; // Assign primaryStage
    }

    public void show() {
        // Sidebar (menu)
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: #2A3F54;"); // Same as Patient Menu
        sidebar.setPrefWidth(220);
        sidebar.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Admin Dashboard");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
        sidebar.getChildren().add(title);

        // Menu buttons (Admin-specific actions)
        String[] menuLabels = {
                "Add New Doctor", "Add New Patient", "View System Logs",
                "View Doctors", "View Patients", "Manage Appointments", "Send Email Notification", "Logout"
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
        primaryStage.setTitle("Admin Dashboard");
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

        btn.setOnAction(e -> handleMenuSelection(label)); // Remove 'primaryStage' from here
        return btn;
    }

    private void handleMenuSelection(String label) {
        contentArea.getChildren().clear();
        System.out.println("Menu selected: " + label);
        Label placeholder = new Label("You selected: " + label);
        placeholder.setStyle("-fx-font-size: 18px; -fx-text-fill: #333333;");
        contentArea.getChildren().add(placeholder);

        switch (label) {
            case "Add New Doctor":
                System.out.println("Add New Doctor selected");
                try {
                    AddDoctorForm addDoctorForm = new AddDoctorForm(DatabaseConnector.getConnection());
                    Pane formPane = addDoctorForm.getForm(); // Get the form as a pane
                    contentArea.getChildren().setAll(formPane); // Show it in the white area
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case "Add New Patient":
                System.out.println("Add New Patient selected");
                try {
                    AddPatientForm form = new AddPatientForm(DatabaseConnector.getConnection());
                    contentArea.getChildren().setAll(form.getForm());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case "View System Logs":
                System.out.println("View System Logs selected");
                try {
                    // Create a ViewLogsForm and get its layout
                    ViewLogsForm viewLogsForm = new ViewLogsForm(DatabaseConnector.getConnection());
                    contentArea.getChildren().setAll(viewLogsForm.getLayout()); // Set the ViewLogsForm's layout into the content area
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case "View Doctors":
                System.out.println("View Doctors selected");
                try {
                    ViewDoctorsForm viewDoctorsForm = new ViewDoctorsForm(DatabaseConnector.getConnection());
                    VBox layout = viewDoctorsForm.getLayout();  // Get the layout from ViewDoctorsForm
                    contentArea.getChildren().setAll(layout);  // Set it in the content area of AdminDashboard
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case "View Patients":
                System.out.println("View Patients selected");
                try {
                    ViewPatientsForm viewPatientsForm = new ViewPatientsForm(DatabaseConnector.getConnection());
                    VBox layout = viewPatientsForm.getLayout();  // Get the layout from ViewPatientsForm
                    contentArea.getChildren().setAll(layout);  // Set it in the content area of AdminDashboard
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            case "Manage Appointments":
                System.out.println("Manage Appointments selected");
                contentArea.getChildren().add(new AppointmentsManager());
                break;
            case "Send Email Notification":
                System.out.println("Send Email selected");
                contentArea.getChildren().clear(); // Optional: clears previous content
                contentArea.getChildren().add(new EmailForm());
                break;
            case "Logout":
                System.out.println("Logout selected");
                Stage currentStage = (Stage) contentArea.getScene().getWindow();
                currentStage.close(); // Close current window
                // Open Login Page
                Stage loginStage = new Stage();
                new LoginPage().start(loginStage); // Ensure LoginPage has start(Stage) method
                break;

            default:
                System.out.println("Unknown option selected!");
        }
    }
}
