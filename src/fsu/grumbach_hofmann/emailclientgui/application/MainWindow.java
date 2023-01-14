package fsu.grumbach_hofmann.emailclientgui.application;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import java.awt.Font;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import fsu.grumbach_hofmann.emailclientgui.mail.MailReceiver;
import fsu.grumbach_hofmann.emailclientgui.mail.MailSender;
import fsu.grumbach_hofmann.emailclientgui.util.Account;
import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;
import fsu.grumbach_hofmann.emailclientgui.util.MailCellFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainWindow extends Application {

	private DataHandler handler;
	private MailReceiver receiver;
	private MailSender sender;
	private Account selectedAccount;
	private Scene scene;
	private Scene sendScene;
	private Scene newAccountScene;
	private MainSceneController mainSceneController;

	// fxml elements
	private ChoiceBox<String> accountsDropdown;
	private Label inboxLabel, totalMessagesLabel;
	private ListView<MailObject> messagesList;

	private AnchorPane messageDisplayPane;
	private Label senderLabel, dateLabel, subjectLabel, recipientsLabel, contentLabel;

	private Button btnReceiveMails, btnWriteMail;
	
	private MenuItem addAccountItem;

	private TextField sendToTextField, sendCopyTextField, sendSubjectTextField, sendFromTextField;
	private TextArea sendMessageTextArea;
	private Button btnSendMail;

	@Override
	public void init() {
		handler = new DataHandler();
		receiver = new MailReceiver(handler);
		sender = new MailSender();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/fsu/grumbach_hofmann/emailclientgui/application/MainScene.fxml"));
			Parent root = rootLoader.load();
			mainSceneController = rootLoader.getController();
			scene = new Scene(root, 1000, 700);
			scene.getStylesheets().add(getClass()
					.getResource("/fsu/grumbach_hofmann/emailclientgui/style/MainScene.css").toExternalForm());
			scene.getStylesheets().add(getClass()
					.getResource("/fsu/grumbach_hofmann/emailclientgui/style/DarkTheme.css").toExternalForm());
			primaryStage.setTitle("Java Mail Client");
			primaryStage.setScene(scene);
			primaryStage.show();

			FXMLLoader sendLoader = new FXMLLoader(getClass().getResource("/fsu/grumbach_hofmann/emailclientgui/application/SendScene.fxml"));
			Parent sendRoot = sendLoader.load();
			sendScene = new Scene(sendRoot);
			
			FXMLLoader newAccountLoader = new FXMLLoader(getClass().getResource("/fsu/grumbach_hofmann/emailclientgui/application/NewAccountScene.fxml"));
			Parent newAccountRoot = newAccountLoader.load();
			newAccountScene = new Scene(newAccountRoot);
			
			postInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void postInit() {
		initFXMLElements();
		initAccountList();
		initMessagesList();
	}

	private void initFXMLElements() {
		//declare elements
		inboxLabel = (Label) scene.lookup("#inboxLabel");
		totalMessagesLabel = (Label) scene.lookup("#totalMessagesLabel");
		accountsDropdown = (ChoiceBox) scene.lookup("#accountsDropdown");
		messagesList = (ListView) scene.lookup("#messagesList");

		messageDisplayPane = (AnchorPane) scene.lookup("#messageDisplayPane");
		senderLabel = (Label) scene.lookup("#senderLabel");
		dateLabel = (Label) scene.lookup("#dateLabel");
		subjectLabel = (Label) scene.lookup("#subjectLabel");
		recipientsLabel = (Label) scene.lookup("#recipientsLabel");
		contentLabel = (Label) scene.lookup("#contentLabel");

		btnReceiveMails = (Button) scene.lookup("#btnReceiveMails");
		btnWriteMail = (Button) scene.lookup("#btnWriteMail");
		
		addAccountItem = mainSceneController.getAddAccountItem();
		
		sendToTextField = (TextField) sendScene.lookup("#sendToTextField");
		sendCopyTextField = (TextField) sendScene.lookup("#sendCopyTextField");
		sendSubjectTextField = (TextField) sendScene.lookup("#sendSubjectTextField");
		sendFromTextField = (TextField) sendScene.lookup("#sendFromTextField");
		sendMessageTextArea = (TextArea) sendScene.lookup("#sendMessageTextArea");

		btnSendMail = (Button) sendScene.lookup("#btnSendMail");

		//set button listeners
		btnReceiveMails.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
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
		});

		btnWriteMail.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (selectedAccount != null) {
					Stage stage = new Stage();
					stage.setTitle("Write mail");
					stage.setScene(sendScene);
					sendFromTextField.setText(selectedAccount.getEmail());
					stage.show();
					
					//declare button image after stage is shown to determine actual button size
					Image btnSendNewMailImg = new Image("sendIcon.png");
					ImageView btnSendNewMailImgView = new ImageView(btnSendNewMailImg);
					System.out.println(btnSendMail.getHeight());
					btnSendNewMailImgView.setFitHeight(btnSendMail.getHeight());
					btnSendNewMailImgView.setPreserveRatio(true);
					btnSendMail.setGraphic(btnSendNewMailImgView);
				}
			}
		});

		btnSendMail.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				if (selectedAccount != null) {
					sender.sendMail(selectedAccount, sendSubjectTextField.getText(), sendCopyTextField.getText(),
							sendToTextField.getText(), sendFromTextField.getText(), sendMessageTextArea.getText());
				}
			}
		});

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
		
		addAccountItem.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				Stage stage = new Stage();
				stage.setTitle("Add new account");
				stage.setScene(newAccountScene);
				stage.show();
			}
		});
	}

	private void initAccountList() {
		accountsDropdown.getItems().clear();
		for (Account acc : handler.getAccountData()) {
			accountsDropdown.getItems().add(acc.getEmail());
		}
		accountsDropdown.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number indexOld, Number indexNew) {
				String accMail = (String) accountsDropdown.getItems().get((Integer) indexNew);
				for (Account acc : handler.getAccountData()) {
					if (acc.getEmail().equals(accMail)) {
						selectedAccount = acc;
						handler.loadMails(acc);
						updateMessagesList();
						// TODO: dont receive all new mails on account switch?
						inboxLabel.setText("Inbox - " + selectedAccount.getEmail() + " - receiving mails...");
						new Thread(() -> {
							receiver.receiveMails(selectedAccount);
							Platform.runLater(() -> {
								updateMessagesList();
							});
						}).start();
					}
				}
			}
		});
	}

	private void initMessagesList() {
		messagesList.setCellFactory(new MailCellFactory());
		messagesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {		
				handler.updateSeen(newSelection, selectedAccount, true);
				
				messageDisplayPane.setVisible(true);
				senderLabel.setText(newSelection.getFrom());
				dateLabel.setText(newSelection.getDateSent().toString());
				subjectLabel.setText(newSelection.getSubject());
				recipientsLabel.setText(newSelection.getTo());
				contentLabel.setText(newSelection.getContent());
		
				senderLabel.setWrapText(false);
				dateLabel.setWrapText(false);
				senderLabel.maxWidthProperty().bind(messagesList.widthProperty().divide(2));
				dateLabel.maxWidthProperty().bind(messagesList.widthProperty().divide(2));

				subjectLabel.setWrapText(false);
				subjectLabel.maxWidthProperty().bind(messagesList.widthProperty().subtract(30));

				contentLabel.setWrapText(true);
				contentLabel.maxWidthProperty().bind(messagesList.widthProperty().subtract(30));
			}
		});
		messageDisplayPane.setVisible(false);
	}

	private void updateMessagesList() {
		if (selectedAccount != null) {
			inboxLabel.setText("Inbox - " + selectedAccount.getEmail());
			totalMessagesLabel.setText(handler.getMailList(selectedAccount).size() + " messages found | "+handler.getUnseenMessageCount(selectedAccount)+" unseen.");
			messagesList.getItems().addAll(handler.getMailList(selectedAccount));

		} else {
			inboxLabel.setText("Inbox - <select an account first>");
			totalMessagesLabel.setText("No messages found");
		}

	};
}
