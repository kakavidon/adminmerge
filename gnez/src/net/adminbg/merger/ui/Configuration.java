package net.adminbg.merger.ui;

import java.util.logging.Logger;

import net.adminbg.merger.logging.ApplicationLogger;

/**
 *
 * Enum that holds the application configuration.
 *
 * @author kakavidon
 *
 */
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
    public final static String SHOP_TASK_CLASS;
    public final static String STORE_TASK_CLASS;
    public final static String DEFAULT_SOURCE_DIR;
    public final static String DB_JDBC_URL;
    public final static String DB_JDBC_DRIVER;
    public final static String DB_JDBC_MAX_CONNECTIONS;
    public final static String STORE_TABLE_NAME;
    public final static String STORE_TABLE_COLUMNS;
    public final static String STORE_TABLE_COLUMN_TYPES;
    public final static String SHOP_TABLE_NAME;
    public final static String SHOP_TABLE_COLUMNS;
    public final static String SHOP_TABLE_COLUMN_TYPES;
    public final static String DB_SCEMA;
    public final static String COLUMN_DELIMITER;
    public final static String ROW_DELIMITER;
    public final static String CELL_INDECIES;
    public final static String STORE_EXT;
    public final static String SHOP_EXT;
    public final static String APPLICATION_WELCOME_MESSAGE;
    public final static String APPLICATION_INFO;
    public final static String FILETEST_MESSAGE_CANNOT_READ;
    public final static String FILETEST_MESSAGE_CANNOT_COUNT;
    public final static String FILETEST_MESSAGE_CANNOT_WRITE;
    public final static String EMPTY_STRING;
    public final static String FILETEST_MESSAGE_INVALID_EXTENSION;
    public final static String FILETEST_MESSAGE_IS_EMPTY;
    public final static String FILETEST_MESSAGE_NOT_DIR;
    public final static String FILETEST_MESSAGE_NOT_EXISTS;
    public final static String FILETEST_MESSAGE_NOT_REGULAR_FILE;
    public final static String MERGER_MESSAGES_CONSTRUCTOR;
    public final static String ERROR_LOG_FILE;
    public final static String LOG_FILE;
    public final static String LOG_DIR;
    public final static String MERGERFACTORY_MESSAGE;
    public final static String MERGER_CLASS;
    public final static String MERGER_EXCEPTION;
    public final static String MESSAGE_NO_CHANGE_1;
    public final static String MESSAGE_NO_CHANGE_2;
    public final static String MESSAGE_NO_CHANGE_3;
    public final static String MESSAGE_START;
    public final static String DOT;
    public final static String ESCAPED_DOT;
    public final static String MESSAGE_SUCCESS;
    public final static String MEGRE_END;
    public final static String THREADPOOL_SUCCESS_MESSAGE;
    public final static String THREADPOOL_ERROR_MESSAGE;
    public final static String READCSVFILETASK_MESSAGE_NEW;
    public final static String READCSVFILETASK_CSV_CHARSET;
    public final static String READCSVFILETASK_CSV_DEIMITER;
    public final static String READCSVFILETASK_ERROR_INVALID;
    public final static String READCSVFILETASK_MESSAGE_FAIL;
    public final static String READHEADERFILETASK_MESSAGE_NEW;
    public final static String READHEADERFILETASK_MESSAGE_COPY;
    public final static String READXLSXFILETASK_MESSAGE_NEW;
    public final static String READXLSXFILETASK_MESSAGE_FAIL;
    public final static String TASKDISPATCHER_MESSAGE_MISSING_1;
    public final static String TASKDISPATCHER_MESSAGE_MISSING_2;
    public final static String TASKDISPATCHER_MESSAGE_FINISHED;
    public final static String TASKDISPATCHER_MESSAGE_SUBMIT;
    public final static String TASKDISPATCHER_MESSAGE_ILLEGAL_2;
    public final static String TASKDISPATCHER_MESSAGE_ILLEGAL_1;
    public final static String TASKDISPATCHER_MESSAGE_ANY;
    public final static String TASKDISPATCHER_MESSAGE_COLLECT;
    public final static String TASKDISPATCHER_MESSAGE_SUCCESS;
    public final static String BUILD_TASKS;
    public final static String CREATE_TASKS;
    public final static String ERROR_INSTANCE;
    public final static String SLASH;
    public final static String HEADER;
    public final static String TASK;
    public final static String SIZE;
    public final static String PROGRESS;
    public final static String CALCULATE;
    public final static String FONT_NAME;
    public final static String SHOP_BTN_NAME;
    public final static String STORE_BTN_NAME;
    public final static String DATE_FORMAT;
    public final static String FILE_PATTERN;
    public final static String READING_FILE;
    public final static String FINISHED;
    public final static String ERROR_CAPTION;
    public final static String PROGRESS_DONE;
    public final static String FINAL_MESSAGE;
    public final static String TIME_ELAPSED;
    public final static String DIALOG_CAPTION;
    public final static String MESSAGE_FAIL;

    public final static String XLSX_CELL_KEY_INDEX;
    public final static String XLSX_START_FROM;
    public final static String XLSX_NUMBER_FORMAT_ERROR;

    public final static String XLSX_HEADER_ROW_COUNT;
    public final static String XLSX_HEADER_START_FROM;
    public final static String XLSX_QUANTITY_INDEX;
    
    private static Logger LOGGER;

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
        SHOP_TASK_CLASS = Messages.getString("shop.task.class");
        STORE_TASK_CLASS = Messages.getString("store.task.class");
        DEFAULT_SOURCE_DIR = Messages.getString("default.source.dir");
        DB_JDBC_URL = Messages.getString("db.jdbc.url");
        DB_JDBC_DRIVER = Messages.getString("db.jdbc.driver");
        DB_JDBC_MAX_CONNECTIONS = Messages.getString("db.jdbc.max.connections");
        STORE_TABLE_NAME = Messages.getString("store.table.name");
        STORE_TABLE_COLUMNS = Messages.getString("store.table.columns");
        STORE_TABLE_COLUMN_TYPES = Messages.getString("store.table.column.types");
        SHOP_TABLE_NAME = Messages.getString("shop.table.name");
        SHOP_TABLE_COLUMNS = Messages.getString("shop.table.columns");
        SHOP_TABLE_COLUMN_TYPES = Messages.getString("shop.table.column.types");
        DB_SCEMA = Messages.getString("db.schema");
        COLUMN_DELIMITER = Messages.getString("shop.csv.column.delimiter");
        ROW_DELIMITER = Messages.getString("shop.csv.row.delimiter");
        CELL_INDECIES = Messages.getString("shop.csv.cell.indicies");
        STORE_EXT = Messages.getString("store.ext");
        SHOP_EXT = Messages.getString("shop.ext");
        APPLICATION_WELCOME_MESSAGE = Messages.getString("Application.welcome.message");
        APPLICATION_INFO = Messages.getString("Application.info.message");
        FILETEST_MESSAGE_CANNOT_READ = Messages.getString("FileTest.message.cannot.read");
        FILETEST_MESSAGE_CANNOT_COUNT = Messages.getString("FileTest.message.cannot.count");
        FILETEST_MESSAGE_CANNOT_WRITE = Messages.getString("FileTest.message.cannot.write");
        EMPTY_STRING = Messages.getString("FileTest.message.empty.string");
        FILETEST_MESSAGE_INVALID_EXTENSION = Messages.getString("FileTest.message.invalid.extension");
        FILETEST_MESSAGE_IS_EMPTY = Messages.getString("FileTest.message.is.empty");
        FILETEST_MESSAGE_NOT_DIR = Messages.getString("FileTest.message.not.dir=");
        FILETEST_MESSAGE_NOT_EXISTS = Messages.getString("FileTest.message.not.exists");
        FILETEST_MESSAGE_NOT_REGULAR_FILE = Messages.getString("FileTest.message.not.regular.file");
        MERGER_MESSAGES_CONSTRUCTOR = Messages.getString("Merger.messages.constructor");
        LOG_DIR = Messages.getString("ApplicationLogger.log.dir");
        LOG_FILE = Messages.getString("ApplicationLogger.log.file");
        ERROR_LOG_FILE = Messages.getString("ApplicationLogger.error.log.file");
        MERGERFACTORY_MESSAGE = Messages.getString("MergerFactory.message");
        MERGER_CLASS = Messages.getString("Merger.class");
        MERGER_EXCEPTION = Messages.getString("Merger.exception");
        MESSAGE_NO_CHANGE_1 = Messages.getString("SimpleMerger.message.no.change.1");
        MESSAGE_NO_CHANGE_2 = Messages.getString("SimpleMerger.message.no.change.2");
        MESSAGE_NO_CHANGE_3 = Messages.getString("SimpleMerger.message.no.change.3");
        MESSAGE_START = Messages.getString("SimpleMerger.message.start");
        DOT = Messages.getString("SimpleMerger.dot");
        ESCAPED_DOT = Messages.getString("SimpleMerger.escaped.dot");
        MESSAGE_SUCCESS = Messages.getString("SimpleMerger.message.success");
        MEGRE_END = Messages.getString("MainWindow.megre.end");
        THREADPOOL_SUCCESS_MESSAGE = Messages.getString("ThreadPool.message.success");
        THREADPOOL_ERROR_MESSAGE = Messages.getString("ThreadPool.message.error");
        READCSVFILETASK_MESSAGE_NEW = Messages.getString("ReadCsvFileTask.message.new");
        READCSVFILETASK_CSV_CHARSET = Messages.getString("ReadCsvFileTask.csv.charset");
        READCSVFILETASK_CSV_DEIMITER = Messages.getString("ReadCsvFileTask.csv.deimiter");
        READCSVFILETASK_ERROR_INVALID = Messages.getString("ReadCsvFileTask.error.invalid");
        READCSVFILETASK_MESSAGE_FAIL = Messages.getString("ReadCsvFileTask.message.fail");
        READHEADERFILETASK_MESSAGE_NEW = Messages.getString("ReadHeaderFileTask.message.new");
        READHEADERFILETASK_MESSAGE_COPY = Messages.getString("ReadHeaderFileTask.message.copy");
        READXLSXFILETASK_MESSAGE_NEW = Messages.getString("ReadXlsxFileTask.message.new");
        READXLSXFILETASK_MESSAGE_FAIL = Messages.getString("ReadXlsxFileTask.message.fail");
        TASKDISPATCHER_MESSAGE_SUBMIT = Messages.getString("TaskDispatcher.message.submit");
        TASKDISPATCHER_MESSAGE_FINISHED = Messages.getString("TaskDispatcher.message.finished");
        TASKDISPATCHER_MESSAGE_MISSING_1 = Messages.getString("TaskDispatcher.message.missing.1");
        TASKDISPATCHER_MESSAGE_MISSING_2 = Messages.getString("TaskDispatcher.message.missing.2");

        TASKDISPATCHER_MESSAGE_ILLEGAL_1 = Messages.getString("TaskDispatcher.message.illegal.1");
        TASKDISPATCHER_MESSAGE_ILLEGAL_2 = Messages.getString("TaskDispatcher.message.illegal.2");

        TASKDISPATCHER_MESSAGE_ANY = Messages.getString("TaskDispatcher.message.any");
        TASKDISPATCHER_MESSAGE_COLLECT = Messages.getString("TaskDispatcher.message.collect");
        TASKDISPATCHER_MESSAGE_SUCCESS = Messages.getString("TaskDispatcher.message.success");
        CREATE_TASKS = Messages.getString("TaskFactory.create.tasks");
        BUILD_TASKS = Messages.getString("TaskFactory.build.tasks");
        ERROR_INSTANCE = Messages.getString("TaskFactory.error.instance");
        SLASH = Messages.getString("TaskFactory.slash");
        CALCULATE = Messages.getString("TaskFactory.calculate");
        PROGRESS = Messages.getString("TaskFactory.progress");
        SIZE = Messages.getString("TaskFactory.size");
        TASK = Messages.getString("TaskFactory.last.task");
        HEADER = Messages.getString("TaskFactory.add.header");
        MESSAGE_FAIL = Messages.getString("MainWindow.message.fail");
        DIALOG_CAPTION = Messages.getString("MainWindow.error.dialog.caption");
        TIME_ELAPSED = Messages.getString("MainWindow.time.elapsed");
        FINAL_MESSAGE = Messages.getString("MainWindow.final.message");
        PROGRESS_DONE = Messages.getString("MainWindow.progress.done");
        ERROR_CAPTION = Messages.getString("MainWindow.error.caption");
        FINISHED = Messages.getString("MainWindow.finished");
        READING_FILE = Messages.getString("MainWindow.Reading.file");
        FILE_PATTERN = Messages.getString("MainWindow.default.file.pattern");
        DATE_FORMAT = Messages.getString("MainWindow.default.date.format");
        STORE_BTN_NAME = Messages.getString("MainWindow.store.btn.name");
        SHOP_BTN_NAME = Messages.getString("MainWindow.shop.btn.name");
        FONT_NAME = Messages.getString("MainWindow.font.name");
        XLSX_CELL_KEY_INDEX = Messages.getString("xlsx.cell_key.index");
        XLSX_START_FROM = Messages.getString("xlsx.start.from");
        XLSX_NUMBER_FORMAT_ERROR = Messages.getString("xlsx.number.format.error");
        XLSX_HEADER_ROW_COUNT = Messages.getString("xlsx.header.row.count");
        XLSX_HEADER_START_FROM = Messages.getString("xlsx.header.start.from");
        XLSX_QUANTITY_INDEX = Messages.getString("xlsx.quantity.index");
    }

    public static Configuration getInstance() {
        LOGGER = ApplicationLogger.INSTANCE.getLogger(Configuration.class);
        LOGGER.info("Configuration instance created.");
        return INSTANCE;
    }
}
