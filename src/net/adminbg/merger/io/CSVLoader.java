package net.adminbg.merger.io;

import static net.adminbg.merger.ui.Configuration.DB_SCEMA;
import static net.adminbg.merger.ui.Configuration.STORE_TABLE_COLUMNS;
import static net.adminbg.merger.ui.Configuration.STORE_TABLE_COLUMN_TYPES;
import static net.adminbg.merger.ui.Configuration.STORE_TABLE_NAME;

import java.nio.file.Path;
import java.util.logging.Logger;

import net.adminbg.merger.logging.AdminLogger;

public class CSVLoader  {

	private static Logger logger = AdminLogger.INSTANCE.getLogger(CSVLoader.class.getName());
	
	private final String dbSchema = DB_SCEMA;
	private final String tableName = STORE_TABLE_NAME;
	
	private final String[] columnNames = STORE_TABLE_COLUMNS.split(",");
	private final String[] columnTypes = STORE_TABLE_COLUMN_TYPES.split(";");
	
	private final String createStatement = "CREATE TABLE IF NOT EXISTS PUBLIC.%s (%s);";
	private final String insertStatement = "\nINSERT INTO %s ( SELECT * FROM CSVREAD('%s','%s',%s));";
	private final String commaSeparated = STORE_TABLE_COLUMNS.replaceAll(",", ";").replaceAll("\"", "");
	private final String charset = "'charset=UTF-8 fieldSeparator=;'";

//	protected final String dropStatement = String.format(dropStr, dbSchema, tableName);
	protected final String dropStatement = "" ;

//	@Override
	public void loadFile(Path source) throws ImportException {
		logger.info(source.toString());
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
		
 		final String columns = sb.toString().replaceAll(",$", "");
		final String create = String.format(createStatement, tableName, columns.replaceAll("\"", ""));
//		final String insert = String.format(insertStatement, tableName, file, commaSeparated, charset);
//        resetTable();   
//		try {
//			logger.info(create);
//			executeStatement(create);
//			logger.info(insert);
//			executeStatement(insert);
//		} catch (SQLException e) {
//			final String msg = e.getMessage();
//			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
//			throw new ImportException(msg,e);
//		}

	}

	//@Override
	public String getDropStatement() {

		return this.dropStatement;
	}

}
