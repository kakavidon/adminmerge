/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger;

import static net.adminbg.merger.ui.Configuration.SHOP_FILE_READER;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.logging.AdminLogger;

/**
 * 
 * @author lachezar.nedelchev
 */
public enum MergeManger {
	INSTANCE;

	private static Logger logger = AdminLogger.INSTANCE.getLogger(MergeManger.class.getName());


	public void close() {

	}

	public void merge(final String firstFileName, final String secondFileName) throws IllegalArgumentException {

		logger.info("\"firstFileName\" ="+firstFileName); 
		logger.info("\"secondFileName\"="+secondFileName); 
		
		File firstFile = new File(firstFileName);
		File secondFile = new File(secondFileName);
		final String errorMessage = "Could not open %s file \"%s\".";

		if (!firstFile.exists() || !firstFile.canRead()) {
			final String msg = String.format(errorMessage, "first", firstFile);
			throw new IllegalArgumentException(msg);
		}

		if (!secondFile.exists() || !secondFile.canRead()) {
			final String msg = String.format(errorMessage, "second", secondFile);
			throw new IllegalArgumentException(msg);
		}
		getLoaderAndRead(firstFileName);

	}

	private void getLoaderAndRead(final String fileName) {

		if (fileName.toUpperCase().contains(".CSV")) {
			try {
				Class<?> forName = Class.forName(SHOP_FILE_READER);

				Loader loader = (Loader) forName.newInstance();
				loader.read(fileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE,"Could not find Loader class.", e);
			}

		}

	}

}
