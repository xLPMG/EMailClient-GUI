package fsu.grumbach_hofmann.emailclientgui.util;

public class Account {
	private String username;
	private String password;
	private String email;
	private String name;
	private String surname;
	private String inbox;
	private int inboxPort;
	private String outbox;
	private int outboxPort;

	public Account(String username, String email, String password, String name, String surname, String inbox,
			int inboxPort, String outbox, int ouboxPort) {
		this.username=username;
		this.password=password;
		this.email=email;
		this.name=name;
		this.surname=surname;
		this.inbox=inbox;
		this.inboxPort=inboxPort;
		this.outbox=outbox;
		this.outboxPort=ouboxPort;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getInbox() {
		return inbox;
	}

	public void setInbox(String inbox) {
		this.inbox = inbox;
	}

	public int getInboxPort() {
		return inboxPort;
	}

	public void setInboxPort(int inboxPort) {
		this.inboxPort = inboxPort;
	}

	public String getOutbox() {
		return outbox;
	}

	public void setOutbox(String outbox) {
		this.outbox = outbox;
	}

	public int getOutboxPort() {
		return outboxPort;
	}

	public void setOutboxPort(int outboxPort) {
		this.outboxPort = outboxPort;
	}
	
	public String getData() {
	return email+"###"+password+"###"+name+"###"+surname+"###"+inbox+"###"+inboxPort+"###"+outbox+"###"+outboxPort;
	}
}
