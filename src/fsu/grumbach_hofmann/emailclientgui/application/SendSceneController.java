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
    	this.sender=sender;
    	this.selectedAccount = selectedAccount;
    }
    
    public void postInit() {
		sendFromTextField.setText(selectedAccount.getEmail());
		//declare button image after stage is shown to determine actual button size
		Image btnSendNewMailImg = new Image("sendIcon.png");
		ImageView btnSendNewMailImgView = new ImageView(btnSendNewMailImg);
		System.out.println(btnSendMail.getHeight());
		btnSendNewMailImgView.setFitHeight(btnSendMail.getHeight());
		btnSendNewMailImgView.setPreserveRatio(true);
		btnSendMail.setGraphic(btnSendNewMailImgView);
    }
    
    @FXML
    void sendMail(ActionEvent event) {
    	//TODO: input check
    	int valueFrom = checkSendFrom();
    	int valueTo = checkSendTo();
    	if(valueFrom==0) {
    		sendFromTextField.setPromptText("missing input.");
    		return;
    	}
    	if(valueTo==0) {
    		sendToTextField.setPromptText("missing input.");
    		return;
    	}
    	if(valueTo==1) {
    		sendToTextField.setPromptText("set back to account address!"); //oder man setzt send from to noteditable
    	}
    	
    	if (selectedAccount != null) {
			sender.sendMail(selectedAccount, sendSubjectTextField.getText(), sendCopyTextField.getText(),
					sendToTextField.getText(), sendFromTextField.getText(), sendMessageTextArea.getText());
		}
    	Stage stage = (Stage) btnSendMail.getScene().getWindow();
	    stage.close();
    }

 int checkSendTo() {
		// TODO Auto-generated method stub
		if( sendToTextField.getText() == ""){
			return 0;
		}else {
			return 1;
		}
	}


int checkSendFrom() {
	// TODO Auto-generated method stub
	if( sendFromTextField.getText() == ""){
		return 0;
	}else if(sendFromTextField.getText() != selectedAccount.getEmail()){
		return 1;
	}else {
		return 2; 
	}
}
}

