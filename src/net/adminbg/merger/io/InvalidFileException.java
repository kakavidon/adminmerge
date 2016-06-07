package net.adminbg.merger.io;

import java.io.IOException;

public class InvalidFileException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7489680178198814834L;

	public InvalidFileException(final String msg) {
		super(msg);
	}

	public InvalidFileException(String msg, Throwable e) {
		super(msg, e);
	}
}
