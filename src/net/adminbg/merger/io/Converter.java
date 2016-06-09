package net.adminbg.merger.io;

import java.nio.file.Path;

public interface Converter {
	public void mergeFiles(final Path dirPath) throws ImportException;
	public Path getConvertedFile();
}
