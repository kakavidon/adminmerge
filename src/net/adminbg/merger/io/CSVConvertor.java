package net.adminbg.merger.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

import net.adminbg.merger.logging.AdminLogger;

import static net.adminbg.merger.io.FileTest.*;
import static net.adminbg.merger.ui.Configuration.*;

public class CSVConvertor {
	private static Logger logger = AdminLogger.INSTANCE.getLogger(CSVConvertor.class.getName());

	private final StringBuffer data = new StringBuffer();
	private final int[] cellIndecies;

	public CSVConvertor() throws ImportException {
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

	private int[] toIntArray(String[] split) throws ImportException {
		int[] result = {};
		if (split == null) {
			throw new ImportException("Invalid configuration. Cell indecies not defined.");
		}

		for (int i = 0; i < split.length; i++) {
			result[i] = Integer.valueOf(split[i]);
		}

		return result;
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
