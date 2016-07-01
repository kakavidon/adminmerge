package net.adminbg.merger.io;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.adminbg.merger.logging.ApplicationLogger;
import net.adminbg.merger.ui.SelectionInfo;

/**
 *
 * @author kakavidon
 */
final public class TaskFactory {

    private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(TaskFactory.class);
    private final List<FileTask> tasks = new ArrayList<>();
    private final static ResourceBundle BUNDLE = java.util.ResourceBundle.getBundle("net/adminbg/properties");

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

    public List<FileTask> createTasks(final Set<SelectionInfo> selection)  throws MergeException {

        LOGGER.info(BUNDLE.getString("CREATE_TASK_MSG"));
        //
        reset();
        final ArrayList<FileTask> newTasks = new ArrayList<>();
        for (SelectionInfo s : selection) {
            final Path dir = s.getPath();
            final String className = s.getClassName();
            final String extension = s.getExtension();
            newTasks.addAll(buildTasks(dir, className, extension));
        }
//        for (Map.Entry<String, String> e : dirs.entrySet()) {
//            newTasks.addAll(buildTasks(e.getKey(), e.getValue()));
//        }
        return newTasks;
    }

    private List<FileTask> buildTasks(final Path directory, final String className, final String ext) throws MergeException {
        final List<FileTask> localTasks = new ArrayList<>();
        LOGGER.log(Level.INFO, BUNDLE.getString("BUILDING_TASK_MSG"), directory);
        try {
            for (Path file : listFiles(directory.toString(), ext)) {
                final Class<?> classInstcnce =  Class.forName(className);
                @SuppressWarnings("unchecked")
				final Constructor<FileTask> constructor = (Constructor<FileTask>) classInstcnce.getConstructor(Path.class);
                localTasks.add((FileTask) constructor.newInstance(file));
            }

        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            LOGGER.log(Level.SEVERE, String.format("Unable to create instance of %s.", className), ex);
            throw new MergeException(ex);
        }
//        
//        for (Path file : listFiles(directory, extension)) {
//            switch (extension) {
//                case ".csv":
//                    LOGGER.log(Level.INFO, BUNDLE.getString("NEW_READCSVFILETASK_FOR"), file);
//                    localTasks.add(new ReadCsvFileTask(file));
//                    break;
//                case ".xlsx":
//                    localTasks.add(new ReadXlsxFileTask(file));
//                    LOGGER.log(Level.INFO, BUNDLE.getString("NEW_READXLSXFILETASK_MSG"), file);
//                    break;
//                default:
//                    throw new UnsupportedOperationException(java.text.MessageFormat.format(BUNDLE.getString("UNSUPPORTED_EXTENSION_ERROR"), new Object[]{extension}));
//            }
//
//        }
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
        LOGGER.info(BUNDLE.getString("CALCULATING_PERCENTAGE"));
        final int total = 100;
        final int size = tasks.size()+1;
        final int percentDone = total / size;
        int reminder = total - percentDone * size;
        LOGGER.log(Level.INFO, "Percentage = {0} total = {1}", new Object[]{percentDone, percentDone * size});

        for (FileTask task : tasks) {
            task.setPercentDone(percentDone);
        }
        final FileTask lastTask = tasks.get(tasks.size() - 1);
        lastTask.setPercentDone(reminder + percentDone);
        LOGGER.log(Level.INFO, "Size {0}", tasks.size());
        LOGGER.log(Level.INFO, "Last task percent {0}", lastTask.getPercentDone());
    }

    public void setHeaderTask(List<FileTask> tasks) {
        LOGGER.info("Adding the header row.");
        final Path excelFile = getExcelFile();
        tasks.add(new ReadHeaderFileTask(excelFile));
    }

    private void reset() {
        for (Iterator<FileTask> it = this.tasks.iterator(); it.hasNext();) {
            FileTask task = it.next();
            task = null;
        }
        this.tasks.clear();
        
    }
}
