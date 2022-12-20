package fsu.grumbach_hofmann.emailclientgui.application;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import fsu.grumbach_hofmann.emailclientgui.util.Account;
import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class MainWindow extends Application {

	private DataHandler handler;
	private Scene scene;
	private ChoiceBox accountsDropdown;
	private Label inboxLabel;
	private Label totalMessagesLabel;
	private Account selectedAccount;
	private TableView messagesTable;

	@Override
	public void init() {
		handler = new DataHandler();
	}

	private void postInit() {
		inboxLabel = (Label) scene.lookup("#inboxLabel");
		totalMessagesLabel = (Label) scene.lookup("#totalMessagesLabel");
		accountsDropdown = (ChoiceBox) scene.lookup("#accountsDropdown");
		messagesTable = (TableView) scene.lookup("#messagesTable");
		
		initAccountList(accountsDropdown);
		accountsDropdown.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
		      @Override
		      public void changed(ObservableValue<? extends Number> observableValue, Number indexOld, Number indexNew) {
		    	  String accMail = (String) accountsDropdown.getItems().get((Integer) indexNew);
		    	  for (Account acc : handler.getAccountData()) {
		  			if(acc.getEmail().equals(accMail)) {
		  				selectedAccount=acc;
		  				updateShownMessages();	
		  			}
		  		} 
		      }
		    });
		initShownMessages();
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
	
	private void initShownMessages() {
		if(messagesTable!=null) {
		messagesTable.setEditable(true);
		 
        TableColumn subjectCol = new TableColumn("Subject");
        subjectCol.setCellValueFactory(new PropertyValueFactory<>("subject"));
        TableColumn readCol = new TableColumn("Read");
        readCol.setCellValueFactory(new PropertyValueFactory<>("read"));
        TableColumn fromCol = new TableColumn("From");
        fromCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        TableColumn toCol = new TableColumn("To");
        toCol.setCellValueFactory(new PropertyValueFactory<>("to"));
        TableColumn dateSentCol = new TableColumn("Date Sent");
        dateSentCol.setCellValueFactory(new PropertyValueFactory<>("dateSent"));
        
        messagesTable.getColumns().addAll(subjectCol,readCol,fromCol,toCol,dateSentCol);
		}
	}
	
	private void updateShownMessages() {
		if(selectedAccount!=null) {
			inboxLabel.setText("Inbox - "+selectedAccount.getEmail());
			totalMessagesLabel.setText(handler.getEmailList().size()+" messages found");
			for(MailObject mO : handler.getMailObjectList()) {
				messagesTable.getItems().add(mO);
			}
		}else {
			inboxLabel.setText("Inbox - <select an account first>");
			totalMessagesLabel.setText("No messages found");
		}
		
	};
}
