package fsu.grumbach_hofmann.emailclientgui.mail;

import java.time.LocalDate;

public class MailObject {
	
	private String subject, read, from, to;
	private LocalDate dateSent;

	public MailObject(String subject, String read, String from, String to, LocalDate dateSent) {
		this.subject=subject;
		this.read=read;
		this.from=from;
		this.to=to;
		this.dateSent=dateSent;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getRead() {
		return read;
	}

	public void setRead(String read) {
		this.read = read;
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
}
