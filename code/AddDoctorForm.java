import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.util.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class AddDoctorForm {
    private Connection connection;
    private Map<String, String[]> specializationToDepartmentMap;

    public AddDoctorForm(Connection connection) {
        this.connection = connection;
        setupSpecializationToDepartmentMap();
    }

    public VBox getForm() {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Add New Doctor");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #33691e;");

        TextField nameField = createStyledTextField("Enter doctor's full name");
        TextField cnicField = createStyledTextField("Enter CNIC (#####-#######-#)");
        DatePicker dobDatePicker = new DatePicker();
        dobDatePicker.setPromptText("Select Date of Birth");
        dobDatePicker.setStyle("-fx-font-size: 14px; -fx-pref-width: 280px;");
        TextField emailField = createStyledTextField("Enter doctor's email");
        TextField phoneField = createStyledTextField("Enter phone number");
        TextField addressField = createStyledTextField("Enter address");
        Control inputField = new TextField();//upcasting
        ComboBox<String> genderComboBox = new ComboBox<>();
        genderComboBox.getItems().addAll("Male", "Female", "Other");
        genderComboBox.setPromptText("Select gender");
        genderComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 280px;");
        if (inputField instanceof TextField) {
            TextField tf = (TextField) inputField; // downcasting
            tf.setText("Hello");
        }
        ComboBox<String> specializationComboBox = new ComboBox<>();
        specializationComboBox.getItems().addAll(
                "Cardiology", "Orthopedics", "Neurology", "Dermatology", "Pediatrics",
                "Psychiatry", "Radiology", "Ophthalmology", "Dentistry", "General Surgery",
                "ENT (Ear, Nose, and Throat)", "Urology"
        );
        specializationComboBox.setPromptText("Select specialization");
        specializationComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 280px;");

        ComboBox<String> departmentComboBox = new ComboBox<>();
        departmentComboBox.setPromptText("Select department");
        departmentComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 280px;");

        specializationComboBox.setOnAction(e -> {
            String selectedSpecialization = specializationComboBox.getValue();
            updateDepartmentComboBox(departmentComboBox, selectedSpecialization);
        });

        ComboBox<String> degreeComboBox = new ComboBox<>();
        degreeComboBox.getItems().addAll("MBBS", "MD", "MS", "MDS", "PhD");
        degreeComboBox.setPromptText("Select degree");
        degreeComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 280px;");

        TextField experienceField = createStyledTextField("Enter years of experience");
        TextField feeField = createStyledTextField("Enter consultation fee");

        Button saveButton = new Button("Save Doctor");
        saveButton.setStyle("-fx-background-color: #558b2f; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 10;");
        saveButton.setOnAction(e -> {
            try {
                saveDoctor(
                        nameField.getText(), cnicField.getText(), emailField.getText(), phoneField.getText(),
                        addressField.getText(), genderComboBox.getValue(), specializationComboBox.getValue(),
                        degreeComboBox.getValue(), experienceField.getText(), departmentComboBox.getValue(),
                        feeField.getText(), dobDatePicker.getValue()
                );
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        mainLayout.getChildren().addAll(
                titleLabel,
                createLabeledField("Name:", nameField),
                createLabeledField("CNIC:", cnicField),
                createLabeledField("Date of Birth:", dobDatePicker),
                createLabeledField("Email:", emailField),
                createLabeledField("Phone:", phoneField),
                createLabeledField("Address:", addressField),
                createLabeledField("Gender:", genderComboBox),
                createLabeledField("Specialization:", specializationComboBox),
                createLabeledField("Degree:", degreeComboBox),
                createLabeledField("Department:", departmentComboBox),
                createLabeledField("Experience:", experienceField),
                createLabeledField("Fee:", feeField),
                saveButton
        );

        return mainLayout;
    }

    private void setupSpecializationToDepartmentMap() {
        specializationToDepartmentMap = new HashMap<>();
        specializationToDepartmentMap.put("Cardiology", new String[]{"Cardiology Department"});
        specializationToDepartmentMap.put("Orthopedics", new String[]{"Orthopedics Department"});
        specializationToDepartmentMap.put("Neurology", new String[]{"Neurology Department"});
        specializationToDepartmentMap.put("Dermatology", new String[]{"Dermatology Department"});
        specializationToDepartmentMap.put("Pediatrics", new String[]{"Pediatrics Department"});
        specializationToDepartmentMap.put("Psychiatry", new String[]{"Psychiatry Department"});
        specializationToDepartmentMap.put("Radiology", new String[]{"Radiology Department"});
        specializationToDepartmentMap.put("Ophthalmology", new String[]{"Ophthalmology Department"});
        specializationToDepartmentMap.put("Dentistry", new String[]{"Dentistry Department"});
        specializationToDepartmentMap.put("General Surgery", new String[]{"Surgery Department"});
        specializationToDepartmentMap.put("ENT (Ear, Nose, and Throat)", new String[]{"ENT Department"});
        specializationToDepartmentMap.put("Urology", new String[]{"Urology Department"});
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setStyle("-fx-font-size: 14px; -fx-pref-width: 280px;");
        return field;
    }

    private HBox createLabeledField(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 16px; -fx-pref-width: 120px; -fx-font-weight: bold;");
        HBox box = new HBox(10, label, inputField);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void updateDepartmentComboBox(ComboBox<String> departmentComboBox, String specialization) {
        departmentComboBox.getItems().clear();
        String[] departments = specializationToDepartmentMap.get(specialization);
        if (departments != null) {
            departmentComboBox.getItems().addAll(departments);
        }
    }

    private void saveDoctor(String name, String cnic, String email, String phone, String address,
                            String gender, String specialization, String degree, String experienceStr,
                            String department, String feeStr, LocalDate dob) throws Exception {

        if (name.isEmpty() || cnic.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() ||
                gender == null || specialization == null || degree == null || experienceStr.isEmpty() ||
                department == null || feeStr.isEmpty() || dob == null) {
            showAlert(Alert.AlertType.ERROR, "All fields must be filled!");
            return;
        }

        int experience;
        double fee;
        try {
            experience = Integer.parseInt(experienceStr);
            fee = Double.parseDouble(feeStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Experience must be an integer and Fee must be a number!");
            return;
        }

        if (!cnic.matches("\\d{5}-\\d{7}-\\d{1}")) {
            showAlert(Alert.AlertType.ERROR, "Invalid CNIC format. Expected format: #####-#######-#");
            return;
        }

        String username = cnic;
        String plainPassword = generateRandomPassword(8);

        String sql = "INSERT INTO doctors (name, cnic, email, phone_number, address, gender, specialization, " +
                "medical_degree, years_of_experience, department, consultation_fee, date_of_birth, username, password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, cnic);
            stmt.setString(3, email);
            stmt.setString(4, phone);
            stmt.setString(5, address);
            stmt.setString(6, gender);
            stmt.setString(7, specialization);
            stmt.setString(8, degree);
            stmt.setInt(9, experience);
            stmt.setString(10, department);
            stmt.setDouble(11, fee);
            stmt.setDate(12, java.sql.Date.valueOf(dob));
            stmt.setString(13, username);
            stmt.setString(14, plainPassword);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                String subject = "Your Login Credentials - Remote Hospital Monitoring System";
                String body = "Dear Dr. " + name + ",\n\n" +
                        "You have been successfully registered in Remote Hospital Monitoring System.\n\n" +
                        "Username: " + username + "\n" +
                        "Password: " + plainPassword + "\n\n" +
                        "Please change your password after first login.\n\n" +
                        "Regards,\nHospital Admin";

                try {
                    sendEmail(email, subject, body);
                    showAlert(Alert.AlertType.INFORMATION, "Doctor saved successfully!\n\nUsername: " + username + "\nPassword: " + plainPassword + "\n\nLogin credentials have been sent to the doctor's email.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.WARNING, "Doctor saved but failed to send email.\n\nUsername: " + username + "\nPassword: " + plainPassword);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Failed to add doctor!");
            }
        }
    }

    private void sendEmail(String recipientEmail, String subject, String messageBody) throws MessagingException {
        final String senderEmail = "remotepatientmonitoringsystem1@gmail.com";
        final String senderPassword = "drfwrwofmevlhgjr";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(senderEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setText(messageBody);

        Transport.send(message);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.INFORMATION ? "Success" : "Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$_";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = (int) (Math.random() * chars.length());
            sb.append(chars.charAt(index));
        }
        return sb.toString();
    }
}
