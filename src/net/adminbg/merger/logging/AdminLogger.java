package net.adminbg.merger.logging;

import static net.adminbg.merger.ui.Configuration.IS_DEVELOPMENT;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import net.adminbg.merger.EmptySupplier;

public enum AdminLogger {
	INSTANCE;

	private static FileHandler fileTxt;

	private static SimpleFormatter formatterTxt;
	public static final EmptySupplier<String> EMPTY_SUPPLIER = new EmptySupplier<String>();

	public Logger getLogger(final String className) {

		Logger logger = Logger.getLogger(className);

		Boolean isDevelopment = Boolean.valueOf(IS_DEVELOPMENT);
		if (!isDevelopment) {
			Logger rootLogger = Logger.getLogger("");
			Handler[] handlers = rootLogger.getHandlers();

			if (handlers[0] instanceof ConsoleHandler) {
				if (handlers != null && handlers.length > 0) {
					rootLogger.removeHandler(handlers[0]);
				}
			}
		}

		logger.setLevel(Level.INFO);
		try {
			fileTxt = new FileHandler("./logs/adminmere.log");
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		// create a TXT formatter
		formatterTxt = new SimpleFormatter();
		fileTxt.setFormatter(formatterTxt);
		logger.addHandler(fileTxt);
		return logger;

	}

}
