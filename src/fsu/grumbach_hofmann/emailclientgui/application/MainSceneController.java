package fsu.grumbach_hofmann.emailclientgui.application;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;

public class MainSceneController {

    @FXML
    private ChoiceBox<String> accountsDropdown;

    @FXML
    private Menu accountsMenuItem;

    @FXML
    private MenuItem addAccountItem;

    @FXML
    private Button btnReceiveMails;

    @FXML
    private Button btnWriteMail;

    @FXML
    private Label contentLabel;

    @FXML
    private Label dateLabel;

    @FXML
    private Label inboxLabel;

    @FXML
    private AnchorPane messageDisplayPane;

    @FXML
    private ScrollPane messageDisplayScrollPane;

    @FXML
    private Separator messageSperarator;

    @FXML
    private ListView<MailObject> messagesList;

    @FXML
    private Label recipientsLabel;

    @FXML
    private MenuItem removeAccountItem;

    @FXML
    private Label senderLabel;

    @FXML
    private Label subjectLabel;

    @FXML
    private Label toTextLabel;

    @FXML
    private Label totalMessagesLabel;

	public MenuItem getAddAccountItem() {
		return addAccountItem;
	}
 
}

