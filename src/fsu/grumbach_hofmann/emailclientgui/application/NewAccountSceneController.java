package fsu.grumbach_hofmann.emailclientgui.application;

import java.util.ArrayList;

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
	private ArrayList<TextField> textFieldArrayList;

	public void initController(DataHandler handler, MailUtils mailUtils) {
		this.handler = handler;
		this.mailUtils = mailUtils;
		initTextFieldArrayList();
	}

	private void initTextFieldArrayList() {
		textFieldArrayList = new ArrayList();
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

		if (!isCorrectInput()) {
			return;
		}

		int inPort = Integer.parseInt(newAccInboxPortTextField.getText());
		int outPort = Integer.parseInt(newAccOutboxPortTextField.getText());

		int emailExceptionCode = mailUtils.isCorrectEmail(newAccOutboxAddressTextField.getText(), outPort,
				newAccEmailTextField.getText(), newAccPasswordTextField.getText());
		if (emailExceptionCode == 2) {
			newAccEmailTextField.setText("");
			newAccEmailTextField.setPromptText("Wrong input");
			newAccPasswordTextField.setText("");
			newAccPasswordTextField.setPromptText("Wrong input");
			return;
		} else if (emailExceptionCode == 3) {
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

	private boolean isCorrectInput() {
		boolean correct = true;
		for (TextField tf : textFieldArrayList) {
			if (tf.getText().equals("")) {
				tf.setPromptText("missing input.");
				correct = false;
			}
		}
		if (!newAccInboxPortTextField.getText().matches("[0-9]+")
				|| !newAccOutboxPortTextField.getText().matches("[0-9]+")) {
			correct = false;
		}
		return correct;
	}

}
