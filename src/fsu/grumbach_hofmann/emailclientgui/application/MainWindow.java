package fsu.grumbach_hofmann.emailclientgui.application;

import java.util.ArrayList;
import java.util.Collections;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import fsu.grumbach_hofmann.emailclientgui.mail.MailReceiver;
import fsu.grumbach_hofmann.emailclientgui.util.Account;
import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;
import fsu.grumbach_hofmann.emailclientgui.util.MailCellFactory;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class MainWindow extends Application {

	private DataHandler handler;
	private MailReceiver receiver;
	private Scene scene;
	private ChoiceBox accountsDropdown;
	private Label inboxLabel;
	private Label totalMessagesLabel;
	private Account selectedAccount;
	private ListView messagesList;

	@Override
	public void init() {
		handler = new DataHandler();
		receiver = new MailReceiver(handler);
	}

	private void postInit() {
		inboxLabel = (Label) scene.lookup("#inboxLabel");
		totalMessagesLabel = (Label) scene.lookup("#totalMessagesLabel");
		accountsDropdown = (ChoiceBox) scene.lookup("#accountsDropdown");
		messagesList = (ListView) scene.lookup("#messagesList");
		
		initAccountList(accountsDropdown);
		accountsDropdown.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number indexOld, Number indexNew) {
				String accMail = (String) accountsDropdown.getItems().get((Integer) indexNew);
				for (Account acc : handler.getAccountData()) {
					if (acc.getEmail().equals(accMail)) {
						selectedAccount = acc;
//						receiver.receiveMails(acc.getInbox(), acc.getInboxPort()+"", acc.getUsername(), acc.getPassword());
						updateMessagesList();
					}
				}
			}
		});
		initMessagesList();
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

	private void initAccountList(ChoiceBox list) {
		list.getItems().clear();
		for (Account acc : handler.getAccountData()) {
			list.getItems().add(acc.getEmail());
		}
	}

	private void initMessagesList() {
		messagesList.setCellFactory(new MailCellFactory());
	}

	private void updateMessagesList() {
		if (selectedAccount != null) {
			inboxLabel.setText("Inbox - " + selectedAccount.getEmail());
			totalMessagesLabel.setText(handler.getEmailList().size() + " messages found");
//			for(MailObject mO : handler.getMailObjectList()) {
//				messagesList.getItems().add(mO);
//			}
			Collections.sort(handler.getMailObjectList());
			messagesList.getItems().addAll(handler.getMailObjectList());
			
		} else {
			inboxLabel.setText("Inbox - <select an account first>");
			totalMessagesLabel.setText("No messages found");
		}

	};
}
