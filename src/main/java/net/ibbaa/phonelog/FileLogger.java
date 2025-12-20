package net.ibbaa.phonelog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link net.ibbaa.phonelog.ILogger} implemenation that writes logs to a file.
 */
public class FileLogger implements ILogger {

    private final static LogLevel DEFAULT_LOG_LEVEL = LogLevel.DEBUG;
    private final static int DEFAULT_MAX_FILE_SIZE = 1024 * 1024 * 10;
    private final static int DEFAULT_ARCHIVE_FILE_COUNT = 50;
    private final static int DEFAULT_DELETE_FILE_COUNT = -1;
    private final static String DEFAULT_LOG_FILE_BASE_NAME = "app.log";

    private final static int LOG_QUEUE_PUT_TIMEOUT = 500;
    private final static int LOG_QUEUE_TAKE_TIMEOUT = 1000;

    private final static ReentrantLock loggerLock = new ReentrantLock();

    private final LogLevel maxLevel;
    private final int maxFileSize;
    private final int archiveFileCount;
    private final int deleteFileCount;
    private final String logDirectory;
    private final String logFileName;
    private final ILogFormatter logFormatter;
    private final ILogger delegateLog;

    private final LinkedBlockingQueue<LogFileEntry> logQueue;
    private final AtomicBoolean logThreadActive;

