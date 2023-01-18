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

import fsu.grumbach_hofmann.emailclientgui.util.Account;
import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;

public class MailReceiver {

	DataHandler dH;
	
	public MailReceiver(DataHandler dH) {
		this.dH=dH;
	}
	
	public void receiveMails(Account acc) {
		try {
			String serverAddress = acc.getInbox();
			int serverPort = acc.getInboxPort();
			String username = acc.getUsername();
			String password = acc.getPassword();
			Properties properties = new Properties();
			properties.put("mail.pop3.host", serverAddress);
			properties.put("mail.pop3.port", serverPort);
			
			Session emailSession = Session.getDefaultInstance(properties);
			// use pop3s instead of pop3 to enable ssl/tls encryption
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
			inboxFolder.open(Folder.READ_ONLY);

			Message[] messages = inboxFolder.getMessages();
			System.out.println("DEBUG: Loading messages");
			int i = messages.length-1;
			while(i>0) {
				System.out.println((messages.length-i)+"/"+messages.length);
				dH.saveMail(messages[i], username, false);
				i--;
			}
			dH.loadMails(acc);
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
}

