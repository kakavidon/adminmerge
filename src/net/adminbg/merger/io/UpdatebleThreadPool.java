/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.adminbg.merger.io;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import net.adminbg.merger.ui.TaskComponent;
import net.adminbg.merger.ui.TaskEvent;

/**
 *
 * @author kakavidon
 */
public class UpdatebleThreadPool extends ThreadPoolExecutor {

	private final TaskComponent taskComponent;
	// int count = 0;

	public UpdatebleThreadPool(int nThreads, TaskComponent taskComponent) {
		super(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		// count = 0;
		this.taskComponent = taskComponent;
		// this.invoker = invoker;
	}

	@Override
	protected void afterExecute(Runnable r, Throwable t) {
		super.afterExecute(r, t);
		// count++;
		// System.out.println("afterExecute " + count);
		if (t == null && r instanceof Future<?>) {
			try {
				FileTask result = ((Future<FileTask>) r).get();
				final EventQueue q = new EventQueue() {
					protected void dispatchEvent(AWTEvent event) {
						if (event instanceof TaskEvent) {
							super.dispatchEvent(event);
						}
					}
				};
				TaskEvent theEvent = new TaskEvent(taskComponent);
				theEvent.setProgress(result.getPercentDone());
				theEvent.setMessage("File \"" + result.getFile() + "\" is processed.");
				q.postEvent(theEvent);

				// taskComponent.dispatchEvent(result.getPercentDone());
			} catch (CancellationException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (t != null) {
			t.printStackTrace();
		}

	}

}
