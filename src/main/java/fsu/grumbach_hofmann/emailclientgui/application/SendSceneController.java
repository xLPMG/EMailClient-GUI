package fsu.grumbach_hofmann.emailclientgui.application;

import fsu.grumbach_hofmann.emailclientgui.mail.MailSender;
import fsu.grumbach_hofmann.emailclientgui.util.Account;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class SendSceneController {

	@FXML
	private Button btnSendMail;

	@FXML
	private TextField sendCopyTextField;

	@FXML
	private TextField sendFromTextField;

	@FXML
	private TextArea sendMessageTextArea;

	@FXML
	private TextField sendSubjectTextField;

	@FXML
	private TextField sendToTextField;

	private Account selectedAccount;
	private MailSender sender;

	public void initController(Account selectedAccount, MailSender sender) {
		this.sender = sender;
		this.selectedAccount = selectedAccount;
	}

	public void postInit() {
		sendFromTextField.setText(selectedAccount.getEmail());
		// declare button image after stage is shown to determine actual button size
		Image btnSendNewMailImg = new Image("sendIcon.png");
		ImageView btnSendNewMailImgView = new ImageView(btnSendNewMailImg);
		System.out.println(btnSendMail.getHeight());
		btnSendNewMailImgView.setFitHeight(btnSendMail.getHeight());
		btnSendNewMailImgView.setPreserveRatio(true);
		btnSendMail.setGraphic(btnSendNewMailImgView);
	}

	@FXML
	void sendMail(ActionEvent event) {
		if (sendToTextField.getText().equals("")) {
			sendToTextField.setPromptText("missing input.");
			return;
		}
		
		//TODO: not really necessary as field is non editable
		if (sendFromTextField.getText().equals("")) {
			sendFromTextField.setPromptText("missing input.");
			return;
		}
		if (!sendFromTextField.getText().equals(selectedAccount.getEmail())) {
			sendFromTextField.setPromptText("Use your set e-mail address");
			return;
		}
		//END TODO

		if (selectedAccount != null) {
			sender.sendMail(selectedAccount, sendSubjectTextField.getText(), sendCopyTextField.getText(),
					sendToTextField.getText(), sendFromTextField.getText(), sendMessageTextArea.getText());
		}
		Stage stage = (Stage) btnSendMail.getScene().getWindow();
		stage.close();
	}
}
