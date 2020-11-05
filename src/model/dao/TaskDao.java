package model.dao;

import java.util.List;

import model.entities.Tasklist;
import model.entities.Task;

public interface TaskDao {
	
	void insert(Task task);
	void update(Task task);
	void delete(Integer id);
	void reset(Tasklist tasklist);
	Task findById(Integer id);
	List<Task> findByTasklist(Tasklist tasklist);
	List<Task> findAll();

}
