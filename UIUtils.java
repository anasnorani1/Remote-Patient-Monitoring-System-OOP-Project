// UIUtils.java
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.util.Arrays;
import java.util.List;

public class UIUtils {
    public static final String PRIMARY_COLOR = "#2c3e50";
    public static final String SECONDARY_COLOR = "#3498db";
    public static final String ACCENT_COLOR = "#e74c3c";

    public static Button createHoverButton(String text, String icon, String color) {
        Button btn = new Button(icon + "  " + text);
        btn.setStyle("-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-padding: 15px 30px;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2);");
        btn.setMaxWidth(Double.MAX_VALUE);

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
    // UIUtils.java
    public static void showAlert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("System Message");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static String showPlatformChoiceDialog() {
        List<String> platforms = Arrays.asList("Zoom", "Google Meet");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("Zoom", platforms);
        dialog.setTitle("Select Platform");
        dialog.setHeaderText("Choose the platform for the video call");
        dialog.setContentText("Select platform:");

        return dialog.showAndWait().orElse(null); // Returns selected platform or null if canceled
    }
}