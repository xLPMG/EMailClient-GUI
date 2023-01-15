package fsu.grumbach_hofmann.emailclientgui.application;

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

	public void initController(DataHandler handler) {
		this.handler = handler;
	}

	@FXML
	void addAccount(ActionEvent event) {
		// TODO: check remaining inputs
		if (!newAccInboxPortTextField.getText().matches("[0-9]+")
				|| !newAccOutboxPortTextField.getText().matches("[0-9]+")) {
			return;
		}
		int inPort = Integer.parseInt(newAccInboxPortTextField.getText());
		int outPort = Integer.parseInt(newAccOutboxPortTextField.getText());
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

}
