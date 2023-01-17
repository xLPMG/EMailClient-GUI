package fsu.grumbach_hofmann.emailclientgui.application;

import java.io.IOException;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import fsu.grumbach_hofmann.emailclientgui.mail.MailReceiver;
import fsu.grumbach_hofmann.emailclientgui.mail.MailSender;
import fsu.grumbach_hofmann.emailclientgui.util.Account;
import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;
import fsu.grumbach_hofmann.emailclientgui.util.MailCellFactory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
	private SplitPane splitPane;

	@FXML
	private Label subjectLabel;

	@FXML
	private Label toTextLabel;

	@FXML
	private Label totalMessagesLabel;

	private DataHandler handler;
	private MailReceiver receiver;
	private MailSender sender;
	private Account selectedAccount;
	private NewAccountSceneController newAccountSceneController;
	private SendSceneController sendSceneController;

	public void initController(DataHandler handler, MailReceiver receiver, MailSender sender) {
		this.handler = handler;
		this.receiver = receiver;
		this.sender = sender;
	}

	public void postInitController() {
		initAccountList();
		initMessagesList();

		Image btnReceiveMailsImg = new Image("receiveIcon.png");
		ImageView btnReceiveMailsImgView = new ImageView(btnReceiveMailsImg);
		btnReceiveMailsImgView.setFitHeight(btnReceiveMails.getHeight());
		btnReceiveMailsImgView.setPreserveRatio(true);

		Image btnWriteNewMailImg = new Image("writeIcon.png");
		ImageView btnWriteNewMailImgView = new ImageView(btnWriteNewMailImg);
		btnWriteNewMailImgView.setFitHeight(btnWriteMail.getHeight());
		btnWriteNewMailImgView.setPreserveRatio(true);

		btnReceiveMails.setGraphic(btnReceiveMailsImgView);
		btnWriteMail.setGraphic(btnWriteNewMailImgView);
	}

	@FXML
	void addAccount(ActionEvent event) {
		try {
			FXMLLoader newAccountLoader = new FXMLLoader(
					getClass().getResource("/view/NewAccountScene.fxml"));
			Parent newAccountRoot = newAccountLoader.load();
			newAccountSceneController = newAccountLoader.getController();
			newAccountSceneController.initController(handler);
			Scene newAccountScene = new Scene(newAccountRoot);

			Stage stage = new Stage();
			stage.setTitle("Add new account");
			stage.setScene(newAccountScene);
			stage.show();
			// update accounts menu after closing window
			stage.setOnHidden(e -> {
				updateAccountsList();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void removeAccount(ActionEvent event) {
		handler.removeAccount(selectedAccount);
		updateAccountsList();

		selectedAccount = null;
		updateMessagesList();
		messageDisplayPane.setVisible(false);
	}

	@FXML
	void receiveMails(ActionEvent event) {
		if (selectedAccount == null)
			return;
		handler.loadMails(selectedAccount);
		inboxLabel.setText("Inbox - " + selectedAccount.getEmail() + " - receiving mails...");
		new Thread(() -> {
			receiver.receiveMails(selectedAccount);
			Platform.runLater(() -> {
				updateMessagesList();
			});
		}).start();
	}

	@FXML
	void writeMail(ActionEvent event) {
		if (selectedAccount == null)
			return;
		try {
			FXMLLoader sendLoader = new FXMLLoader(
					getClass().getResource("/view/SendScene.fxml"));
			Parent sendRoot = sendLoader.load();
			sendSceneController = sendLoader.getController();
			sendSceneController.initController(selectedAccount, sender);
			Scene sendScene = new Scene(sendRoot);
			Stage stage = new Stage();
			stage.setTitle("Write mail");
			stage.setScene(sendScene);
			stage.show();
			sendSceneController.postInit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void initMessagesList() {
		messagesList.setCellFactory(new MailCellFactory());
		messagesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				if (selectedAccount == null)
					return;
				handler.updateSeen(newSelection, selectedAccount, true);
				updateUnseenMessageCount();

				messageDisplayPane.setVisible(true);
				senderLabel.setText(newSelection.getFrom());
				dateLabel.setText(newSelection.getDateSent().toString());
				subjectLabel.setText(newSelection.getSubject());
				recipientsLabel.setText(newSelection.getTo());
				contentLabel.setText(newSelection.getContent());
			}
		});
		messageDisplayPane.setVisible(false);
	}

	private void initAccountList() {
		accountsDropdown.getItems().clear();
		for (Account acc : handler.getAccountData()) {
			accountsDropdown.getItems().add(acc.getEmail());
		}
		accountsDropdown.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number indexOld, Number indexNew) {
				if ((Integer) indexNew==-1 || accountsDropdown.getItems().get((Integer) indexNew) == null) {
						return;
				} else {
					String accMail = (String) accountsDropdown.getItems().get((Integer) indexNew);
					for (Account acc : handler.getAccountData()) {
						if (acc.getEmail().equals(accMail)) {
							selectedAccount = acc;
							handler.loadMails(acc);
							updateMessagesList();
							// TODO: dont receive all new mails on account switch?
//							inboxLabel.setText("Inbox - " + selectedAccount.getEmail() + " - receiving mails...");
//							new Thread(() -> {
//								receiver.receiveMails(selectedAccount);
//								Platform.runLater(() -> {
//									updateMessagesList();
//								});
//							}).start();
						}
					}
				}
			}
		});
	}

	private void updateAccountsList() {
		accountsDropdown.getItems().clear();
		for (Account acc : handler.getAccountData()) {
			accountsDropdown.getItems().add(acc.getEmail());
			if(acc==selectedAccount) {
				accountsDropdown.setValue(selectedAccount.getEmail());
			}
		}
	}

	public void updateMessagesList() {
		if (selectedAccount != null) {
			inboxLabel.setText("Inbox - " + selectedAccount.getEmail());
			updateUnseenMessageCount();
			messagesList.getItems().clear();
			messagesList.getItems().addAll(handler.getMailList(selectedAccount));
		} else {
			inboxLabel.setText("Inbox - <select an account first>");
			totalMessagesLabel.setText("No messages found");
			messagesList.getItems().clear();
		}

	};

	private void updateUnseenMessageCount() {
		totalMessagesLabel.setText(handler.getMailsCount(selectedAccount) + " messages found | "
				+ handler.getUnseenMessageCount(selectedAccount) + " unseen.");
	}

}
