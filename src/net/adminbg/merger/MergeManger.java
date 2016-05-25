/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger;

import java.io.File;

/**
 * 
 * @author lachezar.nedelchev
 */
public enum MergeManger {
	INSTANCE;

	public static MergeManger getInstance() {
		return INSTANCE;
	}

	public void close() {

	}

	public void merge(final String firstFileName, final String secondFileName)
			throws IllegalArgumentException {

		
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

	}

}
