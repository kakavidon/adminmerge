package net.adminbg.merger.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public enum AdminLogger {
	INSTANCE;
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static FileHandler fileTxt;

	private static SimpleFormatter formatterTxt;
	
	static {
		
		try {
			init();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}

	private static void init() throws SecurityException, IOException {
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();

		if (handlers[0] instanceof ConsoleHandler) {

			rootLogger.removeHandler(handlers[0]);

		}
		logger.setLevel(Level.INFO);
		fileTxt = new FileHandler("adminmere.log");
		// create a TXT formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);

	}
	
	public void info(final String message){
		logger.info(message);
	}
	
	public void warning(final String message){
		logger.warning(message);
	}
	
	

}
