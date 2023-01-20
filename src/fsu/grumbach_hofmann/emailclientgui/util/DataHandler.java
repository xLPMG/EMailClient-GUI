package fsu.grumbach_hofmann.emailclientgui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import fsu.grumbach_hofmann.emailclientgui.mail.MailUtils;

public class DataHandler {

	String os = "win";

	private ArrayList<Account> accountList = new ArrayList<>();
	private HashMap<Account, ArrayList> mailListMap = new HashMap<>();
	private HashMap<Account, Integer> unseenMessageCount = new HashMap<Account, Integer>();

	private File programFolder;
	private File userData;
	private File emailFolder;
	private AES aes;
	private String keycode;
	private MailUtils mU;

	public DataHandler() {
		os = System.getProperty("os.name").toLowerCase();
		init();
		loadAccountData();
		loadAllMails();
	}

	private void init() {
		mU = new MailUtils();
		if (os.contains("win")) {
			programFolder = new File(System.getenv("APPDATA") + "/JEMC-GrumHofm");
			if (!programFolder.exists()) {
				programFolder.mkdirs();
			}
			userData = new File(System.getenv("APPDATA") + "/JEMC-GrumHofm/userData.dat");
			if (!userData.exists()) {
				try {
					userData.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			emailFolder = new File(System.getenv("APPDATA") + "/JEMC-GrumHofm/emails");
			if (!emailFolder.exists()) {
				emailFolder.mkdirs();
			}
		} else if (os.contains("mac")) {
			programFolder = new File(System.getProperty("user.home", "."),
					"Library/Application Support/" + "JEMC-GrumHofm");
			if (!programFolder.exists()) {
				programFolder.mkdirs();
			}
			userData = new File(System.getProperty("user.home", "."),
					"Library/Application Support/JEMC-GrumHofm/userData.dat");
			if (!userData.exists()) {
				try {
					userData.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			emailFolder = new File(System.getProperty("user.home", "."),
					"Library/Application Support/JEMC-GrumHofm/emails");
			if (!emailFolder.exists()) {
				emailFolder.mkdirs();
			}
		}

		aes = new AES();
		try {
			keycode = getKeycode();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getKeycode() throws IOException {
		String salt = "u8IkY=Tnij?S";

		if (os.contains("win")) {
			String command = "wmic csproduct get UUID";
			StringBuffer output = new StringBuffer();

			Process SerNumProcess = Runtime.getRuntime().exec(command);
			BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));

			String line = "";
			while ((line = sNumReader.readLine()) != null) {
				output.append(line + "\n");
			}
			String MachineID = output.toString().substring(output.indexOf("\n"), output.length()).trim();
			try {
				SerNumProcess.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return MachineID + salt;
		} else if (os.contains("mac")) {
			String command = "system_profiler SPHardwareDataType | awk '/UUID/ { print $3; }'";
			StringBuffer output = new StringBuffer();

			Process SerNumProcess = Runtime.getRuntime().exec(command);
			BufferedReader sNumReader = new BufferedReader(new InputStreamReader(SerNumProcess.getInputStream()));

			String line = "";
			while ((line = sNumReader.readLine()) != null) {
				output.append(line + "\n");
			}

			String MachineID = output.toString().substring(output.indexOf("UUID: "), output.length()).replace("UUID: ",
					"");
			try {
				SerNumProcess.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			sNumReader.close();
			return MachineID + salt;
		}
		return salt + salt + salt;

	}

	private void loadAccountData() {
		try {
			accountList.clear();
			BufferedReader br = new BufferedReader(new FileReader(userData.getAbsolutePath()));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.isEmpty()) {
					String decodedLine = aes.decode(line, keycode);
					String[] data = decodedLine.split("[,]");
					String[] values = data[1].split("###");
					addAccount(data[0], values[0], values[1], values[2], values[3], values[4],
							Integer.parseInt(values[5]), values[6], Integer.parseInt(values[7]));
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveAccountData() {
		try {
			FileWriter writer = new FileWriter(userData.getAbsolutePath());
			writer.write("");
			for (Account acc : accountList) {
				String key = acc.getUsername();
				String value = acc.getData();
				writer.write(aes.encode(key + "," + value, keycode));
				writer.write(System.getProperty("line.separator"));
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Account> getAccountData() {
		return accountList;
	}

	public void addAccount(String username, String email, String password, String name, String surname, String serverIN,
			int portIN, String serverOUT, int portOUT) {
		for (Account acc : accountList) {
			if (acc.getUsername().equals(username)) {
				return;
			}
		}
		Account newAcc = new Account(username, email, password, name, surname, serverIN, portIN, serverOUT, portOUT);
		accountList.add(newAcc);
		ArrayList<MailObject> mailList = new ArrayList<MailObject>();
		mailListMap.put(newAcc, mailList);
		saveAccountData();
	}

	public void removeAccount(String username) {
		for (Account acc : accountList) {
			if (acc.getUsername().equals(username)) {
				removeAccount(acc);
			}
		}
	}

	public void removeAccount(Account account) {
		deleteAccountData(account);
		saveAccountData();
	}

	public Account getAccount(String username) {
		for (Account acc : accountList) {
			if (acc.getUsername().equals(username)) {
				return acc;
			}
		}
		return null;
	}

	private void deleteAccountData(Account account) {
		deleteMails(account);
		mailListMap.remove(account);
		accountList.remove(account);
	}
	
	public void saveMail(Message message, String username, boolean force) {
		try {
			String fileNameRaw="NoSender";
			if(message.getFrom()!=null) {
				fileNameRaw = message.getSentDate() + "-" + message.getFrom()[0] + ".eml";
			}
			String fileName = fileNameRaw.replace(" ", "_").replace("/", "|");
			File userSubdirectory = new File(emailFolder.getAbsolutePath() + "/" + username);
			if (!userSubdirectory.exists()) {
				userSubdirectory.mkdirs();
			}
			File mailFile = new File(userSubdirectory.getAbsolutePath() + "/" + fileName);
			if (force) {
				message.writeTo(new FileOutputStream(mailFile));
			} else {
				if (!mailFile.exists()) {
					message.writeTo(new FileOutputStream(mailFile));
				}
			}
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
	}

	public void loadMails(Account acc) {
		ArrayList<MailObject> mailList = mailListMap.get(acc);
		mailList.clear();
		unseenMessageCount.put(acc, 0);
		File userSubdirectory = new File(emailFolder.getAbsolutePath() + "/" + acc.getUsername());
		if (!userSubdirectory.exists()) {
			userSubdirectory.mkdirs();
			return;
		}

		for (File mailFile : userSubdirectory.listFiles()) {
			if (!mailFile.isDirectory()) {

				Properties properties = new Properties();
				properties.put("mail.pop3.host", "");
				properties.put("mail.pop3.port", "");

				Session emailSession = Session.getDefaultInstance(properties);

				try {
					InputStream source = new FileInputStream(mailFile);
					MimeMessage m = new MimeMessage(emailSession, source);
					boolean isSeen = isMessageSeen(m, acc);;
					MailObject newMO = new MailObject(m, mU, isSeen);
					mailList.add(newMO);
					if (!isSeen) {
						unseenMessageCount.put(acc, unseenMessageCount.getOrDefault(acc, 0) + 1);
					}
				} catch (MessagingException | IOException e) {
					e.printStackTrace();
				}
			}
		}
		Collections.sort(mailList);
		mailListMap.put(acc, mailList);
	}
	
	private void deleteMails(Account account) {
		File userSubdirectory = new File(emailFolder.getAbsolutePath() + "/" + account.getUsername());
		if (!userSubdirectory.exists()) {
			return;
		}

		for (File mailFile : userSubdirectory.listFiles()) {
			if (!mailFile.isDirectory()) {
				mailFile.delete();
			}
		}
		userSubdirectory.delete();
	}
	
	public void deleteMail(MailObject mailObject, Account account) {
		Message message = mailObject.getMessage();
		try {
			String fileNameRaw="NoSender";
			if(message.getFrom()!=null) {
				fileNameRaw = message.getSentDate() + "-" + message.getFrom()[0] + ".eml";
			}
			String fileName = fileNameRaw.replace(" ", "_").replace("/", "|");
			File userSubdirectory = new File(emailFolder.getAbsolutePath() + "/" + account.getUsername());
			if (!userSubdirectory.exists()) {
				return;
			}
			File mailFile = new File(userSubdirectory.getAbsolutePath() + "/" + fileName);
			if(mailFile.exists()) {
				mailFile.delete();
			}
			mailListMap.get(account).remove(mailObject);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}

	private boolean isMessageSeen(Message message, Account account) {
		try {
			String[] seenHeader = message.getHeader("Seen");
			return seenHeader[0].equalsIgnoreCase("true") ? true : false;
		} catch (Exception e) {
			try {
				message.addHeader("Seen", "false");
				saveMail(message, account.getUsername(), true);
				return false;
			} catch (MessagingException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}

	public void updateSeen(MailObject mailObject, Account account, boolean seen) {
		Message message = mailObject.getMessage();
		if (isMessageSeen(message, account) != seen) {
			try {
				mailObject.setSeen(seen);
				message.setHeader("Seen", seen + "");
				saveMail(message, account.getUsername(), true);
				if (seen) {
					unseenMessageCount.put(account, unseenMessageCount.getOrDefault(account, 1) - 1);
				}
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
	}

	public void loadAllMails() {
		for (Account account : accountList) {
			loadMails(account);
		}

	}

	public ArrayList<MailObject> getMailList(Account account) {
		return mailListMap.get(account);
	}

	public int getMailsCount(Account account) {
		return mailListMap.get(account).size();
	}

	public int getUnseenMessageCount(Account account) {
		return unseenMessageCount.getOrDefault(account, 0);
	}

}
