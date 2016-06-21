package net.adminbg.merger.ui;

import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventListener;
//  w w  w.  ja v a  2  s.com
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class Main extends JPanel implements TaskListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int currentValue = 0;
	JProgressBar bar = new JProgressBar(1, 100);

	public Main() {
		TaskComponent t = new TaskComponent();
		t.addTakListener(this);
		bar.setStringPainted(true);
		add(bar);
	}

	public void percentDone(TaskEvent evt) {
		currentValue += 1;
		bar.setValue(currentValue);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("Customized Event");
		frame.setSize(300, 80);
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		Container contentPane = frame.getContentPane();
		contentPane.add(new Main());

		frame.setVisible(true);
	}
}

interface TaskListener extends EventListener {
	public void percentDone(TaskEvent evt);
}