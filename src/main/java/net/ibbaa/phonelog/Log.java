package net.ibbaa.phonelog;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Log class to be used in client code.
 */
public class Log {

    private static final ReentrantReadWriteLock debugLoggerLock = new ReentrantReadWriteLock();

    private static ILogger logger;

    /**
     * Sets the {@link net.ibbaa.phonelog.ILogger} implementation
     * 
     * @param logger the {@link net.ibbaa.phonelog.ILogger} implementation
     */
    public static void initialize(ILogger logger) {
	debugLoggerLock.writeLock().lock();
	Log.logger = logger;
	debugLoggerLock.writeLock().unlock();
    }

    /**
     * Returns the {@link net.ibbaa.phonelog.ILogger} implementation
     * 
     * @return the {@link net.ibbaa.phonelog.ILogger} implementation
     */
    public static ILogger getLogger() {
	debugLoggerLock.readLock().lock();
	try {
	    return logger;
	} finally {
	    debugLoggerLock.readLock().unlock();
	}
    }

    /**
     * Writes a log entry with INFO level
     * 
     * @param tag     the tag
     * @param message the message
     */
    public static void i(String tag, String message) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, null, LogLevel.INFO);
	}
    }

    /**
     * Writes a log entry with INFO level
     * 
     * @param tag       the tag
     * @param message   the message
     * @param Throwable the Throwable
     */
    public static void i(String tag, String message, Throwable Throwable) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, Throwable, LogLevel.INFO);
	}
    }

    /**
     * Writes a log entry with VERBOSE level
     * 
     * @param tag     the tag
     * @param message the message
     */
    public static void v(String tag, String message) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, null, LogLevel.VERBOSE);
	}
    }

    /**
     * Writes a log entry with VERBOSE level
     * 
     * @param tag       the tag
     * @param message   the message
     * @param Throwable the Throwable
     */
    public static void v(String tag, String message, Throwable Throwable) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, Throwable, LogLevel.VERBOSE);
	}
    }

    /**
     * Writes a log entry with DEBUG level
     * 
     * @param tag     the tag
     * @param message the message
     */
    public static void d(String tag, String message) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, null, LogLevel.DEBUG);
	}
    }

    /**
     * Writes a log entry with DEBUG level
     * 
     * @param tag       the tag
     * @param message   the message
     * @param Throwable the Throwable
     */
    public static void d(String tag, String message, Throwable Throwable) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, Throwable, LogLevel.DEBUG);
	}
    }

    /**
     * Writes a log entry with WARN level
     * 
     * @param tag     the tag
     * @param message the message
     */
    public static void w(String tag, String message) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, null, LogLevel.WARN);
	}
    }

    /**
     * Writes a log entry with WARN level
     * 
     * @param tag       the tag
     * @param message   the message
     * @param Throwable the Throwable
     */
    public static void w(String tag, String message, Throwable Throwable) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, Throwable, LogLevel.WARN);
	}
    }

    /**
     * Writes a log entry with ERROR level
     * 
     * @param tag     the tag
     * @param message the message
     */
    public static void e(String tag, String message) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, null, LogLevel.ERROR);
	}
	;
    }

    /**
     * Writes a log entry with ERROR level
     * 
     * @param tag       the tag
     * @param message   the message
     * @param Throwable the Throwable
     */
    public static void e(String tag, String message, Throwable Throwable) {
	ILogger logger = getLogger();
	if (logger != null) {
	    logger.log(tag, message, Throwable, LogLevel.ERROR);
	}
    }
}
