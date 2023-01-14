package fsu.grumbach_hofmann.emailclientgui.mail;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import fsu.grumbach_hofmann.emailclientgui.util.MailUtils;

public class MailObject implements Comparable<MailObject> {

	private String subject, from, to, content, preview, html;
	private LocalDate dateSent;
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
			preview = content.substring(0, Math.min(content.length(), 100)).replace("\n", " ").replace("\r", " ");
			dateSent = Instant.ofEpochMilli(message.getSentDate().getTime()).atZone(ZoneId.systemDefault())
					.toLocalDate();
		} catch (MessagingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int compareTo(MailObject o) {
		if (getDateSent() == null || o.getDateSent() == null)
			return 0;
		return -getDateSent().compareTo(o.getDateSent());
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public LocalDate getDateSent() {
		return dateSent;
	}

	public void setDateSent(LocalDate dateSent) {
		this.dateSent = dateSent;
	}

	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
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
