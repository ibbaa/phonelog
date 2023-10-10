package net.ibbaa.phonelog;

/**
 * Representation of a log entry.
 */
public class LogFileEntry {

    private final long timestamp;
    private final String thread;
    private final LogLevel level;
    private final String tag;
    private final String message;
    private final Throwable throwable;

    /**
     * Constructor
     * 
     * @param timestamp the timestam
     * @param thread    the thread id
     * @param level     the log level
     * @param tag       the tag
     * @param message   the message
     * @param throwable the Throwable
     */
    public LogFileEntry(long timestamp, String thread, LogLevel level, String tag, String message,
	    Throwable throwable) {
	this.timestamp = timestamp;
	this.thread = thread;
	this.level = level;
	this.tag = tag;
	this.message = message;
	this.throwable = throwable;
    }

    /**
     * Returns the timestamp
     * 
     * @return the timestamp
     */
    public long getTimestamp() {
	return timestamp;
    }

    /**
     * Returns the thread id
     * 
     * @return the thread id
     */
    public String getThread() {
	return thread;
    }

    /**
     * Returns the log level
     * 
     * @return the log level
     */
    public LogLevel getLevel() {
	return level;
    }

    /**
     * Returns the tag
     * 
     * @return the tag
     */
    public String getTag() {
	return tag;
    }

    /**
     * Returns the message
     * 
     * @return the message
     */
    public String getMessage() {
	return message;
    }

    /**
     * Returns the Throwable
     * 
     * @return the Throwable
     */
    public Throwable getThrowable() {
	return throwable;
    }

    /**
     * toString() implementation
     * 
     * @return formatted string
     */
    @Override
    public String toString() {
	return "LogFileEntry{" + "timestamp=" + timestamp + ", thread='" + thread + '\'' + ", level=" + level
		+ ", tag='" + tag + '\'' + ", message='" + message + '\'' + ", throwable=" + throwable + '}';
    }
}
