package net.adminbg.merger.io;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ConverterFactory {

	private static final Logger logger = Logger.getLogger(ConverterFactory.class.getName());

	public static Converter createConverter(final String className) throws ImportException {
		try {
			final Class<?> classInstance = Class.forName(className);
			return (Converter) classInstance.newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			final String msg = "Could not create coverter for " + className;
			logger.log(Level.SEVERE, msg, e);
		}
		return null;

	}
}
