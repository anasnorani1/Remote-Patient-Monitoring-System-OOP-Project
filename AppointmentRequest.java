public class AppointmentRequest {

    private int id;
    private int patientId;
    private int doctorId;
    private String requestedDate;
    private String requestedTime;
    private String status;

    // Constructor to initialize the AppointmentRequest
    public AppointmentRequest(int id, int patientId, int doctorId, String requestedDate, String requestedTime, String status) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.requestedDate = requestedDate;
        this.requestedTime = requestedTime;
        this.status = status;
    }

    // Getter methods for each field
    public int getId() {
        return id;
    }

    public int getPatientId() {
        return patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public String getRequestedDate() {
        return requestedDate;
    }

    public String getRequestedTime() {
        return requestedTime;
    }

    public String getStatus() {
        return status;
    }

    // Optional: Override toString() method to easily print the object
    @Override
    public String toString() {
        return "AppointmentRequest{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", requestedDate='" + requestedDate + '\'' +
                ", requestedTime='" + requestedTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
