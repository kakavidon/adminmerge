package net.adminbg.merger.io;

import static net.adminbg.merger.io.FileTest.EXISTS;
import static net.adminbg.merger.io.FileTest.IS_FILE;
import static net.adminbg.merger.io.FileTest.READABLE;
import static net.adminbg.merger.io.FileTest.validate;
import static net.adminbg.merger.logging.AdminLogger.EMPTY_SUPPLIER;
import static net.adminbg.merger.ui.Configuration.DB_SCEMA;
import static net.adminbg.merger.ui.Configuration.DEFAULT_SOURCE_DIR;
import static net.adminbg.merger.ui.Configuration.SHOP_TABLE_NAME;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.adminbg.merger.logging.AdminLogger;

public class XSLXImporter extends Importer implements Exporter {

	private static Logger logger = AdminLogger.INSTANCE.getLogger(XSLXImporter.class.getName());
	protected final String extension = ".xlsx";
	private final String tableName = SHOP_TABLE_NAME;
	private final String dbSchema = DB_SCEMA;
	private final String dropStatement = String.format(dropStr, dbSchema, tableName);
	private Path destination = null;
	private final String tempCsv = DEFAULT_SOURCE_DIR + "tmp.csv";

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

	@Override
	public void importFiles(final Path sourceDirectory) throws InvalidFileException, ImportException {
		logger.info(sourceDirectory.toString());
		final Path firstFile = getFirstFile(sourceDirectory);
		try {
			copyHeader(firstFile);
			
		} catch (InvalidFormatException e) {
			final String msg = e.getMessage();
			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);			
		}
		super.importFiles(sourceDirectory);
	}

	@Override
	protected void importFile(Path source) throws ImportException {
		logger.info(source.toString());
		toCsv(source);
		// TODO Load file into database
		// TODO Join
		// TODO append matching to destination

	}

	@Override
	public void setDestination(final Path path) throws InvalidFileException {
		logger.info(path.toString());
		if (path == null || path.toString().equals("")) {
			final String msg = "File name shoul be not null and non empty.";
			logger.warning(msg);
			throw new InvalidFileException(msg);
		}

		if (!EXISTS.check(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				final String msg = e.getMessage();
				logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
				throw new InvalidFileException(msg, e);
			}

		}
		validate(path, EXISTS, IS_FILE, READABLE);
		this.destination = path;

	}

	public Path getDestination() {
		return this.destination;

	}

	private Path getFirstFile(final Path sourceDirectory) throws InvalidFileException, ImportException {
		logger.info(sourceDirectory.toString());
		Path firstFile = null;
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
					firstFile = file;
				}
			}

		} catch (IOException e) {
			final String msg = e.getMessage();
			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);
		}

		if (firstFile == null) {
			final String msg = "Source foulder does not comtain any xlsx file. Abort.";
			logger.warning(msg);
			throw new ImportException(msg);
		}

		return firstFile;
	}

	private void toCsv(final Path file) throws ImportException {
		logger.info(file.toString());
		try (final InputStream is = Files.newInputStream(file);
				final BufferedWriter br = Files.newBufferedWriter(Paths.get(tempCsv), StandardOpenOption.CREATE);
				final XSSFWorkbook workbook = new XSSFWorkbook(is);) {

			final XSSFSheet sheet = workbook.getSheetAt(0);
			convert(sheet, br, 1, sheet.getLastRowNum());

		} catch (IOException e) {
			final String msg = e.getMessage();
			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);
		}

	}

	public void convert(final XSSFSheet worksheet, BufferedWriter br, int rowIndex, int rowsCount) throws IOException {
		logger.info("cloneHeader");
		final StringBuffer data = new StringBuffer();
		final int[] cellIndecies = { 0, 3, 17 };
		for (int i = rowIndex; i < rowsCount; i++) {
			final XSSFRow row = worksheet.getRow(i);

			if (row == null) {
				continue;
			}

			for (int j = 0; j < cellIndecies.length; j++) {
				final XSSFCell cell = row.getCell(cellIndecies[j]);
				if (cell == null) {
					data.append(",");
					continue;
				}

				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BLANK:
					data.append(cell.getStringCellValue()).append(",");
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					data.append(cell.getBooleanCellValue()).append(",");
					break;
				case Cell.CELL_TYPE_ERROR:
					data.append(cell.getErrorCellValue()).append(",");
					break;
				case Cell.CELL_TYPE_FORMULA:
					data.append(cell.getCellFormula()).append(",");
					break;
				case Cell.CELL_TYPE_NUMERIC:
					data.append(cell.getNumericCellValue()).append(",");
					break;
				case Cell.CELL_TYPE_STRING:
					data.append(cell.getRichStringCellValue()).append(",");
					break;
				}

			}

			data.append("\n");
		}

		br.write(data.toString());
	}

	private void copyHeader(final Path source) throws ImportException, InvalidFormatException {
		final Path dest = getDestination();
		logger.info("Copying header from " + source + " to " + dest);
		try (final XSSFWorkbook sourceWorkbook = new XSSFWorkbook(source.toFile());
				final XSSFWorkbook destinationWorkbook = new XSSFWorkbook();
				final OutputStream br = Files.newOutputStream(dest, StandardOpenOption.CREATE);

		) {

			final XSSFSheet sourceSheet = sourceWorkbook.getSheetAt(0);
			sourceSheet.getSheetName();
			destinationWorkbook.createSheet(sourceSheet.getSheetName());
			final XSSFSheet destSheet = destinationWorkbook.getSheetAt(0);
			copyRows(sourceWorkbook, destinationWorkbook, sourceSheet, destSheet, 0, 1);

			destinationWorkbook.write(br);
			sourceWorkbook.close();
			destinationWorkbook.close();

		} catch (IOException e) {
			final String msg = e.getMessage();
			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);
		}

	}

	private void copyRows(XSSFWorkbook workbook, XSSFWorkbook workbook2, XSSFSheet worksheet, XSSFSheet destSheet,
			int sourceRowNum, int rowCount) {
		for (int i = sourceRowNum; i < rowCount; i++) {
			XSSFRow sourceRow = worksheet.getRow(i);
			XSSFRow newRow = destSheet.createRow(i);

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
				XSSFCellStyle newCellStyle = workbook2.createCellStyle();
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

	}
}
