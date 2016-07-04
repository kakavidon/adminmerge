package net.adminbg.merger.io;

import static net.adminbg.merger.ui.Configuration.READXLSXFILETASK_MESSAGE_FAIL;
import static net.adminbg.merger.ui.Configuration.READXLSXFILETASK_MESSAGE_NEW;
import static net.adminbg.merger.ui.Configuration.XLSX_CELL_KEY_INDEX;
import static net.adminbg.merger.ui.Configuration.XLSX_START_FROM;
import static net.adminbg.merger.ui.Configuration.XLSX_NUMBER_FORMAT_ERROR;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.adminbg.merger.logging.ApplicationLogger;

/**
 * 
 * Reads an excel file. Puts the key field as key in the result map. Puts entire
 * excel row as a value into the result map. Duplicate keys and empty keys are
 * ignored.
 * 
 * @author kakavidon
 */
public class ReadXlsxFileTask extends FileTask<String, XSSFRow> {
	private static final ApplicationLogger appLog = ApplicationLogger.INSTANCE;
	private static final Logger LOGGER = appLog.getLogger(ReadXlsxFileTask.class);

	private int START_FROM;
	private int CELL_KEY_INDEX;

	private final Map<String, XSSFRow> map = new TreeMap<>();

	public ReadXlsxFileTask(final Path file) throws MergeException {
		super(file);
		LOGGER.log(Level.INFO, READXLSXFILETASK_MESSAGE_NEW);
		try {
			START_FROM = Integer.valueOf(XLSX_START_FROM);
			CELL_KEY_INDEX = Integer.valueOf(XLSX_CELL_KEY_INDEX);
		} catch (NumberFormatException e) {
			LOGGER.log(Level.SEVERE, XLSX_NUMBER_FORMAT_ERROR, e);
			throw new MergeException(e);
		}
	}

	@Override
	public Map<String, XSSFRow> getMap() {
		return this.map;
	}

	@Override
	public FileTask<String, XSSFRow> call() throws MergeException {
		int rIndex = 0;

		final File file = getFile().toFile();
		try (final XSSFWorkbook xlsx = (XSSFWorkbook) WorkbookFactory.create(file);) {
			final XSSFSheet sheet = xlsx.getSheetAt(0);
			for (int rowIndex = START_FROM; rowIndex < sheet.getLastRowNum(); rowIndex++) {
				final XSSFRow row = sheet.getRow(rowIndex);
				rIndex = rowIndex;
				if (row == null) {
					continue;
				}

				final XSSFCell cell = row.getCell(CELL_KEY_INDEX);
				if (cell == null) {
					continue;
				}
				map.put(readString(cell), row);
			}

		} catch (IOException | IllegalStateException | InvalidFormatException e) {
			LOGGER.log(Level.SEVERE, READXLSXFILETASK_MESSAGE_FAIL, new Object[] { getFile().toString(), rIndex });
			throw new MergeException(e);
		}
		return this;
	}

	private String readString(final XSSFCell cell) {
		String result = ""; //$NON-NLS-1$
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			final double numericCellValue = cell.getNumericCellValue();
			result = String.valueOf(numericCellValue);
			break;
		case Cell.CELL_TYPE_BLANK:
			result = ""; //$NON-NLS-1$
			break;
		case Cell.CELL_TYPE_STRING:
			result = cell.getStringCellValue();
			break;
		default:
			break;
		}
		if (result.contains(".")) { //$NON-NLS-1$
			result = result.split("\\.", 2)[0]; //$NON-NLS-1$
		}
		return result;
	}

	@Override
	public int getWeight() {
		return 4;
	}

}
