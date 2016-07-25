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

import net.adminbg.merger.logging.ApplicationLogger;
import net.adminbg.merger.ui.TaskComponent;
import net.adminbg.merger.ui.TaskEvent;
import static net.adminbg.merger.ui.Configuration.THREADPOOL_ERROR_MESSAGE;
import static net.adminbg.merger.ui.Configuration.THREADPOOL_SUCCESS_MESSAGE;

/**
 * 
 * This class extends ThreadPoolExecutor with the only purpose to report
 * progress after task is executed.
 * 
 * @author kakavidon
 */
public class ThreadPool extends ThreadPoolExecutor {

	private static final Logger LOGGER = ApplicationLogger.INSTANCE
			.getLogger(ThreadPool.class);
	private final TaskComponent taskComponent;

	public ThreadPool(int corePoolSize, TaskComponent taskComponent) {
		super(corePoolSize, corePoolSize + 2, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

		this.taskComponent = taskComponent;
	}

	/**
	 * 
	 * Report progress when task is finished.
	 * 
	 */
	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		if (t == null && r instanceof Future<?>) {
			try {
				@SuppressWarnings("unchecked")
				FileTask<?, ?> result = ((Future<FileTask<?, ?>>) r).get();
				createAndPostTackEvent(result);
			} catch (CancellationException | ExecutionException
					| InterruptedException e) {
				shutdown();
				LOGGER.log(Level.SEVERE, THREADPOOL_ERROR_MESSAGE, e);
			}
		}
		if (t != null) {
			LOGGER.log(Level.SEVERE, THREADPOOL_ERROR_MESSAGE, t);
			shutdown();
		}

	}

	/**
	 * 
	 * Creates and posts a TackEvent.
	 * 
	 * @param task
	 *            a finished task
	 */
	private void createAndPostTackEvent(final FileTask<?, ?> task) {
		final Path file = task.getFile();
		final EventQueue eventQueue = new EventQueue() {
			@Override
			protected void dispatchEvent(AWTEvent event) {
				if (event instanceof TaskEvent) {
					super.dispatchEvent(event);
				}
			}
		};
		TaskEvent taskEvent = c(task, file);
		eventQueue.postEvent(taskEvent);
	}

	private TaskEvent c(FileTask<?, ?> result, final Path file) {
		TaskEvent taskEvent = new TaskEvent(taskComponent);
		taskEvent.setProgress(result.getPercentDone());

		final String msg = String.format(THREADPOOL_SUCCESS_MESSAGE,
				file.toString());
		taskEvent.setMessage(msg);
		LOGGER.info(msg);
		return taskEvent;
	}

}
