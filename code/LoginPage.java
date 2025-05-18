import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.util.Optional;
public class
LoginPage extends Application {

    private static final String PRIMARY_COLOR = "#2c3e50";
    private static final String SECONDARY_COLOR = "#3498db";
    private static final String ACCENT_COLOR = "#e74c3c";
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    // Only ONE start() method remains
    @Override
    public void start(Stage primaryStage) {
        // Test database connection first
        try (Connection conn = DatabaseConnector.getConnection()) {
            System.out.println("✅ Database connection successful!");
            initializeGUI(primaryStage); // GUI setup after successful connection
        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            showAlert("Database Connection Failed",
                    "Cannot connect to database:\n" + e.getMessage());
            Platform.exit();
        }
    }

    // All GUI code moved here from the duplicate start() method
    private void initializeGUI(Stage primaryStage) {
        primaryStage.setTitle("Remote Patient Monitoring System");
        primaryStage.setWidth(1700);
        primaryStage.setHeight(900);

        // Hospital background image
        String bgPath = "D:/Data science/programming/oop project/FRONTEND/bg.jpg";
        Image backgroundImage = new Image(new File(bgPath).toURI().toString());
        ImageView backgroundView = new ImageView(backgroundImage);
        backgroundView.setOpacity(0.3);
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundView.fitHeightProperty().bind(primaryStage.heightProperty());

        // Overlay
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2);");

        // Main layout
        HBox mainLayout = new HBox(40);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));

        // Create panels
        VBox loginPanel = createLoginPanel(primaryStage);
        StackPane logoPanel = createLogoPanel();

        mainLayout.getChildren().addAll(loginPanel, logoPanel);

        // Root layout
        StackPane root = new StackPane(backgroundView, overlay, mainLayout);

        // Scene setup
        Scene scene = new Scene(root);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), mainLayout);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        primaryStage.setScene(scene);
        primaryStage.show(); // Moved here from original duplicate
        fadeIn.play();
    }
    // Original createLoginPanel with critical fix
    private VBox createLoginPanel(Stage primaryStage) {
        VBox panel = new VBox(25);
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(40, 50, 40, 50));
        panel.setMaxWidth(600);

        // Header (unchanged)
        VBox headerBox = new VBox(10);
        headerBox.setAlignment(Pos.CENTER);

        Label mainTitle = new Label("REMOTE PATIENT MONITORING");
        mainTitle.setStyle(
                "-fx-text-fill: " + PRIMARY_COLOR + ";" +
                        "-fx-font-size: 28px;" +
                        "-fx-font-weight: 800;" +
                        "-fx-alignment: center;"
        );

        Label rpmsLabel = new Label("SYSTEM (RPMS)");
        rpmsLabel.setStyle(
                "-fx-text-fill: " + PRIMARY_COLOR + ";" +
                        "-fx-font-size: 24px;" +
                        "-fx-font-weight: 700;" +
                        "-fx-alignment: center;"
        );

        headerBox.getChildren().addAll(mainTitle, rpmsLabel);

        // Buttons (unchanged with critical action fix)
        Button[] buttons = {
                createHoverButton(" Patient Login", "👤", PRIMARY_COLOR),
                createHoverButton(" Doctor Login", "👨", PRIMARY_COLOR),
                createHoverButton(" Admin Login", "🔒", PRIMARY_COLOR),
                createHoverButton(" Exit", "🚪", ACCENT_COLOR)
        };
        buttons[0].setOnAction(e -> {
            PatientDashboard patientDashboard = new PatientDashboard();
            patientDashboard.show(primaryStage);

        });

        buttons[1].setOnAction(e -> {
            DoctorDashboard doctorDashboard = new DoctorDashboard();
            doctorDashboard.show(primaryStage);
        });

        buttons[2].setOnAction(e -> {
            AdminLoginPopup adminLoginPopup = new AdminLoginPopup(); // ✅ Now matches the corrected class name
            adminLoginPopup.show(primaryStage); // ✅ Works because AdminLoginPopup implements show(Stage)
        });


        // Exit functionality (unchanged)
        buttons[3].setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to exit?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Platform.exit();
            }
        });

        panel.getChildren().addAll(headerBox);
        panel.getChildren().addAll(buttons);
        return panel;
    }

    // Original createHoverButton (unchanged)
    private Button createHoverButton(String text, String icon, String color) {
        Button btn = new Button(icon + "  " + text);
        btn.setStyle("-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-padding: 15px 30px;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        btn.setMaxWidth(Double.MAX_VALUE);

        // Hover effect (unchanged)
        btn.setOnMouseEntered(e -> {
            btn.setStyle(btn.getStyle().replace(color, "derive(" + color + ", -20%)"));
            btn.setEffect(new DropShadow(20, Color.web(color + "80")));
        });

        btn.setOnMouseExited(e -> {
            btn.setStyle(btn.getStyle().replace("derive(" + color + ", -20%)", color));
            btn.setEffect(new DropShadow(10, Color.web(color + "30")));
        });

        return btn;
    }

    // Original createLogoPanel (unchanged)
    private StackPane createLogoPanel() {
        try {
            String imagePath = "D:/Data science/programming/oop project/FRONTEND/logo.png";
            File file = new File(imagePath);

            if (!file.exists()) {
                throw new IllegalArgumentException("Logo not found: " + file.getAbsolutePath());
            }

            Image logoImage = new Image(file.toURI().toString());
            ImageView logoView = new ImageView(logoImage);
            logoView.setFitWidth(500);
            logoView.setPreserveRatio(true);
            logoView.setEffect(new DropShadow(20, Color.web(PRIMARY_COLOR + "40")));

            // Animation (unchanged)
            FadeTransition floatAnim = new FadeTransition(Duration.seconds(3), logoView);
            floatAnim.setFromValue(0.95);
            floatAnim.setToValue(1.0);
            floatAnim.setCycleCount(FadeTransition.INDEFINITE);
            floatAnim.setAutoReverse(true);
            floatAnim.play();

            return new StackPane(logoView);
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
            return new StackPane(new Label("Logo Missing"));
        }
    }

    // Only critical fix added: launch() call
    public static void main(String[] args) {

        launch(args); // Essential fix for JavaFX startup
    }
}