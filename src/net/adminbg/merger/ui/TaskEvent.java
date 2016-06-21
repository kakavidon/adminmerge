package net.adminbg.merger.ui;

import java.awt.AWTEvent;

import javax.swing.JComponent;

public class TaskEvent extends AWTEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1427372972454620242L;

	public static final int TASK_EVENT = AWTEvent.RESERVED_ID_MAX + 5555;

	private int progress;

	private String message;

	public String getMessage() {
		return message;
	}

	public TaskEvent(JComponent parent) {
		super(parent, TASK_EVENT);
	}

	public int getProgress() {
		return progress;
	}

	public void setProgress(final int progress) {
		this.progress = progress;
	}

	public void setMessage(String message) {
		this.message = message;
		
	}

}