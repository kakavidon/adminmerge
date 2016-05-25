package net.adminbg.merger.logging;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import static net.adminbg.merger.ui.Configuration.*;

public enum AdminLogger {
	INSTANCE;
	private static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private static FileHandler fileTxt;

	private static SimpleFormatter formatterTxt;

	public void init(final String className) throws SecurityException, IOException {
		
		logger = Logger.getLogger(className); 
		
		Boolean isDevelopment = Boolean.valueOf(IS_DEVELOPMENT);
		if (!isDevelopment) {
			Logger rootLogger = Logger.getLogger("");
			Handler[] handlers = rootLogger.getHandlers();

			if (handlers[0] instanceof ConsoleHandler) {

				rootLogger.removeHandler(handlers[0]);

			}
		}
		
		logger.setLevel(Level.INFO);
		fileTxt = new FileHandler("./logs/adminmere.log");
		// create a TXT formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);

	}

	public void info(final String message) {
		logger.info(message);
	}

	public void warning(final String message) {
		logger.warning(message);
	}
	
	public void error(final String message, final Throwable throwable ) {
		logger.log(Level.SEVERE,message, throwable);
	}
	
	public void error(final String message) {
		logger.log(Level.SEVERE,message);
	}	
	public void error(final String message, final Exception ex ) {
		logger.log(Level.SEVERE,message, ex);
	}

}
