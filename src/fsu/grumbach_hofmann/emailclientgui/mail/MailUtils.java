package fsu.grumbach_hofmann.emailclientgui.mail;

import java.io.IOException;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

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
}
