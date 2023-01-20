package fsu.grumbach_hofmann.emailclientgui.mail;

import java.io.IOException;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.MimeMultipart;

import fsu.grumbach_hofmann.emailclientgui.util.Account;

public class MailUtils {

	public String getTextFromMessage(Message message) throws MessagingException, IOException {
		if (message.isMimeType("text/plain")) {
			return message.getContent().toString();
		}
		if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			return getTextFromMimeMultipart(mimeMultipart);
		}
		return "";
	}

	public String getTextFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/plain")) {
				return result + "\n" + bodyPart.getContent(); // without return, same text appears twice in my tests
			}
			result += this.parseBodyPart(bodyPart);
		}
		return result;
	}
	
	public String getHTMLFromMessage(Message message) throws MessagingException, IOException {
		if (message.isMimeType("text/html")) {
			return message.getContent().toString();
		}
		if (message.isMimeType("multipart/*")) {
			MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
			return getTextFromMimeMultipart(mimeMultipart);
		}
		return "";
	}

	public String getHTMLFromMimeMultipart(MimeMultipart mimeMultipart) throws MessagingException, IOException {
		String result = "";
		for (int i = 0; i < mimeMultipart.getCount(); i++) {
			BodyPart bodyPart = mimeMultipart.getBodyPart(i);
			if (bodyPart.isMimeType("text/html")) {
				return result + "\n" + bodyPart.getContent(); // without return, same text appears twice in my tests
			}
			result += this.parseHTMLBodyPart(bodyPart);
		}
		return result;
	}
	
	private String parseHTMLBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
		if (bodyPart.isMimeType("text/html")) {
			return bodyPart.getContent().toString();
		}
		if (bodyPart.getContent() instanceof MimeMultipart) {
			return getHTMLFromMimeMultipart((MimeMultipart) bodyPart.getContent());
		}

		return "";
	}

	private String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
		if (bodyPart.isMimeType("text/html")) {
			return "";
		}
		if (bodyPart.getContent() instanceof MimeMultipart) {
			return getTextFromMimeMultipart((MimeMultipart) bodyPart.getContent());
		}

		return "";
	}
	
	public int isCorrectEmail(String outboxAddress, int outboxPort, String email, String password) {
		try {
			Properties props = new Properties();
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.auth", "true");

			Session session = Session.getInstance(props, null);
			Transport transport = session.getTransport("smtp");
			transport.connect(outboxAddress, outboxPort, email, password);
			transport.close();
			return 1;
		} catch (AuthenticationFailedException ignore) {
			System.out.println("falsche email / passwort");
			return 2;
		} catch (MessagingException ignore) {
			System.out.println("falscher server");
			return 3;
		}
	}
	
	public void deleteMailFromServer(MailObject mailObject, Account account) throws Exception {
		Message messageToDelete = mailObject.getMessage();
		
		String serverAddress = account.getInbox();
		int serverPort = account.getInboxPort();
		String username = account.getUsername();
		String password = account.getPassword();
		Properties properties = new Properties();
		properties.put("mail.pop3.host", serverAddress);
		properties.put("mail.pop3.port", serverPort);
		
		Session emailSession = Session.getDefaultInstance(properties);
		Store store = emailSession.getStore("pop3s");
		store.connect(serverAddress, username, password);

		Folder[] folders = store.getDefaultFolder().list("*");
	    for (Folder folder : folders) {
	            System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
	    }
		
		Folder inboxFolder = store.getFolder("INBOX");
		if (inboxFolder == null) {
			throw new Exception("Invalid folder");
		}
		inboxFolder.open(Folder.READ_WRITE);

		Message[] messages = inboxFolder.getMessages();
		for(Message m : messages) {
			//check if m equals messageToDelete
		}
	}
}
