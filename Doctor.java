import java.util.Objects;

public class Doctor {
    private String name;
    private String cnic;
    private String email;
    private String phoneNumber;
    private String address;
    private String specialization;
    private String medicalDegree;
    private int yearsOfExperience;
    private String department;
    private double consultationFee;
    private String dateOfBirth;
    private String gender;  // Added gender field

    public Doctor(String name, String cnic, String email, String phoneNumber, String address,
                  String specialization, String medicalDegree, int yearsOfExperience,
                  String department, double consultationFee, String dateOfBirth, String gender) {
        this.name = name;
        this.cnic = cnic;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.specialization = specialization;
        this.medicalDegree = medicalDegree;
        this.yearsOfExperience = yearsOfExperience;
        this.department = department;
        this.consultationFee = consultationFee;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;  // Initialize gender
    }

    // Getters and Setters for gender
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Other getters and setters remain unchanged...
    public String getName() { return name; }
    public String getCnic() { return cnic; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getSpecialization() { return specialization; }
    public String getMedicalDegree() { return medicalDegree; }
    public int getYearsOfExperience() { return yearsOfExperience; }
    public String getDepartment() { return department; }
    public double getConsultationFee() { return consultationFee; }
    public String getDateOfBirth() { return dateOfBirth; }

    // Override toString for easy display
    @Override
    public String toString() {
        return "Doctor{" +
                "name='" + name + '\'' +
                ", cnic='" + cnic + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", specialization='" + specialization + '\'' +
                ", medicalDegree='" + medicalDegree + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", department='" + department + '\'' +
                ", consultationFee=" + consultationFee +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", gender='" + gender + '\'' +  // Include gender in toString
                '}';
    }

    // Override equals and hashCode for object comparison and usage in collections
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Doctor doctor = (Doctor) o;
        return yearsOfExperience == doctor.yearsOfExperience &&
                Double.compare(doctor.consultationFee, consultationFee) == 0 &&
                Objects.equals(name, doctor.name) &&
                Objects.equals(cnic, doctor.cnic) &&
                Objects.equals(email, doctor.email) &&
                Objects.equals(phoneNumber, doctor.phoneNumber) &&
                Objects.equals(address, doctor.address) &&
                Objects.equals(specialization, doctor.specialization) &&
                Objects.equals(medicalDegree, doctor.medicalDegree) &&
                Objects.equals(department, doctor.department) &&
                Objects.equals(dateOfBirth, doctor.dateOfBirth) &&
                Objects.equals(gender, doctor.gender);  // Include gender in equals
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, cnic, email, phoneNumber, address, specialization, medicalDegree,
                yearsOfExperience, department, consultationFee, dateOfBirth, gender);  // Include gender in hashCode
    }
}
