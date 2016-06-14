package net.adminbg.merger.io;

import static net.adminbg.merger.io.FileTest.EXISTS;
import static net.adminbg.merger.io.FileTest.IS_FILE;
import static net.adminbg.merger.io.FileTest.READABLE;
import static net.adminbg.merger.io.FileTest.validate;
import static net.adminbg.merger.logging.AdminLogger.EMPTY_SUPPLIER;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellFill;

import net.adminbg.merger.logging.AdminLogger;

public class MergeXSSFWorkbooks {
	private static Logger logger = AdminLogger.INSTANCE.getLogger(MergeXSSFWorkbooks.class.getName());

	public static void main(String[] args)
			throws ImportException, InvalidFileException, EncryptedDocumentException, InvalidFormatException {
		final long a = System.currentTimeMillis();
		final Path dir = Paths.get("C:\\Users\\Lachezar.Nedelchev\\git\\adminmerge\\shop\\xxx\\");

		// final XSSFWorkbook newWorkbook = new XSSFWorkbook();
		try {
			DirectoryStream<Path> stream = Files.newDirectoryStream(dir, new DirectoryStream.Filter<Path>() {
				@Override
				public boolean accept(Path entry) throws IOException {
					final String file = entry.toString().toLowerCase();
					return Files.isRegularFile(entry) && file.endsWith("xlsx".toLowerCase());
				}
			});

			XSSFSheet newSheet = null;
			if (stream != null) {
				for (Path file : stream) {
					validate(file, EXISTS, IS_FILE, READABLE);
					// Read xlsx file
					final long currentTimeMillis = System.currentTimeMillis();
					System.out.println(file.toFile().toString());
					XSSFWorkbook newWorkbook = new XSSFWorkbook();
					XSSFWorkbook oldWorkbook = (XSSFWorkbook) WorkbookFactory.create(file.toFile());
					final XSSFSheet oldSheet = oldWorkbook.getSheetAt(0); 

					if (newSheet == null || newWorkbook.getNumberOfSheets() == 0) {
						newSheet = newWorkbook.createSheet(oldSheet.getSheetName());
					}

					traverse(oldWorkbook, newSheet, newWorkbook);
					newWorkbook.write(new FileOutputStream("new.xlsx"));
					newWorkbook.close();
					oldWorkbook.close();

					newWorkbook = null;
					oldWorkbook = null;
					System.gc();
					final long s = System.currentTimeMillis();
					System.out.println(s - currentTimeMillis);
					// XSSFWorkbook oldWorkbook = null;
					// try {
					// } catch (Exception e) {
					// e.printStackTrace();
					// return;
					// }
					// final XSSFSheet oldSheet = oldWorkbook.getSheetAt(0);
					//
					// if (newSheet == null || newWorkbook.getNumberOfSheets()
					// == 0) {
					// newSheet =
					// newWorkbook.createSheet(oldSheet.getSheetName());
					// }

					// appendFile(oldWorkbook, newWorkbook, newSheet, oldSheet);

				}
			}

			// newWorkbook.close();

		} catch (IOException e) {
			final String msg = e.getMessage();
			logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
			throw new ImportException(msg, e);
		}

		final long b = System.currentTimeMillis();
		System.out.println(b - a);
	}

	private static void traverse(XSSFWorkbook oldWorkbook, XSSFSheet newSheet, XSSFWorkbook newWorkbook) {
		int i = 0;
		final XSSFSheet oldSheet = oldWorkbook.getSheetAt(0);
		for (int rowNumber = oldSheet.getFirstRowNum(); rowNumber < oldSheet.getLastRowNum(); rowNumber++) {
			i++;
			final XSSFRow oldRow = oldSheet.getRow(rowNumber);
			if (oldRow != null) {
				final XSSFRow newRow = newSheet.createRow(rowNumber);
				newRow.setHeight(oldRow.getHeight());

				for (int columnNumber = oldRow.getFirstCellNum(); columnNumber < oldRow
						.getLastCellNum(); columnNumber++) {
					newSheet.setColumnWidth(columnNumber, oldSheet.getColumnWidth(columnNumber));

					final XSSFCell oldCell = oldRow.getCell(columnNumber);
					if (oldCell != null) {
						final XSSFCell newCell = newRow.createCell(columnNumber);

						// Copy value
						setCellValue(newCell, getCellValue(oldCell));

						// Copy style
						XSSFCellStyle newCellStyle = newWorkbook.createCellStyle();
						newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
						newCell.setCellStyle(newCellStyle);
					}
				}
			}
		}
		System.out.println("Total = " + i);
	}

	private static void appendFile(final XSSFWorkbook oldWorkbook, final XSSFWorkbook newWorkbook,
			final XSSFSheet newSheet, final XSSFSheet oldSheet) {

		// Copy style source
		final StylesTable oldStylesSource = oldWorkbook.getStylesSource();
		final StylesTable newStylesSource = newWorkbook.getStylesSource();

		oldStylesSource.getFonts().forEach(font -> newStylesSource.putFont(font, true));
		oldStylesSource.getFills().forEach(fill -> newStylesSource.putFill(new XSSFCellFill(fill.getCTFill())));
		oldStylesSource.getBorders()
				.forEach(border -> newStylesSource.putBorder(new XSSFCellBorder(border.getCTBorder())));

		// Copy sheets

		newSheet.setDefaultRowHeight(oldSheet.getDefaultRowHeight());
		newSheet.setDefaultColumnWidth(oldSheet.getDefaultColumnWidth());

		// Copy content
		for (int rowNumber = oldSheet.getFirstRowNum(); rowNumber < oldSheet.getLastRowNum(); rowNumber++) {
			final XSSFRow oldRow = oldSheet.getRow(rowNumber);
			if (oldRow != null) {
				final XSSFRow newRow = newSheet.createRow(rowNumber);
				newRow.setHeight(oldRow.getHeight());

				for (int columnNumber = oldRow.getFirstCellNum(); columnNumber < oldRow
						.getLastCellNum(); columnNumber++) {
					newSheet.setColumnWidth(columnNumber, oldSheet.getColumnWidth(columnNumber));

					final XSSFCell oldCell = oldRow.getCell(columnNumber);
					if (oldCell != null) {
						final XSSFCell newCell = newRow.createCell(columnNumber);

						// Copy value
						setCellValue(newCell, getCellValue(oldCell));

						// Copy style
						XSSFCellStyle newCellStyle = newWorkbook.createCellStyle();
						newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
						newCell.setCellStyle(newCellStyle);
					}
				}
			}

		}
		System.out.println("end Reading ");
		try {
			oldWorkbook.close();
			newWorkbook.write(new FileOutputStream("new.xlsx"));
			// newWorkbook.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	private static void setCellValue(final XSSFCell cell, final Object value) {
		if (value instanceof Boolean) {
			cell.setCellValue((boolean) value);
		} else if (value instanceof Byte) {
			cell.setCellValue((byte) value);
		} else if (value instanceof Double) {
			cell.setCellValue((double) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		} else {
			throw new IllegalArgumentException();
		}
	}

	private static Object getCellValue(final XSSFCell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue(); // boolean
		case Cell.CELL_TYPE_ERROR:
			return cell.getErrorCellValue(); // byte
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue(); // double
		case Cell.CELL_TYPE_FORMULA:
		case Cell.CELL_TYPE_STRING:
		case Cell.CELL_TYPE_BLANK:
			return cell.getStringCellValue(); // String
		default:
			throw new IllegalArgumentException();
		}
	}
}
