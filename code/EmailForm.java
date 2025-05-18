import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class EmailForm extends VBox {

    public EmailForm() {
        this.setSpacing(20);
        this.setPadding(new Insets(40));
        this.setAlignment(Pos.TOP_CENTER);
        this.setStyle("-fx-background-color: #f5f6fa;");

        Label heading = new Label("📧 Send Email Notification");
        heading.setStyle("-fx-font-size: 28px; -fx-font-weight: bold;");

        Label emailLabel = new Label("Recipient Email:");
        TextField emailField = new TextField();
        emailField.setPromptText("example@gmail.com");
        emailField.setMaxWidth(300);

        Label messageLabel = new Label("Message:");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Write your message here...");
        messageArea.setWrapText(true);
        messageArea.setPrefRowCount(10);
        messageArea.setMaxWidth(300);

        Button sendBtn = new Button("Send Email");
        sendBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        sendBtn.setOnMouseEntered(e -> sendBtn.setStyle("-fx-background-color: #45A049; -fx-text-fill: white; -fx-font-weight: bold;"));
        sendBtn.setOnMouseExited(e -> sendBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;"));

        sendBtn.setOnAction(e -> {
            String to = emailField.getText().trim();
            String msg = messageArea.getText().trim();

            if (to.isEmpty() || msg.isEmpty()) {
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setHeaderText(null);
                warning.setContentText("Please fill in both fields before sending.");
                warning.showAndWait();
                return;
            }

            boolean success = EmailSender.sendEmail(to, "Admin Notification", msg);

            Alert alert = new Alert(success ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText(success ? "✅ Email sent successfully!" : "❌ Failed to send email.");
            alert.showAndWait();
        });

        this.getChildren().addAll(heading, emailLabel, emailField, messageLabel, messageArea, sendBtn);
    }
}
