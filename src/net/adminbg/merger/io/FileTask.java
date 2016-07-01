package net.adminbg.merger.io;

import java.nio.file.Path;
import java.util.concurrent.Callable;
import java.util.Map;

public abstract class FileTask implements Callable<FileTask> {

    private int percentDone;
    private final Path file;

    public FileTask(final Path file) {
        this.file = file;

    }

    public void setPercentDone(final int percentDone) {
        this.percentDone = percentDone;
    }

    public int getPercentDone() {
        return percentDone;
    }

    public Path getFile() {
        return this.file;
    }

    public abstract Map<?, ?> getMap();

    public abstract int getWeight();

}
