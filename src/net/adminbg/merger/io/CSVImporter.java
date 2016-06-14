package net.adminbg.merger.io;

import static net.adminbg.merger.logging.AdminLogger.EMPTY_SUPPLIER;
import static net.adminbg.merger.ui.Configuration.DB_SCEMA;
import static net.adminbg.merger.ui.Configuration.NEW_LINE;
import static net.adminbg.merger.ui.Configuration.STORE_TABLE_COLUMNS;
import static net.adminbg.merger.ui.Configuration.STORE_TABLE_COLUMN_TYPES;
import static net.adminbg.merger.ui.Configuration.STORE_TABLE_NAME;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.logging.AdminLogger;
public class CSVImporter extends LoaderCopy {
	// TODO externalize constants
	protected final String extension = ".csv";

	private static Logger logger = AdminLogger.INSTANCE.getLogger(CSVImporter.class.getName());
	private final String[] columnNames = STORE_TABLE_COLUMNS.split(",");
	private final String[] columnTypes = STORE_TABLE_COLUMN_TYPES.split(";");
	private final String tableName = STORE_TABLE_NAME;
	private final String createStatement = "CREATE TABLE IF NOT EXISTS PUBLIC.%s (%s);";
	private final String insertStatement = "\nINSERT INTO %s ( SELECT * FROM CSVREAD('%s','%s',%s));";
	private final String commaSeparated = STORE_TABLE_COLUMNS.replaceAll(",", ";").replaceAll("\"", "");
	private final String charset = "'charset=Windows-1251 fieldSeparator=;'";
	
	
	private final String dbSchema = DB_SCEMA;
	private final String dropStatement = String.format(dropStr, dbSchema, tableName);

	@Override
	protected void importFile(final Path source) throws ImportException {
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

		//final String file = source.toAbsolutePath().toString();

		//System.out.println("Loading ... " + file);
		final Path cleanedFile = Paths.get(source.toString()+".tmp"); 
		clearFile(source, cleanedFile, 2);
		final String columns = sb.toString().replaceAll(",$", "");
		final String create = String.format(createStatement, tableName, columns.replaceAll("\"", ""));
		final String insert = String.format(insertStatement, tableName, cleanedFile, commaSeparated, charset);
		 
		try {
			logger.info(create);
			executeStatement(create);
			logger.info(insert);
			executeStatement(insert);
		} catch (SQLException e) {
			final String msg = e.getMessage();
			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg,e);
		} finally {
			try {
				Files.delete(cleanedFile);
			} catch (IOException e) {
				final String msg = e.getMessage();
				logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
				throw new ImportException(msg,e);
			}
		}

	}
	
	public void clearFile(final Path sourcePath, final Path targetPath, final int skipLines) throws ImportException {

		final Charset charset = StandardCharsets.UTF_8;

		try (final FileInputStream fis = new FileInputStream(sourcePath.toString());
				final InputStreamReader r = new InputStreamReader(fis, "Windows-1251");
				final FileOutputStream fos = new FileOutputStream(targetPath.toString(), true);
				final OutputStreamWriter w = new OutputStreamWriter(fos, charset);
				final BufferedWriter writer = new BufferedWriter(w);
				final BufferedReader br = new BufferedReader(r);) {

			String readLine;
			int curLineNr = 1;

			while ((readLine = br.readLine()) != null) {
				if (curLineNr++ <= skipLines) {
					continue;
				}
				writer.write(readLine + NEW_LINE);

			}
		} catch (IOException e) {
			final String msg = "Error while trying to clear store csv files.";
			logger.log(Level.SEVERE, msg, e);
			throw new ImportException(msg, e);
		}
	}

	@Override
	public String getExtension() {
		return this.extension;
	}

	@Override
	protected void resetTable() throws ImportException {
		try {
			logger.info(dropStatement);
			executeStatement(dropStatement);
		} catch (SQLException e) {
			final String msg = e.getMessage();
        	logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);

		}

	}

}
