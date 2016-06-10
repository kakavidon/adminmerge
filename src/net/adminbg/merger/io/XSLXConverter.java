package net.adminbg.merger.io;

import static net.adminbg.merger.io.FileTest.EXISTS;
import static net.adminbg.merger.io.FileTest.IS_DIRECTORY;
import static net.adminbg.merger.io.FileTest.NON_EMPTY;
import static net.adminbg.merger.io.FileTest.READABLE;
import static net.adminbg.merger.io.FileTest.WRITABLE;
import static net.adminbg.merger.io.FileTest.validate;
import static net.adminbg.merger.ui.Configuration.CELL_INDECIES;
import static net.adminbg.merger.ui.Configuration.COLUMN_DELIMITER;
import static net.adminbg.merger.ui.Configuration.DB_SCEMA;
import static net.adminbg.merger.ui.Configuration.DEFAULT_SOURCE_DIR;
import static net.adminbg.merger.ui.Configuration.ROW_DELIMITER;
import static net.adminbg.merger.ui.Configuration.SHOP_TABLE_COLUMNS;
import static net.adminbg.merger.ui.Configuration.SHOP_TABLE_COLUMN_TYPES;
import static net.adminbg.merger.ui.Configuration.SHOP_TABLE_NAME;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
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
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.adminbg.merger.io.Loader.LoaderBuilder;
import net.adminbg.merger.logging.AdminLogger;

public class XSLXConverter implements Converter {

	private static Logger logger = AdminLogger.INSTANCE.getLogger(XSLXConverter.class.getName());
	protected final String extension = "*.{xlsx}";
	private final StringBuffer data = new StringBuffer();
	private final Integer[] cellIndecies;
	private Path convertedFile = Paths.get(DEFAULT_SOURCE_DIR + "\\shop_merged_xlsx.csv");
	private Path mergedFile = Paths.get(DEFAULT_SOURCE_DIR + "\\shop_merged_all.xlsx");

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

