/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger;

import java.io.File;
import java.io.IOException;

import net.adminbg.merger.logging.AdminLogger;

import static net.adminbg.merger.ui.Configuration.*;

/**
 * 
 * @author lachezar.nedelchev
 */
public enum MergeManger {
	INSTANCE;

	final static AdminLogger logger = AdminLogger.INSTANCE;

	public static MergeManger getInstance() {
		try {
			logger.init(MergeManger.class.getName());
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return INSTANCE;
	}

	public void close() {

	}

	public void merge(final String firstFileName, final String secondFileName) throws IllegalArgumentException {
		logger.info("net.adminbg.merger.MergeManger  merge() "); 
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
				logger.error("Could not find Loader class.", e);
			}

		}

	}

}
