package model.dao;

import java.util.List;

import model.entities.Tasklist;

public interface TasklistDao {
	
	void insert(Tasklist tasklist);
	void update(Tasklist tasklist);
	void delete(Integer id);
	Tasklist findById(Integer id);
	List<Tasklist> findAll();

}
