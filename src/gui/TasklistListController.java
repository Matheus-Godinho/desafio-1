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
import model.entities.Tasklist;
import model.services.TasklistService;

public class TasklistListController implements Initializable {
	
	private TasklistService service;
	
	public void setTasklistService(TasklistService service) {
		this.service = service;
	}

	@FXML
	private Button buttonNew;
	@FXML
	private TableView<Tasklist> tableViewTasklist;
		private ObservableList<Tasklist> obsList;
		@FXML
		private TableColumn<Tasklist, Integer> tableColumnId;
		@FXML
		private TableColumn<Tasklist, String> tableColumnType;
	
	@FXML
	public void onButtonNewAction() {
		System.out.printf("onButtonNewAction%n");
	}
	
	private void initializeNodes() {
		Stage stage;
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnType.setCellValueFactory(new PropertyValueFactory<>("Type"));
		stage = (Stage) Main.getMainScene().getWindow();
		tableViewTasklist.prefHeightProperty().bind(stage.heightProperty());
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
			List<Tasklist> list;
			
			list = service.findAll();
			obsList = FXCollections.observableArrayList(list);
			tableViewTasklist.setItems(obsList);
		}
	}

}