    /**
     * Constructor
     * 
     * @param logDirectory directory to write log files to
     */
    public FileLogger(String logDirectory) {
	this(DEFAULT_LOG_LEVEL, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), null);
    }

    /**
     * Constructor
     * 
     * @param logDirectory directory to write log files to
     * @param delegateLog  an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                     as delegate. Default is <code>null</code>.
     */
    public FileLogger(String logDirectory, ILogger delegateLog) {
	this(DEFAULT_LOG_LEVEL, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), delegateLog);
    }

    /**
     * Constructor
     * 
     * @param maxFileSize  the max file size for rotating. Default is 10 MByte.
     * @param logDirectory directory to write log files to
     */
    public FileLogger(int maxFileSize, String logDirectory) {
	this(DEFAULT_LOG_LEVEL, maxFileSize, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), null);
    }

    /**
     * Constructor
     * 
     * @param maxFileSize  the max file size for rotating. Default is 10 MByte.
     * @param logDirectory directory to write log files to
     * @param delegateLog  an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                     as delegate. Default is <code>null</code>.
     */
    public FileLogger(int maxFileSize, String logDirectory, ILogger delegateLog) {
	this(DEFAULT_LOG_LEVEL, maxFileSize, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), delegateLog);
    }

    /**
     * Constructor
     * 
     * @param logDirectory directory to write log files to
     * @param logFileName  base name for log files. Default is 'app.log'.
     */
    public FileLogger(String logDirectory, String logFileName) {
	this(DEFAULT_LOG_LEVEL, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, logFileName, new DefaultLogFormatter(), null);
    }

    /**
     * Constructor
     * 
     * @param logDirectory directory to write log files to
     * @param logFileName  base name for log files. Default is 'app.log'.
     * @param delegateLog  an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                     as delegate. Default is <code>null</code>.
     */
    public FileLogger(String logDirectory, String logFileName, ILogger delegateLog) {
	this(DEFAULT_LOG_LEVEL, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, logFileName, new DefaultLogFormatter(), delegateLog);
    }

    /**
     * Constructor
     * 
     * @param maxFileSize      the max file size for rotating. Default is 10 MByte.
     * @param archiveFileCount limit of log files, when exceeded an archive will be
     *                         created. Default is 50.
     * @param deleteFileCount  limit of archive files, when exceeded oldest one will
     *                         be deleted. Default is -1 (no deleting).
     * @param logDirectory     directory to write log files to
     */
    public FileLogger(int maxFileSize, int archiveFileCount, int deleteFileCount, String logDirectory) {
	this(DEFAULT_LOG_LEVEL, maxFileSize, archiveFileCount, deleteFileCount, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), null);
    }

    /**
     * Constructor
     * 
     * @param maxFileSize      the max file size for rotating. Default is 10 MByte.
     * @param archiveFileCount limit of log files, when exceeded an archive will be
     *                         created. Default is 50.
     * @param deleteFileCount  limit of archive files, when exceeded oldest one will
     *                         be deleted. Default is -1 (no deleting).
     * @param logDirectory     directory to write log files to
     * @param delegateLog      an {@link net.ibbaa.phonelog.ILogger} implementation
     *                         used as delegate. Default is <code>null</code>.
     */
    public FileLogger(int maxFileSize, int archiveFileCount, int deleteFileCount, String logDirectory, ILogger delegateLog) {
	this(DEFAULT_LOG_LEVEL, maxFileSize, archiveFileCount, deleteFileCount, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), delegateLog);
    }

    /**
     * Constructor
     * 
     * @param maxLevel     the max log level. Default is DEBUG.
     * @param logDirectory directory to write log files to
     */
    public FileLogger(LogLevel maxLevel, String logDirectory) {
	this(maxLevel, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), null);
    }

    /**
     * Constructor
     * 
     * @param maxLevel     the max log level. Default is DEBUG.
     * @param logDirectory directory to write log files to
     * @param delegateLog  an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                     as delegate. Default is <code>null</code>.
     */
    public FileLogger(LogLevel maxLevel, String logDirectory, ILogger delegateLog) {
	this(maxLevel, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), delegateLog);
    }

    /**
     * Constructor
     * 
     * @param maxLevel     the max log level. Default is DEBUG.
     * @param maxFileSize  the max file size for rotating. Default is 10 MByte.
     * @param logDirectory directory to write log files to
     */
    public FileLogger(LogLevel maxLevel, int maxFileSize, String logDirectory) {
	this(maxLevel, maxFileSize, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), null);
    }

    /**
     * Constructor
     * 
     * @param maxLevel     the max log level. Default is DEBUG.
     * @param maxFileSize  the max file size for rotating. Default is 10 MByte.
     * @param logDirectory directory to write log files to
     * @param delegateLog  an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                     as delegate. Default is <code>null</code>.
     */
    public FileLogger(LogLevel maxLevel, int maxFileSize, String logDirectory, ILogger delegateLog) {
	this(maxLevel, maxFileSize, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), delegateLog);
    }

    /**
     * Constructor
     * 
     * @param maxLevel     the max log level. Default is DEBUG.
     * @param logDirectory directory to write log files to
     * @param logFileName  base name for log files
     */
    public FileLogger(LogLevel maxLevel, String logDirectory, String logFileName) {
	this(maxLevel, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, logFileName, new DefaultLogFormatter(), null);
    }

    /**
     * Constructor
     * 
     * @param maxLevel     the max log level. Default is DEBUG.
     * @param logDirectory directory to write log files to
     * @param logFileName  base name for log files. Default is 'app.log'.
     * @param delegateLog  an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                     as delegate. Default is <code>null</code>.
     */
    public FileLogger(LogLevel maxLevel, String logDirectory, String logFileName, ILogger delegateLog) {
	this(maxLevel, DEFAULT_MAX_FILE_SIZE, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, logDirectory, logFileName, new DefaultLogFormatter(), delegateLog);
    }

    /**
     * Constructor
     * 
     * @param maxLevel         the max log level. Default is DEBUG.
     * @param maxFileSize      the max file size for rotating. Default is 10 MByte.
     * @param archiveFileCount limit of log files, when exceeded an archive will be
     *                         created. Default is 50.
     * @param deleteFileCount  limit of archive files, when exceeded oldest one will
     *                         be deleted. Default is -1 (no deleting).
     * @param logDirectory     directory to write log files to
     */
    public FileLogger(LogLevel maxLevel, int maxFileSize, int archiveFileCount, int deleteFileCount, String logDirectory) {
	this(maxLevel, maxFileSize, archiveFileCount, deleteFileCount, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), null);
    }

    /**
     * Constructor
     * 
     * @param maxLevel         the max log level. Default is DEBUG.
     * @param maxFileSize      the max file size for rotating. Default is 10 MByte.
     * @param archiveFileCount limit of log files, when exceeded an archive will be
     *                         created. Default is 50.
     * @param deleteFileCount  limit of archive files, when exceeded oldest one will
     *                         be deleted. Default is -1 (no deleting).
     * @param logDirectory     directory to write log files to
     * @param delegateLog      an {@link net.ibbaa.phonelog.ILogger} implementation
     *                         used as delegate. Default is <code>null</code>
     */
    public FileLogger(LogLevel maxLevel, int maxFileSize, int archiveFileCount, int deleteFileCount, String logDirectory, ILogger delegateLog) {
	this(maxLevel, maxFileSize, archiveFileCount, deleteFileCount, logDirectory, DEFAULT_LOG_FILE_BASE_NAME, new DefaultLogFormatter(), delegateLog);
    }

    /**
     * Constructor
     * 
     * @param maxLevel         the max log level. Default is DEBUG.
     * @param maxFileSize      the max file size for rotating. Default is 10 MByte.
     * @param archiveFileCount limit of log files, when exceeded an archive will be
     *                         created. Default is 50.
     * @param deleteFileCount  limit of archive files, when exceeded oldest one will
     *                         be deleted. Default is -1 (no deleting).
     * @param logDirectory     directory to write log files to
     * @param logFileName      base name for log files. Default is 'app.log'.
     * @param logFormatter     the {@link net.ibbaa.phonelog.ILogFormatter}
     *                         implementation. Default is
     *                         {@link net.ibbaa.phonelog.DefaultLogFormatter}.
     * @param delegateLog      an {@link net.ibbaa.phonelog.ILogger} implementation
     *                         used as delegate. Default is <code>null</code>.
     */
    public FileLogger(LogLevel maxLevel, int maxFileSize, int archiveFileCount, int deleteFileCount, String logDirectory, String logFileName, ILogFormatter logFormatter, ILogger delegateLog) {
	this.maxLevel = maxLevel;
	this.maxFileSize = maxFileSize;
	this.archiveFileCount = archiveFileCount;
	this.deleteFileCount = deleteFileCount;
	this.logDirectory = logDirectory;
	this.logFileName = logFileName;
	this.logFormatter = logFormatter;
	this.delegateLog = delegateLog;
	this.logQueue = new LinkedBlockingQueue<>();
	this.logThreadActive = new AtomicBoolean(false);
    }

    /**
     * log method
     * 
     * @param tag       the tag
     * @param message   the message
     * @param throwable the Throwable (can be null)
     * @param level     the log level
     */
    @Override
    public void log(String tag, String message, Throwable throwable, LogLevel level) {
	if (delegateLog != null) {
	    delegateLog.log(tag, message, throwable, level);
	}
	if (level == null || level.getLevel() < maxLevel.getLevel()) {
	    return;
	}
	if (message == null) {
	    return;
	}
	try {
	    LogFileEntry logEntry = new LogFileEntry(System.currentTimeMillis(), Thread.currentThread().getName(), level, tag, message, throwable);
	    logQueue.offer(logEntry, LOG_QUEUE_PUT_TIMEOUT, TimeUnit.MILLISECONDS);
	    if (logThreadActive.compareAndSet(false, true)) {
		Thread logThread = new Thread(this::doLog);
		logThread.start();
	    }
	} catch (InterruptedException exc) {
	    // Do nothing
	}
    }

    private void doLog() {
	OutputStream logStream = null;
	try {
	    loggerLock.lock();
	    String normalizedLogFileName = normalizeFileName(this.logFileName);
	    File logFolder = new File(logDirectory);
	    if (!logFolder.exists()) {
		logFolder.mkdirs();
	    }
	    File logFile = new File(logFolder, normalizedLogFileName);
	    long fileSize = 0;
	    if (logFile.exists()) {
		fileSize = logFile.length();
	    }
	    logStream = initializeLogStream(logFile);
	    LogFileManager fileManager = new LogFileManager();
	    LogFileEntry entry;
	    while ((entry = logQueue.poll(LOG_QUEUE_TAKE_TIMEOUT, TimeUnit.MILLISECONDS)) != null) {
		byte[] message = logFormatter.formatLogFileEntry(entry, Charsets.UTF8_CHARSET);
		logStream.write(message);
		fileSize += message.length;
		if (fileSize >= maxFileSize) {
		    closeLogStream(logStream);
		    String newFileName = fileManager.getValidFileName(new File(logDirectory), normalizedLogFileName, System.currentTimeMillis());
		    if (newFileName != null) {
			if (logFile.renameTo(new File(new File(logDirectory), newFileName))) {
			    logFile = new File(logDirectory, normalizedLogFileName);
			    fileSize = 0;
			    logStream = initializeLogStream(logFile);
			    if (archiveFileCount > 0) {
				Housekeeper housekeeper = new Housekeeper(logDirectory, normalizedLogFileName, archiveFileCount, deleteFileCount, this::shouldBeArchived);
				Thread housekeeperThread = new Thread(housekeeper);
				housekeeperThread.start();
			    }
			} else {
			    return;
			}
		    } else {
			return;
		    }
		}
	    }
	} catch (Exception exc) {
	    // Do nothing
	} finally {
	    logThreadActive.set(false);
	    closeLogStream(logStream);
	    loggerLock.unlock();
	}
    }

    private boolean shouldBeArchived(File dir, String name) {
	if (logFileName.equals(name)) {
	    return false;
	}
	LogFileManager fileManager = new LogFileManager();
	String logFileBaseName = fileManager.getFileNameWithoutExtension(logFileName);
	String logFileSuffix = fileManager.getFileNameExtension(logFileName);
	return name.startsWith(logFileBaseName) && name.endsWith(logFileSuffix);
    }

    private OutputStream initializeLogStream(File logFile) throws IOException {
	return new BufferedOutputStream(new FileOutputStream(logFile, true));
    }

    private String normalizeFileName(String fileName) {
	if (fileName == null) {
	    return DEFAULT_LOG_FILE_BASE_NAME;
	}
	return fileName.replaceAll("/", "");
    }

    private void closeLogStream(OutputStream logstream) {
	try {
	    if (logstream != null) {
		logstream.flush();
		logstream.close();
	    }
	} catch (Exception exc) {
	    // Do nothing
	}
    }
}
