package fsu.grumbach_hofmann.emailclientgui.application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow extends Application {

	private Scene scene;
	private MainSceneController mainSceneController;

	@Override
	public void init() {}

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader rootLoader = new FXMLLoader(getClass().getResource("/view/MainScene.fxml"));
			Parent root = rootLoader.load();
			mainSceneController = rootLoader.getController();
			mainSceneController.initController();
			
			scene = new Scene(root, 1000, 700);
			scene.getStylesheets().add(getClass()
					.getResource("/style/MainScene.css").toExternalForm());
			scene.getStylesheets().add(getClass()
					.getResource("/style/DarkTheme.css").toExternalForm());
			primaryStage.setTitle("Java Mail Client");
			primaryStage.setScene(scene);
			primaryStage.show();
			
			mainSceneController.postInitController();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
