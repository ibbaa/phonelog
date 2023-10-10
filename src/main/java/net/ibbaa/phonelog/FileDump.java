package net.ibbaa.phonelog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link net.ibbaa.phonelog.IDump} implemenation that writes dumps to a file.
 */
public class FileDump implements IDump {

    private final static int DEFAULT_ARCHIVE_FILE_COUNT = 50;
    private final static int DEFAULT_DELETE_FILE_COUNT = -1;
    private final static String DEFAULT_DUMP_FILE_EXTENSION = "txt";
    private final static String DEFAULT_EMPTY_MESSAGE = "No entries.";

    private final int archiveFileCount;
    private final int deleteFileCount;
    private final String dumpFileExtension;
    private final String emptyMessage;
    private final String dumpDirectory;

    private final static ReentrantLock dumpLock = new ReentrantLock();

    /**
     * Constructor
     * 
     * @param dumpDirectory directory to write dump files to
     */
    public FileDump(String dumpDirectory) {
	this(dumpDirectory, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, DEFAULT_DUMP_FILE_EXTENSION,
		DEFAULT_EMPTY_MESSAGE);
    }

    /**
     * Constructor
     * 
     * @param dumpDirectory     directory to write dump files to
     * @param dumpFileExtension extension for dump files. Default is 'txt'.
     */
    public FileDump(String dumpDirectory, String dumpFileExtension) {
	this(dumpDirectory, DEFAULT_ARCHIVE_FILE_COUNT, DEFAULT_DELETE_FILE_COUNT, dumpFileExtension,
		DEFAULT_EMPTY_MESSAGE);
    }

    /**
     * Constructor
     * 
     * @param dumpDirectory     directory to write dump files to
     * @param archiveFileCount  limit of dump files, when exceeded an archive will
     *                          be created. Default is 50.
     * @param deleteFileCount   limit of archive files, when exceeded oldest one
     *                          will be deleted. Default is -1 (no deleting).
     * @param dumpFileExtension extension for dump files. Default is 'txt'.
     * @param emptyMessage      message if nothing to dump
     */
    public FileDump(String dumpDirectory, int archiveFileCount, int deleteFileCount, String dumpFileExtension,
	    String emptyMessage) {
	this.archiveFileCount = archiveFileCount;
	this.deleteFileCount = deleteFileCount;
	this.dumpFileExtension = dumpFileExtension;
	this.emptyMessage = emptyMessage;
	this.dumpDirectory = dumpDirectory;
    }

    /**
     * Dump method
     * 
     * @param tag          the tag
     * @param message      the message
     * @param baseFileName the file name to write dump to
     * @param source       the dump source implementation
     */
    @Override
    public void dump(String tag, String message, String baseFileName, IDumpSource source) {
	if (source == null) {
	    return;
	}
	LogFileEntry logEntry = null;
	if (tag != null && message != null) {
	    logEntry = new LogFileEntry(System.currentTimeMillis(), Thread.currentThread().getName(), LogLevel.DEBUG,
		    tag, message, null);
	}
	Thread dumpThread = new Thread(new DumpThread(logEntry, baseFileName, source));
	dumpThread.start();
    }

    private class DumpThread implements Runnable {

	private final LogFileEntry logEntry;
	private final String baseFileName;
	private final IDumpSource source;

	public DumpThread(LogFileEntry logEntry, String baseFileName, IDumpSource source) {
	    this.logEntry = logEntry;
	    this.baseFileName = baseFileName;
	    this.source = source;
	}

	@Override
	public void run() {
	    try {
		dumpLock.lock();
		List<?> objectsToDump = source.objectsToDump();
		File dumpFolder = new File(dumpDirectory);
		if (!dumpFolder.exists()) {
		    dumpFolder.mkdirs();
		}
		LogFileManager fileManager = new LogFileManager();
		DefaultLogFormatter formatter = new DefaultLogFormatter();
		String baseDumpFileName = baseFileName;
		if (baseDumpFileName == null) {
		    if (objectsToDump == null || objectsToDump.isEmpty()) {
			return;
		    }
		    baseDumpFileName = objectsToDump.get(0).getClass().getSimpleName().toLowerCase();
		}
		baseDumpFileName += "." + dumpFileExtension;
		long timestamp = logEntry != null ? logEntry.getTimestamp() : System.currentTimeMillis();
		String header = logEntry != null ? formatter.formatLogFileEntry(logEntry) : null;
		String dumpFileName = fileManager.suffixFileName(baseDumpFileName,
			fileManager.getTimestampSuffix(timestamp));
		dumpFileName = fileManager.getValidFileName(dumpFolder, dumpFileName, null);
		fileManager.writeListToFile(header, emptyMessage, objectsToDump, new File(dumpFolder, dumpFileName));
		if (archiveFileCount > 0) {
		    Housekeeper housekeeper = new Housekeeper(dumpDirectory, baseDumpFileName, archiveFileCount,
			    deleteFileCount, new DumpFilenameFilter(baseDumpFileName));
		    housekeeper.doHousekeepingNow();
		}
	    } catch (Exception exc) {
		// Do nothing
	    } finally {
		dumpLock.unlock();
	    }
	}
    }

    private class DumpFilenameFilter implements FilenameFilter {

	private final String baseDumpFileName;

	public DumpFilenameFilter(String baseDumpFileName) {
	    this.baseDumpFileName = baseDumpFileName;
	}

	@Override
	public boolean accept(File dir, String name) {
	    LogFileManager fileManager = new LogFileManager();
	    String dumpFileBaseName = fileManager.getFileNameWithoutExtension(baseDumpFileName);
	    String dumpFileSuffix = fileManager.getFileNameExtension(baseDumpFileName);
	    return name.startsWith(dumpFileBaseName) && name.endsWith(dumpFileSuffix);
	}
    }
}
