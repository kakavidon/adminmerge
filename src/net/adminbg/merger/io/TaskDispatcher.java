package net.adminbg.merger.io;

import java.io.File;
import java.io.FilenameFilter;
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

import org.apache.poi.xssf.usermodel.XSSFRow;

import net.adminbg.merger.logging.ApplicationLogger;
import net.adminbg.merger.ui.SelectionInfo;
import net.adminbg.merger.ui.TaskComponent;

public enum TaskDispatcher {

    INSTANCE;
    private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(TaskDispatcher.class);
    private Map<String, String> directories = new TreeMap<>();
    private Set<SelectionInfo> selection = new TreeSet<>();
//    private final List<FileTask> fileTasks = new ArrayList<>();
    private Path targetFile;

    public void parseDirectories() throws IOException {
        if (directories.isEmpty()) {
            throw new IllegalArgumentException("No directories selected. \nPlease, specify 2 valid directories.");
        } else {
            final int size = directories.size();
            if (size != 2) {
                throw new IllegalArgumentException(
                        "Current version of the application supports processing\n"
                        + " of 2 distinct directories. Your selections contains "
                        + size + " .");
            }
        }

        for (String dirString : directories.keySet()) {

            Path dir = Paths.get(dirString);
            try {
                FileTest.validate(dir, FileTest.EXISTS, FileTest.IS_DIRECTORY, FileTest.READABLE);
            } catch (MergeException ex) {
                throw new IllegalArgumentException(ex);
            }

            final String extension = directories.get(dirString);
            if (isEmpty(dirString, extension)) {
                final String msg = "Directory \"%s\"\n does not contain any files with extension \"%s\". Aborting ...";
                throw new IllegalArgumentException(String.format(msg, dirString, extension));
            }
        }
    }

    private boolean isEmpty(final String dir, final String extension) {
        File dirName = new File(dir);
        FilenameFilter f = new FilenameFilter() {
            @Override
            public boolean accept(File arg0, String filename) {
                return filename.endsWith(extension);
            }
        };

        return dirName.listFiles(f).length == 0;

    }

    public void setSelection(final Set<SelectionInfo> selection) {
        this.selection = new TreeSet<>(selection);
        for (SelectionInfo s : selection) {
            directories.put(s.getPath().toString(), s.getExtension());
        }
    }

    public List<Future<FileTask>> execute(TaskComponent taskComponent) throws MergeException {
        final TaskFactory taskFactory = TaskFactory.getInstance();

        final List<FileTask> tasks = taskFactory.createTasks(selection);
        taskFactory.setHeaderTask(tasks);
        taskFactory.setPercentage();
        final int size = taskFactory.counTasks();
        LOGGER.info(String.format("Total %d tasks submitted.", size));
        final ThreadPool executor = new ThreadPool(size, taskComponent);
        List<Future<FileTask>> result = new ArrayList<>();
        try {
            result = executor.invokeAll(tasks);
        } catch (InterruptedException | UnsupportedOperationException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            throw new MergeException(ex);
        }

        executor.shutdown();
        return result;
    }

    public void mergeFiles(List<FileTask> finishedTasks) throws MergeException {

        try {
            LOGGER.info("Collecting results: ");
            Map<String, String> storeMap = new TreeMap<>();
            storeMap.putAll(getStoreMap(finishedTasks));

            Map<String, XSSFRow> shopMap = new TreeMap<>();
            List<XSSFRow> headerRows = getHeaderMapRows(finishedTasks);
            shopMap.putAll(getShopMap(finishedTasks));

            final Merger merger = MergerFactory.getInstance().createMerger(getTargetFile(), storeMap, shopMap, headerRows);
            merger.merge();
            LOGGER.log(Level.INFO, "File processed successfully. {0}", storeMap.size());
            LOGGER.info("");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, null, e);
            throw new MergeException(e);
        }
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

    public void setTargetFile(Path targetFile) {
        this.targetFile = targetFile;
    }

    public Path getTargetFile() {
        return this.targetFile;
    }

    private List<XSSFRow> getHeaderMapRows(List<FileTask> finishedTasks) {
        List<XSSFRow> result = new ArrayList<>();
        for (FileTask fileTask : finishedTasks) {
            if (fileTask instanceof ReadHeaderFileTask) {
                @SuppressWarnings("unchecked")
				final Map<String, XSSFRow> map = (Map<String, XSSFRow>) fileTask.getMap();

                result.addAll(map.values());

            }
        }
        return result;
    }

}
