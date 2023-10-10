package net.ibbaa.phonelog;

/**
 * Representation of log level.
 */
public enum LogLevel {
    /**
     * VERBOSE log level
     */
    VERBOSE(1),
    /**
     * DEBUG log level
     */
    DEBUG(2),
    /**
     * INFO log level
     */
    INFO(3),
    /**
     * WARN log level
     */
    WARN(4),
    /**
     * ERROR log level
     */
    ERROR(5);

    private final int level;

    /**
     * Constructor
     * 
     * @param level
     */
    LogLevel(int level) {
	this.level = level;
    }

    /**
     * Returns the log level
     * 
     * @return the log level
     */
    public int getLevel() {
	return level;
    }
}
