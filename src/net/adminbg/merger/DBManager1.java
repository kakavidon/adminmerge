package net.adminbg.merger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.HashSet;

import org.h2.jdbcx.JdbcConnectionPool;

import net.adminbg.merger.ui.Configuration;

public class DBManager1 {

	private JdbcConnectionPool pool = null;
	private final Set<ResultSet> resultSets = new HashSet<ResultSet>();

	private DBManager1() {
	}

	public static DBManager1 getInstance() {
		return DBManager1Holder.INSTANCE;
	}

	private static class DBManager1Holder {

		private static final DBManager1 INSTANCE = new DBManager1();
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
		stmt = getConnection().createStatement();
		ResultSet resultSet = stmt.executeQuery(sql);
		resultSets.add(resultSet);
		return resultSet;
	}
	
	public long countRows(final String schema, final String table) throws SQLException {
		Statement stmt = null;
		stmt = getConnection().createStatement();
		ResultSet resultSet = stmt.executeQuery("SELECT COUNT(*) FROM "+schema+"."+table+";");
		Long rowCount = -1L;
		if (resultSet != null ){
			rowCount = resultSet.getLong(1);
		}
		resultSet.close();
		return rowCount;
	}	
	
	public void executeStatement(final String sql) throws SQLException {
		Statement stmt = null;
		stmt = getConnection().createStatement();
		stmt.execute(sql);
	}	

}