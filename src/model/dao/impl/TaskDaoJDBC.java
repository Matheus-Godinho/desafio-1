package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.TaskDao;
import model.entities.Tasklist;
import model.entities.Task;

public class TaskDaoJDBC implements TaskDao {
	
	private Connection connection;

	public TaskDaoJDBC(Connection connection) {
		this.connection = connection;
	}
	
	private Tasklist instantiateTasklist(ResultSet rs) throws SQLException {
		Tasklist tasklist;
		
		tasklist = new Tasklist();
		tasklist.setId(rs.getInt("TasklistId"));
		tasklist.setType(rs.getString("TaskType"));
		return tasklist;
	}
	private Task instantiateTask(ResultSet rs, Tasklist tasklist) throws SQLException {
		Task task;
		
		task = new Task();
		task.setId(rs.getInt("Id"));
		task.setName(rs.getString("Name"));
		task.setTasklist(tasklist);
		return task;
	}

	@Override
	public void insert(Task task) {
		PreparedStatement ps;
		int updatedRows;
		
		try {
			ps = connection.prepareStatement(
					"INSERT INTO task "
					+ "(Name, TasklistId) "
					+ "VALUES "
					+ "(?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, task.getName());
			ps.setInt(2, task.getTasklist().getId());
			updatedRows = ps.executeUpdate();
			if (updatedRows > 0) {
				ResultSet rs;
				int newId;
				
				rs = ps.getGeneratedKeys();
				if (rs.next()) {
					newId = rs.getInt(1);
					task.setId(newId);
				}
				DB.closeResultSet();
			} else {
				throw new DbException("Unexpected error! No rows were updated");
			}
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
	}

	@Override
	public void update(Task task) {
		PreparedStatement ps;
		
		try {
			ps = connection.prepareStatement(
					"UPDATE task "
					+ "SET Name = ?, TasklistId = ? "
					+ "WHERE Id = ?");
			ps.setString(1, task.getName());
			ps.setInt(2, task.getTasklist().getId());
			ps.setInt(3, task.getId());
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
	}
	

	@Override
	public void delete(Integer id) {
		PreparedStatement ps;
		
		try {
			ps = connection.prepareStatement(
					"DELETE FROM task "
					+ "WHERE Id = ?");
			ps.setInt(1, id);
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
		
	}
	
	@Override
	public void reset(Tasklist tasklist) {
		PreparedStatement ps;
		
		try {
			ps = connection.prepareStatement(
					"UPDATE task "
					+ "SET Name = ?"
					+ "WHERE TasklistId = ?");
			ps.setString(1, null);
			ps.setInt(2, tasklist.getId());
			ps.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
	}

	@Override
	public Task findById(Integer id) {
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = connection.prepareStatement(
					"SELECT task.*,tasklist.Type as TaskType "
					+ "FROM task INNER JOIN tasklist "
					+ "ON task.TasklistId = tasklist.Id "
					+ "WHERE task.Id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				Tasklist tasklist;
				Task task;
				
				tasklist = instantiateTasklist(rs);
				task = instantiateTask(rs, tasklist);
				return task;
			}				
			return null;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet();
			DB.closeStatement();
		}
	}
	
	@Override
	public List<Task> findByTasklist(Tasklist tasklist) {
		PreparedStatement ps;
		ResultSet rs;
		List<Task> tasks;
		Map<Integer, Tasklist> map;
		
		try {
			ps = connection.prepareStatement(
					"SELECT task.*,tasklist.Type as TaskType "
					+ "FROM task INNER JOIN tasklist "
					+ "ON task.TasklistId = tasklist.Id "
					+ "WHERE TasklistId = ? "
					+ "ORDER BY Name");
			ps.setInt(1, tasklist.getId());
			rs = ps.executeQuery();
			tasks = new ArrayList<>();
			map = new HashMap<>();
			while (rs.next()) {
				Task task;
				
				if (!map.containsKey(rs.getInt("TasklistId"))) {
					tasklist = instantiateTasklist(rs);
					map.put(rs.getInt("TasklistId"), tasklist);
				}
				tasklist = map.get(rs.getInt("TasklistId"));
				task = instantiateTask(rs, tasklist);
				tasks.add(task);
			}
			return tasks;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet();
			DB.closeStatement();
		}
	}

	@Override
	public List<Task> findAll() {
		PreparedStatement ps;
		ResultSet rs;
		List<Task> tasks;
		Map<Integer, Tasklist> map;
		
		try {
			ps = connection.prepareStatement(
					"SELECT task.*,tasklist.Type as TaskType "
					+ "FROM task INNER JOIN tasklist "
					+ "ON task.TasklistId = tasklist.Id "
					+ "ORDER BY Name");
			rs = ps.executeQuery();
			tasks = new ArrayList<>();
			map = new HashMap<>();
			while (rs.next()) {
				Tasklist tasklist;
				Task task;
				
				if (!map.containsKey(rs.getInt("TasklistId"))) {
					tasklist = instantiateTasklist(rs);
					map.put(rs.getInt("TasklistId"), tasklist);
				}
				tasklist = map.get(rs.getInt("TasklistId"));
				task = instantiateTask(rs, tasklist);
				tasks.add(task);
			}
			return tasks;
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeResultSet();
			DB.closeStatement();
		}
	}

	
	
}
