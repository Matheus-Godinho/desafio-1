package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Task;
import model.services.TaskService;
import model.services.TasklistService;

public class TaskListController implements Initializable, DataChangeListener {
	
	private TaskService service;
	
	public void setTaskService(TaskService service) {
		this.service = service;
	}

	@FXML
	private Button buttonNew;
	@FXML
	private TableView<Task> tableViewTask;
		private ObservableList<Task> obsList;
		@FXML
		private TableColumn<Task, Integer> tableColumnId;
		@FXML
		private TableColumn<Task, String> tableColumnName;
	
	@FXML
	public void onButtonNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Task obj = new Task();
		createDialogForm(obj, "/gui/TaskForm.fxml", parentStage);
	}
	
	private void createDialogForm(Task obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader;
			Pane pane;
			TaskFormController controller;
			Stage dialogStage;

			loader = new FXMLLoader(getClass().getResource(absoluteName));
			pane = loader.load();
			controller = loader.getController();
			controller.setTask(obj);
			controller.setServices(new TaskService(), new TasklistService());
			controller.loadAssociatedObjects();
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			dialogStage = new Stage();
			dialogStage.setTitle("Enter Seller data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error in loading view", e.getMessage(), AlertType.ERROR);
		}
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	private void initializeNodes() {
		Stage stage;
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		stage = (Stage) Main.getMainScene().getWindow();
		tableViewTask.prefHeightProperty().bind(stage.heightProperty());
	}
	
	@Override
	public void onDataChanged() {
		updateTableView();
	}
	
	public void updateTableView() {
		if (service == null)
			Alerts.showAlert("Illegal State Exception", "Error updating table view",
					"Service was null", AlertType.ERROR);
		else {
			List<Task> list;
			
			list = service.findAll();
			obsList = FXCollections.observableArrayList(list);
			tableViewTask.setItems(obsList);
		}
	}

}
