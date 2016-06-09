package net.adminbg.merger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.HashSet;

import org.h2.jdbcx.JdbcConnectionPool;

import net.adminbg.merger.ui.Configuration;

public class DBManager implements AutoCloseable{

	private JdbcConnectionPool pool = null;
	private final Set<ResultSet> resultSets = new HashSet<ResultSet>();

	private DBManager() {
	}

	public static DBManager getInstance() {
		return DBManager1Holder.INSTANCE;
	}

	private static class DBManager1Holder {

		private static final DBManager INSTANCE = new DBManager();
	}

	public void start() {
		pool = JdbcConnectionPool.create(Configuration.DB_JDBC_URL, "", "");
		final Integer maxConnections = Integer.valueOf(Configuration.DB_JDBC_MAX_CONNECTIONS);
		pool.setMaxConnections(maxConnections);

	}

	public void dispose() throws SQLException {
		for (ResultSet rs : resultSets) {
			if (!rs.isClosed()) {
				rs.close();
				rs = null;
			}
		}
		pool.dispose();
	}

	public Connection getConnection() throws SQLException {
		return pool.getConnection();
	}

	public ResultSet runQuery(final String sql) throws SQLException {

		Statement stmt = null;
		final Connection connection = getConnection();
		stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery(sql);
		resultSets.add(resultSet);
		connection.close();
		return resultSet;
	}

	public void truncate(final String schema, final String table) throws SQLException {
		Statement stmt = null;
		final Connection connection = getConnection();
		stmt = connection.createStatement();
		stmt.execute("TRUNCATE TABLE " + schema + "." + table + ";");
		connection.close();

	}

	public long countRows(final String schema, final String table) throws SQLException {
		Statement stmt = null;
		final Connection connection = getConnection();
		stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM " + schema + "." + table + ";");
		Long rowCount = -1L;
		if (resultSet != null) {
			rowCount = resultSet.getLong(1);
		}
		resultSet.close();
		connection.close();
		return rowCount;
	}

	public void executeStatement(final String sql) throws SQLException {
		Statement stmt = null;
		final Connection connection = getConnection();
		stmt = connection.createStatement();
		stmt.execute(sql);
		connection.close();
	}

	@Override
	public void close() throws Exception {
		dispose();
		
	}

}