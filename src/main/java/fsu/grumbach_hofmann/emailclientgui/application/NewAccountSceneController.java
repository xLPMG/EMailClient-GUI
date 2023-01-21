package fsu.grumbach_hofmann.emailclientgui.application;

import java.util.ArrayList;
import java.util.List;

import fsu.grumbach_hofmann.emailclientgui.mail.MailUtils;
import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class NewAccountSceneController {

	@FXML
	private Button newAccAddButton;

	@FXML
	private Button newAccCancelButton;

	@FXML
	private TextField newAccEmailTextField;

	@FXML
	private TextField newAccInboxAddressTextField;

	@FXML
	private TextField newAccInboxPortTextField;

	@FXML
	private TextField newAccNameTextField;

	@FXML
	private TextField newAccOutboxAddressTextField;

	@FXML
	private TextField newAccOutboxPortTextField;

	@FXML
	private TextField newAccPasswordTextField;

	@FXML
	private TextField newAccSurnameTextField;

	@FXML
	private TextField newAccUsernameTextField;

	private DataHandler handler;
	private MailUtils mailUtils;
	private List<TextField> textFieldArrayList;

	public void initController(DataHandler handler, MailUtils mailUtils) {
		this.handler = handler;
		this.mailUtils = mailUtils;
		initTextFieldArrayList();
	}

	private void initTextFieldArrayList() {
		textFieldArrayList = new ArrayList<>();
		textFieldArrayList.add(newAccEmailTextField);
		textFieldArrayList.add(newAccInboxAddressTextField);
		textFieldArrayList.add(newAccInboxPortTextField);
		textFieldArrayList.add(newAccNameTextField);
		textFieldArrayList.add(newAccOutboxAddressTextField);
		textFieldArrayList.add(newAccOutboxPortTextField);
		textFieldArrayList.add(newAccPasswordTextField);
		textFieldArrayList.add(newAccSurnameTextField);
		textFieldArrayList.add(newAccUsernameTextField);
	}

	@FXML
	void addAccount(ActionEvent event) {

		if (!checkMandatoryFields(textFieldArrayList) || isPortCorrect(newAccInboxPortTextField.getText())) {
			return;
		}

		int inPort = Integer.parseInt(newAccInboxPortTextField.getText());
		int outPort = Integer.parseInt(newAccOutboxPortTextField.getText());

		MailUtils.MailCheckResult result = mailUtils.isCorrectEmail(newAccOutboxAddressTextField.getText(), outPort,
				newAccEmailTextField.getText(), newAccPasswordTextField.getText());
		if (result == MailUtils.MailCheckResult.AUTHENTICATION_FAILED) {
			newAccEmailTextField.setText("");
			newAccEmailTextField.setPromptText("Wrong input");
			newAccPasswordTextField.setText("");
			newAccPasswordTextField.setPromptText("Wrong input");
			return;
		} else if (result == MailUtils.MailCheckResult.WRONG_SERVER) {
			newAccOutboxAddressTextField.setText("");
			newAccOutboxAddressTextField.setPromptText("Wrong input");
			newAccOutboxPortTextField.setText("");
			newAccOutboxPortTextField.setPromptText("Wrong input");
			return;
		}

		handler.addAccount(newAccUsernameTextField.getText(), newAccEmailTextField.getText(),
				newAccPasswordTextField.getText(), newAccNameTextField.getText(), newAccSurnameTextField.getText(),
				newAccInboxAddressTextField.getText(), inPort, newAccOutboxAddressTextField.getText(), outPort);
		Stage stage = (Stage) newAccAddButton.getScene().getWindow();
		stage.close();
	}

	@FXML
	void cancelAction(ActionEvent event) {
		Stage stage = (Stage) newAccCancelButton.getScene().getWindow();
		stage.close();
	}

	boolean checkMandatoryFields(List<TextField> fields) {
		boolean correct = true;
		for (TextField tf : fields) {
			if (tf.getText().equals("")) {
				tf.setPromptText("missing input.");
				correct = false;
			}
		}
		return correct;
	}
	
	boolean isPortCorrect(String port)
	{
		if (port != null) 
		{
			return port.matches("[0-9]+");
		}
		return false;
	}

}
