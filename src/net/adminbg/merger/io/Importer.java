package net.adminbg.merger.io;

import static net.adminbg.merger.io.FileTest.EXISTS;
import static net.adminbg.merger.io.FileTest.READABLE;
import static net.adminbg.merger.io.FileTest.IS_DIRECTORY;
import static net.adminbg.merger.io.FileTest.IS_FILE;
import static net.adminbg.merger.io.FileTest.validate;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.DBManager1;
import net.adminbg.merger.logging.AdminLogger;

import static net.adminbg.merger.logging.AdminLogger.EMPTY_SUPPLIER;

public abstract class Importer {
	
	private static Logger logger = AdminLogger.INSTANCE.getLogger(Importer.class.getName());
		
	private DBManager1 dbMAnager = DBManager1.getInstance();
	protected String extension;
	protected String dropStr = "DROP TABLE IF EXISTS %s.%s;";
    
	abstract protected void resetTable() throws ImportException;

	abstract protected void importFile(final Path source) throws ImportException;

	public void importFiles(final Path sourceDirectory) throws InvalidFileException, ImportException {

		validate(sourceDirectory, EXISTS, IS_DIRECTORY, READABLE);
		resetTable();
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(sourceDirectory,
					new DirectoryStream.Filter<Path>() {
						@Override
						public boolean accept(Path entry) throws IOException {
							final String extension = entry.toString().toLowerCase();
							return Files.isRegularFile(entry) && extension.endsWith(getExtension().toLowerCase());
						}
					});
			if (stream != null) {
				for (Path file : stream) {
					validate(file, EXISTS, IS_FILE, READABLE);
					importFile(file);
				}
			}

		} catch (IOException e) {
			final String msg = e.getMessage();
        	logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);
		}

	}

	protected void executeStatement(final String statement) throws SQLException {
		dbMAnager.executeStatement(statement);
	}

public DBManager1 getDBManager(){
	return dbMAnager;
}

	public String getExtension() {
		return extension;
	}

}
