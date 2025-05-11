import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Properties;

public class PatientReminderService {

    private static final String FROM_EMAIL = "remotepatientmonitoringsystem1@gmail.com"; // Replace with your email
    private static final String PASSWORD = "drfwrwofmevlhgjr";      // App-specific password

    public static void sendEmail(String toEmail, String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Hospital Management"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);

            // Debugging statement
            System.out.println("[DEBUG] Email sent to: " + toEmail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String composeBody(String patientName) {
        return "Dear " + patientName + ",\n\n"
                + "This is a reminder of your appointment scheduled for today.\n"
                + "Please arrive 10–15 minutes early and bring your documents.\n\n"
                + "If you need to reschedule, contact us.\n\n"
                + "Regards,\n"
                + "Hospital Management Team\n"
                + "Email: remotepatientmonitoringsystem1@gmail.com\n"
                + "Phone: +92 320 806 8311";
    }
}
