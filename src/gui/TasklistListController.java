package gui;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import application.Main;
import db.DbIntegrityException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.entities.Tasklist;
import model.services.TasklistService;

public class TasklistListController implements Initializable, DataChangeListener {
	
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
		private TableColumn<Tasklist, Tasklist> tableColumnEDIT;
		@FXML
		private TableColumn<Tasklist, Tasklist> tableColumnRESET;
	
	@FXML
	public void onButtonNewAction(ActionEvent event) {
		Stage parentStage = Utils.currentStage(event);
		Tasklist obj = new Tasklist();
		createDialogForm(obj, "/gui/TasklistForm.fxml", parentStage);
	}
	
	private void createDialogForm(Tasklist obj, String absoluteName, Stage parentStage) {
		try {
			FXMLLoader loader;
			Pane pane;
			TasklistFormController controller;
			Stage dialogStage;
			
			loader = new FXMLLoader(getClass().getResource(absoluteName));
			pane = loader.load();
			controller = loader.getController();
			controller.setTasklist(obj);
			controller.setTasklistService(new TasklistService());
			controller.subscribeDataChangeListener(this);
			controller.updateFormData();
			dialogStage = new Stage();
			dialogStage.setTitle("Enter Department data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		}
		catch (IOException e) {
			Alerts.showAlert("IO Exception", "Error in loading view",
					e.getMessage(), AlertType.ERROR);
		}
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	private void initializeNodes() {
		Stage stage;
		
		tableColumnId.setCellValueFactory(new PropertyValueFactory<>("Id"));
		tableColumnType.setCellValueFactory(new PropertyValueFactory<>("Type"));
		stage = (Stage) Main.getMainScene().getWindow();
		tableViewTasklist.prefHeightProperty().bind(stage.heightProperty());
	}
	
	@Override
	public void onDataChanged() {
		updateTableView();
	}
	
	public void updateTableView() {
		if (service == null)
			Alerts.showAlert("Illegal State Exception", "Error in updating table view",
					"Service was null", AlertType.ERROR);
		else {
			List<Tasklist> list;
			
			list = service.findAll();
			obsList = FXCollections.observableArrayList(list);
			tableViewTasklist.setItems(obsList);
			initEditButtons();
			initResetButtons();
		}
	}
	
	private void initEditButtons() {
		tableColumnEDIT.setCellValueFactory(parameter -> new ReadOnlyObjectWrapper<>(parameter.getValue()));
		tableColumnEDIT.setCellFactory(parameter -> new TableCell<Tasklist, Tasklist>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Tasklist obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj,
						"/gui/TasklistForm.fxml", Utils.currentStage(event)));
			}
		});
	}
	
	private void initResetButtons() {
		tableColumnRESET.setCellValueFactory(parameter -> new ReadOnlyObjectWrapper<>(parameter.getValue()));
		tableColumnRESET.setCellFactory(parameter -> new TableCell<Tasklist, Tasklist>() {
			private final Button button = new Button("reset");

			@Override
			protected void updateItem(Tasklist obj, boolean empty) {
				super.updateItem(obj, empty);
				if (obj == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> resetEntity(obj));
			}
		});
	}
	private void resetEntity(Tasklist obj) {
		Optional<ButtonType> result;
		
		result = Alerts.showConfirmation("Confirmation", "Are you sure to reset?");
		if (result.get() == ButtonType.OK) {
			if (service == null)
				Alerts.showAlert("Illegal State Exception", "Error in removing object",
						"Service was null", AlertType.ERROR);
			try {
				service.reset(obj);
				result = Alerts.showConfirmation("Confirmation", "Are you sure to delete?");
				if (result.get() == ButtonType.OK)
					service.remove(obj);
				updateTableView();
			}
			catch (DbIntegrityException e) {
				Alerts.showAlert("Database Integrity Exception", "Error in removing object",
						e.getMessage(), AlertType.ERROR);
			}
		}
	}


}
