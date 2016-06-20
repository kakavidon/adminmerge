package net.adminbg.merger.io;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.ui.MainWindow;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;

public enum TaskDispatcher {

	INSTANCE;
	private static final Logger LOGGER = Logger.getLogger(TaskDispatcher.class.getName());
	private Map<String, String> directories = new TreeMap<>();
	// private final List<Path> dirs = new ArrayList<>();
	private final List<FileTask> fileTasks = new ArrayList<>();

	public void parseDirectories() throws IOException {

		if (directories.isEmpty()) {
			throw new IllegalArgumentException("No directories selected. Two valid directories are expected.");
		} else {
			final int size = directories.size();
			if (size != 2) {
				throw new IllegalArgumentException(
						"Current version of the application supports processing of 2 distinct directories. Arguments passed = "
								+ size);
			}
		}

		for (String dirString : directories.keySet()) {
			// for (String s : new File(dirString).list()){
			// System.out.println(s);
			// }
			Path dir = Paths.get(dirString);
			if (!Files.exists(dir)) {
				final String msg = "\"%s\" does not exists.";
				throw new IllegalArgumentException(String.format(msg, dirString));
			}
			if (!Files.isDirectory(dir)) {
				final String msg = "\"%s\" is not a directory.";
				throw new IllegalArgumentException(String.format(msg, dirString));
			}
			if (!Files.isReadable(dir)) {
				final String msg = "Cannot read \"%s\".";
				throw new IllegalArgumentException(String.format(msg, dirString));

			}
			final String extension = directories.get(dirString);
			if (isEmpty(dirString, extension)) {
				final String msg = "Directory \"%s\" does not contain any files with extension \"%s\". Aborting ...";
				throw new IllegalArgumentException(String.format(msg, dirString, extension));
			}
			// dirs.add(dir);
		}
	}

	private boolean isEmpty(final String dir, final String extension) {
		File dirName = new File(dir);
		FilenameFilter f = new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String filename) {
				// System.out.println(filename);
				return filename.endsWith(extension);
			}
		};

		return dirName.listFiles(f).length == 0;

	}

	public void setDirectories(final Map<String, String> dirs) {
		this.directories = new TreeMap<>(dirs);
	}

	public List<Future<FileTask>> execute(MainWindow invoker) {
		final TaskFactory taskFactory = TaskFactory.getInstance();
		final List<FileTask<String, String>> tasks = taskFactory.createTasks(directories);
		taskFactory.setHeaderTask();
		taskFactory.setPercentage();
		final int size = taskFactory.counTasks();
		final UpdatebleThreadPool executor = new UpdatebleThreadPool(size, invoker);
		List<Future<FileTask>> result = new ArrayList<>();
		try {
			result = executor.invokeAll(tasks);
		} catch (InterruptedException ex) {
			LOGGER.log(Level.SEVERE, null, ex);
		}

		executor.shutdown();
		return result;
	}

	public void mergeFiles(List<FileTask> result) {

		try {
			LOGGER.info("Collecting results: ");
			Map<String, String> storeMap = new TreeMap<>();
			Map<String, XSSFRow> shopMap = new TreeMap<>();
			shopMap.putAll(getHeaderMap(result));
			shopMap.putAll(getShopMap(result));
			storeMap.putAll(getStoreMap(result));

			print10a(storeMap);
			print10(shopMap);
			storeMap.keySet().retainAll(shopMap.keySet());
			LOGGER.info(storeMap.isEmpty() ? "Epmty" : "Not Empty");
			LOGGER.info("File processed successfully. " + storeMap.size());
			LOGGER.info("");
			print10a(storeMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void print10(Map<String, XSSFRow> map) {
		System.out.println(map.size() + "");
		int i = 0;
		for (Map.Entry<String, XSSFRow> entrySet : map.entrySet()) {
			if (i < 10) {
				String key = entrySet.getKey();
				XSSFRow row = entrySet.getValue();
				final XSSFCell cell = row.getCell(17);
				if (cell == null) {
					System.out.println("key=" + key + " value NULL");
				} else {
					System.out.println("key = " + key + " value = " + cell.getStringCellValue());
				}
			}
			i++;
		}
	}

	public void print10a(Map<String, String> map) {
		System.out.println(map.size() + "");
		int i = 0;
		for (Map.Entry<String, String> entrySet : map.entrySet()) {
			if (i < 10) {
				String key = entrySet.getKey();
				String c = entrySet.getValue();
				System.out.println("key = " + key + " value = " + c);
			}
			i++;

		}
	}

	private Map<String, XSSFRow> getHeaderMap(List<FileTask> result) {
		Map<String, XSSFRow> resultMap = new TreeMap<>();
		for (FileTask fileTask : result) {
			if (fileTask instanceof ReadHeaderFileTask) {
				final Map<String, XSSFRow> map = fileTask.getMap();
				// final Map<String, XSSFRow> map = ((ReadHeaderFileTask)
				// fileTask).getMap();
				resultMap.putAll(map);

			}
		}
		return resultMap;
	}

	private Map<String, XSSFRow> getShopMap(List<FileTask> result) {

		Map<String, XSSFRow> resultMap = new TreeMap<>();
		for (FileTask fileTask : result) {
			if (fileTask instanceof ReadXlsxFileTask) {
				final Map<String, XSSFRow> map = ((ReadXlsxFileTask) fileTask).getMap();
				resultMap.putAll(map);

			}
		}
		return resultMap;
	}

	private Map<String, String> getStoreMap(List<FileTask> result) {
		Map<String, String> resultMap = new TreeMap<>();
		for (FileTask fileTask : result) {
			if (fileTask instanceof ReadCsvFileTask) {
				final Map<String, String> map = ((ReadCsvFileTask) fileTask).getMap();
				resultMap.putAll(map);

			}
		}
		return resultMap;
	}

}
