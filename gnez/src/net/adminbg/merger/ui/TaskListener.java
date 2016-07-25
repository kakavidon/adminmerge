package net.adminbg.merger.ui;

import java.util.EventListener;

/**
 * 
 * EventListener for tasks processed by tje application.
 * 
 * @author lnedelc
 */
public interface TaskListener extends EventListener {
	/**
	 * 
	 * Fired when a progress is made.
	 * 
	 * @param taskEvent
	 */
	public void percentDone(final TaskEvent taskEvent);

}
