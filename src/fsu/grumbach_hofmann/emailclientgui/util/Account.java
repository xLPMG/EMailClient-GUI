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

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getInbox() {
		return inbox;
	}

	public int getInboxPort() {
		return inboxPort;
	}

	public String getOutbox() {
		return outbox;
	}

	public int getOutboxPort() {
		return outboxPort;
	}
	
	public String getData() {
	return email+"###"+password+"###"+name+"###"+surname+"###"+inbox+"###"+inboxPort+"###"+outbox+"###"+outboxPort;
	}
}
