package fsu.grumbach_hofmann.emailclientgui.mail;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

public class MailObject implements Comparable<MailObject> {

	private String subject, from, to, content, preview, html;
	private LocalDateTime dateSent;
	private Message message;
	private boolean seen;

	public MailObject(Message message, MailUtils mailUtils, boolean seen) {
		try {
			this.message = message;
			this.seen = seen;
			subject = message.getSubject();
			Address[] froms = message.getFrom();
			Address[] tos = message.getAllRecipients();
			from = froms == null ? null : ((InternetAddress) froms[0]).getAddress();
			to = "";
			if (tos != null) {
				for (Address toAdd : tos) {
					to += tos == null ? null : ((InternetAddress) toAdd).getAddress();
				}
			} else {
				to = "???";
			}
			content = mailUtils.getTextFromMessage(message);
			html = mailUtils.getHTMLFromMessage(message);
			preview = content.substring(0, Math.min(content.length(), 100)).replace("\n", " ").replace("\r", " ")+"...";
			if(message.getSentDate()!=null) {
				dateSent = Instant.ofEpochMilli(message.getSentDate().getTime()).atZone(ZoneId.systemDefault())
						.toLocalDateTime();
			}else {
				dateSent = null;
			}
			
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(MailObject o) {
		if (getDateSent() == null || o.getDateSent() == null)
			//TODO: check if this is a correct implementation
			return 1;
		return -getDateSent().compareTo(o.getDateSent());
	}

	public String getSubject() {
		return subject==null ? "Unknown subject" : subject;
	}

	public String getFrom() {
		return from==null ? "Unknown sender" : from;
	}

	public String getTo() {
		return to==null ? "Unknown recipient" : to;
	}

	public LocalDateTime getDateSent() {
		return dateSent;
	}

	public String getPreview() {
		return preview==null ? "No preview available" : preview;
	}
	
	public String getContent() {
		return content==null ? "" : content;
	}
	
	public String getHtml() {
		return html==null ? "No html available" : html;
	}
	
	public Message getMessage() {
		return message;
	}
	
	public boolean isSeen() {
		return seen;
	}
	
	public void setSeen(boolean seen) {
		this.seen=seen;
	}
}
