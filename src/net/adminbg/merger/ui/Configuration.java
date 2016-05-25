package net.adminbg.merger.ui;


public enum Configuration {
	
	INSTANCE;
	
	public final static String BTN_EXIT;
	public final static String BTN_MERGE;
	public final static String BTN_OPEN;
	public final static String CANCEL_FILE_SELECTION;
	public final static String FILE_READ_ERROR_CAPTION;
	public final static String LBL_STORE;
	public final static String LBL_SHOP;
	public final static String NEW_LINE;
	public final static String TEXTAREA_LOG_MESSAGE_SHOP;
	public final static String TEXTAREA_LOG_MESSAGE_STORE;
	public final static String UI_STYLE;

	static {
		BTN_EXIT = Messages.getString("MainWindow.btn.exit");
		BTN_MERGE = Messages.getString("MainWindow.btn.merge");
		BTN_OPEN = Messages.getString("MainWindow.btn.open");
		CANCEL_FILE_SELECTION = Messages.getString("MainWindow.cancel.file.selection");
		FILE_READ_ERROR_CAPTION = Messages.getString("MainWindow.file.read.error.caption");
		LBL_STORE = Messages.getString("MainWindow.lbl.store");
		LBL_SHOP = Messages.getString("MainWindow.lbl.shop");
		NEW_LINE = Messages.getString("MainWindow.new.line");
		TEXTAREA_LOG_MESSAGE_SHOP = Messages.getString("MainWindow.textarea.log.message.shop");
		TEXTAREA_LOG_MESSAGE_STORE = Messages.getString("MainWindow.textarea.log.message.store");
		UI_STYLE = Messages.getString("MainWindow.ui.style");
	}



	public static Configuration getInstance() {
		return INSTANCE;
	}
}
