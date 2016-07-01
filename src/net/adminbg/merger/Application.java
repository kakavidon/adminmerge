package net.adminbg.merger;

import java.util.logging.Logger;
import net.adminbg.merger.logging.ApplicationLogger;

import net.adminbg.merger.ui.MainWindow;

/**
 *
 * @author lachezar.nedelchev
 */
public class Application {

    private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(Application.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOGGER.info("Starting main window");
        new MainWindow().start();
    }

}
