
package net.adminbg.merger.io;

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
 * @author lnedelc
 */
public class SimpleMerger extends Merger {

	private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(SimpleMerger.class);

	public SimpleMerger(Path targetFile, Map<?, ?> firstDirRows, Map<?, ?> secondDirRows,
			final List<XSSFRow> headerRows) {
		super(targetFile, firstDirRows, secondDirRows, headerRows);
	}

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
				final String msg = "There are no changes detectd. Either \n" + "there is no match by key or nothing "
						+ "was changed.";
				LOGGER.warning(msg);
				throw new MergeException(msg);
			}

			final XSSFSheet sheet = book.createSheet("Products");

			final List<XSSFRow> headerRows = getHeaderRows();
			int rowIndex = copyRows(book, sheet, headerRows);
			LOGGER.log(Level.INFO, "Starting processing of {0} matches", firstDirRows.size());
			for (Map.Entry<String, String> entry : firstDirRows.entrySet()) {
				final String key = entry.getKey();
				final String value = entry.getValue();
				final Integer intValue = Integer.valueOf(value);
				final XSSFRow row = sheet.createRow(rowIndex);
				final XSSFRow oldRow = secondDirRowsRows.get(key);
				final XSSFCell cell = oldRow.getCell(15);
				if (cell == null) {
					continue;
				}
				final int quantity = getCellValue(cell);
				if (quantity == intValue) {
					continue;
				}
				copyRow(book, sheet, oldRow, row, quantity);
				rowIndex++;
			}

			book.write(out);
			LOGGER.log(Level.INFO, "Merging finished successfully.");
		} catch (IOException ex) {
			throw new MergeException(ex);

		}

	}//

	protected void copyRow(final XSSFWorkbook destinationWorkbook, final XSSFSheet sheet, final XSSFRow oldRow,
			final XSSFRow newRow, int newQuantity) {
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

	protected void copyRow(final XSSFWorkbook destinationWorkbook, final XSSFSheet sheet, final XSSFRow oldRow,
			final XSSFRow newRow) {
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

	protected int copyRows(final XSSFWorkbook destinationWorkbook, final XSSFSheet sheet,
			final List<XSSFRow> headerRow) {
		int result = 0;
		for (XSSFRow oldRow : headerRow) {
			final XSSFRow newRow = sheet.createRow(result);
			copyRow(destinationWorkbook, sheet, oldRow, newRow);
			result++;
		}
		return result;
	}

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
