package net.adminbg.merger;


import java.nio.file.Paths;
import java.sql.SQLException;

import net.adminbg.merger.io.CSVImporter;
import net.adminbg.merger.io.ImportException;
import net.adminbg.merger.io.LoaderCopy;
import net.adminbg.merger.io.InvalidFileException;
import net.adminbg.merger.io.XSLXImporter;

public class ToCSV {

	public static void main(String[] args) {
		for (int i = 0; i < 1; i++) {
			test();
		}

	}

	private static void test() {
		System.out.println("Start");
		
		final long startTime = System.currentTimeMillis();
		final DBManager instance = DBManager.getInstance();
		instance.start();

		LoaderCopy i = new CSVImporter();
		XSLXImporter ex = new XSLXImporter();
		try {
			i.importFiles(Paths.get("C:\\Users\\Lachezar.Nedelchev\\git\\adminmerge\\store"));
			ex.setDestination(Paths.get("D:\\Dev\\result.xlsx"));
			ex.importFiles(Paths.get("C:\\Users\\Lachezar.Nedelchev\\git\\adminmerge\\shop"));
		} catch (InvalidFileException | ImportException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			instance.dispose();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final long after = System.currentTimeMillis();
		System.out.println("elapsed : " +(after - startTime));
	}
}