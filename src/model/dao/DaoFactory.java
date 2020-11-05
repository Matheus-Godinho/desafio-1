package model.dao;

import db.DB;
import model.dao.impl.TasklistDaoJDBC;
import model.dao.impl.TaskDaoJDBC;

public class DaoFactory {
	
	public static TasklistDao createTasklistDao() {
		return new TasklistDaoJDBC(DB.getConnection());
	}
	public static TaskDao createTaskDao() {
		return new TaskDaoJDBC(DB.getConnection());
	}

}
