package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DB {
	
	private static Connection connection = null;
	private static Statement statement = null;
	private static ResultSet rs = null;
	
	private static Properties loadProperties() {
		try (FileInputStream fis = new FileInputStream("db.properties")) {
			Properties properties;
			
			properties = new Properties();
			properties.load(fis);
			return properties;
		}
		catch (IOException e) {
			throw new DbException(e.getMessage());
		}
	}
	
	public static Connection getConnection() {
		if (connection == null) {
			try {
				Properties properties;
				String url;
				
				properties = loadProperties();
				url = properties.getProperty("dburl");
				connection = DriverManager.getConnection(url, properties);
			}
			catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return connection;
	}
	
	public static void closeConnection() {
		try {
			if (connection != null)
				connection.close();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}
	public static void closeStatement() {
		try {
			if (statement != null)
				statement.close();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}
	public static void closeResultSet() {
		try {
			if (rs != null)
				rs.close();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
	}

}
