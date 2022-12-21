package fsu.grumbach_hofmann.emailclientgui.mail;

import java.time.LocalDate;

public class MailObject implements Comparable<MailObject>{
	
	private String subject, read, from, to, dateSent,preview;
	private LocalDate date;

	public MailObject(String subject, String read, String from, String to, String dateSent, String preview, LocalDate date) {
		this.subject=subject;
		this.read=read;
		this.from=from;
		this.to=to;
		this.dateSent=dateSent;
		this.preview=preview;
		this.date=date;
	}
	
	  @Override
	  public int compareTo(MailObject o) {
		  if (getDate() == null || o.getDate() == null)
		      return 0;
		    return -getDate().compareTo(o.getDate());
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

	public String getDateSent() {
		return dateSent;
	}

	public void setDateSent(String dateSent) {
		this.dateSent = dateSent;
	}
	
	public String getPreview() {
		return preview;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}
	
	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}
