package net.adminbg.merger;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.adminbg.merger.logging.AdminLogger;

public class ExcelReader extends CsvLoader{

	private final static String FILE_EXTENSION = "*.{xls,xlsx}"; 
	private static Logger logger = AdminLogger.INSTANCE.getLogger(ExcelReader.class.getName());
	
	@Override
	public String getFileExtension() {
		logger.info("");
		return FILE_EXTENSION;
	}

	@Override
	public void load(Path dirPath) throws SQLException,
			IllegalArgumentException {

		logger.info("AAAAA");
	}
	
	

}
