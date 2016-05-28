package net.adminbg.merger;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.ui.MainWindow;

/**
 * 
 * @author lachezar.nedelchev
 */
public class AdminMerger {

	private static final Logger logger = Logger.getLogger(AdminMerger.class.getName());

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {

		try {
			logger.info("Starting DBManager ... ");
			DBManager.INSTANCE.connect();
			logger.info("Starting main window");
			MainWindow.start();
		} catch (ClassNotFoundException e) {
			logger.log(Level.SEVERE, "Unable to create DBManager.", e);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "SQL error .", e);
		}

	}

}
