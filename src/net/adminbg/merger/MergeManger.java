/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger;

import static net.adminbg.merger.ui.Configuration.*;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.logging.AdminLogger;

/**
 * 
 * @author lachezar.nedelchev
 */
public enum MergeManger {
	INSTANCE;

	private final Logger logger = AdminLogger.INSTANCE
			.getLogger("net.adminbg.merger.MergeManger");

	public void close() {

		try {
			DBManager.INSTANCE.disconnect();
		} catch (SQLException e) {
			logger.log(Level.SEVERE,
					"Could not close connection to database .", e);
		}

	}

	public void merge(final Map<String, String> loaderMapping)
	
			throws IllegalArgumentException {
		final String errorMessage = "Could not open \"%s\".";
		
        for (String dirName : loaderMapping.keySet()){
    		logger.info("\"firstDirName\" =" + dirName);
    		Path path = Paths.get(dirName);
    		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS) || !Files.isReadable(path) || !Files.isDirectory(path, LinkOption.NOFOLLOW_LINKS)) {
    			final String msg = String.format(errorMessage, dirName);
    			throw new IllegalArgumentException(msg);
    		}
    		load(path, loaderMapping.get(dirName));
        }



		

	}

	private void load(final Path path, final String loaderClass) {
		String pathName = path.toString();
		logger.info(pathName);
		Loader loader = null;
		try {
			Class<?> forName = Class.forName(loaderClass);
			loader = (Loader) forName.newInstance();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Could not find Loader class.", e);
		}
		if (loader != null) {
			try {
				loader.load(path);
			} catch (IllegalArgumentException e) {
				logger.log(Level.SEVERE, "Invalid argument.", e);
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Invalis SQL statement.", e);

			}
		}
	}

}
