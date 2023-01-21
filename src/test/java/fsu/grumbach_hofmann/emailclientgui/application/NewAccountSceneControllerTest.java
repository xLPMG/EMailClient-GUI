package fsu.grumbach_hofmann.emailclientgui.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

public class NewAccountSceneControllerTest {

	private NewAccountSceneController newAccountSceneController;

	@BeforeEach
	protected void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		newAccountSceneController = new NewAccountSceneController();
	}

	@Test
	public void testIsPortCorrect_Null() {
		assertFalse(newAccountSceneController.isPortCorrect(null));
	}

	@Test
	public void testIsPortCorrect_Empty() {
		assertFalse(newAccountSceneController.isPortCorrect(""));
	}

	@Test
	public void testIsPortCorrect_ContainsLetter() {
		assertFalse(newAccountSceneController.isPortCorrect("1122A4"));
	}

	@Test
	public void testIsPortCorrect_OnlyNumbers() {
		assertTrue(newAccountSceneController.isPortCorrect("11224"));
	}

}
