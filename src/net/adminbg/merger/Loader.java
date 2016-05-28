package net.adminbg.merger;

import java.nio.file.Path;
import java.sql.SQLException;

public interface Loader {
	
	public String getFileExtension();

	public void load(final Path path) throws SQLException,IllegalArgumentException;
}
