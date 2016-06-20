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

import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author kakavidon
 */
final public class TaskFactory<U, V> {

    private final List<FileTask<U, V>> tasks = new ArrayList<>();

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

    public List<FileTask<U, V>> createTasks(final Map<String, String> dirs) {
        final ArrayList<FileTask<U, V>> tasks = new ArrayList<>();
        for (Map.Entry<String, String> e : dirs.entrySet()) {
            tasks.addAll(buildTasks(e.getKey(), e.getValue()));
        }
        return tasks;
    }

    private List<FileTask<U, V>> buildTasks(final String directory, final String extension) {
        final List<FileTask<U, V>> tasks = new ArrayList<>();
        final Path dir = Paths.get(directory);
        for (Path file : listFiles(directory, extension)) {
            switch (extension) {
                case ".csv":
                    tasks.add(new ReadCsvFileTask(file));
                    break;
                case ".xlsx":
                    tasks.add( new ReadXlsxFileTask<String,String>(file));
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
        final int total = 100;
        int count = 0;
        for (FileTask task : tasks) {
            count += task.getWeight();
        }

        for (FileTask task : tasks) {
            final int percentDone = total / tasks.size();
            task.setPercentDone(percentDone);
//            System.out.println(task.getClass() + ": percent = " + task.getPercentDone());
        }
        int reminder = 0;
        for (FileTask task : tasks) {
            reminder += task.getPercentDone();
        }
        final FileTask<U, V> lastTask = tasks.get(tasks.size() - 1);

        lastTask.setPercentDone(total - reminder);
       // System.out.println(lastTask.getClass() + ":<reset> percent = " + lastTask.getPercentDone());
    }

    public void setHeaderTask() {
        final Path excelFile = getExcelFile();
        tasks.add(new ReadHeaderFileTask(excelFile));
    }
}
