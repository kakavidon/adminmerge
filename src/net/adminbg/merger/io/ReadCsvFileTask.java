package net.adminbg.merger.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.adminbg.merger.logging.ApplicationLogger;

/**
 *
 * @author kakavidon
 */
public class ReadCsvFileTask extends FileTask {

    private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(ReadCsvFileTask.class);
    private final Map<String, String> map = new TreeMap<>();
    private final int START_FROM = 2;

    public ReadCsvFileTask(Path file) {
        super(file);
        LOGGER.log(Level.INFO, "New instance of ReadCsvFileTask created.");
    }

    @Override
    public FileTask call() throws MergeException {
        final Charset forName = Charset.forName("Windows-1251");
        final Path file = getFile();
        try (final BufferedReader br = Files.newBufferedReader(file, forName);) {
            StringBuilder s = new StringBuilder();
            String line;
            int i = 0;
            while ((line = br.readLine()) != null) {
                if (i >= START_FROM) {
                    final String[] columns = line.split(";");
                    if (columns == null || columns.length < 8) {
                        final String msg = "File \"%s\" has invalid format.";
                        MergeException ex = new MergeException(String.format(msg, file));
                        LOGGER.log(Level.SEVERE, null, ex);
                        throw ex;
                    } else {

                        s.append(line).append("\n");
                        map.put(columns[3], columns[7]);
                    }

                }
                i++;
            }

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to read " + file, e);
            throw new MergeException(e);
        }

        return this;
    }

    @Override
    public Map<String, String> getMap() {
        return this.map;
    }

    @Override
    public int getWeight() {
        return 1;
    }

}
