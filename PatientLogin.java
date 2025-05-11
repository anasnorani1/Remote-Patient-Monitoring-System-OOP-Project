public class PatientLogin {
    private static int loggedInPatientId = 0; // Default value to indicate no patient is logged in
    private static String loggedInPatientName = "Unknown"; // Default value for patient name

    // Method to set the logged-in patient's information
    public static synchronized void setLoggedInPatient(int patientId, String patientName) {
        loggedInPatientId = patientId;
        loggedInPatientName = (patientName != null) ? patientName : "Unknown"; // Ensure no null value for patientName
    }

    // Method to get the logged-in patient's ID
    public static synchronized int getLoggedInPatientId() {
        return loggedInPatientId;
    }

    // Method to get the logged-in patient's name
    public static synchronized String getLoggedInPatientName() {
        return loggedInPatientName;
    }

    // Optional: Reset logged-in patient details (e.g., for logout)
    public static synchronized void resetLoggedInPatient() {
        loggedInPatientId = 0;
        loggedInPatientName = "Unknown";
    }

    // Optional: Check if a patient is logged in
    public static synchronized boolean isPatientLoggedIn() {
        return loggedInPatientId != 0; // Assuming 0 means no patient is logged in
    }
}
