package net.adminbg.merger.io;

import static net.adminbg.merger.ui.Configuration.DOT;
import static net.adminbg.merger.ui.Configuration.ESCAPED_DOT;
import static net.adminbg.merger.ui.Configuration.MESSAGE_NO_CHANGE_1;
import static net.adminbg.merger.ui.Configuration.MESSAGE_NO_CHANGE_2;
import static net.adminbg.merger.ui.Configuration.MESSAGE_NO_CHANGE_3;
import static net.adminbg.merger.ui.Configuration.MESSAGE_START;
import static net.adminbg.merger.ui.Configuration.MESSAGE_SUCCESS;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.logging.ApplicationLogger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 
 * This class will match exactly (literally) the keys from the source files and
 * then will lookup and replace the target values.
 * 
 * @author lnedelc
 */
public class SimpleMerger extends Merger {
	private static final ApplicationLogger appLog = ApplicationLogger.INSTANCE;
	private static final Logger LOGGER = appLog.getLogger(SimpleMerger.class);

	public SimpleMerger(Path targetFile, Map<?, ?> firstDirRows,
			Map<?, ?> secondDirRows, final List<XSSFRow> headerRows) {
		super(targetFile, firstDirRows, secondDirRows, headerRows);
	}

	/**
	 * 
	 * Matches the source files by a key value. If a match is found the quantity
	 * is replaced and row is inserted into the result file.
	 * 
	 */

	@Override
	public void merge() throws MergeException {
		try (final XSSFWorkbook book = new XSSFWorkbook();
				final OutputStream out = Files.newOutputStream(getTargetFile());) {
			@SuppressWarnings("unchecked")
			final Map<String, String> firstDirRows = (Map<String, String>) getFirstDirRows();
			@SuppressWarnings("unchecked")
			final Map<String, XSSFRow> secondDirRowsRows = (Map<String, XSSFRow>) getSecondDirRows();
			firstDirRows.keySet().retainAll(secondDirRowsRows.keySet());

			if (firstDirRows.isEmpty()) {
				final String msg = MESSAGE_NO_CHANGE_1 + MESSAGE_NO_CHANGE_2
						+ MESSAGE_NO_CHANGE_3;
				LOGGER.warning(msg);
				throw new MergeException(msg);
			}
			final XSSFRow firstRow = secondDirRowsRows.values().iterator()
					.next();
			final XSSFSheet sheet = book.createSheet(firstRow.getSheet()
					.getSheetName());

			final List<XSSFRow> headerRows = getHeaderRows();
			int rowIndex = copyRows(book, sheet, headerRows);
			LOGGER.log(Level.INFO, MESSAGE_START, firstDirRows.size());
			int lookups = 0;
			for (Map.Entry<String, String> entry : firstDirRows.entrySet()) {
				final String key = entry.getKey();
				String value = entry.getValue();
				if (value.contains(DOT)) {
					value = value.split(ESCAPED_DOT, 2)[0];
				}
				final Integer intValue = Integer.valueOf(value);
				final XSSFRow row = sheet.createRow(rowIndex);
				final XSSFRow oldRow = secondDirRowsRows.get(key);
				final XSSFCell cell = oldRow.getCell(17);
				if (cell == null) {
					continue;
				}
				final int quantity = getCellValue(cell);
				if (quantity == intValue) {
					continue;
				}
				lookups++;
				copyRow(book, sheet, oldRow, row, quantity);
				rowIndex++;
			}

			book.write(out);
			LOGGER.log(Level.INFO, MESSAGE_SUCCESS, lookups);
		} catch (IOException ex) {
			throw new MergeException(ex);

		}

	}

	/**
	 * Copies an excel row from one workbook to another. Also replaces the
	 * quantity value.
	 * 
	 * @param destinationWorkbook
	 *            Destination Workbook
	 * @param sheet
	 *            Destination sheet
	 * @param oldRow
	 *            original row
	 * @param newRow
	 *            new row
	 * @param newQuantity
	 *            new quantity
	 */
	protected void copyRow(final XSSFWorkbook destinationWorkbook,
			final XSSFSheet sheet, final XSSFRow oldRow, final XSSFRow newRow,
			int newQuantity) {
		for (int cellIndx = 0; cellIndx < oldRow.getLastCellNum(); cellIndx++) {
			// Grab a copy of the old/new cell
			XSSFCell oldCell = oldRow.getCell(cellIndx);
			XSSFCell newCell = newRow.createCell(cellIndx);
			// If the old cell is null jump to next cell
			if (oldCell == null) {
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
			if (cellIndx == 15) {
				newCell.setCellValue(newQuantity);
				continue;
			}
			setCellValue(oldCell, newCell);
		}
	}

	/**
	 * Copies an excel row from one workbook to another. quantity value.
	 * 
	 * @param destinationWorkbook
	 *            Destination Workbook
	 * @param sheet
	 *            Destination sheet
	 * @param oldRow
	 *            original row
	 * @param newRow
	 *            new row
	 * @param newQuantity
	 *            new quantity
	 */
	protected void copyRow(final XSSFWorkbook destinationWorkbook,
			final XSSFSheet sheet, final XSSFRow oldRow, final XSSFRow newRow) {
		for (int cellIndx = 0; cellIndx < oldRow.getLastCellNum(); cellIndx++) {
			// Grab a copy of the old/new cell
			XSSFCell oldCell = oldRow.getCell(cellIndx);
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
			setCellValue(oldCell, newCell);
		}
	}

	/**
	 * 
	 * Copy header rows from one workbook to another.
	 * 
	 * @param destinationWorkbook
	 *            Destination Workbook
	 * @param sheet
	 *            new sheet
	 * @param headerRow
	 *            header row(s)
	 * @return
	 */
	protected int copyRows(final XSSFWorkbook destinationWorkbook,
			final XSSFSheet sheet, final List<XSSFRow> headerRow) {
		int result = 0;
		for (XSSFRow oldRow : headerRow) {
			final XSSFRow newRow = sheet.createRow(result);
			copyRow(destinationWorkbook, sheet, oldRow, newRow);
			result++;
		}
		return result;
	}

	/**
	 * Sets a value to a new cell
	 * 
	 * @param oldCell
	 *            original cell
	 * @param newCell
	 *            a new cell
	 */
	protected void setCellValue(XSSFCell oldCell, XSSFCell newCell) {
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

	/**
	 * Extracts an integer value from a cell
	 * 
	 * @param cell
	 *            the cell
	 * @return the integer value from the cell
	 */

	private int getCellValue(XSSFCell cell) {
		int result = -1;
		switch (cell.getCellType()) {

		case Cell.CELL_TYPE_NUMERIC:
			result = (int) cell.getNumericCellValue();
			break;
		case Cell.CELL_TYPE_STRING:
			result = Integer.valueOf(cell.getStringCellValue());
			break;
		default:
			result = -1;
			break;
		}

		return result;
	}
}
