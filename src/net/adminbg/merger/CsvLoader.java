package net.adminbg.merger;

import static net.adminbg.merger.ui.Configuration.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static java.nio.file.StandardOpenOption.*;

import net.adminbg.merger.logging.AdminLogger;

public class CsvLoader implements Loader {
	private final static String FILE_EXTENSION = "*.{csv}";
	private static Logger logger = AdminLogger.INSTANCE.getLogger(CsvLoader.class.getName());

	@Override
	public void load(final Path dirPath) throws SQLException, IllegalArgumentException {

		logger.info("Reading directory:" + dirPath);

		if (dirPath == null) {
			final String message = "Directory name should not be null";
			IllegalArgumentException ex = new IllegalArgumentException(message);
			logger.log(Level.SEVERE, message, ex);
			throw ex;
		}

		final Path tmpPathName = Paths.get(DEFAULT_SOURCE_DIR + "\\_new.csv");

		DirectoryStream<Path> stream;
		try {
			stream = Files.newDirectoryStream(dirPath, getFileExtension());

			for (Path path : stream) {
				logger.info("Appending file  : " + path.getFileName().toString());
				appendFile(path, tmpPathName, 2);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// final String sql = "SELECT * FROM TEST";

		// DBManager.INSTANCE.executeSQL(sql);

	}

	public void appendFile(final Path sourcePath, final Path targetPath, final int skipLines) throws IOException {
		final FileInputStream fis = new FileInputStream(sourcePath.toString());
		final InputStreamReader r = new InputStreamReader(fis, StandardCharsets.UTF_8);
		final FileOutputStream fos = new FileOutputStream(targetPath.toString(), true);
		final OutputStreamWriter w = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
		final BufferedWriter writer = new BufferedWriter(w);
		final BufferedReader br = new BufferedReader(r);
		try {

			String readLine;
			int curLineNr = 1;

			while ((readLine = br.readLine()) != null) {
				if (curLineNr++ <= skipLines) {
					continue;
				}

				writer.write(readLine + NEW_LINE);

			}
		} finally {
			writer.close();
			br.close();
		}
	}

	@Override
	public String getFileExtension() {
		return FILE_EXTENSION;
	}
}
