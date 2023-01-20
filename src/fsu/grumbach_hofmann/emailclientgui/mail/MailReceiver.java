package fsu.grumbach_hofmann.emailclientgui.mail;

import java.security.NoSuchProviderException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import fsu.grumbach_hofmann.emailclientgui.util.Account;
import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;

public class MailReceiver {

	private DataHandler handler;
	private HashMap<Account, Boolean> isLockedMap;
	
	public MailReceiver(DataHandler handler) {
		this.handler=handler;
		isLockedMap=new HashMap<>();
	}
	
	public void receiveMails(Account acc, int n) {
		if(!isLockedMap.containsKey(acc)) {
			isLockedMap.put(acc, true);
		}else {
			if(isLockedMap.get(acc)) {
				return;
			}else {
				isLockedMap.put(acc, true);
			}
		}
		long startTime = System.currentTimeMillis();
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
			Folder inboxFolder = store.getFolder("INBOX");
			if (inboxFolder == null) {
				throw new Exception("Invalid folder");
			}
			inboxFolder.open(Folder.READ_ONLY);

			Message[] messages = inboxFolder.getMessages();
			System.out.println("DEBUG: Loading messages");
			saveMails(messages, acc, n);
			handler.loadMails(acc);
			inboxFolder.close();
			store.close();
			
			long endTime = System.currentTimeMillis();
			long execTime = endTime-startTime;
			long execTimeMessageM;
			if(n==-1) {
				execTimeMessageM = execTime/messages.length;
			}else {
				execTimeMessageM = execTime/n;
			}
			DecimalFormat df = new DecimalFormat("##.####");
			float execTimeMessageS = (float)execTimeMessageM/1000;
		     System.out.println(acc.getUsername()+": Execution time per message: " + execTimeMessageM + "ms ("+df.format(execTimeMessageS)+"s)"); 
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		isLockedMap.put(acc, false);
	}
	
	private void saveMails(Message[] messages, Account account, int n) {
		int i = messages.length-1;
		int max = n;
		int limit = i-n;
		if(limit<0 || n==-1) {
			limit=0;
			max=i;
		}
		while(i>limit) {
			int perc = (int)(((float)(messages.length-i)/(float)(max))*100);
			System.out.println(account.getUsername()+":"+perc+"%");
			handler.saveMail(messages[i], account.getUsername(), false);
			i--;
		}
	}

}

