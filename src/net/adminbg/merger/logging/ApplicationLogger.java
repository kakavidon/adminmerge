package net.adminbg.merger.logging;

import static net.adminbg.merger.ui.Configuration.IS_DEVELOPMENT;
import static net.adminbg.merger.ui.Configuration.EMPTY_STRING;
import static net.adminbg.merger.ui.Configuration.LOG_DIR;
import static net.adminbg.merger.ui.Configuration.LOG_FILE;
import static net.adminbg.merger.ui.Configuration.ERROR_LOG_FILE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import net.adminbg.merger.io.FileTest;
import net.adminbg.merger.io.MergeException;

public enum ApplicationLogger {
	INSTANCE;

	private static final FileHandler fileTxtHandler = getHandler();
	private static final Logger LOGGER = Logger
			.getLogger(ApplicationLogger.class.getName());
	private static SimpleFormatter formatterTxt;

	public Logger getLogger(final Class<?> theClass) {

		Logger logger = Logger.getLogger(theClass.getClass().toString());

		Boolean isDevelopment = Boolean.valueOf(IS_DEVELOPMENT);
		if (!isDevelopment) {
			Logger rootLogger = Logger.getLogger(EMPTY_STRING);
			Handler[] handlers = rootLogger.getHandlers();
			if (handlers != null && handlers.length > 0) {
				if (handlers[0] instanceof ConsoleHandler) {

					rootLogger.removeHandler(handlers[0]);
				}
			}
		}

		logger.setLevel(Level.INFO);
		logger.addHandler(fileTxtHandler);
		return logger;

	}

	private static FileHandler getHandler() {
		FileHandler fileHandler;
		try {
			final Path path = Paths.get(LOG_DIR);
			if (!FileTest.EXISTS.check(path)) {
				Files.createDirectory(path);
			}
			fileHandler = new FileHandler(LOG_DIR + LOG_FILE);
			// create a TXT formatter
			formatterTxt = new SimpleFormatter();
			fileHandler.setFormatter(formatterTxt);
			return fileHandler;
		} catch (SecurityException | MergeException | IOException e) {
			try {
				e.printStackTrace();
				LOGGER.addHandler(new FileHandler(ERROR_LOG_FILE));
				LOGGER.log(Level.SEVERE, null, e);
				return null;
			} catch (IOException | SecurityException ex) {
				ex.printStackTrace();
				return null;
			}

		}

	}

}
