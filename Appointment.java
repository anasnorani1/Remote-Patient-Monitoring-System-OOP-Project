public class Appointment {
    private String appointmentDate, doctorName, reason, status;

    public Appointment(String appointmentDate, String doctorName, String reason, String status) {
        this.appointmentDate = appointmentDate;
        this.doctorName = doctorName;
        this.reason = reason;
        this.status = status;
    }

    public String getAppointmentDate() { return appointmentDate; }
    public String getDoctorName() { return doctorName; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}
