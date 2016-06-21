package net.adminbg.merger.ui;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JComponent;

import net.adminbg.merger.io.FileTask;
import net.adminbg.merger.io.TaskDispatcher;

public class TaskComponent extends JComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TaskListener listener;

	private static EventQueue evtq;

	public TaskComponent() {
		evtq = Toolkit.getDefaultToolkit().getSystemEventQueue();
		enableEvents(0);
	}

	public void addTakListener(final TaskListener listener) {
		this.listener = listener;
	}

	public void chep(TaskDispatcher dipatcher) throws IOException {

		try {
			dipatcher.parseDirectories();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		List<Future<FileTask>> results = dipatcher.execute(this);

		// setProgress(0);
//		int i = 0;
//		List<FileTask> finishedTasks = new ArrayList<>();
//		for (Future<FileTask> task : results) {
//			try {
//				final FileTask fileTask = task.get();
//				finishedTasks.add(fileTask);
//				final int progress = fileTask.getPercentDone();
//
//			} catch (InterruptedException | ExecutionException ex) {
//				ex.printStackTrace();
//			}
//
//		}
//		System.out.println("Task returned = " + finishedTasks.size());
//		dipatcher.mergeFiles(finishedTasks);

	}

	public void dispatchEvent(final int progress) {
		TaskEvent taskEvent = new TaskEvent(this);
		taskEvent.setProgress(progress);
		evtq.postEvent(taskEvent);
	}

	public void processEvent(AWTEvent evt) {
		if (evt instanceof TaskEvent) {
			if (listener != null)
				listener.percentDone((TaskEvent) evt);
		} else
			super.processEvent(evt);
	}

}