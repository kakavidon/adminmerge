package net.adminbg.merger;

import java.sql.SQLException;

public interface Loader {
	public void read(final String fileName) throws SQLException, IllegalArgumentException;
}
