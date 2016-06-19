/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger.io;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author kakavidon
 */
public class ReadXlsxFileTask extends FileTask {

    private final int START_FROM = 1;

    private Map<String, XSSFRow> map = new TreeMap<>();

    public ReadXlsxFileTask(final Path file) {
        super(file);
    }

    @Override
    public Map<String, XSSFRow> getMap() {
        return this.map;
    }

    @Override
    public FileTask call() throws Exception {
        final XSSFWorkbook xlsx;
        try {
            xlsx = new XSSFWorkbook(getFile().toFile());
            final XSSFSheet sheet = xlsx.getSheetAt(0);
            for (int rowIndex = START_FROM; rowIndex < sheet.getLastRowNum(); rowIndex++) {
                final XSSFRow row = sheet.getRow(rowIndex);
                if (row == null) {
                    continue;
                }
                final XSSFCell cell = row.getCell(3);
                if (cell == null) {
                    continue;
                }

                map.put(readString(cell), row);

            }
            
        } catch (IOException e) {
            //TODO handle
        }
        return this;
    }

    private String readString(final XSSFCell cell) {
        return cell.getStringCellValue();
    }
    
    @Override
    public int getWeight(){
        return 4;
    }

}
