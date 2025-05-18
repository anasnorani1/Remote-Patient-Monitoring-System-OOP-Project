import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;

public class ViewDoctorsForm {
    private TableView<Doctor> table;
    private ObservableList<Doctor> doctors;
    private Connection connection;

    public ViewDoctorsForm(Connection connection) {
        this.connection = connection;
        table = new TableView<>();
        doctors = FXCollections.observableArrayList();
    }

    public VBox getLayout() {
        // Define and setup the table columns
        setupTableColumns();

        // Create and configure the search field
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterDoctors(newValue);
        });

        // Load doctors from the database
        loadDoctorsFromDatabase();

        // Layout setup
        VBox layout = new VBox(10, searchField, table);
        layout.setPadding(new Insets(20));

        return layout;  // Return the layout instead of showing a new Stage
    }

    private void setupTableColumns() {
        table.getColumns().addAll(
                createColumn("Name", "name", 100),
                createColumn("CNIC", "cnic", 120),
                createColumn("Email", "email", 150),
                createColumn("Gender", "gender", 100),
                createColumn("Phone", "phoneNumber", 100),
                createColumn("Address", "address", 150),
                createColumn("Specialization", "specialization", 120),
                createColumn("Degree", "medicalDegree", 120),
                createColumn("Experience", "yearsOfExperience", 100),
                createColumn("Department", "department", 120),
                createColumn("Fee", "consultationFee", 80),
                createColumn("DOB", "dateOfBirth", 100)
        );

        // Add action column with Edit button
        TableColumn<Doctor, Void> actionCol = new TableColumn<>("Action");
        actionCol.setMinWidth(100);  // Ensure that the column has a sufficient width
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");

            {
                editButton.setMinWidth(80);  // Ensure button does not get too small
                editButton.setMaxWidth(Double.MAX_VALUE);  // Ensure it fills the available width within the cell
                editButton.setStyle("");  // Remove any styles to ensure it appears as before
                editButton.setOnAction(e -> {
                    Doctor selected = getTableView().getItems().get(getIndex());
                    new EditDoctorForm(selected, connection, ViewDoctorsForm.this::refreshDoctorsList).show();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
        table.getColumns().add(actionCol);
    }

    private TableColumn<Doctor, ?> createColumn(String title, String property, int width) {
        TableColumn<Doctor, Object> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(property));
        col.setMinWidth(width);
        return col;
    }

    private void loadDoctorsFromDatabase() {
        doctors.clear();  // Clear the list to ensure fresh data
        String query = "SELECT * FROM doctors";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                doctors.add(new Doctor(
                        rs.getString("name"),
                        rs.getString("cnic"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("specialization"),
                        rs.getString("medical_degree"),
                        rs.getInt("years_of_experience"),
                        rs.getString("department"),
                        rs.getDouble("consultation_fee"),
                        rs.getString("date_of_birth"),
                        rs.getString("gender")
                ));
            }
            table.setItems(FXCollections.observableArrayList(doctors));  // Refresh the table view
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load doctor data");
        }
    }

    private void filterDoctors(String query) {
        if (query.isEmpty()) {
            table.setItems(FXCollections.observableArrayList(doctors));
        } else {
            ObservableList<Doctor> filteredList = doctors.filtered(
                    doctor -> doctor.getName().toLowerCase().contains(query.toLowerCase())
            );
            table.setItems(filteredList);
        }
    }

    private void refreshDoctorsList() {
        loadDoctorsFromDatabase();  // Reload the list after update
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle(title);
        alert.showAndWait();
    }
}
