import java.sql.*;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DoctorDatabase {

    public static ObservableList<String> getDoctors() {
        ObservableList<String> doctors = FXCollections.observableArrayList();

        String query = "SELECT doctor_id, name FROM doctors";  // Adjust the query to your schema

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthcare_system", "root", "Anasnorani@107");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String doctorId = rs.getString("doctor_id");
                String name = rs.getString("name");
                doctors.add(name + " (" + doctorId + ")");  // Display doctor name and ID
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return doctors;
    }
}
