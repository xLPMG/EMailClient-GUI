package fsu.grumbach_hofmann.emailclientgui.mail;

import java.io.IOException;
import java.security.NoSuchProviderException;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;

public class MailReceiver {

	DataHandler dH;
	
	public MailReceiver(DataHandler dH) {
		this.dH=dH;
	}
	
	public void receiveMails(String serverAddress, String serverPort, String username, String password) {
		try {

			Properties properties = new Properties();
			properties.put("mail.pop3.host", serverAddress);
			properties.put("mail.pop3.port", serverPort);
			
			Session emailSession = Session.getDefaultInstance(properties);
			// use pop3s instead of pop3 to enable ssl/tls encryption
			Store store = emailSession.getStore("pop3s");
			store.connect(serverAddress, username, password);

//			Folder[] folders = store.getDefaultFolder().list("*");
//		    for (Folder folder : folders) {
//		        if ((folder.getType() & Folder.HOLDS_MESSAGES) != 0) {
//		            System.out.println(folder.getFullName() + ": " + folder.getMessageCount());
//		        }
//		    }
			
			Folder inboxFolder = store.getFolder("INBOX");
			if (inboxFolder == null) {
				throw new Exception("Invalid folder");
			}
			inboxFolder.open(Folder.READ_ONLY);

			Message[] messages = inboxFolder.getMessages();
			System.out.println("DEBUG: Loading messages");
			int i = 1;
			for(Message m : messages) {
				System.out.println(i+"/"+messages.length);
				dH.saveMail(m);
				i++;
			}
			inboxFolder.close(false);
			store.close();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//TODO
	private void writeContent(Multipart part) throws MessagingException, IOException {
		for (int i = 0; i < part.getCount(); i++) {
			BodyPart bodyPart = part.getBodyPart(i);
			Object content = bodyPart.getContent();
			if (content instanceof Multipart) {
				writeContent((Multipart) content);
			} else if (bodyPart.getContentType().toString().contains("text/plain")) {
				System.out.println(content.toString());
			} else if (bodyPart.getContentType().toString().contains("image/jpeg")) {
				System.out.println("Warning: E-Mail contains image.");
			} else if (bodyPart.getContentType().toString().contains("application")) {
				System.out.println("Warning: E-Mail contains additional attachment.");
			}
		}
	}

}

