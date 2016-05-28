package net.adminbg.merger.ui;

import java.util.logging.Logger;

import net.adminbg.merger.logging.AdminLogger;


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
	public final static String IS_DEVELOPMENT;
	public final static String SHOP_FILE_READER; 
	public final static String STORE_FILE_READER;
	public final static String DEFAULT_SOURCE_DIR;
	
	private static Logger logger = AdminLogger.INSTANCE.getLogger(Configuration.class.getName());	
	
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
		IS_DEVELOPMENT = Messages.getString("AdminLogger.is.development");
		SHOP_FILE_READER = Messages.getString("shop.file.loader");
		STORE_FILE_READER  = Messages.getString("store.file.loader");
		DEFAULT_SOURCE_DIR = Messages.getString("default.source.dir");
		
		
		logger.info(BTN_EXIT);
		logger.info(BTN_MERGE);
		logger.info(BTN_OPEN);
		logger.info(CANCEL_FILE_SELECTION);
		logger.info(FILE_READ_ERROR_CAPTION);
		logger.info(LBL_STORE);
		logger.info(LBL_SHOP);
		logger.info(NEW_LINE);
	}



	public static Configuration getInstance() {
		return INSTANCE;
	}
}
