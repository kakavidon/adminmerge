package net.adminbg.merger.io;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.logging.ApplicationLogger;
import net.adminbg.merger.ui.SelectionInfo;
import static net.adminbg.merger.ui.Configuration.*;

/**
 * 
 * Creates tasks to process input files.
 * 
 * @author kakavidon
 */
final public class TaskFactory {
	private static final ApplicationLogger appLog = ApplicationLogger.INSTANCE;
	private static final Logger LOGGER = appLog.getLogger(TaskFactory.class);
	private final List<FileTask<?, ?>> tasks = new ArrayList<>();

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

	public List<FileTask<?, ?>> createTasks(final Set<SelectionInfo> selection) throws MergeException {

		LOGGER.info(CREATE_TASKS);

		reset();
		final ArrayList<FileTask<?, ?>> newTasks = new ArrayList<>();
		for (SelectionInfo s : selection) {
			final Path dir = s.getPath();
			final String className = s.getClassName();
			final String extension = s.getExtension();
			newTasks.addAll(buildTasks(dir, className, extension));
		}

		return newTasks;
	}

	private List<FileTask<?, ?>> buildTasks(final Path directory, final String className, final String ext)
			throws MergeException {
		final List<FileTask<?, ?>> localTasks = new ArrayList<>();
		LOGGER.log(Level.INFO, BUILD_TASKS, directory);
		try {
			for (Path file : listFiles(directory.toString(), ext)) {
				final Class<?> classInstcnce = Class.forName(className);
				@SuppressWarnings("unchecked")
				final Constructor<FileTask<?, ?>> constructor = (Constructor<FileTask<?, ?>>) classInstcnce
						.getConstructor(Path.class);
				localTasks.add((FileTask<?, ?>) constructor.newInstance(file));
			}

		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
				| SecurityException | IllegalArgumentException | InvocationTargetException ex) {
			final String msg = String.format(ERROR_INSTANCE, className);
			LOGGER.log(Level.SEVERE, msg, ex);
			throw new MergeException(ex);
		}
		this.tasks.addAll(new ArrayList<>(localTasks));
		return localTasks;
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
			final String dirStr = String.format("%s%s%s", dir, SLASH, str);
			result.add(Paths.get(dirStr));
		}
		return result;
	}

	public Path getExcelFile() {
		for (FileTask<?, ?> task : tasks) {
			if (task instanceof ReadXlsxFileTask) {
				return task.getFile();
			}
		}
		return null;
	}

	public void setPercentage() {
		LOGGER.info(CALCULATE);
		final int total = 100;
		final int size = tasks.size() + 1;
		final int percentDone = total / size;
		int reminder = total - percentDone * size;
		LOGGER.log(Level.INFO, PROGRESS, new Object[] { percentDone, percentDone * size });

		for (FileTask<?, ?> task : tasks) {
			task.setPercentDone(percentDone);
		}
		final FileTask<?, ?> lastTask = tasks.get(tasks.size() - 1);
		lastTask.setPercentDone(reminder + percentDone);
		LOGGER.log(Level.INFO, SIZE, tasks.size());
		LOGGER.log(Level.INFO, TASK, lastTask.getPercentDone());
	}

	public void setHeaderTask() throws MergeException {
		LOGGER.info(HEADER);
		final Path excelFile = getExcelFile();
		tasks.add(new ReadHeaderFileTask(excelFile));
	}

	private void reset() {
		this.tasks.clear();

	}
}
