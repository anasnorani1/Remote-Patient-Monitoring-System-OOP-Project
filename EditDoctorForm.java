import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class EditDoctorForm extends Stage {

    public EditDoctorForm(Doctor doctor, Connection conn, Runnable onSuccess) {
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(10);

        // Text fields pre-populated
        TextField nameField = new TextField(doctor.getName());
        TextField cnicField = new TextField(doctor.getCnic());
        TextField emailField = new TextField(doctor.getEmail());
        TextField phoneField = new TextField(doctor.getPhoneNumber());
        TextField addressField = new TextField(doctor.getAddress());
        TextField specializationField = new TextField(doctor.getSpecialization());
        TextField degreeField = new TextField(doctor.getMedicalDegree());
        TextField expField = new TextField(String.valueOf(doctor.getYearsOfExperience()));
        TextField deptField = new TextField(doctor.getDepartment());
        TextField feeField = new TextField(String.valueOf(doctor.getConsultationFee()));

        // Date of Birth
        LocalDate dob = null;
        try {
            dob = (doctor.getDateOfBirth() != null) ? LocalDate.parse(doctor.getDateOfBirth()) : null;
        } catch (Exception ignored) {}
        DatePicker dobPicker = new DatePicker(dob);

        // Gender
        ComboBox<String> genderBox = new ComboBox<>();
        genderBox.getItems().addAll("Male", "Female", "Other");
        genderBox.setValue(doctor.getGender());

        // Buttons
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");

        // Layout
        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("CNIC:"), cnicField);
        grid.addRow(2, new Label("Email:"), emailField);
        grid.addRow(3, new Label("Phone:"), phoneField);
        grid.addRow(4, new Label("Address:"), addressField);
        grid.addRow(5, new Label("Specialization:"), specializationField);
        grid.addRow(6, new Label("Degree:"), degreeField);
        grid.addRow(7, new Label("Experience:"), expField);
        grid.addRow(8, new Label("Department:"), deptField);
        grid.addRow(9, new Label("Fee:"), feeField);
        grid.addRow(10, new Label("Date of Birth:"), dobPicker);
        grid.addRow(11, new Label("Gender:"), genderBox);
        grid.add(updateBtn, 1, 12);
        grid.add(deleteBtn, 1, 13);

        // Update Button Logic
        updateBtn.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                String cnic = cnicField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String specialization = specializationField.getText().trim();
                String degree = degreeField.getText().trim();
                String department = deptField.getText().trim();
                String dobStr = (dobPicker.getValue() != null) ? dobPicker.getValue().toString() : null;
                String gender = genderBox.getValue();

                if (name.isEmpty() || cnic.isEmpty() || email.isEmpty()) {
                    showAlert(Alert.AlertType.WARNING, "Name, CNIC, and Email are required.");
                    return;
                }

                if (!email.matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                    showAlert(Alert.AlertType.WARNING, "Invalid email format.");
                    return;
                }

                if (!phone.matches("^\\d{10,15}$")) {
                    showAlert(Alert.AlertType.WARNING, "Invalid phone number. Enter 10–15 digits.");
                    return;
                }

                int experience = Integer.parseInt(expField.getText().trim());
                double fee = Double.parseDouble(feeField.getText().trim());

                String checkExistenceSql = "SELECT * FROM doctors WHERE cnic = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkExistenceSql)) {
                    checkStmt.setString(1, cnic);
                    ResultSet rs = checkStmt.executeQuery();
                    if (!rs.next()) {
                        showAlert(Alert.AlertType.ERROR, "Doctor with CNIC " + cnic + " does not exist.");
                        return;
                    }
                }

                // Updated SQL with gender
                String updateSql = """
                    UPDATE doctors SET name=?, email=?, phone_number=?, address=?, specialization=?, 
                    medical_degree=?, years_of_experience=?, department=?, consultation_fee=?, 
                    date_of_birth=?, gender=?
                    WHERE cnic=?
                """;

                try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                    stmt.setString(1, name);
                    stmt.setString(2, email);
                    stmt.setString(3, phone);
                    stmt.setString(4, address);
                    stmt.setString(5, specialization);
                    stmt.setString(6, degree);
                    stmt.setInt(7, experience);
                    stmt.setString(8, department);
                    stmt.setDouble(9, fee);
                    stmt.setString(10, dobStr);
                    stmt.setString(11, gender);
                    stmt.setString(12, cnic);

                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Doctor details updated successfully.");
                        onSuccess.run();
                        this.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Update failed. Doctor may not exist.");
                    }
                }

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.WARNING, "Experience and Fee must be numeric.");
            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Failed to update doctor details.");
            }
        });

        // Delete Button Logic
        deleteBtn.setOnAction(e -> {
            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this doctor?", ButtonType.YES, ButtonType.NO);
            confirmation.setHeaderText(null);
            confirmation.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        String cnic = cnicField.getText().trim();

                        // Step 1: Get doctor_id using CNIC
                        String getDoctorIdSql = "SELECT doctor_id FROM doctors WHERE cnic = ?";
                        int doctorId = -1;
                        try (PreparedStatement stmt = conn.prepareStatement(getDoctorIdSql)) {
                            stmt.setString(1, cnic);
                            ResultSet rs = stmt.executeQuery();
                            if (rs.next()) {
                                doctorId = rs.getInt("doctor_id");
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Doctor not found.");
                                return;
                            }
                        }

                        // Step 2: Delete from appointment_requests
                        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM appointment_requests WHERE doctor_id = ?")) {
                            stmt.setInt(1, doctorId);
                            stmt.executeUpdate();
                        }

                        // Step 3: Delete from appointments
                        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM appointments WHERE doctor_id = ?")) {
                            stmt.setInt(1, doctorId);
                            stmt.executeUpdate();
                        }

                        // Step 4: Delete from emergency_alerts (NEW LINE ADDED HERE)
                        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM emergency_alerts WHERE doctor_id = ?")) {
                            stmt.setInt(1, doctorId);
                            stmt.executeUpdate();
                        }

                        // Step 5: Delete from vital_signs
                        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM vital_signs WHERE doctor_id = ?")) {
                            stmt.setInt(1, doctorId);
                            stmt.executeUpdate();
                        }

                        // Step 6: Finally delete from doctors
                        try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM doctors WHERE doctor_id = ?")) {
                            stmt.setInt(1, doctorId);
                            int rowsAffected = stmt.executeUpdate();
                            if (rowsAffected > 0) {
                                showAlert(Alert.AlertType.INFORMATION, "Doctor deleted successfully.");
                                onSuccess.run();
                                this.close();
                            } else {
                                showAlert(Alert.AlertType.ERROR, "Deletion failed. Doctor may not exist.");
                            }
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Failed to delete doctor.");
                    }
                }
            });
        });

        setScene(new Scene(grid, 450, 650));
        setTitle("Edit Doctor Details");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}
