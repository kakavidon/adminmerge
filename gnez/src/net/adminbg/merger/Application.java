package net.adminbg.merger;

import static net.adminbg.merger.ui.Configuration.APPLICATION_WELCOME_MESSAGE;
import static net.adminbg.merger.ui.Configuration.APPLICATION_INFO;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.logging.ApplicationLogger;
import net.adminbg.merger.ui.Configuration;
import net.adminbg.merger.ui.MainWindow;

/**
 * 
 * Main class of the  application.
 * 
 * @author lachezar.nedelchev
 */
public class Application {
	private static final ApplicationLogger appLog = ApplicationLogger.INSTANCE;
	private static final Logger LOGGER = appLog.getLogger(Application.class);

	/**
	 * 
	 * The main method of the application.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String[] args) {
		printConfiguration();
		LOGGER.info(APPLICATION_WELCOME_MESSAGE);
		final MainWindow mainWindow = new MainWindow();
		mainWindow.start();
	}

	/**
	 * Prints public static field names and values from
	 * net.adminbg.merger.ui.Configuration
	 * 
	 */
	private static void printConfiguration() {
		try {
			final Class<? extends Configuration> instance = Configuration.INSTANCE
					.getClass();
			final Field[] declaredFields = instance.getDeclaredFields();
			for (Field field : declaredFields) {
				final int modifiers = field.getModifiers();
				if ((java.lang.reflect.Modifier.isStatic(modifiers))
						&& java.lang.reflect.Modifier.isPublic(modifiers)) {
					final String name = field.getName();
					final Object object = field.get(null);
					if (field.getType() == String.class) {
						final String strValue = (String) object;
						LOGGER.log(Level.INFO, APPLICATION_INFO, new Object[] {
								name, strValue });
					}
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
