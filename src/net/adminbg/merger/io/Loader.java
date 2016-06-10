package net.adminbg.merger.io;

import static net.adminbg.merger.logging.AdminLogger.EMPTY_SUPPLIER;
import static net.adminbg.merger.ui.Configuration.COLUMN_DELIMITER;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.DBManager;
import net.adminbg.merger.logging.AdminLogger;

public class Loader {

	private static Logger logger = AdminLogger.INSTANCE.getLogger(Loader.class.getName());

	private DBManager dbManager = DBManager.getInstance();

	private String dbSchema;
	private String tableName;
	private String tableColumns;
	private String columnTypes;

	private final String[] columnNames;
	private final String[] types;
	private final String commaSeparated ;

	private final String charset = "'charset=UTF-8 fieldSeparator="+COLUMN_DELIMITER+"'";
	private final String dropStr = "DROP TABLE IF EXISTS %s.%s;";

	/**
	 * SQL ddl statements.
	 */
	private final String createStatement = "CREATE TABLE IF NOT EXISTS PUBLIC.%s (%s);";
	private final String dropStatement;
	private final String insertStatement = "\nINSERT INTO %s ( SELECT * FROM CSVREAD('%s','%s',%s));";

	private Loader(final String dbSchema, final String tableName, final String tableColumns, String columnTypes) {
		this.dbSchema = dbSchema;
		this.tableName = tableName;
		this.tableColumns = tableColumns;
		this.columnTypes = columnTypes;
		this.columnNames = tableColumns.split(",");
		this.types = columnTypes.split(";");
		this.commaSeparated = tableColumns.replaceAll(",", ";").replaceAll("\"", "");
		this.dropStatement = String.format(dropStr, dbSchema, tableName);
	}

	public static class LoaderBuilder {
		private String schema;
		private String table;
		private String tableColumns;
		private String columnTypes;

		public LoaderBuilder schema(final String schema) {
			this.schema = schema;
			return this;
		}

		public LoaderBuilder table(final String table) {
			this.table = table;
			return this;
		}

		public LoaderBuilder tableColumns(final String tableColumns) {
			this.tableColumns = tableColumns;
			return this;
		}

		public LoaderBuilder types(final String types) {
			this.columnTypes = types;
			return this;
		}

		public Loader build() {
			return new Loader(schema, table, tableColumns, columnTypes);
		}
	}

	public void loadFile(final Path source) throws ImportException {
		logger.info(source.toString());
		if (columnNames == null || types == null || columnNames.length != types.length) {
			throw new ImportException("Collumn's names should equals collumn types.");
		}
		StringBuffer sb = new StringBuffer();
		int idx = 0;
		for (String column : columnNames) {
			sb.append(column).append(" ").append(types[idx]).append(",");
			idx++;
		}

		final String file = source.toAbsolutePath().toString();

		final String columns = sb.toString().replaceAll(",$", "");
		final String create = String.format(createStatement, tableName, columns.replaceAll("\"", ""));
		final String insert = String.format(insertStatement, tableName, file, commaSeparated, charset);
		resetTable();
		try {
			logger.info(create);
			executeStatement(create);
			logger.info(insert);
			executeStatement(insert);
		} catch (SQLException e) {
			final String msg = e.getMessage();
			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);
		}

	}
	/* abstract public String getDropStatement(); */

	protected void executeStatement(final String statement) throws SQLException {
		dbManager.executeStatement(statement);
	}

	protected DBManager getDBManager() {
		return dbManager;
	}

	protected void resetTable() throws ImportException {

		try {
			logger.info(this.dropStatement);
			executeStatement(this.dropStatement);
		} catch (SQLException e) {
			final String msg = e.getMessage();
			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);

		}

	}

	public String getDbSchema() {
		return dbSchema;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableColumns() {
		return tableColumns;
	}
	public String getColumnTypes() {
		return columnTypes;
	}
}
