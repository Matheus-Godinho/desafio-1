package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Task;
import model.entities.Tasklist;
import model.exceptions.ValidationException;
import model.services.TaskService;
import model.services.TasklistService;

public class TaskFormController implements Initializable {

	private Task entity;
	private TaskService service;
	private TasklistService tasklistService;
	private List<DataChangeListener> dataChangeListeners;
	
	public TaskFormController () {
		dataChangeListeners = new ArrayList<>();
	}
	
	public void setTask(Task entity) {
		this.entity = entity;
	}
	public void setServices(TaskService service, TasklistService tasklistService) {
		this.service = service;
		this.tasklistService = tasklistService;
	}
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtName;
	@FXML
	private ComboBox<Tasklist> comboBoxTasklist;
		private ObservableList<Tasklist> obsList;
	@FXML
	private Label labelErrorName;
	@FXML
	private Button buttonSave;
	@FXML
	private Button buttonCancel;
	
	@FXML
	public void onButtonSaveAction(ActionEvent event) {
		if (entity == null)
			Alerts.showAlert("Illegal State Exception", "Error in saving object",
					"Entity was null", AlertType.ERROR);
		if (service == null)
			Alerts.showAlert("Illegal State Exception", "Error in saving object",
					"Service was null", AlertType.ERROR);
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		}
		catch (ValidationException e) {
			setErrorMessages(e.getErrors());
		}
		catch (DbException e) {
			Alerts.showAlert("Database Exception", "Error in saving object",
					e.getMessage(), AlertType.ERROR);
		}
	}
	@FXML
	public void onButtonCancelAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	private Task getFormData() {
		Task obj;
		ValidationException exception;
		
		obj = new Task();
		exception = new ValidationException("Validation error");
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtName.getText() == null || txtName.getText().trim().equals("")) {
			exception.addError("name", "Field can't be empty");
		}
		obj.setName(txtName.getText());
		obj.setTasklist(comboBoxTasklist.getValue());
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}
	
	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(listener -> listener.onDataChanged());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		labelErrorName.setText((fields.contains("name") ? errors.get("name") : ""));
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
		initializeComboBoxTasklist();
	}	
	private void initializeComboBoxTasklist() {
		Callback<ListView<Tasklist>, ListCell<Tasklist>> factory = lv -> new ListCell<>() {
			@Override
			protected void updateItem(Tasklist item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getType());
			}
		};
		comboBoxTasklist.setCellFactory(factory);
		comboBoxTasklist.setButtonCell(factory.call(null));
	}
	
	public void loadAssociatedObjects() {
		if (tasklistService == null)
			Alerts.showAlert("Illegal State Exception", "Error in loading associated objects",
					"Tasklist service was null", AlertType.ERROR);
		List<Tasklist> list;
		
		list = tasklistService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxTasklist.setItems(obsList);
	}
	
	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	public void updateFormData() {
		if (entity == null) {
			Alerts.showAlert("Illegal State Exception", "Error in updating form data",
					"Entity was null", AlertType.ERROR);
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
		if (entity.getTasklist() == null) {
			comboBoxTasklist.getSelectionModel().selectFirst();
		} else {
			comboBoxTasklist.setValue(entity.getTasklist());
		}
	}
	
}
