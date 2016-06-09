/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger;

import static net.adminbg.merger.io.FileTest.EXISTS;
import static net.adminbg.merger.io.FileTest.IS_DIRECTORY;
import static net.adminbg.merger.io.FileTest.READABLE;
import static net.adminbg.merger.io.FileTest.validate;
import static net.adminbg.merger.logging.AdminLogger.EMPTY_SUPPLIER;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.io.CSVLoader;
import net.adminbg.merger.io.Converter;
import net.adminbg.merger.io.ConverterFactory;
import net.adminbg.merger.io.ImportException;
import net.adminbg.merger.io.InvalidFileException;
import net.adminbg.merger.io.XSLXConverter;
import net.adminbg.merger.logging.AdminLogger;

/**
 * 
 * @author lachezar.nedelchev
 */
public enum MergeManager {
	INSTANCE;

	private final Logger logger = AdminLogger.INSTANCE.getLogger("net.adminbg.merger.MergeManger");

	public void close() {

		try {
			DBManager.getInstance().dispose();
		} catch (SQLException e) {
			final String msg = "Could not dispose connection to database .";
			logger.log(Level.SEVERE, msg, e);
		}

	}

	public void merge(final Map<String, String> loaderMapping, final File targetFile) throws ImportException {

		for (String dirName : loaderMapping.keySet()) {
			Path targetDirectory = Paths.get(dirName);
			logger.info("\"Directory Name\" = " + dirName);
			try {
				validate(targetDirectory, EXISTS, IS_DIRECTORY, READABLE);
			} catch (InvalidFileException e) {
				final String errorMessage = "Could not open \"%s\".";
				logger.log(Level.SEVERE, e, EMPTY_SUPPLIER);
				throw new ImportException(errorMessage, e);
			}
			final String className = loaderMapping.get(dirName);
			final Converter converter = ConverterFactory.createConverter(className);
			if ( converter instanceof XSLXConverter) {
				continue;
			}
			converter.mergeFiles(targetDirectory);
			final Path convertedTempFile = converter.getConvertedFile();
			System.out.println(convertedTempFile.toFile().length());
			CSVLoader c = new CSVLoader();
			c.loadFile(converter.getConvertedFile());
			logger.info("ready");
			//csvConverter.mergeFiles(targetDirectory);
		}

	}

}
