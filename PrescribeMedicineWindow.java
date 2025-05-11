import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescribeMedicineWindow extends Stage {

    private int doctorId;
    private int selectedPatientId;
    private List<TextField> medicineFields;
    private List<CheckBox> whenToTakeCheckBoxes;
    private ComboBox<String> beforeAfterCombo;
    private TextField quantityField;
    private Button submitButton;
    private TextField timesPerDayField;
    private ComboBox<String> patientComboBox;
    private TextArea noteTextArea;
    private Button addMoreMedicineButton;
    private ComboBox<String> appointmentComboBox;

    public PrescribeMedicineWindow(int doctorId) {
        this.doctorId = doctorId;
        this.medicineFields = new ArrayList<>();
    }

    // Returns the UI as a VBox layout to be added to the main content area
    public VBox createPrescriptionForm(int doctorId) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #f4f7fa;");

        // Set max width for the layout to prevent overflow and ensure responsiveness
        layout.setMaxWidth(600); // Adjust the width as needed
        layout.setPrefWidth(600); // Set preferred width to make sure it doesn't expand too much

        Label title = new Label("Prescribe Medicine");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #333333;");

        // Select Patient ComboBox
        Label patientLabel = new Label("Select Patient:");
        patientComboBox = new ComboBox<>();
        patientComboBox.setPromptText("Choose a patient");
        patientComboBox.setMaxWidth(300); // Set max width for combo box

        // Select Appointment ComboBox
        Label appointmentLabel = new Label("Select Appointment:");
        appointmentComboBox = new ComboBox<>();
        appointmentComboBox.setPromptText("Choose an appointment");
        appointmentComboBox.setMaxWidth(300); // Set max width for combo box

        loadPatients();  // Method to load available patients into the ComboBox

        // Medicine Name input (Initially one field for medicine)
        Label medicineLabel = new Label("Medicine Name:");
        TextField firstMedicineField = new TextField();
        firstMedicineField.setPromptText("Enter medicine name");
        firstMedicineField.setMaxWidth(300); // Set max width for text field
        medicineFields.add(firstMedicineField);

        // When to Take Checkboxes
        Label whenToTakeLabel = new Label("When to Take:");
        whenToTakeCheckBoxes = new ArrayList<>();
        CheckBox breakfastCheck = new CheckBox("Breakfast");
        CheckBox lunchCheck = new CheckBox("Lunch");
        CheckBox dinnerCheck = new CheckBox("Dinner");
        whenToTakeCheckBoxes.add(breakfastCheck);
        whenToTakeCheckBoxes.add(lunchCheck);
        whenToTakeCheckBoxes.add(dinnerCheck);

        VBox whenToTakeVBox = new VBox(10, breakfastCheck, lunchCheck, dinnerCheck);
        whenToTakeVBox.setMaxWidth(300);  // Limit the width of the checkboxes

        // Before or After ComboBox
        Label beforeAfterLabel = new Label("Before or After Meal:");
        beforeAfterCombo = new ComboBox<>();
        beforeAfterCombo.getItems().addAll("Before", "After");
        beforeAfterCombo.setValue("Before");
        beforeAfterCombo.setMaxWidth(300); // Set max width for combo box

        // Quantity input
        Label quantityLabel = new Label("Quantity:");
        quantityField = new TextField();
        quantityField.setPromptText("Enter quantity");
        quantityField.setMaxWidth(300); // Set max width for text field

        // Times Per Day for more than 3 times case
        Label timesPerDayLabel = new Label("Times Per Day (if more than 3):");
        timesPerDayField = new TextField();
        timesPerDayField.setPromptText("Enter number of times");
        timesPerDayField.setMaxWidth(300); // Set max width for text field

        // Optional Note TextArea
        Label noteLabel = new Label("Note (Optional):");
        noteTextArea = new TextArea();
        noteTextArea.setPromptText("Enter any notes here...");
        noteTextArea.setWrapText(true);
        noteTextArea.setMaxHeight(100);  // Limit the height of the text area
        noteTextArea.setMaxWidth(300);   // Limit width for text area

        // Submit Button
        submitButton = new Button("Submit Prescription");
        submitButton.setStyle("-fx-font-size: 15px; -fx-background-color: #4caf50; -fx-text-fill: white; -fx-background-radius: 12px; -fx-padding: 12px 20px;");
        submitButton.setEffect(new DropShadow(10, Color.BLACK));

        submitButton.setOnAction(e -> handlePrescriptionSubmission());

        // Add all UI components to the layout
        layout.getChildren().addAll(title, patientLabel, patientComboBox, appointmentLabel, appointmentComboBox,
                medicineLabel, firstMedicineField, whenToTakeLabel, whenToTakeVBox,
                beforeAfterLabel, beforeAfterCombo, quantityLabel, quantityField,
                timesPerDayLabel, timesPerDayField, noteLabel, noteTextArea, submitButton);

        // Add a ScrollPane to the layout if content overflows
        ScrollPane scrollPane = new ScrollPane(layout);
        scrollPane.setFitToHeight(true);  // Enable auto-scrolling for the height
        scrollPane.setFitToWidth(true);   // Enable auto-scrolling for the width
        scrollPane.setMaxWidth(600);      // Adjust the width to match the layout width

        // Return the ScrollPane (the parent layout is wrapped within it)
        return layout;
    }


    // Existing methods for loading patients, submitting prescription, etc.
    private void loadPatients() {
        String query = "SELECT id, name FROM patients";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String patientName = rs.getString("name");
                patientComboBox.getItems().add(patientName);
            }

            if (!patientComboBox.getItems().isEmpty()) {
                patientComboBox.setValue(patientComboBox.getItems().get(0));
                selectedPatientId = getPatientIdByName(patientComboBox.getValue());
                loadAppointments();
            }

            patientComboBox.setOnAction(e -> {
                String selectedPatientName = patientComboBox.getValue();
                selectedPatientId = getPatientIdByName(selectedPatientName);
                loadAppointments();
            });

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private int getPatientIdByName(String patientName) {
        String query = "SELECT id FROM patients WHERE name = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, patientName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private void loadAppointments() {
        appointmentComboBox.getItems().clear();
        String query = "SELECT id, date FROM appointments WHERE patient_id = ?";
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, selectedPatientId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String appointmentInfo = "Appointment ID: " + rs.getInt("id") + " - " + rs.getString("date");
                appointmentComboBox.getItems().add(appointmentInfo);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }




    private void handlePrescriptionSubmission() {
        String selectedPatientName = patientComboBox.getValue();
        int patientId = getPatientIdByName(selectedPatientName);

        // Get selected appointment ID from the ComboBox
        String selectedAppointment = appointmentComboBox.getValue();  // Format: "Appointment ID: X - date"
        int appointmentId = -1;
        if (selectedAppointment != null && selectedAppointment.contains("Appointment ID:")) {
            String[] parts = selectedAppointment.split(" ");
            appointmentId = Integer.parseInt(parts[2]);  // Extracts appointment ID
        } else {
            UIUtils.showAlert("Please select a valid appointment.");
            return;
        }

        for (TextField medicineField : medicineFields) {
            String medicineName = medicineField.getText();
            if (medicineName.isEmpty()) continue;

            String whenToTake = getWhenToTake();
            String beforeAfterMeal = beforeAfterCombo.getValue();
            String quantity = quantityField.getText();
            String timesPerDay = timesPerDayField.getText().isEmpty() ? "0" : timesPerDayField.getText();
            String note = noteTextArea.getText();

            String query = "INSERT INTO prescriptions (doctor_id, patient_id, appointment_id, medicine_name, " +
                    "when_to_take, before_after, quantity, times_per_day, note) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {

                pstmt.setInt(1, doctorId);
                pstmt.setInt(2, patientId);
                pstmt.setInt(3, appointmentId);
                pstmt.setString(4, medicineName);
                pstmt.setString(5, whenToTake);
                pstmt.setString(6, beforeAfterMeal);
                pstmt.setString(7, quantity);
                pstmt.setString(8, timesPerDay);
                pstmt.setString(9, note);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    UIUtils.showAlert("Prescription submitted successfully!");
                } else {
                    UIUtils.showAlert("Error submitting prescription.");
                }

                askForAnotherMedicine();

            } catch (SQLException ex) {
                ex.printStackTrace();
                UIUtils.showAlert("Database error occurred.");
            }
        }
    }

    private String getWhenToTake() {
        StringBuilder whenToTake = new StringBuilder();
        for (CheckBox checkBox : whenToTakeCheckBoxes) {
            if (checkBox.isSelected()) {
                whenToTake.append(checkBox.getText()).append(", ");
            }
        }
        // Remove the last comma and space
        if (whenToTake.length() > 0) {
            whenToTake.setLength(whenToTake.length() - 2);
        }
        return whenToTake.toString();
    }

    private void askForAnotherMedicine() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Add Another Medicine?");
        alert.setHeaderText("Would you like to prescribe another medicine?");
        alert.setContentText("Click Yes to enter another medicine or No to finish.");

        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");

        alert.getButtonTypes().setAll(yesButton, noButton);

        alert.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                // Reset the fields for the doctor to enter a new medicine
                resetFieldsForNewMedicine();
            } else {
                // Finish the prescription process, if needed
                this.close();  // Close the prescription window if done
            }
        });
    }

    private void resetFieldsForNewMedicine() {
        // Reset all input fields to allow the doctor to prescribe another medicine
        for (TextField field : medicineFields) {
            field.clear();
        }

        // Reset checkboxes and combo boxes
        for (CheckBox checkBox : whenToTakeCheckBoxes) {
            checkBox.setSelected(false);
        }
        beforeAfterCombo.setValue("Before");
        quantityField.clear();
        timesPerDayField.clear();
        noteTextArea.clear();
    }
}
