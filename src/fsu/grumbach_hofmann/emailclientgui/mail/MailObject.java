package fsu.grumbach_hofmann.emailclientgui.mail;

public class MailObject {
	
	private String subject, read, from, to, dateSent,preview;

	public MailObject(String subject, String read, String from, String to, String dateSent, String preview) {
		this.subject=subject;
		this.read=read;
		this.from=from;
		this.to=to;
		this.dateSent=dateSent;
		this.preview=preview;
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
}
