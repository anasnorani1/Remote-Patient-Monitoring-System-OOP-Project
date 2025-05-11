public class Patient {
    private int id;
    private String name;
    private String cnic;
    private int age;
    private String gender;
    private String illness;
    private String contact;
    private String address;
    private String dateOfBirth;
    private String password;
    private String username;
    private String passwordHash;
    private String email;

    // Constructor
    public Patient(int id, String name, String cnic, int age, String gender, String illness,
                   String contact, String address, String dateOfBirth, String password,
                   String username, String passwordHash, String email) {
        this.id = id;
        this.name = name;
        this.cnic = cnic;
        this.age = age;
        this.gender = gender;
        this.illness = illness;
        this.contact = contact;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.password = password;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // To string method for displaying patient details
    @Override
    public String toString() {
        return "Patient ID: " + id + "\n" +
                "Name: " + name + "\n" +
                "CNIC: " + cnic + "\n" +
                "Age: " + age + "\n" +
                "Gender: " + gender + "\n" +
                "Illness: " + illness + "\n" +
                "Contact: " + contact + "\n" +
                "Address: " + address + "\n" +
                "Date of Birth: " + dateOfBirth + "\n" +
                "Email: " + email;
    }
}
