package net.adminbg.merger.io;

import static net.adminbg.merger.io.FileTest.EXISTS;
import static net.adminbg.merger.io.FileTest.IS_DIRECTORY;
import static net.adminbg.merger.io.FileTest.READABLE;
import static net.adminbg.merger.io.FileTest.*;
import static net.adminbg.merger.ui.Configuration.DB_SCEMA;
import static net.adminbg.merger.ui.Configuration.DEFAULT_SOURCE_DIR;
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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.io.Loader.LoaderBuilder;
import net.adminbg.merger.logging.AdminLogger;

public class CSVConverter implements Converter {

	private static Logger logger = AdminLogger.INSTANCE.getLogger(CSVConverter.class.getName());
	private final static String FILE_EXTENSION = "*.{csv}";

	private Path convertedFile = Paths.get(DEFAULT_SOURCE_DIR + "\\store_merged_files.csv");

	public void mergeFiles(final Path dirPath) throws ImportException {

		logger.info(String.format("Reading directory: %s and merge into %s", dirPath, convertedFile));
		try {
			validate(dirPath, EXISTS, IS_DIRECTORY, READABLE);
			if (EXISTS.check(convertedFile) && NON_EMPTY.check(convertedFile)) {
				Files.delete(convertedFile);
			}
		} catch (InvalidFileException | IOException e) {
			final String msg = "Error while trying to write temp csv.";
			logger.log(Level.SEVERE, msg, e);
			throw new ImportException(msg, e);
		}

		try (final DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, getFileExtension());) {

			if (stream == null) {
				final String msg = String.format("Could not list %s.", dirPath);
				logger.log(Level.SEVERE, msg);
				throw new ImportException(msg);
			}

			for (Path path : stream) {
				logger.info("Merging file  : " + path.getFileName().toString());
				mergeFile(path, convertedFile, 2);
			}

		} catch (IOException e) {
			final String msg = "Error while trying to write temp csv.";
			logger.log(Level.SEVERE, msg, e);
			throw new ImportException(msg, e);
		}
	}

	public void mergeFile(final Path sourcePath, final Path targetPath, final int skipLines) throws ImportException {

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
			final String msg = "Error while trying to merge store csv files.";
			logger.log(Level.SEVERE, msg, e);
			throw new ImportException(msg, e);
		}
	}

	public String getFileExtension() {
		return FILE_EXTENSION;
	}

	@Override
	public Path getConvertedFile() {
		return convertedFile;
	}

	@Override
	public Loader getLoader() {
		final LoaderBuilder loaderBuilder = new LoaderBuilder();
		loaderBuilder.schema(DB_SCEMA);
		loaderBuilder.table(STORE_TABLE_NAME);
		loaderBuilder.tableColumns(STORE_TABLE_COLUMNS);
		loaderBuilder.types(STORE_TABLE_COLUMN_TYPES);
		return loaderBuilder.build();
	}

}
