package net.adminbg.merger.io;

import static net.adminbg.merger.io.FileTest.EXISTS;
import static net.adminbg.merger.io.FileTest.IS_DIRECTORY;
import static net.adminbg.merger.io.FileTest.READABLE;
import static net.adminbg.merger.io.FileTest.isEmptyDirectory;
import static net.adminbg.merger.io.FileTest.validate;
import static net.adminbg.merger.ui.Configuration.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.logging.ApplicationLogger;
import net.adminbg.merger.ui.SelectionInfo;
import net.adminbg.merger.ui.TaskComponent;

import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 * 
 * Dispatches tasks to read input files and then merges the result by using
 * Megrer specified in the configuration of the application.
 * 
 * @author kakavidon
 * 
 */

public enum TaskDispatcher {

	INSTANCE;
	private static final ApplicationLogger appLog = ApplicationLogger.INSTANCE;
	private static final Logger LOGGER = appLog.getLogger(TaskDispatcher.class);
	private Map<String, String> directories = new TreeMap<>();
	private Set<SelectionInfo> selection = new TreeSet<>();

	private Path targetFile;

	public void parseDirectories() throws IOException, MergeException {
		if (directories.isEmpty()) {
			throw new IllegalArgumentException(TASKDISPATCHER_MESSAGE_MISSING_1
					+ TASKDISPATCHER_MESSAGE_MISSING_2);
		} else {
			final int size = directories.size();
			if (size != 2) {
				throw new IllegalArgumentException(String.format(
						TASKDISPATCHER_MESSAGE_ILLEGAL_1
								+ TASKDISPATCHER_MESSAGE_ILLEGAL_2, size));
			}
		}

		for (String dirString : directories.keySet()) {

			Path dir = Paths.get(dirString);
			try {
				validate(dir, EXISTS, IS_DIRECTORY, READABLE);
			} catch (MergeException ex) {
				throw new IllegalArgumentException(ex);
			}

			final String extension = directories.get(dirString);
			if (isEmptyDirectory(dir, extension)) {
				final String msg = TASKDISPATCHER_MESSAGE_ANY;
				throw new IllegalArgumentException(String.format(msg,
						dirString, extension));
			}
		}
	}

	public void setSelection(final Set<SelectionInfo> selection) {
		this.selection = new TreeSet<>(selection);
		directories.clear();
		for (SelectionInfo s : selection) {
			directories.put(s.getPath().toString(), s.getExtension());
		}
	}

	public List<Future<FileTask<?, ?>>> execute(TaskComponent taskComponent)
			throws MergeException {
		final TaskFactory taskFactory = TaskFactory.getInstance();

		final List<FileTask<?, ?>> tasks = taskFactory.createTasks(selection);
		taskFactory.setHeaderTask();
		taskFactory.setPercentage();
		final int size = taskFactory.counTasks();
		LOGGER.info(String.format(TASKDISPATCHER_MESSAGE_SUBMIT, size));
		final ThreadPool executor = new ThreadPool(size, taskComponent);
		List<Future<FileTask<?, ?>>> result = new ArrayList<>();
		try {
			result = executor.invokeAll(tasks);
		} catch (InterruptedException | UnsupportedOperationException ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
			throw new MergeException(ex);
		}
		LOGGER.info(String.format(TASKDISPATCHER_MESSAGE_FINISHED,
				result.size()));
		executor.shutdown();
		return result;
	}

	public void mergeFiles(List<FileTask<?, ?>> finishedTasks)
			throws MergeException {

		try {
			LOGGER.info(TASKDISPATCHER_MESSAGE_COLLECT);
			Map<String, String> storeMap = new TreeMap<>();
			storeMap.putAll(getStoreMap(finishedTasks));

			Map<String, XSSFRow> shopMap = new TreeMap<>();
			List<XSSFRow> headerRows = getHeaderMapRows(finishedTasks);
			shopMap.putAll(getShopMap(finishedTasks));

			final Merger merger = MergerFactory.getInstance().newMerger(
					getTargetFile(), storeMap, shopMap, headerRows);
			merger.merge();
			LOGGER.log(Level.INFO, TASKDISPATCHER_MESSAGE_SUCCESS);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, null, e);
			throw new MergeException(e);
		}
	}

	private Map<String, XSSFRow> getShopMap(List<FileTask<?, ?>> result) {
		Map<String, XSSFRow> resultMap = new TreeMap<>();
		for (FileTask<?, ?> fileTask : result) {
			if (fileTask instanceof ReadXlsxFileTask) {
				final Map<String, XSSFRow> map = ((ReadXlsxFileTask) fileTask)
						.getMap();
				resultMap.putAll(map);

			}
		}
		return resultMap;
	}

	private Map<String, String> getStoreMap(List<FileTask<?, ?>> result) {
		Map<String, String> resultMap = new TreeMap<>();
		for (FileTask<?, ?> fileTask : result) {
			if (fileTask instanceof ReadCsvFileTask) {
				final Map<String, String> map = ((ReadCsvFileTask) fileTask)
						.getMap();
				resultMap.putAll(map);

			}
		}
		return resultMap;
	}

	public void setTargetFile(Path targetFile) {
		this.targetFile = targetFile;
	}

	public Path getTargetFile() {
		return this.targetFile;
	}

	private List<XSSFRow> getHeaderMapRows(List<FileTask<?, ?>> finishedTasks) {
		List<XSSFRow> result = new ArrayList<>();
		for (FileTask<?, ?> fileTask : finishedTasks) {
			if (fileTask instanceof ReadHeaderFileTask) {
				@SuppressWarnings("unchecked")
				final Map<String, XSSFRow> map = (Map<String, XSSFRow>) fileTask
						.getMap();

				result.addAll(map.values());

			}
		}
		return result;
	}

}
