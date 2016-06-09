package net.adminbg.merger.io;

import static net.adminbg.merger.io.FileTest.EXISTS;
import static net.adminbg.merger.io.FileTest.IS_DIRECTORY;
import static net.adminbg.merger.io.FileTest.NON_EMPTY;
import static net.adminbg.merger.io.FileTest.READABLE;
import static net.adminbg.merger.io.FileTest.WRITABLE;
import static net.adminbg.merger.io.FileTest.validate;
import static net.adminbg.merger.ui.Configuration.CELL_INDECIES;
import static net.adminbg.merger.ui.Configuration.COLUMN_DELIMITER;
import static net.adminbg.merger.ui.Configuration.DEFAULT_SOURCE_DIR;
import static net.adminbg.merger.ui.Configuration.ROW_DELIMITER;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.adminbg.merger.logging.AdminLogger;

public class XSLXConverter implements Converter {

	private static Logger logger = AdminLogger.INSTANCE.getLogger(XSLXConverter.class.getName());
	protected final String extension = "*.{xlsx}";
	private final StringBuffer data = new StringBuffer();
	private final Integer[] cellIndecies;
	private Path convertedFile = Paths.get(DEFAULT_SOURCE_DIR + "\\shop_merged_xlsx.csv");

	@Override
	public void mergeFiles(Path dirPath) throws ImportException {
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

			for (Path source : stream) {
				logger.info("Merging file  : " + source.getFileName().toString());
				XSSFWorkbook sourceWorkbook = new XSSFWorkbook(source.toFile());
				final XSSFSheet sheet = sourceWorkbook.getSheetAt(0);
				for (int rowNumber = sheet.getFirstRowNum(); rowNumber < sheet.getLastRowNum(); rowNumber++) {
					if (rowNumber == 1) {
						continue;
					}
					final XSSFRow row = sheet.getRow(rowNumber);
					if (row != null) {
						appendRow(row);
					}
				}
				// mergeFile(source, convertedFile);
				sourceWorkbook.close();
				sourceWorkbook = null;
			}
			write(convertedFile);
		} catch (IOException | InvalidFormatException | InvalidFileException e) {
			final String msg = "Error while trying to write temp xslx.";
			logger.log(Level.SEVERE, msg, e);
			throw new ImportException(msg, e);
		}
	}

	private String getFileExtension() {
		return this.extension;
	}

	public Path getConvertedFile() {
		return convertedFile;
	}

	public XSLXConverter() throws ImportException {
		cellIndecies = toIntArray(CELL_INDECIES.split(","));
	}

	public void write(final Path target) throws ImportException, InvalidFileException {

		if (target == null) {
			throw new ImportException("Destination csv cannot be null.");
		}

		logger.info(target.toString());

		final Path parent = target.getParent();
		if (parent == null) {
			throw new ImportException("Unable to retrieve parent directory.");
		}

		validate(parent, EXISTS, IS_DIRECTORY, WRITABLE);

		if (data.length() == 0) {
			logger.warning("Nothing to convert.");
			return;
		}

		try (BufferedWriter bw = Files.newBufferedWriter(target, StandardOpenOption.CREATE_NEW);) {
			bw.write(data.toString());
		} catch (IOException e) {
			final String msg = "Error while trying to write temp csv.";
			logger.log(Level.SEVERE, msg, e);
			throw new ImportException(msg, e);
		}
	}

	private Integer[] toIntArray(String[] split) throws ImportException {

		if (split == null) {
			throw new ImportException("Invalid configuration. Cell indecies not defined.");
		}
		Set<Integer> result = new TreeSet<>();
		for (int i = 0; i < split.length; i++) {
			result.add(Integer.valueOf(split[i]));
		}

		return result.toArray(new Integer[result.size()]);
	}

	public void appendRow(final XSSFRow row) {
		for (int j = 0; j < cellIndecies.length; j++) {
			final XSSFCell cell = row.getCell(cellIndecies[j]);
			if (cell == null) {
				data.append(",");
				continue;
			}
			data.append(getCellValue(cell)).append(COLUMN_DELIMITER);
		}
		data.append(ROW_DELIMITER);
	}

	private String getCellValue(final XSSFCell cell) {
		String result = "";
		if (cell == null) {
			return "";

		}
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BLANK:
			final String stringCellValue = cell.getStringCellValue();
			result = stringCellValue.trim();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			final Boolean booleanCellValue = cell.getBooleanCellValue();
			result = booleanCellValue.toString().trim();
			break;
		case Cell.CELL_TYPE_ERROR:
			result = "";
			break;
		case Cell.CELL_TYPE_FORMULA:
			result = "";
			break;
		case Cell.CELL_TYPE_NUMERIC:
			final Double numericCellValue = cell.getNumericCellValue();
			Integer toInt = numericCellValue.intValue();
			result = toInt.toString().trim();
			break;
		case Cell.CELL_TYPE_STRING:
			final String str = cell.getStringCellValue();
			str.trim();
			result = "";
			break;
		default:
			result = "";
		}
		return result;
	}
}
