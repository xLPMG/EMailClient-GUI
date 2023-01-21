package fsu.grumbach_hofmann.emailclientgui.application;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {

	@Override
	public void init() {
	}

	@Override
	public void start(Stage primaryStage) {
		FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/view/MainScene.fxml"));
		Parent root = null;
		try {
			root = rootLoader.load();
		} catch (IOException e) {

			e.printStackTrace();
			System.err.println("Could no initialize FXMLLoader.");
			System.exit(1);
		}

		MainSceneController mainSceneController = rootLoader.getController();
		mainSceneController.initController();

		Scene scene = new Scene(root, 1000, 700);
		scene.getStylesheets().add(getClass().getResource("/style/MainScene.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/style/DarkTheme.css").toExternalForm());
		primaryStage.setTitle("Java Mail Client");
		primaryStage.setScene(scene);
		primaryStage.show();

		mainSceneController.postInitController();
	}
}
