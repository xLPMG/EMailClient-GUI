package fsu.grumbach_hofmann.emailclientgui.application;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import fsu.grumbach_hofmann.emailclientgui.mail.MailObject;
import fsu.grumbach_hofmann.emailclientgui.mail.MailReceiver;
import fsu.grumbach_hofmann.emailclientgui.mail.MailSender;
import fsu.grumbach_hofmann.emailclientgui.mail.MailUtils;
import fsu.grumbach_hofmann.emailclientgui.util.Account;
import fsu.grumbach_hofmann.emailclientgui.util.DataHandler;
import fsu.grumbach_hofmann.emailclientgui.util.MailCellFactory;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
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
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class MainSceneController {

	@FXML
	private ChoiceBox<String> accountsDropdown;

	@FXML
	private Menu accountsMenuItem;

	@FXML
	private MenuItem addAccountItem;

	@FXML
	private Button btnDeleteMail;

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
	private WebView messageWebView;

	@FXML
	private HBox messagesMenuBar;

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
	private MailUtils mailUtils;

	private Account selectedAccount;
	private NewAccountSceneController newAccountSceneController;
	private SendSceneController sendSceneController;

	private MailObject selectedMail;

	public void initController() {
		mailUtils = new MailUtils();

		handler = new DataHandler(mailUtils);
		receiver = new MailReceiver(handler);
		sender = new MailSender();
	}

	public void postInitController() {
		initAccountList();
		initMessagesElements();

		Image btnReceiveMailsImg = new Image("receiveIcon.png");
		ImageView btnReceiveMailsImgView = new ImageView(btnReceiveMailsImg);
		btnReceiveMailsImgView.setFitHeight(btnReceiveMails.getHeight());
		btnReceiveMailsImgView.setPreserveRatio(true);

		Image btnWriteNewMailImg = new Image("writeIcon.png");
		ImageView btnWriteNewMailImgView = new ImageView(btnWriteNewMailImg);
		btnWriteNewMailImgView.setFitHeight(btnWriteMail.getHeight());
		btnWriteNewMailImgView.setPreserveRatio(true);
		
		Image btnDeleteMailImg = new Image("deleteMailIcon.png");
		ImageView btnDeleteMailImgView = new ImageView(btnDeleteMailImg);
		btnDeleteMailImgView.setFitHeight(btnDeleteMail.getHeight());
		btnDeleteMailImgView.setPreserveRatio(true);

		btnReceiveMails.setGraphic(btnReceiveMailsImgView);
		btnWriteMail.setGraphic(btnWriteNewMailImgView);
		btnDeleteMail.setGraphic(btnDeleteMailImgView);
	}

	@FXML
	void addAccount(ActionEvent event) {
		try {
			FXMLLoader newAccountLoader = new FXMLLoader(getClass().getResource("/view/NewAccountScene.fxml"));
			Parent newAccountRoot = newAccountLoader.load();
			newAccountSceneController = newAccountLoader.getController();
			newAccountSceneController.initController(handler, mailUtils);
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
		if (selectedAccount == null) {
			return;
		}

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
			receiver.receiveMails(selectedAccount, -1);
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
			FXMLLoader sendLoader = new FXMLLoader(getClass().getResource("/view/SendScene.fxml"));
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

	@FXML
	void deleteMail(ActionEvent event) {
		if (selectedMail == null)
			return;
		
		handler.deleteMail(selectedMail, selectedAccount);
		updateMessagesList();
		new Thread(() -> {
			try {
				mailUtils.deleteMailFromServer(selectedMail, selectedAccount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}

	private void initMessagesElements() {
		messageDisplayScrollPane.setMinWidth(messagesMenuBar.getWidth() + 15);

		messageDisplayPane.setVisible(false);
		messagesList.setCellFactory(new MailCellFactory());
		messagesList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				if (selectedAccount == null)
					return;
				handler.updateSeen(newSelection, selectedAccount, true);
				updateUnseenMessageCount();

				messageDisplayPane.setVisible(true);
				senderLabel.setText(newSelection.getFrom());
				dateLabel.setText(dateCalc(newSelection.getDateSent()));

				subjectLabel.setText(newSelection.getSubject());
				recipientsLabel.setText(newSelection.getTo());
				if (!newSelection.getContent().equals("")) {
					messageWebView.setVisible(false);
					messageWebView.setManaged(false);
					contentLabel.setText(newSelection.getContent());
				} else {
					contentLabel.setText("");
					messageWebView.getEngine().loadContent("<html><body>" + "<div id=\"jmcdiv\">"
							+ newSelection.getHtml() + "</div>" + "</body></html>");
					adjustWebViewHeight();
					messageWebView.setMinWidth(messageDisplayPane.getWidth() - 10);
					messageWebView.setMaxWidth(messageDisplayPane.getWidth() - 10);
					messageWebView.setVisible(true);
					messageWebView.setManaged(true);
				}
				selectedMail = newSelection;
				messagesList.refresh();
			}
		});

		messageDisplayScrollPane.widthProperty().addListener(new ChangeListener<Object>() {
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				messageDisplayPane.setMaxWidth(messageDisplayScrollPane.getWidth() - 30);
				messageDisplayPane.setMinWidth(messageDisplayScrollPane.getWidth() - 30);
			}
		});

		// webview:
		// fit webview to container
		messageDisplayPane.widthProperty().addListener(new ChangeListener<Object>() {
			public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
				if (messageWebView.isVisible() && messageDisplayPane.isVisible()) {
					messageWebView.setMinWidth(messageDisplayPane.getWidth() - 10);
					messageWebView.setMaxWidth(messageDisplayPane.getWidth() - 10);
				}
			}
		});

		messageWebView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> arg0, State oldState, State newState) {
				if (newState == State.SUCCEEDED) {
					adjustWebViewHeight();
				}
			}
		});
		messageWebView.getEngine()
				.setUserStyleSheetLocation(getClass().getResource("/style/webview.css").toExternalForm());
	}

	private void initAccountList() {
		accountsDropdown.getItems().clear();
		for (Account acc : handler.getAccountData()) {
			accountsDropdown.getItems().add(acc.getEmail());
		}
		accountsDropdown.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observableValue, Number indexOld, Number indexNew) {
				if ((Integer) indexNew == -1 || accountsDropdown.getItems().get((Integer) indexNew) == null) {
					return;
				} else {
					String accMail = (String) accountsDropdown.getItems().get((Integer) indexNew);
					for (Account acc : handler.getAccountData()) {
						if (acc.getEmail().equals(accMail)) {
							selectedAccount = acc;
							handler.loadMails(acc);
							updateMessagesList();
							// TODO: receive all new mails on account switch?
							inboxLabel.setText("Inbox - " + selectedAccount.getEmail() + " - receiving mails...");
							new Thread(() -> {
								receiver.receiveMails(selectedAccount, 10);
								Platform.runLater(() -> {
									updateMessagesList();
								});
							}).start();
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
			if (acc == selectedAccount) {
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

	private String dateCalc(LocalDateTime date) {
		if (date != null) {
			String dateText = "";
			DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMdd");
			if (fmt.format(date).equals(fmt.format(LocalDateTime.now()))) {
				dateText = "Today";
			} else if (fmt.format(date).equals(fmt.format(LocalDateTime.now().minusDays(1)))) {
				dateText = "Yesterday";
			} else {
				dateText = DateTimeFormatter.ofPattern("dd.MM.yy", Locale.GERMANY).format(date);
			}
			dateText += (" at " + DateTimeFormatter.ofPattern("hh:mm", Locale.GERMANY).format(date));
			return dateText;
		} else {
			return "unknown";
		}
	}

	private void adjustWebViewHeight() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				Object result = messageWebView.getEngine()
						.executeScript("document.getElementById('jmcdiv').offsetHeight");
				if (result instanceof Integer) {
					int height = (Integer) result;
					messageWebView.setMinHeight(height + 20);
					messageWebView.setMaxHeight(height + 20);
				}

			}
		});
	}

}
