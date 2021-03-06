package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.TaskDao;
import model.entities.Task;

public class TaskService {
	
	private TaskDao dao;
	
	public TaskService() {
		dao = DaoFactory.createTaskDao();
	}
	
	public List<Task> findAll() {
		return dao.findAll();
	}

	public void saveOrUpdate(Task obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Task obj) {
		dao.delete(obj.getId());
	}
	
}
