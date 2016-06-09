package net.adminbg.merger.io;

import static net.adminbg.merger.logging.AdminLogger.EMPTY_SUPPLIER;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.DBManager;
import net.adminbg.merger.logging.AdminLogger;

public abstract class Loader {

	private static Logger logger = AdminLogger.INSTANCE.getLogger(Loader.class.getName());

	private DBManager dbManager = DBManager.getInstance();
	protected String extension;
	protected String dropStr = "DROP TABLE IF EXISTS %s.%s;";
	protected String dropStatement;

	abstract public void loadFile(final Path source) throws ImportException;

	protected void executeStatement(final String statement) throws SQLException {
		dbManager.executeStatement(statement);
	}

	protected DBManager getDBManager() {
		return dbManager;
	}

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
