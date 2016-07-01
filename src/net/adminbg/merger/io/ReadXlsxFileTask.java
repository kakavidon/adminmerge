package net.adminbg.merger.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.adminbg.merger.logging.ApplicationLogger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author kakavidon
 */
public class ReadXlsxFileTask extends FileTask {

	private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(ReadXlsxFileTask.class);

	private final int START_FROM = 1;

	private final Map<String, XSSFRow> map = new TreeMap<>();

	public ReadXlsxFileTask(final Path file) {
		super(file);
		LOGGER.log(Level.INFO, "New instance of ReadXlsxFileTask created.");
	}

	@Override
	public Map<String, XSSFRow> getMap() {
		return this.map;
	}

	@Override
	public FileTask call() throws MergeException {
		int rIndex = 0;

		try (final XSSFWorkbook xlsx = new XSSFWorkbook(getFile().toFile());) {
			final XSSFSheet sheet = xlsx.getSheetAt(0);
			for (int rowIndex = START_FROM; rowIndex < sheet.getLastRowNum(); rowIndex++) {
				final XSSFRow row = sheet.getRow(rowIndex);
				rIndex = rowIndex;
				if (row == null) {
					continue;
				}
				final XSSFCell cell = row.getCell(13);
				if (cell == null) {
					continue;
				}

				map.put(readString(cell), row);

			}

		} catch (IOException | IllegalStateException | InvalidFormatException e) {
			LOGGER.log(Level.SEVERE, "Failed to read \"{0}\" at row {1}. ",
					new Object[] { getFile().toString(), rIndex });
			throw new MergeException(e);
		}
		return this;
	}

	private String readString(final XSSFCell cell) {
		String result = "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_NUMERIC:
			final double numericCellValue = cell.getNumericCellValue();
			result = String.valueOf(numericCellValue);
			break;
		case Cell.CELL_TYPE_BLANK:
			result = "";
			break;
		case Cell.CELL_TYPE_STRING:
			result = cell.getStringCellValue();
			break;
		default:
			break;
		}
		if (result.contains(".")) {
			result = result.split("\\.", 2)[0];
		}
		return result;
	}

	@Override
	public int getWeight() {
		return 4;
	}

}
