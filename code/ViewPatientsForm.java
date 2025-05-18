import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewPatientsForm {
    private Connection connection;
    private TableView<Patient> patientTable;
    private ObservableList<Patient> patientList;
    private VBox layout;  // Make layout a class-level variable

    public ViewPatientsForm(Connection connection) {
        this.connection = connection;
        setupUI();
    }

    private void setupUI() {
        // Initialize the table and the list that will hold the data
        patientTable = new TableView<>();
        patientList = FXCollections.observableArrayList();

        // Define table columns
        TableColumn<Patient, Integer> idColumn = new TableColumn<>("Patient ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Patient, String> cnicColumn = new TableColumn<>("CNIC");
        cnicColumn.setCellValueFactory(new PropertyValueFactory<>("cnic"));

        TableColumn<Patient, String> ageColumn = new TableColumn<>("Age");
        ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));

        TableColumn<Patient, String> contactColumn = new TableColumn<>("Contact");
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));

        // Create the Edit button column
        TableColumn<Patient, Void> editColumn = new TableColumn<>("Edit");
        editColumn.setCellFactory(param -> new TableCell<Patient, Void>() {
            private final Button editButton = new Button("Edit");

            {
                // Set action for the Edit button
                editButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        Patient selectedPatient = getTableView().getItems().get(getIndex());
                        openEditPatientForm(selectedPatient); // Open the edit form for the selected patient
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });

        patientTable.getColumns().addAll(idColumn, nameColumn, cnicColumn, ageColumn, contactColumn, editColumn);

        // Create a search bar (TextField)
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name, CNIC, or contact...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterPatients(newValue);  // Filter the table based on search query
        });

        // Fetch patients from the database
        try {
            String sql = "SELECT * FROM patients";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String cnic = rs.getString("cnic");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                String illness = rs.getString("illness");
                String contact = rs.getString("contact");
                String address = rs.getString("address");
                String dateOfBirth = rs.getString("date_of_birth");
                String password = "";
                String username = "";
                String passwordHash = "";
                String email = rs.getString("email");

                // Create a new Patient object and add it to the list
                patientList.add(new Patient(id, name, cnic, age, gender, illness, contact, address, dateOfBirth, password, username, passwordHash, email));
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to load patients data");
            e.printStackTrace();
        }

        // Add data to the table
        patientTable.setItems(patientList);

        // Layout the scene with search bar and table
        layout = new VBox(10);  // Initialize the layout
        layout.getChildren().addAll(searchField, patientTable);
    }

    // Getter for the layout
    public VBox getLayout() {
        return layout;  // Return the layout for this form
    }

    private void filterPatients(String query) {
        ObservableList<Patient> filteredList = FXCollections.observableArrayList();

        for (Patient patient : patientList) {
            if (patient.getName().toLowerCase().contains(query.toLowerCase()) ||
                    patient.getCnic().toLowerCase().contains(query.toLowerCase()) ||
                    patient.getContact().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(patient);
            }
        }

        patientTable.setItems(filteredList);  // Set filtered list to table
    }

    private void openEditPatientForm(Patient selectedPatient) {
        EditPatientForm editForm = new EditPatientForm(selectedPatient, connection, () -> {
            // Refresh the table after editing
            refreshPatientList();
        });
        editForm.show();  // Show the form
    }
    private void refreshPatientList() {
        patientList.clear();  // Clear the existing list

        try {
            String sql = "SELECT * FROM patients";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String cnic = rs.getString("cnic");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                String illness = rs.getString("illness");
                String contact = rs.getString("contact");
                String address = rs.getString("address");
                String dateOfBirth = rs.getString("date_of_birth");
                String email = rs.getString("email");

                patientList.add(new Patient(id, name, cnic, age, gender, illness, contact, address, dateOfBirth, "", "", "", email));
            }

            patientTable.setItems(patientList);  // Update the table
        } catch (Exception e) {
            showAlert("Error", "Failed to refresh patients data");
            e.printStackTrace();
        }
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
