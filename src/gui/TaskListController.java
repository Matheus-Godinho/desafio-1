package gui;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.Main;
import gui.util.Alerts;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entities.Task;
import model.services.TaskService;

public class TaskListController implements Initializable {
	
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
	public void onButtonNewAction() {
		System.out.printf("onButtonNewAction%n");
	}
	
	private void initializeNodes() {
		Stage stage;
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnName.setCellValueFactory(new PropertyValueFactory<>("Name"));
		stage = (Stage) Main.getMainScene().getWindow();
		tableViewTask.prefHeightProperty().bind(stage.heightProperty());
	}
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
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
