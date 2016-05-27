package net.adminbg.merger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public enum DBManager {
	INSTANCE;
	private static final Logger logger = Logger.getLogger(DBManager.class.getName());
	private Connection connection;
	private Set<ResultSet> resultSets = new TreeSet<ResultSet>();

	public void connect() throws SQLException, ClassNotFoundException {

		final String url = "jdbc:h2:.\\data\\admin.dat;FILE_LOCK=FS;PAGE_SIZE=1024;CACHE_SIZE=8192";
		Class.forName("org.h2.Driver");
		this.setConnection(DriverManager.getConnection(url));
		System.out.println("Connected database successfully...");

	}

	public ResultSet runSQL(final String sql) throws SQLException {
		Statement stmt = null;
		stmt = getConnection().createStatement();
		stmt.executeQuery(sql);
		ResultSet resultSet = stmt.executeQuery(sql);
		resultSets.add(resultSet);
		return resultSet;
	}

	public void disconnect() throws SQLException {
		for (ResultSet rs : resultSets) {
			rs.close();
		}
		connection.close();
	}

	public void executeSQL(final String sql) throws SQLException {
		Statement stmt = null;
		stmt = getConnection().createStatement();
		stmt.executeQuery(sql);
		boolean result = stmt.execute(sql);
		if (result) {
			logger.log(Level.INFO, "Success:  \"" + sql + "\".");
		} else {
			logger.log(Level.SEVERE, "Failed to execute \"" + sql + "\".");
		}
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
