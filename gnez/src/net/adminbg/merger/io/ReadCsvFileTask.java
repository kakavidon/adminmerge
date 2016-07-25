package net.adminbg.merger.io;

import static net.adminbg.merger.ui.Configuration.NEW_LINE;
import static net.adminbg.merger.ui.Configuration.READCSVFILETASK_CSV_CHARSET;
import static net.adminbg.merger.ui.Configuration.READCSVFILETASK_CSV_DEIMITER;
import static net.adminbg.merger.ui.Configuration.READCSVFILETASK_ERROR_INVALID;
import static net.adminbg.merger.ui.Configuration.READCSVFILETASK_MESSAGE_FAIL;
import static net.adminbg.merger.ui.Configuration.READCSVFILETASK_MESSAGE_NEW;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.logging.ApplicationLogger;

/**
 * 
 * Reads a csv file. Puts the key field as key in the result map and the lookup
 * field as a value.
 * 
 * @author kakavidon
 * 
 */
public class ReadCsvFileTask extends FileTask<String, String> {

	private final Map<String, String> map = new TreeMap<>();
	private final int START_FROM = 2;
	private final int STORE_KEY_CELL_INDEX = 4;
	private final int STORE_VALUE_CELL_INDEX = 7;
	private static ApplicationLogger appLog = ApplicationLogger.INSTANCE;
	private static Logger LOGGER = appLog.getLogger(ReadCsvFileTask.class);

	public ReadCsvFileTask(Path file) {
		super(file);
		LOGGER.log(Level.INFO, READCSVFILETASK_MESSAGE_NEW);

	}

	@Override
	public Map<String, String> getMap() {
		return this.map;
	}

	@Override
	public int getWeight() {
		return 1;
	}

	@Override
	public FileTask<String, String> call() throws MergeException {
		final Charset forName = Charset.forName(READCSVFILETASK_CSV_CHARSET);
		final Path file = getFile();
		try (final BufferedReader br = Files.newBufferedReader(file, forName);) {
			StringBuilder s = new StringBuilder();
			java.lang.String line;
			int i = 0;
			while ((line = br.readLine()) != null) {
				if (i >= START_FROM) {
					final String[] columns = line
							.split(READCSVFILETASK_CSV_DEIMITER);
					if (columns == null || columns.length < 8) {
						final String msg = READCSVFILETASK_ERROR_INVALID;
						final String errorMsg = String.format(msg, file);
						MergeException ex = new MergeException(errorMsg);
						LOGGER.log(Level.SEVERE, errorMsg, ex);
						throw ex;
					} else {

						s.append(line).append(NEW_LINE);

						map.put(columns[STORE_KEY_CELL_INDEX],
								columns[STORE_VALUE_CELL_INDEX]);
					}

				}
				i++;
			}

		} catch (IOException e) {

			LOGGER.log(Level.SEVERE,
					String.format(READCSVFILETASK_MESSAGE_FAIL, file), e);
			throw new MergeException(e);
		}

		return this;
	}

}
