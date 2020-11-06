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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Tasklist;
import model.exceptions.ValidationException;
import model.services.TasklistService;

public class TasklistFormController implements Initializable {

	private Tasklist entity;
	private TasklistService service;
	private List<DataChangeListener> dataChangeListeners;
	
	public TasklistFormController () {
		dataChangeListeners = new ArrayList<>();
	}
	
	public void setTasklist(Tasklist entity) {
		this.entity = entity;
	}
	public void setTasklistService(TasklistService service) {
		this.service = service;
	}
	
	@FXML
	private TextField txtId;
	@FXML
	private TextField txtType;
	@FXML
	private Label labelErrorType;
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
	
	private Tasklist getFormData() {
		Tasklist obj;
		ValidationException exception;
		
		obj = new Tasklist();
		exception = new ValidationException("Validation error");
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		if (txtType.getText() == null || txtType.getText().trim().equals("")) {
			exception.addError("type", "Field can't be empty");
		}
		obj.setType(txtType.getText());
		if (exception.getErrors().size() > 0) {
			throw exception;
		}
		return obj;
	}
	
	private void notifyDataChangeListeners() {
		dataChangeListeners.forEach(listener -> listener.onDataChanged());
	}
	
	private void setErrorMessages(Map<String, String> errors) {
		Set<String> fields;
		
		fields = errors.keySet();
		labelErrorType.setText((fields.contains("type") ? errors.get("type") : ""));
	}
	
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtType, 30);
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
		txtType.setText(entity.getType());
	}
	
}
