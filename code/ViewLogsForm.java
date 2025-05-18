import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ViewLogsForm {

    private VBox layout;
    private TableView<LogEntry> tableView;

    public ViewLogsForm(Connection conn) {
        setupUI(conn);
    }

    private void setupUI(Connection conn) {
        layout = new VBox(20);
        layout.setStyle("-fx-background-color: white;"); // Set the background color to white

        // Create table
        tableView = new TableView<>();

        // Create columns for the table
        TableColumn<LogEntry, String> actionColumn = new TableColumn<>("Action");
        TableColumn<LogEntry, String> timestampColumn = new TableColumn<>("Timestamp");
        TableColumn<LogEntry, String> detailsColumn = new TableColumn<>("Details");

        // Set cell value factories for each column
        actionColumn.setCellValueFactory(cellData -> cellData.getValue().actionProperty());
        timestampColumn.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());
        detailsColumn.setCellValueFactory(cellData -> cellData.getValue().detailsProperty());

        // Add columns to the table
        tableView.getColumns().add(actionColumn);
        tableView.getColumns().add(timestampColumn);
        tableView.getColumns().add(detailsColumn);

        // Fetch logs from the database and populate the table
        ObservableList<LogEntry> logs = fetchLogs(conn);
        tableView.setItems(logs);

        // Add table to the layout
        layout.getChildren().add(tableView);
    }

    private ObservableList<LogEntry> fetchLogs(Connection conn) {
        ObservableList<LogEntry> logs = FXCollections.observableArrayList();

        // Fetch logs from the database
        try (Statement stmt = conn.createStatement()) {
            String sql = "SELECT * FROM system_logs ORDER BY timestamp DESC";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String action = rs.getString("action");
                String timestamp = rs.getString("timestamp");
                String details = rs.getString("details");

                // Create a LogEntry object with fetched data
                logs.add(new LogEntry(action, timestamp, details));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return logs;
    }

    // LogEntry class to hold log data
    public static class LogEntry {
        private final String action;
        private final String timestamp;
        private final String details;

        public LogEntry(String action, String timestamp, String details) {
            this.action = action;
            this.timestamp = timestamp;
            this.details = details;
        }

        public String getAction() {
            return action;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getDetails() {
            return details;
        }

        public javafx.beans.property.StringProperty actionProperty() {
            return new javafx.beans.property.SimpleStringProperty(action);
        }

        public javafx.beans.property.StringProperty timestampProperty() {
            return new javafx.beans.property.SimpleStringProperty(timestamp);
        }

        public javafx.beans.property.StringProperty detailsProperty() {
            return new javafx.beans.property.SimpleStringProperty(details);
        }
    }

    // Method to return the layout (can be embedded in the main form window)
    public VBox getLayout() {
        return layout;
    }
}
