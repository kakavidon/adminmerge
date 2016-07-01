package net.adminbg.merger.ui;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JComponent;


public class TaskComponent extends JComponent {

    /**
     * TODO merge with TaskDispatcher
     */
    private static final long serialVersionUID = 1L;

    private TaskListener listener;

    private static EventQueue eventQueue;

    public TaskComponent() {
        eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        enableEvents(0);
    }

    /**
     *
     * @param listener
     */
    public void addTakListener(final TaskListener listener) {
        this.listener = listener;
    }

    public void dispatchEvent(final int progress) {
        TaskEvent taskEvent = new TaskEvent(this);
        taskEvent.setProgress(progress);
        eventQueue.postEvent(taskEvent);
    }

    @Override
    public void processEvent(AWTEvent evt) {
        if (evt instanceof TaskEvent) {
            if (listener != null) {
                listener.percentDone((TaskEvent) evt);
            }
        } else {
            super.processEvent(evt);
        }
    }

}
