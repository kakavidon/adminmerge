package net.adminbg.merger;

import static net.adminbg.merger.ui.Configuration.NEW_LINE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CsvLoader implements Loader {

	private static final Logger logger = Logger.getLogger(DBManager.class.getName());

	@Override
	public void read(final String fileName) throws SQLException, IllegalArgumentException {

		logger.info("Creating a new instance of net.adminbg.merger.ExcelReader");
		if (fileName == null || fileName.equals("")) {
			final String message = "File name should not be empty";
			IllegalArgumentException ex = new IllegalArgumentException(message);
			logger.log(Level.SEVERE,message, ex);
			throw ex;
		}

		File file = new File(fileName);
		if (!file.exists() || !file.canRead()) {
			final String message = "File \"" + fileName + "\" is invalid.";
			IllegalArgumentException ex = new IllegalArgumentException(message);
			logger.log(Level.SEVERE,message, ex);
			throw ex;
		}
		try {
			readTextFile(fileName, fileName + "_new", 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final String sql = "create table test(id int ,name char(10));SELECT * FROM TEST";
		try {
			try {
				DBManager.INSTANCE.connect();
				DBManager.INSTANCE.runSQL(sql);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			DBManager.INSTANCE.disconnect();
		}

	}

	public void readTextFile(String fileName, String out, int skipLines) throws IOException {

		FileInputStream fStream = new FileInputStream(fileName);
		DataInputStream in = new DataInputStream(fStream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));
		String readLine;
		int curLineNr = 1;

		while ((readLine = br.readLine()) != null) {
			if (curLineNr++ <= skipLines) {
				continue;
			}

			writer.write(readLine + NEW_LINE);
		}

		in.close();
		writer.close();

	}
}
