package fsu.grumbach_hofmann.emailclientgui.mail;

import java.util.Properties;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import fsu.grumbach_hofmann.emailclientgui.util.Account;

public class MailSender {
	private String serverAddress;
	private int serverPort;
	private String username;
	private String password;
	private Session session;

	public void sendMail(Account acc, String subject, String copy, String to, String from, String messageContent) {
		serverAddress = acc.getOutbox();
		serverPort = acc.getOutboxPort();
		username = acc.getUsername();
		password = acc.getPassword();

		try {
			Properties properties = System.getProperties();
			properties.put("mail.smtp.host", serverAddress);
			properties.put("mail.smtp.port", serverPort);
			properties.put("mail.smtp.starttls.enable", "true");
			properties.put("mail.smtp.auth", "true");
			properties.put("mail.smtp.ssl.trust", serverAddress);

			session = Session.getInstance(properties, new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password);
				}
			});

			MimeMessage message = new MimeMessage(session);

			String[] recipients = to.split(",");
			for (String recipient : recipients) {
				message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			}

			String[] recipientsCopy = copy.split(",");
			for (String recipient : recipientsCopy) {
				if (!recipient.equals("")) {
					message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
				}
			}

			message.setFrom(new InternetAddress(from));
			message.setSubject(subject);

			message.setText(messageContent);

			Transport.send(message);
			System.out.println("message sent");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
