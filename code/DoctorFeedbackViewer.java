import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DoctorFeedbackViewer {

    public VBox createFeedbackView(int patientId) {
        String query = "SELECT d.name, f.feedback, f.feedback_datetime " +
                "FROM feedback f " +
                "JOIN doctors d ON f.doctor_id = d.doctor_id " +
                "WHERE f.patient_id = ?";

        VBox layout = new VBox(20);
        layout.setPadding(new Insets(40));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #ffffff;");

        Label title = new Label("Doctor's Feedback");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #4C4C4C;");

        TableView<DoctorFeedback> table = new TableView<>();
        table.setEditable(false);

        TableColumn<DoctorFeedback, String> doctorColumn = new TableColumn<>("Doctor Name");
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

        TableColumn<DoctorFeedback, String> feedbackColumn = new TableColumn<>("Feedback");
        feedbackColumn.setCellValueFactory(new PropertyValueFactory<>("feedback"));

        TableColumn<DoctorFeedback, String> feedbackDateColumn = new TableColumn<>("Feedback Date & Time");
        feedbackDateColumn.setCellValueFactory(new PropertyValueFactory<>("feedbackDateTime"));

        table.getColumns().addAll(doctorColumn, feedbackColumn, feedbackDateColumn);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String doctorName = rs.getString("name");
                String feedback = rs.getString("feedback");
                String feedbackDateTime = rs.getString("feedback_datetime");
                table.getItems().add(new DoctorFeedback(doctorName, feedback, feedbackDateTime));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            UIUtils.showAlert("Error loading doctor feedback: " + ex.getMessage());
        }

        layout.getChildren().addAll(title, table);
        return layout;
    }

    // Inner class for feedback model
    public static class DoctorFeedback {
        private final String doctorName;
        private final String feedback;
        private final String feedbackDateTime;

        public DoctorFeedback(String doctorName, String feedback, String feedbackDateTime) {
            this.doctorName = doctorName;
            this.feedback = feedback;
            this.feedbackDateTime = feedbackDateTime;
        }

        public String getDoctorName() {
            return doctorName;
        }

        public String getFeedback() {
            return feedback;
        }

        public String getFeedbackDateTime() {
            return feedbackDateTime;
        }
    }
}
