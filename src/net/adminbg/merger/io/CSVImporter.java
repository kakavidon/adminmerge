package net.adminbg.merger.io;

import java.nio.file.Path;
import java.sql.SQLException;

import net.adminbg.merger.ui.Configuration;

public class CSVImporter extends Importer {
	// TODO externalize constants
	protected String extension = ".csv";

	@Override
	protected void importFile(final Path source) throws ImportException {
		final String[] columnNames = Configuration.STORE_TABLE_COLUMNS.split(",");
		final String[] columnTypes = Configuration.STORE_TABLE_COLUMN_TYPES.split(";");
		final String tableName = Configuration.STORE_TABLE_NAME;
		final String createStatement = "CREATE TABLE IF NOT EXISTS PUBLIC.%s (%s);";
		final String insertStatement = "\nINSERT INTO %s ( SELECT * FROM CSVREAD('%s','%s',%s));";

		if (columnNames == null || columnTypes == null || columnNames.length != columnTypes.length) {
			throw new ImportException("Collumn's names should equals collumn types.");
		}
		StringBuffer sb = new StringBuffer();
		int idx = 0;
		for (String column : columnNames) {
			sb.append(column).append(" ").append(columnTypes[idx]).append(",");
			idx++;
		}

		final String file = source.toAbsolutePath().toString();

		System.out.println("Loading ... " + file);

		final String columns = sb.toString().replaceAll(",$", "");
		final String create = String.format(createStatement, tableName, columns);

		final String commaSeparated = Configuration.STORE_TABLE_COLUMNS.replaceAll(",", ";").replaceAll("\"", "");
		final String charset = "'charset=Windows-1251 fieldSeparator=;'";
		final String insert = String.format(insertStatement, tableName, file, commaSeparated, charset);

		try {
			System.out.println(create);
			executeStatement(create);
		
			System.out.println(insert);
			executeStatement(insert);

		//	final long countRows = getDBManager().countRows("PUBLIC", tableName);
		//	System.out.println("Total rows inserted: " + countRows);
		} catch (SQLException e) {
			throw new ImportException(e.getMessage());
		}

	}

	@Override
	public String getExtension() {
		return this.extension;
	}

	@Override
	protected void resetTable() throws ImportException {
		final String dropTable = "DROP TABLE IF EXISTS PUBLIC." + Configuration.STORE_TABLE_NAME;
		try {
			System.out.println(dropTable);
			executeStatement(dropTable);
		} catch (SQLException e) {
			throw new ImportException(e.getMessage());
		}

	}

}
