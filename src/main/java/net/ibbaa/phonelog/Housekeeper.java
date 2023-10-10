package net.ibbaa.phonelog;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility class for housekeeping.
 */
public class Housekeeper implements Runnable {

    private final static String ZIP_FILE_EXTENSION = "zip";

    private final static ReentrantLock housekeepingLock = new ReentrantLock();

    private final String directory;
    private final String baseFileName;
    private final int archiveFileCount;
    private final int deleteFileCount;
    private final FilenameFilter filter;

    /**
     * Constructor
     * 
     * @param directory        the directory to clean
     * @param baseFileName     the base file name
     * @param archiveFileCount limit of log files, when exceeded an archive will be
     *                         created
     * @param deleteFileCount  limit of archive files, when exceeded oldest one will
     *                         be deleted
     * @param filter           filter which files should be recognized
     */
    public Housekeeper(String directory, String baseFileName, int archiveFileCount, int deleteFileCount,
	    FilenameFilter filter) {
	this.directory = directory;
	this.baseFileName = baseFileName;
	this.archiveFileCount = archiveFileCount;
	this.deleteFileCount = deleteFileCount;
	this.filter = filter;
    }

    /**
     * Start house keeping
     */
    public void doHousekeepingNow() {
	run();
    }

    /**
     * run method
     */
    @Override
    public void run() {
	try {
	    housekeepingLock.lock();
	    File[] filesToArchive;
	    if (filter == null) {
		filesToArchive = new File(directory).listFiles();
	    } else {
		filesToArchive = new File(directory).listFiles(filter);
	    }
	    if (filesToArchive != null && filesToArchive.length >= archiveFileCount) {
		LogFileManager fileManager = new LogFileManager();
		String zipFileName = fileManager.getFileNameWithoutExtension(baseFileName) + "." + ZIP_FILE_EXTENSION;
		zipFileName = fileManager.suffixFileName(zipFileName,
			fileManager.getTimestampSuffix(System.currentTimeMillis()));
		zipFileName = fileManager.getValidFileName(new File(directory), zipFileName, null);
		fileManager.zipFiles(Arrays.asList(filesToArchive), new File(directory, zipFileName));
		if (deleteFileCount > 0) {
		    File[] deleteableFiles = new File(directory).listFiles(this::isDeletableArchive);
		    if (deleteableFiles != null && deleteableFiles.length >= deleteFileCount) {
			fileManager.deleteOldest(deleteableFiles);
		    }
		}
	    }
	} catch (Exception exc) {
	    // Do nothing
	} finally {
	    housekeepingLock.unlock();
	}
    }

    private boolean isDeletableArchive(File dir, String name) {
	LogFileManager fileManager = new LogFileManager();
	String zipFileName = fileManager.getFileNameWithoutExtension(baseFileName);
	if ((zipFileName + "." + ZIP_FILE_EXTENSION).equals(name)) {
	    return false;
	}
	return name.startsWith(zipFileName) && name.endsWith(ZIP_FILE_EXTENSION);
    }
}
