package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import application.Main;
import gui.util.Alerts;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import model.services.TaskService;
import model.services.TasklistService;

public class MainViewController implements Initializable {

	@FXML
	private MenuItem menuItemTask;
	@FXML
	private MenuItem menuItemTasklist;
	@FXML
	private MenuItem menuItemAbout;
	
	@FXML
	public void onMenuItemTaskAction() {
		loadView("/gui/TaskList.fxml", (TaskListController controller) -> {
			controller.setTaskService(new TaskService());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuItemTasklistAction() {
		loadView("/gui/TasklistList.fxml", (TasklistListController controller) -> {
			controller.setTasklistService(new TasklistService());
			controller.updateTableView();
		});
	}
	@FXML
	public void onMenuItemAboutAction() {
		loadView("/gui/About.fxml", x -> {});
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// TODO Auto-generated method stub
		
	}
	
	private synchronized <T> void loadView(String absoluteName, Consumer<T> initializingAction) {
		FXMLLoader loader;
		VBox newVBox, mainVBox;
		Scene mainScene;
		Node mainMenu;
		T controller;
		
		try {
			loader = new FXMLLoader(getClass().getResource(absoluteName));
			newVBox = loader.load();
			mainScene = Main.getMainScene();
			mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();
			mainMenu = mainVBox.getChildren().get(0);
			mainVBox.getChildren().clear();
			mainVBox.getChildren().add(mainMenu);
			mainVBox.getChildren().addAll(newVBox.getChildren());
			controller = loader.getController();
			initializingAction.accept(controller);
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

}
