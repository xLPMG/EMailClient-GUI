package fsu.grumbach_hofmann.emailclientgui.application;

import java.io.IOException;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import fsu.grumbach_hofmann.emailclientgui.mail.MailReceiver;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class MainWindow extends Application {

	private DataHandler handler;
	private MailReceiver receiver;
	private Account selectedAccount;
	private Scene scene;

	// fxml elements
	private ChoiceBox<String> accountsDropdown;
	private Label inboxLabel, totalMessagesLabel;
	private ListView<MailObject> messagesList;

	private AnchorPane messageDisplayPane;
	private Label senderLabel, dateLabel, subjectLabel, recipientsLabel, contentLabel;

	private Button btnReceiveMails, btnWriteMail;

	@Override
	public void init() {
		handler = new DataHandler();
		receiver = new MailReceiver(handler);
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader
					.load(getClass().getResource("/fsu/grumbach_hofmann/emailclientgui/application/MainScene.fxml"));
			scene = new Scene(root);
			scene.getStylesheets().add(getClass()
					.getResource("/fsu/grumbach_hofmann/emailclientgui/style/MainScene.css").toExternalForm());
			scene.getStylesheets().add(getClass()
					.getResource("/fsu/grumbach_hofmann/emailclientgui/style/DarkTheme.css").toExternalForm());
			primaryStage.setTitle("Java Mail Client");
			primaryStage.setScene(scene);
			primaryStage.show();

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

		btnReceiveMails.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if(selectedAccount==null)
					return;
				inboxLabel.setText("Inbox - " + selectedAccount.getEmail()+" - receiving mails...");
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
		        try {
		        	Parent root = FXMLLoader
							.load(getClass().getResource("/fsu/grumbach_hofmann/emailclientgui/application/SendScene.fxml"));
		            Stage stage = new Stage();
		            stage.setTitle("Write mail");
		            stage.setScene(new Scene(root, 450, 450));
		            stage.show();
		        }
		        catch (IOException e) {
		            e.printStackTrace();
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
						inboxLabel.setText("Inbox - " + selectedAccount.getEmail()+" - receiving mails...");
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
			totalMessagesLabel.setText(handler.getEmailList(selectedAccount).size() + " messages found");
			messagesList.getItems().addAll(handler.getMailObjectList(selectedAccount));

		} else {
			inboxLabel.setText("Inbox - <select an account first>");
			totalMessagesLabel.setText("No messages found");
		}

	};
}
