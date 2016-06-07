package net.adminbg.merger.io;

import java.nio.file.Path;

public interface Exporter {
	
	public void setDestination(final Path path)  throws InvalidFileException;
}
