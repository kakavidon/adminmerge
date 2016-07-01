package net.adminbg.merger.io;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.nio.file.Path;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.adminbg.merger.ui.TaskComponent;
import net.adminbg.merger.ui.TaskEvent;
import net.adminbg.merger.logging.ApplicationLogger;

/**
 *
 * @author kakavidon
 */
public class ThreadPool extends ThreadPoolExecutor {

    private static final Logger LOGGER = ApplicationLogger.INSTANCE.getLogger(ThreadPool.class);
    private final TaskComponent taskComponent;
    private final String SUCCESS_MESSAGE = "File \"%s\" has been processed.";
    private final String ERROR_MESSAGE = "Failed to read file.";

    public ThreadPool(int nThreads, TaskComponent taskComponent) {
        super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        this.taskComponent = taskComponent;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);
        if (t == null && r instanceof Future<?>) {
            try {
                @SuppressWarnings("unchecked")
				FileTask result = ((Future<FileTask>) r).get();
                final Path file = result.getFile();
                final EventQueue eventQueue = new EventQueue() {
                    @Override
                    protected void dispatchEvent(AWTEvent event) {
                        if (event instanceof TaskEvent) {
                            super.dispatchEvent(event);
                        }
                    }
                };
                TaskEvent taskEvent = new TaskEvent(taskComponent);
                taskEvent.setProgress(result.getPercentDone());

                final String msg = String.format(SUCCESS_MESSAGE, file.toString());
                taskEvent.setMessage(msg);
                LOGGER.info(msg);
                eventQueue.postEvent(taskEvent);
            } catch (CancellationException | ExecutionException | InterruptedException e) {
                shutdown();
                LOGGER.log(Level.SEVERE, ERROR_MESSAGE, e);
            }
        }
        if (t != null) {
            LOGGER.log(Level.SEVERE, ERROR_MESSAGE, t);
            shutdown();
        }

    }

}
