package fsu.grumbach_hofmann.emailclientgui.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;

public class DataHandler {

	String os = "win";

	private ArrayList<Account> accountList = new ArrayList<>();
	private ArrayList<Message> emailList = new ArrayList<>();
	private ArrayList<MailObject> mailObjectList = new ArrayList<>();
	private File programFolder;
	private File userData;
	private File emailFolder;
	private AES aes;
	private String keycode;

	public DataHandler() {
		os = System.getProperty("os.name").toLowerCase();
		init();
		loadAccountData();
		loadMails();
	}

	private void init() {
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
		accountList.add(new Account(username, email, password, name, surname, serverIN, portIN, serverOUT, portOUT));
		saveAccountData();
	}

	public void removeAccount(String username) {
		for (Account acc : accountList) {
			if (acc.getUsername().equals(username)) {
				accountList.remove(acc);
			}
		}
		saveAccountData();
	}

	public void saveMail(Message message) {
		try {
			String fileNameRaw = message.getSentDate() + "-" + message.getFrom()[0] + ".eml";
			String fileName = fileNameRaw.replace(" ", "_").replace("/", "|");
			File mailFile = new File(emailFolder.getAbsolutePath() + "/" + fileName);
			if (!mailFile.exists()) {
				message.writeTo(new FileOutputStream(mailFile));
			}
		} catch (IOException | MessagingException e) {
			e.printStackTrace();
		}
		loadMails();
	}

	public void loadMails() {
		mailObjectList.clear();
		emailList.clear();
		for (File mailFile : emailFolder.listFiles()) {
			if (!mailFile.isDirectory()) {
				Properties properties = new Properties();
				properties.put("mail.pop3.host", "");
				properties.put("mail.pop3.port", "");

				Session emailSession = Session.getDefaultInstance(properties);

				try {
					InputStream source = new FileInputStream(mailFile);
					MimeMessage m = new MimeMessage(emailSession, source);
					emailList.add(m);
					// create mail objects
					Address[] froms = m.getFrom();
					Address[] tos = m.getAllRecipients();
					String from = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
					String recipients = "";
					if (tos != null) {
						for (Address to : tos) {
							recipients += tos == null ? null : ((InternetAddress) to).getAddress();
						}
					} else {
						recipients = "???";
					}
					if (m.getSentDate() != null) {
						mailObjectList.add(new MailObject(m.getSubject(), "no", from, recipients,
								m.getSentDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()));
					}

				} catch (MessagingException | FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public ArrayList<Message> getEmailList() {
		return emailList;
	}

	public void setEmailList(ArrayList<Message> emailList) {
		this.emailList = emailList;
	}

	public ArrayList<MailObject> getMailObjectList() {
		return mailObjectList;
	}

}
