package net.adminbg.merger.io;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.adminbg.merger.logging.ApplicationLogger;
import org.apache.poi.xssf.usermodel.XSSFRow;

/**
 *
 * @author lnedelc
 */
public abstract class Merger{

    private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(Merger.class);
    private final Path targetFile;
    private final Map<?, ?> secondDirRows;
    private final Map<?, ?> firstDirRows;
    private final List<XSSFRow> headerRows;

    public Merger(Path targetFile, Map<?, ?> firstDirRows, Map<?, ?> secondDirRows, final List<XSSFRow> headerRow) {
        LOGGER.log( Level.INFO, "Creating merger for file {0} .", targetFile);
        this.targetFile = targetFile;
        this.firstDirRows = firstDirRows;
        this.secondDirRows = secondDirRows;
        this.headerRows = headerRow;
    }

    abstract public void merge() throws MergeException;

    /**
     * @return the targetFile
     */
    public Path getTargetFile() {
        return targetFile;
    }

    public Map<?, ?> getFirstDirRows() {
        return firstDirRows;
    }

    /**
     * @return the rows
     */
    public Map<?, ?> getSecondDirRows() {
        return secondDirRows;
    }

    /**
     * @return the headerRow
     */
    public List<XSSFRow> getHeaderRows() {
        return headerRows;
    }

}
