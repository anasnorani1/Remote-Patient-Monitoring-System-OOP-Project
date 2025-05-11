import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.*;
import java.time.LocalDate;
import org.mindrot.jbcrypt.BCrypt;
import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;
import java.util.Random;

public class AddPatientForm {
    private Connection connection;
    private VBox mainLayout;

    public AddPatientForm(Connection connection) {
        this.connection = connection;
        setupUI();
    }

    public Pane getForm() {
        return mainLayout;
    }

    private void setupUI() {
        // Create a VBox layout for the form
        mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER); // Center everything vertically and horizontally
        mainLayout.setMaxWidth(400); // Limit max width for better alignment
        mainLayout.setStyle("-fx-background-color: #e3f2fd;");

        // Title label
        Label titleLabel = new Label("Add New Patient");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        // Create the input fields
        TextField nameField = createStyledTextField("Enter patient's full name");
        TextField cnicField = createStyledTextField("Enter CNIC (#####-#######-#)");
        DatePicker dobDatePicker = new DatePicker();
        dobDatePicker.setPromptText("Select Date of Birth");
        dobDatePicker.setStyle("-fx-font-size: 12px; -fx-pref-width: 250px;");
        TextField ageField = createStyledTextField("Enter patient's age");

        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setValue("Male");
        genderBox.setStyle("-fx-font-size: 12px; -fx-pref-width: 250px;");

        TextField illnessField = createStyledTextField("Enter illness/problem");
        TextField contactField = createStyledTextField("Enter contact number");
        TextField emailField = createStyledTextField("Enter email address");
        TextArea addressField = createStyledTextArea("Enter address");

        // Save button
        Button saveButton = new Button("Save Patient");
        saveButton.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 10;");
        saveButton.setOnAction(e -> {
            try {
                savePatient(nameField.getText(), cnicField.getText(), dobDatePicker.getValue(), ageField.getText(),
                        genderBox.getValue(), illnessField.getText(), contactField.getText(), addressField.getText(),
                        emailField.getText());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Add all elements to the layout
        mainLayout.getChildren().addAll(
                titleLabel,
                createLabeledField("Name:", nameField),
                createLabeledField("CNIC:", cnicField),
                createLabeledField("Date of Birth:", dobDatePicker),
                createLabeledField("Age:", ageField),
                createLabeledField("Gender:", genderBox),
                createLabeledField("Illness:", illnessField),
                createLabeledField("Contact:", contactField),
                createLabeledField("Email:", emailField),
                createLabeledField("Address:", addressField),
                saveButton
        );
    }

    // Method to create styled text fields
    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-font-size: 12px; -fx-pref-width: 250px;");
        return field;
    }

    // Method to create styled text area
    private TextArea createStyledTextArea(String prompt) {
        TextArea area = new TextArea();
        area.setPromptText(prompt);
        area.setStyle("-fx-font-size: 12px; -fx-pref-width: 250px; -fx-pref-height: 80px;");
        return area;
    }

    // Method to create labeled input field (align label and input field)
    private VBox createLabeledField(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");
        VBox box = new VBox(5, label, inputField);
        box.setAlignment(Pos.CENTER); // Center the label and field horizontally
        return box;
    }

    // Method to save patient data
    private void savePatient(String name, String cnic, LocalDate dob, String ageStr, String gender, String illness, String contact, String address, String email) throws SQLException {
        if (name.isEmpty() || cnic.isEmpty() || ageStr.isEmpty() || illness.isEmpty() || contact.isEmpty() || address.isEmpty() || email.isEmpty() || dob == null) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled!");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Age must be a number!");
            return;
        }

        if (!cnic.matches("\\d{5}-\\d{7}-\\d{1}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid CNIC format. Expected format: #####-#######-#");
            return;
        }

        if (isCnicAlreadyTaken(cnic)) {
            showAlert(Alert.AlertType.ERROR, "Patient with CNIC " + cnic + " is already registered.");
            return;
        }

        String username = cnic;
        String password = generatePassword();
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String sql = "INSERT INTO patients (name, cnic, date_of_birth, age, gender, illness, contact, address, username, password_hash, password, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, cnic);
            stmt.setDate(3, java.sql.Date.valueOf(dob));
            stmt.setInt(4, age);
            stmt.setString(5, gender);
            stmt.setString(6, illness);
            stmt.setString(7, contact);
            stmt.setString(8, address);
            stmt.setString(9, username);
            stmt.setString(10, hashedPassword);
            stmt.setString(11, password);
            stmt.setString(12, email);

            stmt.executeUpdate();

            logAction("Add New Patient", "Name: " + name + ", CNIC: " + cnic + ", Illness: " + illness);

            try {
                sendEmail(email, name, username, password);
            } catch (Exception e) {
                showAlert(Alert.AlertType.WARNING, "Patient saved, but failed to send email: " + e.getMessage());
                e.printStackTrace();
            }

            showAlert(Alert.AlertType.INFORMATION, "Patient saved successfully!\nUsername: " + username + "\nPassword: " + password);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error saving patient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isCnicAlreadyTaken(String cnic) throws SQLException {
        String query = "SELECT 1 FROM patients WHERE cnic = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, cnic);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private void logAction(String action, String details) throws SQLException {
        String sql = "INSERT INTO system_logs (action, timestamp, details) VALUES (?, NOW(), ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, action);
            stmt.setString(2, details);
            stmt.executeUpdate();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random rand = new Random();
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return password.toString();
    }

    private void sendEmail(String to, String name, String username, String password) throws MessagingException {
        String emailContent = "<html><body style='font-family: Arial;'>" +
                "<h2 style='color: #1976d2;'>Hello, " + name + "!</h2>" +
                "<p>Your account has been created with the following credentials:</p>" +
                "<p><strong>Username:</strong> " + username + "<br>" +
                "<strong>Password:</strong> " + password + "</p>" +
                "<p>Please keep this information secure.</p>" +
                "<p>Best regards,<br>RPMS Team</p></body></html>";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("remotepatientmonitoringsystem1@gmail.com", "drfwrwofmevlhgjr");
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("your-email@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("Your RPMS Account Credentials");
        message.setContent(emailContent, "text/html");

        Transport.send(message);
    }
}
