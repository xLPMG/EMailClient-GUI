package fsu.grumbach_hofmann.emailclientgui.application;

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
	private Scene scene;
	private MainSceneController mainSceneController;

	@Override
	public void init() {
		handler = new DataHandler();
		receiver = new MailReceiver(handler);
		sender = new MailSender();
	}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/view/MainScene.fxml"));
			Parent root = rootLoader.load();
			mainSceneController = rootLoader.getController();
			mainSceneController.initController(handler, receiver, sender);
			
			scene = new Scene(root, 1000, 700);
			scene.getStylesheets().add(getClass()
					.getResource("/style/MainScene.css").toExternalForm());
			scene.getStylesheets().add(getClass()
					.getResource("/style/DarkTheme.css").toExternalForm());
			primaryStage.setTitle("Java Mail Client");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			postInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void postInit() {
		mainSceneController.postInitController();
	}


}