		try (final DirectoryStream<Path> fileList = Files.newDirectoryStream(dirPath, getFileExtension());) {

			if (fileList == null) {
				final String msg = String.format("Could not list %s.", dirPath);
				logger.log(Level.SEVERE, msg);
				throw new ImportException(msg);
			}
			int finalRowNum = 1;

			for (Path source : fileList) {
				logger.info("Merging file  : " + source.getFileName().toString());
				// Read the excel file that we want to process
				XSSFWorkbook sourceWorkbook = new XSSFWorkbook(source.toFile());
				final XSSFSheet sheet = sourceWorkbook.getSheetAt(0);
				for (int rowNumber = sheet.getFirstRowNum(); rowNumber < sheet.getLastRowNum(); rowNumber++) {
					final XSSFRow row = sheet.getRow(rowNumber);
					if (row != null && rowNumber > 0) {
						appendRow(row, finalRowNum);

					}
				}
				sourceWorkbook.close();
				sourceWorkbook = null;
			}
			write(convertedFile);
		} catch (IOException | InvalidFileException | InvalidFormatException e) {
			final String msg = "Error while trying to write temp xslx.";
			logger.log(Level.SEVERE, msg, e);
			throw new ImportException(msg, e);
		}
		chlen(dirPath);
	}

	public void chlen(Path dirPath) throws ImportException {
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

		try (final DirectoryStream<Path> fileList = Files.newDirectoryStream(dirPath, getFileExtension());) {

			if (fileList == null) {
				final String msg = String.format("Could not list %s.", dirPath);
				logger.log(Level.SEVERE, msg);
				throw new ImportException(msg);
			}

			int fileNum = 1;

			try (final XSSFWorkbook destinationWorkbook = new XSSFWorkbook();
					final OutputStream br = Files.newOutputStream(mergedFile, StandardOpenOption.CREATE);) {
				for (Path source : fileList) {
					logger.info("Merging file  : " + source.getFileName().toString());
					// Read the excel file that we want to process
					XSSFWorkbook sourceWorkbook = new XSSFWorkbook(source.toFile());
					XSSFSheet sheet = sourceWorkbook.getSheetAt(0);
					for (int rowNumber = sheet.getFirstRowNum(); rowNumber < sheet.getLastRowNum(); rowNumber++) {

						XSSFRow row = sheet.getRow(rowNumber);
						if (row == null) {
							continue;
						}
						if (fileNum == 1 && rowNumber == 0) {
							// When we read the first file - create a new
							// sheet with the same name
							destinationWorkbook.createSheet(sheet.getSheetName());
							// and copy the header row
							copyRow(destinationWorkbook, row, sheet);
						} else if (fileNum != 1 && rowNumber == 0) {
							// Skip the header if we are not at the first
							// file
							continue;
						} else {
							// Copy every row that is not a header
							copyRow(destinationWorkbook, row, sheet);
						}
						row = null;
					}

					sourceWorkbook.close();
					sourceWorkbook = null;
					fileNum++;
				}

				destinationWorkbook.close();
				destinationWorkbook.write(br);
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException | InvalidFormatException e) {
			final String msg = "Error while trying to write temp xslx.";
			logger.log(Level.SEVERE, msg, e);
			throw new ImportException(msg, e);
		}
	}

	private void copyRow(final XSSFWorkbook destinationWorkbook, XSSFRow sourceRow, final XSSFSheet sheet) {
		final XSSFSheet destinationSheet = destinationWorkbook.getSheetAt(0);
		XSSFRow newRow = destinationSheet.createRow(1);

		// Loop through source columns to add to new row
		for (int cellIndx = 0; cellIndx < sourceRow.getLastCellNum(); cellIndx++) {
			// Grab a copy of the old/new cell
			XSSFCell oldCell = sourceRow.getCell(cellIndx);
			XSSFCell newCell = newRow.createCell(cellIndx);

			// If the old cell is null jump to next cell
			if (oldCell == null) {
				newCell = null;
				continue;

			}

			// Copy style from old cell and apply to new cell
			XSSFCellStyle newCellStyle = destinationWorkbook.createCellStyle();
			newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
			newCell.setCellStyle(newCellStyle);
			// If there is a cell comment, copy
			if (oldCell.getCellComment() != null) {
				newCell.setCellComment(oldCell.getCellComment());
			}
			// If there is a cell hyperlink, copy
			if (oldCell.getHyperlink() != null) {
				newCell.setHyperlink(oldCell.getHyperlink());
			}
			// Set the cell data type
			newCell.setCellType(oldCell.getCellType());
			// Set the cell data value
			switch (oldCell.getCellType()) {
			case Cell.CELL_TYPE_BLANK:
				newCell.setCellValue(oldCell.getStringCellValue());
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				newCell.setCellValue(oldCell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_ERROR:
				newCell.setCellErrorValue(oldCell.getErrorCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA:
				newCell.setCellFormula(oldCell.getCellFormula());
				break;
			case Cell.CELL_TYPE_NUMERIC:
				newCell.setCellValue(oldCell.getNumericCellValue());
				break;
			case Cell.CELL_TYPE_STRING:
				newCell.setCellValue(oldCell.getRichStringCellValue());
				break;
			}
		}
	}

	@Override
	public Loader getLoader() {
		final LoaderBuilder loaderBuilder = new LoaderBuilder();
		loaderBuilder.schema(DB_SCEMA);
		loaderBuilder.table(SHOP_TABLE_NAME);
		loaderBuilder.tableColumns(SHOP_TABLE_COLUMNS);
		loaderBuilder.types(SHOP_TABLE_COLUMN_TYPES);
		return loaderBuilder.build();
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

	public void appendRow(final XSSFRow row, final int finalRowNum) {
		for (int j = 0; j < cellIndecies.length; j++) {
			final XSSFCell cell = row.getCell(cellIndecies[j]);
			if (cell == null) {
				data.append(",");
				continue;
			}
			data.append(getCellValue(cell)).append(COLUMN_DELIMITER);
		}
		data.append(finalRowNum).append(COLUMN_DELIMITER);
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
			result = str.trim();
			break;
		default:
			result = "";
		}

		return result.replaceAll("\\u00a0", "");
	}

	public String getFileExtension() {
		return this.extension;
	}
}
