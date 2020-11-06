package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.TasklistDao;
import model.entities.Tasklist;

public class TasklistService {
	
	private TasklistDao dao;
	
	public TasklistService() {
		dao = DaoFactory.createTasklistDao();
	}
	
	public List<Tasklist> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Tasklist obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Tasklist obj) {
		dao.delete(obj.getId());
	}

}
