package net.adminbg.merger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import static net.adminbg.merger.ui.Configuration.*;
import org.h2.tools.Csv;
import org.h2.tools.SimpleResultSet;

import net.adminbg.merger.logging.AdminLogger;

public class CsvLoader implements Loader {

	final AdminLogger logger = AdminLogger.INSTANCE;

	@Override
	public void read(final String fileName) throws SQLException, IllegalArgumentException {

		logger.info("Creating a new instance of net.adminbg.merger.ExcelReader");
		if (fileName == null || fileName.equals("")) {
			final String message = "File name should not be empty";
			IllegalArgumentException ex = new IllegalArgumentException(message);
			logger.error(message, ex);
			throw ex;
		}

		File file = new File(fileName);
		if (!file.exists() || !file.canRead()) {
			final String message = "File \"" + fileName + "\" is invalid.";
			IllegalArgumentException ex = new IllegalArgumentException(message);
			logger.error(message, ex);
			throw ex;
		}
		try {
			readTextFile(fileName, fileName + "_new", 2);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Csv csv = new Csv(); // csv.setFieldDelimiter(';');
		SimpleResultSet rs2 = new SimpleResultSet();
		ResultSet rs = csv.read(fileName, null, null);
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount() - 1;
		for (int i = 0; i < columnCount; i++) {
			rs2.addColumn(meta.getColumnLabel(1 + i), Types.VARCHAR, 0, 0);
		}
		int startRowIndex = 2;
		int j = 0;
		while (rs.next()) {
			System.out.println("==" + columnCount);
			if (startRowIndex <= j) {
				continue;
			}
			j++;

			Object[] row = new Object[columnCount];
			for (int i = 0; i < columnCount; i++) {
				String s = rs.getString(1 + i);
				System.out.println(s);
				if (i == 3) {
					int dot = s.indexOf('.');
					if (dot >= 0) {
						s = s.substring(0, dot + 1);
					}
				}
				row[i] = s;
			}
			rs2.addRow(row);
		}
		/*
		 * 
		 * try { BufferedWriter writer = new BufferedWriter(new
		 * FileWriter(fileName + "_new"));
		 * 
		 * csv.write(writer, rs2); rs.close(); } catch (IOException e1) { //
		 * TODO Auto-generated catch block e1.printStackTrace(); }
		 * 
		 * // csv.write(new , arg1) String url = "jdbc:h2:.\\data\\admin.dat" +
		 * ";FILE_LOCK=FS" + ";PAGE_SIZE=1024" + ";CACHE_SIZE=8192";
		 * 
		 * try { Class.forName("org.h2.Driver"); } catch (ClassNotFoundException
		 * e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 * Statement stmt = null; Connection conn =
		 * DriverManager.getConnection(url); System.out.println(
		 * "Connected database successfully...");
		 * 
		 * // STEP 4: Execute a query System.out.println("Creating statement..."
		 * ); stmt = conn.createStatement();
		 * 
		 * String sql = "SELECT * FROM TEST"; ResultSet rsx =
		 * stmt.executeQuery(sql); // STEP 5: Extract data from result set while
		 * (rs.next()) { // Retrieve by column name
		 * 
		 * String first = rsx.getString(1); ; String last = rsx.getString(2); ;
		 * String last1 = rsx.getString(3); ;
		 * 
		 * System.out.println(String.format("%s, %s, %s", first, last, last1));
		 * } rsx.close();
		 * 
		 */
	}

	public void pop(String in, String out, int rowsToSkip) throws Exception {
		String[] ops = { "store", "group", "subgroup", "name", "code", "barcode", "unit", "quantity", "vendor" };
		Csv csv = new Csv();
		csv.setLineCommentCharacter('#');
		csv.setFieldDelimiter(';');
		ResultSet rs = csv.read(in, ops, null);
		SimpleResultSet rs2 = new SimpleResultSet();
		ResultSetMetaData meta = rs.getMetaData();
		int columnCount = meta.getColumnCount() - 1;
		for (int i = 0; i < columnCount; i++) {
			rs2.addColumn(meta.getColumnLabel(1 + i), Types.VARCHAR, 0, 0);
		}
		int j = 0;
		while (rs.next()) {
			Object[] row = new Object[columnCount];

			if (rowsToSkip >= j) {
				j++;
				continue;
			}

			for (int i = 0; i < columnCount; i++) {
				String s = rs.getString(1 + i);
				row[i] = s;
			}
			rs2.addRow(row);
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(out));

		csv = new Csv();
		csv.setLineSeparator("\n");
		csv.write(writer, rs2);
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
