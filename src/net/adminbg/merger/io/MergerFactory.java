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
public class MergerFactory {
    private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(MergerFactory.class);   
    private MergerFactory() {
    }
    
    public static MergerFactory getInstance() {
        return MergeFactoryHolder.INSTANCE;
    }
    
    private static class MergeFactoryHolder {

        private static final MergerFactory INSTANCE = new MergerFactory();
    }
    
    public Merger createMerger(final Path targetFile,  Map<?, ?> firstDirRows, Map<?, ?>  secondDirRows, List<XSSFRow> row ) {
        LOGGER.log( Level.INFO, "Building merger {0} .", SimpleMerger.class.getSimpleName());
        return new SimpleMerger(targetFile, firstDirRows, secondDirRows, row);
    } 



  
}
