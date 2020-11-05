package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.TasklistDao;
import model.entities.Tasklist;

public class TasklistDaoJDBC implements TasklistDao {
	
	private Connection connection;

	public TasklistDaoJDBC(Connection connection) {
		this.connection = connection;
	}
	
	private Tasklist instantiateTasklist(ResultSet rs) throws SQLException {
		Tasklist tasklist;
		
		tasklist = new Tasklist();
		tasklist.setId(rs.getInt("Id"));
		tasklist.setType(rs.getString("Type"));
		return tasklist;
	}

	@Override
	public void insert(Tasklist tasklist) {
		PreparedStatement ps;
		int updatedRows;
		
		try {
			ps = connection.prepareStatement(
					"INSERT INTO tasklist "
					+ "(Type) "
					+ "VALUES "
					+ "(?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, tasklist.getType());
			updatedRows = ps.executeUpdate();
			if (updatedRows > 0) {
				ResultSet rs;
				int newId;
				
				rs = ps.getGeneratedKeys();
				if (rs.next()) {					
					newId = rs.getInt(1);
					tasklist.setId(newId);
				}
				DB.closeResultSet();
			} else
				throw new DbException("Unexpected error! No rows were updated");
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement();
		}
	}

	@Override
	public void update(Tasklist tasklist) {
		PreparedStatement ps;
		
		try {
			ps = connection.prepareStatement(
					"UPDATE tasklist "
					+ "SET Type = ? "
					+ "WHERE Id = ?");
			ps.setString(1, tasklist.getType());
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
	public void delete(Integer id) {
		PreparedStatement ps;
		
		try {
			ps = connection.prepareStatement(
					"DELETE FROM tasklist "
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
	public Tasklist findById(Integer id) {
		PreparedStatement ps;
		ResultSet rs;
		
		try {
			ps = connection.prepareStatement(
					"SELECT tasklist.* "
					+ "FROM tasklist "
					+ "WHERE Id = ?");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				Tasklist tasklist;
				
				tasklist = instantiateTasklist(rs);
				return tasklist;
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
	public List<Tasklist> findAll() {
		PreparedStatement ps;
		ResultSet rs;
		List<Tasklist> tasklists;
		
		try {
			ps = connection.prepareStatement(
					"SELECT * FROM tasklist "
					+ "ORDER BY type");
			rs = ps.executeQuery();
			tasklists = new ArrayList<>();
			while (rs.next()) {
				Tasklist tasklist;
				
				tasklist = instantiateTasklist(rs);
				tasklists.add(tasklist);
			}
			return tasklists;
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
