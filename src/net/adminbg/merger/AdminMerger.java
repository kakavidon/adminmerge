package net.adminbg.merger;

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
		
		logger.info("Starting application");
		
		logger.info("Starting main window");
		MainWindow.start();
		
	}

}
