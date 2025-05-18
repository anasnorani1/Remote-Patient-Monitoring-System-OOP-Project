import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EditPatientForm extends Stage {
    private Connection connection;
    private Patient patient;
    private boolean isEditing; // To differentiate between creating and editing

    // Constructor - Modify the constructor to handle both create and edit
    public EditPatientForm(Patient patient, Connection connection, Runnable onSuccess) {
        this.connection = connection;
        this.patient = patient;
        this.isEditing = patient != null; // If patient is null, we are creating a new patient
        setupUI(onSuccess);
    }

    // UI setup with the updated constructor
    private void setupUI(Runnable onSuccess) {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(30));
        mainLayout.setStyle("-fx-background-color: #e3f2fd;"); // Light Blue Background

        Label titleLabel = new Label(isEditing ? "Edit Patient Details" : "Create New Patient");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #0d47a1;");

        // Create fields with existing values or blank for new patient
        TextField nameField = createStyledTextField(isEditing ? patient.getName() : "");
        TextField cnicField = createStyledTextField(isEditing ? patient.getCnic() : "");
        TextField ageField = createStyledTextField(isEditing ? String.valueOf(patient.getAge()) : "");
        TextField contactField = createStyledTextField(isEditing ? patient.getContact() : "");
        TextField genderField = createStyledTextField(isEditing ? patient.getGender() : "");
        TextField illnessField = createStyledTextField(isEditing ? patient.getIllness() : "");
        TextField addressField = createStyledTextField(isEditing ? patient.getAddress() : "");
        TextField dobField = createStyledTextField(isEditing ? patient.getDateOfBirth() : "");
        TextField emailField = createStyledTextField(isEditing ? patient.getEmail() : "");

        // Save button
        Button saveButton = new Button(isEditing ? "Save Changes" : "Save Patient");
        saveButton.setStyle("-fx-background-color: #1976d2; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 10;");
        saveButton.setOnAction(e -> {
            try {
                if (isEditing) {
                    updatePatient(patient.getId(), nameField.getText(), cnicField.getText(), ageField.getText(), contactField.getText(), genderField.getText(), illnessField.getText(), addressField.getText(), dobField.getText(), emailField.getText(), onSuccess);
                } else {
                    createPatient(nameField.getText(), cnicField.getText(), ageField.getText(), contactField.getText(), genderField.getText(), illnessField.getText(), addressField.getText(), dobField.getText(), emailField.getText(), onSuccess);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });

        // Delete button for editing
        Button deleteButton = null;
        if (isEditing) {
            deleteButton = new Button("Delete Patient");
            deleteButton.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-font-size: 16px; -fx-background-radius: 10;");
            deleteButton.setOnAction(e -> {
                try {
                    deletePatient(patient.getId(), onSuccess);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            });
        }

        // Add UI elements to layout
        mainLayout.getChildren().addAll(
                titleLabel,
                createLabeledField("Name:", nameField),
                createLabeledField("CNIC:", cnicField),
                createLabeledField("Age:", ageField),
                createLabeledField("Contact:", contactField),
                createLabeledField("Gender:", genderField),
                createLabeledField("Illness:", illnessField),
                createLabeledField("Address:", addressField),
                createLabeledField("DOB:", dobField),
                createLabeledField("Email:", emailField),
                saveButton
        );
        if (deleteButton != null) mainLayout.getChildren().add(deleteButton);

        Scene scene = new Scene(mainLayout, 450, 650);
        this.setScene(scene);
        this.setTitle(isEditing ? "Edit Patient" : "Create Patient");
    }

    // Create patient method
    private void createPatient(String name, String cnic, String ageStr, String contact, String gender, String illness, String address, String dob, String email, Runnable onSuccess) throws SQLException {
        if (name.isEmpty() || cnic.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || gender.isEmpty() || illness.isEmpty() || address.isEmpty() || dob.isEmpty() || email.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Age must be a number!");
            return;
        }

        // Validate CNIC format
        if (!cnic.matches("\\d{5}-\\d{7}-\\d{1}")) {
            showAlert("Error", "Invalid CNIC format. Expected format: #####-#######-#");
            return;
        }

        // SQL Query to insert a new patient into the database
        String insertSql = "INSERT INTO patients (name, cnic, age, contact, gender, illness, address, date_of_birth, email) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(insertSql)) {
            stmt.setString(1, name);
            stmt.setString(2, cnic);
            stmt.setInt(3, age);
            stmt.setString(4, contact);
            stmt.setString(5, gender);
            stmt.setString(6, illness);
            stmt.setString(7, address);
            stmt.setString(8, dob);
            stmt.setString(9, email);
            stmt.executeUpdate();

            showAlert("Information", "Patient created successfully!");
            onSuccess.run(); // Refresh the UI or list of patients
            this.close(); // Close the form after saving
        } catch (SQLException e) {
            showAlert("Error", "Error creating patient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Update patient method
    private void updatePatient(int patientId, String name, String cnic, String ageStr, String contact, String gender, String illness, String address, String dob, String email, Runnable onSuccess) throws SQLException {
        if (name.isEmpty() || cnic.isEmpty() || ageStr.isEmpty() || contact.isEmpty() || gender.isEmpty() || illness.isEmpty() || address.isEmpty() || dob.isEmpty() || email.isEmpty()) {
            showAlert("Error", "All fields must be filled!");
            return;
        }

        int age;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            showAlert("Error", "Age must be a number!");
            return;
        }

        // Validate CNIC format
        if (!cnic.matches("\\d{5}-\\d{7}-\\d{1}")) {
            showAlert("Error", "Invalid CNIC format. Expected format: #####-#######-#");
            return;
        }

        // SQL query to update patient details
        String updateSql = "UPDATE patients SET name = ?, cnic = ?, age = ?, contact = ?, gender = ?, illness = ?, address = ?, date_of_birth = ?, email = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(updateSql)) {
            stmt.setString(1, name);
            stmt.setString(2, cnic);
            stmt.setInt(3, age);
            stmt.setString(4, contact);
            stmt.setString(5, gender);
            stmt.setString(6, illness);
            stmt.setString(7, address);
            stmt.setString(8, dob);
            stmt.setString(9, email);
            stmt.setInt(10, patientId);
            stmt.executeUpdate();

            showAlert("Information", "Patient updated successfully!");
            onSuccess.run(); // Refresh the UI
            this.close(); // Close the form
        } catch (SQLException e) {
            showAlert("Error", "Error updating patient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Delete patient method
    private void deletePatient(int patientId, Runnable onSuccess) throws SQLException {
        // First, delete dependent records in appointment_requests
        String deleteAppointmentsSql = "DELETE FROM appointment_requests WHERE patient_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteAppointmentsSql)) {
            stmt.setInt(1, patientId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            showAlert("Error", "Error deleting appointment records: " + e.getMessage());
            e.printStackTrace();
            return; // Return early if there's an error deleting the dependent records
        }

        // Now, delete the patient
        String deletePatientSql = "DELETE FROM patients WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deletePatientSql)) {
            stmt.setInt(1, patientId);
            stmt.executeUpdate();

            showAlert("Information", "Patient deleted successfully!");
            onSuccess.run(); // Refresh the UI
            this.close(); // Close the form
        } catch (SQLException e) {
            showAlert("Error", "Error deleting patient: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper to create labeled text fields
    private HBox createLabeledField(String labelText, Control inputField) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-size: 16px; -fx-pref-width: 80px; -fx-font-weight: bold;");
        HBox box = new HBox(10, label, inputField);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    // Helper to show alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    // Create a styled text field with default text
    private TextField createStyledTextField(String text) {
        TextField field = new TextField(text);
        field.setStyle("-fx-font-size: 14px; -fx-pref-width: 250px;");
        return field;
    }
}
