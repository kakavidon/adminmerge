package net.adminbg.merger.ui;

import static net.adminbg.merger.ui.Configuration.BTN_EXIT;
import static net.adminbg.merger.ui.Configuration.BTN_MERGE;
import static net.adminbg.merger.ui.Configuration.BTN_OPEN;
import static net.adminbg.merger.ui.Configuration.CANCEL_FILE_SELECTION;
import static net.adminbg.merger.ui.Configuration.FILE_READ_ERROR_CAPTION;
import static net.adminbg.merger.ui.Configuration.LBL_SHOP;
import static net.adminbg.merger.ui.Configuration.LBL_STORE;
import static net.adminbg.merger.ui.Configuration.NEW_LINE;
import static net.adminbg.merger.ui.Configuration.TEXTAREA_LOG_MESSAGE_STORE;
import static net.adminbg.merger.ui.Configuration.UI_STYLE;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.adminbg.merger.MergeManger;
import net.adminbg.merger.logging.AdminLogger;

/**
 * 
 * @author lachezar.nedelchev
 */
public class MainWindow extends javax.swing.JFrame {

	private static final long serialVersionUID = 5745764792431031785L;
	private final MergeManger mergeManger;

	private static AdminLogger logger = AdminLogger.INSTANCE;

	/**
	 * Creates new form MainFarame
	 */
	public MainWindow() {
		logger.info("Running Main Window constructor...");
		mergeManger = MergeManger.getInstance();

		initComponents();
	}

	private void initComponents() {
		try {
			logger.init(MainWindow.class.getName());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("Running Main Window constructor...");
		txtShop = new javax.swing.JTextField();
		txtStore = new javax.swing.JTextField();
		btnShop = new javax.swing.JButton();
		btnStore = new javax.swing.JButton();
		lblStore = new javax.swing.JLabel();
		lblShop = new javax.swing.JLabel();
		btnMerge = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		textAreaOutput = new javax.swing.JTextArea();
		btnExit = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setResizable(false);

		txtShop.setFocusable(false);

		txtStore.setFocusable(false);

		btnShop.setText(BTN_OPEN);
		btnShop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnShopActionPerformed(evt);
			}
		});

		btnStore.setText(BTN_OPEN);
		btnStore.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnStoreActionPerformed(evt);
			}
		});

		lblStore.setText(LBL_SHOP);

		lblShop.setText(LBL_STORE);

		btnMerge.setText(BTN_MERGE);
		btnMerge.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnMergeActionPerformed(evt);
			}
		});

		textAreaOutput.setColumns(20);
		textAreaOutput.setRows(5);
		textAreaOutput.setFocusable(false);
		jScrollPane1.setViewportView(textAreaOutput);

		btnExit.setText(BTN_EXIT);
		btnExit.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				btnExitActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(15, 15, 15).addGroup(layout
						.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING,
								false)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
										.addComponent(lblShop).addComponent(lblStore)
										.addComponent(txtStore, javax.swing.GroupLayout.DEFAULT_SIZE, 383,
												Short.MAX_VALUE)
										.addComponent(txtShop))
								.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
										.addComponent(btnShop, javax.swing.GroupLayout.PREFERRED_SIZE, 27,
												javax.swing.GroupLayout.PREFERRED_SIZE)
										.addComponent(btnStore, javax.swing.GroupLayout.PREFERRED_SIZE, 27,
												javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addComponent(jScrollPane1).addComponent(btnMerge, javax.swing.GroupLayout.Alignment.TRAILING))
				.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addComponent(btnExit,
								javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addContainerGap()
						.addComponent(lblShop, javax.swing.GroupLayout.PREFERRED_SIZE, 23,
								javax.swing.GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
								.addComponent(txtShop, javax.swing.GroupLayout.PREFERRED_SIZE,
										javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(btnShop)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(lblStore).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(txtStore, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
						.addComponent(btnStore)).addGap(18, 18, 18).addComponent(btnMerge)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
				.addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
				.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(btnExit)
				.addContainerGap()));

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 3 - this.getSize().width / 2, dim.height / 3 - this.getSize().height / 2);
		pack();
	}

	private void btnMergeActionPerformed(java.awt.event.ActionEvent evt) {

		final String shopFile = txtShop.getText();
		final String storeFile = txtStore.getText();

		try {
			mergeManger.merge(shopFile, storeFile);
		} catch (IllegalArgumentException ex) {

			JOptionPane.showMessageDialog(this, ex.getMessage(), FILE_READ_ERROR_CAPTION, JOptionPane.ERROR_MESSAGE);
		}

	}

	private void btnStoreActionPerformed(java.awt.event.ActionEvent evt) {
		final JFileChooser fileChooser = new JFileChooser();
		int returnVal = fileChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			final String fileName = file.getAbsolutePath();
			txtStore.setText(fileName);
			txtStore.setEnabled(false);
			String message = TEXTAREA_LOG_MESSAGE_STORE + fileName + NEW_LINE;
			textAreaOutput.append(message);
			logger.info(message);

		} else {
			// System.out.println(Messages.getString(CANCEL_FILE_SELECTION));
			logger.info(CANCEL_FILE_SELECTION);
		}
	}

	private void btnShopActionPerformed(java.awt.event.ActionEvent evt) {

		final JFileChooser fileChooser = new JFileChooser();
		final int returnVal = fileChooser.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			final String fileName = file.getAbsolutePath();
			txtShop.setText(fileName);
			txtShop.setEnabled(false);
			String message = TEXTAREA_LOG_MESSAGE_STORE + fileName + NEW_LINE;
			textAreaOutput.append(message);
			logger.info(message);
		} else {
			// System.out.println(Messages.getString(CANCEL_FILE_SELECTION));
			logger.info(CANCEL_FILE_SELECTION);
		}
	}

	private void btnExitActionPerformed(java.awt.event.ActionEvent evt) {
		mergeManger.close();
		this.dispose();
		System.exit(0);
	}

	public static void start() {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if (UI_STYLE.equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			logger.error(null, ex);
		} catch (InstantiationException ex) {
			logger.error(null, ex);
		} catch (IllegalAccessException ex) {
			logger.error(null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			logger.error(null, ex);
		}

		/* Create and display the form */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new MainWindow().setVisible(true);
			}
		});
	}

	// Variables declaration - do not modify
	private javax.swing.JButton btnExit;
	private javax.swing.JButton btnMerge;
	private javax.swing.JButton btnShop;
	private javax.swing.JButton btnStore;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JLabel lblShop;
	private javax.swing.JLabel lblStore;
	private javax.swing.JTextArea textAreaOutput;
	private javax.swing.JTextField txtShop;
	private javax.swing.JTextField txtStore;
	// End of variables declaration
}
