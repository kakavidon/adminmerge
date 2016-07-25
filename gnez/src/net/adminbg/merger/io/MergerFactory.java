package net.adminbg.merger.io;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.adminbg.merger.logging.ApplicationLogger;
import org.apache.poi.xssf.usermodel.XSSFRow;

import static net.adminbg.merger.ui.Configuration.MERGERFACTORY_MESSAGE;
import static net.adminbg.merger.ui.Configuration.MERGER_CLASS;
import static net.adminbg.merger.ui.Configuration.MERGER_EXCEPTION;

/**
 * 
 * Factory class that creates Merger instances based on configuration and user
 * selection.
 * 
 * @author lnedelc
 */
public class MergerFactory {
	private static final Logger LOGGER = ApplicationLogger.INSTANCE
			.getLogger(MergerFactory.class);

	private MergerFactory() {
	}

	public static MergerFactory getInstance() {
		return MergeFactoryHolder.INSTANCE;
	}

	private static class MergeFactoryHolder {
		private static final MergerFactory INSTANCE = new MergerFactory();
	}

	/**
	 * 
	 * Creates a Merger instance based on configuration and user
	 * 
	 * @param targetFile
	 *            Destination file where result will be stored.
	 * @param firstDirRows
	 *            Directory where source files are located (from one type)
	 * @param secondDirRows
	 *            Directory where source files are located (from another type)
	 * @param rows
	 *            header rows
	 * @return
	 */
	public Merger newMerger(final Path targetFile, Map<?, ?> firstDirRows,
			Map<?, ?> secondDirRows, List<XSSFRow> rows) throws MergeException {
		LOGGER.log(Level.INFO, MERGERFACTORY_MESSAGE, MERGER_CLASS);
		Merger newInstance = null;
		Class<?> classInstcnce;
		try {
			classInstcnce = Class.forName(MERGER_CLASS);
			@SuppressWarnings("unchecked")
			final Constructor<Merger> constructor = (Constructor<Merger>) classInstcnce
					.getConstructor(Path.class, Map.class, Map.class,
							List.class);
			newInstance = constructor.newInstance(targetFile, firstDirRows,
					secondDirRows, rows);

		} catch (NoSuchMethodException | SecurityException
				| ClassNotFoundException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {

			LOGGER.log(Level.SEVERE, MERGER_EXCEPTION, MERGER_CLASS);
			throw new MergeException(MERGER_EXCEPTION.replace("{0}",
					MERGER_CLASS));
		}
		if (newInstance == null) {
			throw new MergeException(MERGER_EXCEPTION.replace("{0}",
					MERGER_CLASS));
		}
		return newInstance;

	}
}
