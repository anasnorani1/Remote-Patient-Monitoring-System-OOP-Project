import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailSender {

    private static final String FROM_EMAIL = "remotepatientmonitoringsystem1@gmail.com";
    private static final String FROM_PASSWORD = "drfwrwofmevlhgjr";

    public static boolean sendEmail(String to, String patientName, String messageBody) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, FROM_PASSWORD);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, "Remote Patient Monitoring System"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setReplyTo(InternetAddress.parse(FROM_EMAIL));
            message.setSubject("Your Appointment Update", "UTF-8");

            // Set headers to reduce spam score
            message.setHeader("X-Priority", "1");
            message.setHeader("X-Mailer", "JavaMailer");
            message.setHeader("Content-Type", "multipart/alternative");

            // Plain text (for clients that don’t support HTML)
            String plainText = "Hello " + patientName + ",\n\n"
                    + messageBody + "\n\n"
                    + "For questions, please contact us at this email.\n\n"
                    + "Regards,\nRemote Patient Monitoring System";

            // HTML version
            String htmlContent =
                    "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">" +
                            "<h2 style=\"color: #2c3e50;\">Hello " + patientName + ",</h2>" +
                            "<p style=\"font-size: 14px;\">" + messageBody.replace("\n", "<br>") + "</p>" +
                            "<p style=\"margin-top: 30px; font-size: 14px;\">For questions, reply to this email.</p>" +
                            "<hr style=\"margin-top: 30px;\">" +
                            "<p style=\"font-size: 12px; color: #888;\">© 2025 Remote Patient Monitoring System</p>" +
                            "</div>";

            // Set both plain text and HTML content
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(plainText, "utf-8");

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(htmlContent, "text/html; charset=utf-8");

            Multipart multipart = new MimeMultipart("alternative");
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
