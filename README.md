# 🏥 Remote Patient Monitoring System

A complete Java + JavaFX desktop application with a MySQL backend that facilitates remote healthcare. It supports user roles for patients, doctors, and administrators, offering features like uploading vitals, booking appointments, sending emergency alerts, managing feedback, and more.

---

## 📌 Features

### 👤 Patient
- Upload vital signs via CSV
- View doctor feedback and prescription history
- Book/view/cancel appointments
- Send emergency alerts with optional video call links
- Chat with doctors
- Generate reports of prescriptions

### 👨‍⚕️ Doctor
- View uploaded vitals in chart format using JFreeChart
- Provide medical feedback
- Prescribe medications
- Send reminders
- Initiate video calls via shared links

### 🛠️ Admin
- Add/edit/delete patient and doctor users
- Approve or reject appointment requests
- Monitor system logs
- Send custom or broadcast emails

---

## 🧰 Tech Stack

| Component    | Technology         |
|--------------|--------------------|
| Frontend     | JavaFX             |
| Backend      | Java               |
| Database     | MySQL              |
| Charts       | JFreeChart         |
| Emails       | Jakarta Mail API   |
| CSV Handling | Java File I/O      |
| IDE          | IntelliJ IDEA      |

---

## 🚀 Setup Instructions

To successfully run the project, follow all the steps below **in the given order**:

---

### 🔹 1. Add Dependencies in IntelliJ

Open IntelliJ and follow:
File → Project Structure → Libraries → +
These Files are already given in "Dependencies For Running" Folder
Add the following JAR files:
- JavaFX SDK JARs (`lib/*.jar`)
- MySQL Connector/J (`mysql-connector-java-x.x.x.jar`)
- JFreeChart (`jfreechart-x.x.x.jar`)
- Jakarta Mail (`jakarta.mail-x.x.x.jar`)

✅ Make sure these are added to **both "Libraries" and "Modules > Dependencies"**.

---

### 🔹 2. Run SQL Script in MySQL Workbench

1. Open **MySQL Workbench**
2. Downlaod Script from "Sql Script " Fodler and Execute it
3. THis will give you the table in your database

---

### 🔹 3. Configure Image Paths in LoginPage.java
Download bg.png and logo.png from "Dependencies for running" folder
Open:
src/LoginPage.java
Replace The Following Paths with you ideal path whwere you have saved these images

---
### 🔹 4.  Set MySQL Credentials in Follwong:
Open:
   1. DatabaseConnector.java
   2. VitalSigns.java
   3. AppointmentViewerAdmin.java
   4. ChangePatientPassword.java
   5. ChatHistoryViewer.java
   6. DoctorChatViewer.java
   7. DoctorDatabase.java
   8. MessageDoctorForm.java

Update your local database settings:

1. String url = "jdbc:mysql://localhost:3306/healthcare_system";
2. String user = "root";         // ← Change this
2. String password = "yourpass"; // ← Change this
Ensure your MySQL service is running and credentials are correct.


---### 🔹 5. Configure Email Functionality

This project uses the **Jakarta Mail API** to send emails for:

- Emergency alerts  
- Login credentials  
- Reminders  
- Doctor-patient messaging  

#### 📂 Files to Update

Update the sender email and App Password in the following files:

- `EmailSender.java`
- `EmailUtil.java`
- `EmergencyAlertHandler.java`
- `PatientReminderService.java`
- `AddDoctorForm.java`
- `AddPatientForm.java`
- `MessageDoctorForm.java`

Replace the credentials in each file:

```java
String senderEmail = "youremail@gmail.com";
String senderPassword = "your_app_password";
```
### 🔹 6. 👨‍💻 Contributors
-Anas Norani

---
## 🎥 Demo Video

Watch the working demo of the **Remote Patient Monitoring System** here:

👉 [Click to Watch Demo Video]([https://drive.google.com/file/d/YOUR_VIDEO_ID/view?usp=sharing](https://drive.google.com/file/d/101eFHRd53TDm60aFDLEgJZ6bVbrOZwrf/view?usp=sharing))
https://drive.google.com/file/d/101eFHRd53TDm60aFDLEgJZ6bVbrOZwrf/view?usp=sharing

Let me know if you'd like:
- This as a downloadable `README.md` file
- A `.rar` with full project code fileshas been added you just have to open it in IntelliJ And have to follow the steps 
