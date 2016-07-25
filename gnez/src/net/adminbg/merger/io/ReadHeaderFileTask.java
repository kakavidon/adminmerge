package net.adminbg.merger.io;

import static net.adminbg.merger.ui.Configuration.READHEADERFILETASK_MESSAGE_COPY;
import static net.adminbg.merger.ui.Configuration.READHEADERFILETASK_MESSAGE_NEW;
import static net.adminbg.merger.ui.Configuration.XLSX_HEADER_ROW_COUNT;
import static net.adminbg.merger.ui.Configuration.XLSX_HEADER_START_FROM;
import static net.adminbg.merger.ui.Configuration.XLSX_NUMBER_FORMAT_ERROR;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.adminbg.merger.logging.ApplicationLogger;

/**
 * Reads the header of the excel file and puts it into the result map as a
 * value.Puts the key field as key in the result map.
 *
 * @author kakavidon
 */
class ReadHeaderFileTask extends FileTask<String, XSSFRow> {

    private static final ApplicationLogger appLog = ApplicationLogger.INSTANCE;
    private static final Logger LOGGER = appLog.getLogger(ReadHeaderFileTask.class);

    private int START_FROM;
    private int ROW_COUNT;
    private Map<String, XSSFRow> map = new TreeMap<>();

    public ReadHeaderFileTask(final Path excelFile) throws MergeException {
        super(excelFile);
        LOGGER.log(Level.INFO, READHEADERFILETASK_MESSAGE_NEW);
        try {
            ROW_COUNT = Integer.valueOf(XLSX_HEADER_ROW_COUNT);
            START_FROM = Integer.valueOf(XLSX_HEADER_START_FROM);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.SEVERE, XLSX_NUMBER_FORMAT_ERROR, e);
            throw new MergeException(e);
        }

    }

    @Override
    public Map<String, XSSFRow> getMap() {
        return map;
    }

    @Override
    public FileTask<String, XSSFRow> call() throws MergeException {

        try (final XSSFWorkbook xlsx = (XSSFWorkbook) WorkbookFactory.create(getFile().toFile());) {

            final XSSFSheet sheet = xlsx.getSheetAt(0);
            LOGGER.log(Level.INFO, READHEADERFILETASK_MESSAGE_COPY, ROW_COUNT);
            for (int rowIndex = START_FROM; rowIndex < ROW_COUNT; rowIndex++) {
                final XSSFRow row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                final XSSFCell cell = row.getCell(4);
                if (cell == null) {
                    continue;
                }

                map.put(readString(cell), row);

            }
        } catch (InvalidFormatException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            throw new MergeException(ex);
        }
        return this;
    }

    private String readString(final XSSFCell cell) {
        return cell.getStringCellValue();
    }

    @Override
    public int getWeight() {
        return 4;
    }

}
