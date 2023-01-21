package fsu.grumbach_hofmann.emailclientgui.mail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fsu.grumbach_hofmann.emailclientgui.mail.MailUtils.MailCheckResult;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;

public class MailUtilsTest {

	private MailUtils mailUtils;
	
	@Mock
	private Session session;
	
	@Mock
	private Transport transport;
	
	@BeforeEach
	protected void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		mailUtils = new MailUtils(session);
		
		when(session.getTransport("smtp")).thenReturn(transport);
	}

	@Test
	public void testIsCorrectEmail_AuthenticationFailed() throws MessagingException {
		doThrow(new AuthenticationFailedException()).when(transport).connect(anyString(), anyInt(), anyString(), anyString());
		assertEquals(MailCheckResult.AUTHENTICATION_FAILED, mailUtils.isCorrectEmail("out", 1111, "email", "pass"));
	}

	@Test
	public void testIsCorrectEmail_WrongServer() throws MessagingException {
		doThrow(new MessagingException()).when(transport).connect(anyString(), anyInt(), anyString(), anyString());
		assertEquals(MailCheckResult.WRONG_SERVER, mailUtils.isCorrectEmail("out", 1111, "email", "pass"));
	}
	
	@Test
	public void testIsCorrectEmail() throws MessagingException {
		assertEquals(MailCheckResult.OK, mailUtils.isCorrectEmail("out", 1111, "email", "pass"));
	}
	@Test
	public void testGetMessageHash() {
		//throw new RuntimeException("not yet implemented");
	}

}
