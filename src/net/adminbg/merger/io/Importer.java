package net.adminbg.merger.io;

import static net.adminbg.merger.io.FileTest.FILE_EXISTS;
import static net.adminbg.merger.io.FileTest.FILE_READABLE;
import static net.adminbg.merger.io.FileTest.IS_DIRECTORY;
import static net.adminbg.merger.io.FileTest.IS_FILE;
import static net.adminbg.merger.io.FileTest.validate;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;

import net.adminbg.merger.DBManager1;

public abstract class Importer {
	private DBManager1 dbMAnager = DBManager1.getInstance();
	protected String extension;

	abstract protected void resetTable() throws ImportException;

	abstract protected void importFile(final Path source) throws ImportException;

	public void importFiles(final Path sourceDirectory) throws InvalidFileException, ImportException {

		validate(sourceDirectory, FILE_EXISTS, IS_DIRECTORY, FILE_READABLE);
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
					validate(file, FILE_EXISTS, IS_FILE, FILE_READABLE);
					importFile(file);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
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
