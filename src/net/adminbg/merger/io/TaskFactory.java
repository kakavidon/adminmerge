/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger.io;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author kakavidon
 */
final public class TaskFactory {
	private static final Logger LOGGER = Logger.getLogger(TaskFactory.class.getName());
	private final List<FileTask> tasks = new ArrayList<>();

	private TaskFactory() {
	}

	public static TaskFactory getInstance() {
		return TaskFactoryHolder.INSTANCE;
	}

	public int counTasks() {
		return tasks.size();
	}

	private static class TaskFactoryHolder {

		private static final TaskFactory INSTANCE = new TaskFactory();
	}

	public List<FileTask> createTasks(final Map<String, String> dirs) {
		LOGGER.info("Creating Tasks");
		reset();
		final ArrayList<FileTask> tasks = new ArrayList<>();
		for (Map.Entry<String, String> e : dirs.entrySet()) {
			tasks.addAll(buildTasks(e.getKey(), e.getValue()));
		}
		return tasks;
	}

	private List<FileTask> buildTasks(final String directory, final String extension) {
		final List<FileTask> tasks = new ArrayList<>();
		final Path dir = Paths.get(directory);
		LOGGER.info("Building task for " + dir);
		for (Path file : listFiles(directory, extension)) {
			switch (extension) {
			case ".csv":
				LOGGER.info("New ReadCsvFileTask for " + file);
				tasks.add(new ReadCsvFileTask(file));
				break;
			case ".xlsx":
				tasks.add(new ReadXlsxFileTask(file));
				LOGGER.info("New ReadXlsxFileTask for " + file);
				break;
			default:
				throw new UnsupportedOperationException("Unsupported extension : " + extension);

			}
		}
		this.tasks.addAll(new ArrayList<>(tasks));
		return tasks;
	}

	public List<Path> listFiles(final String dir, final String extension) {
		List<Path> result = new ArrayList<>();
		FilenameFilter f = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String file) {
				return file.endsWith(extension);
			}
		};

		final List<String> list = Arrays.asList(new File(dir).list(f));
		for (String str : list) {
			result.add(Paths.get(dir + "/" + str));
		}
		return result;
	}

	public Path getExcelFile() {
		for (FileTask task : tasks) {
			if (task instanceof ReadXlsxFileTask) {
				return task.getFile();
			}
		}
		return null;
	}

	public void setPercentage() {
		LOGGER.info("Calculating percentage.");
		final int total = 100;
		final int size = tasks.size();
		// int count = 0;
		// for (FileTask<?, ?> task : tasks) {
		// count += task.getWeight();
		// }
		final int percentDone = total / size;
		int reminder =  total - percentDone * size;
		LOGGER.info("Percentage = " + percentDone + " total = " + percentDone * size);

		for (FileTask task : tasks) {
			task.setPercentDone(percentDone);
		}
		final FileTask lastTask = tasks.get(tasks.size() - 1);
		lastTask.setPercentDone(reminder + percentDone);
		LOGGER.info("Size " + tasks.size());
		LOGGER.info("Last task percent " + lastTask.getPercentDone());
	}

	public void setHeaderTask(List<FileTask> tasks) {
		LOGGER.info("Add header");
		final Path excelFile = getExcelFile();
		tasks.add(new ReadHeaderFileTask(excelFile));
	}

	private void reset() {
		for (FileTask task : this.tasks) {
			task = null;
		}
		this.tasks.clear();
	}
}
