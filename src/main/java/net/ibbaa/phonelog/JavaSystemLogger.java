package net.ibbaa.phonelog;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link net.ibbaa.phonelog.ILogger} implementation delegating to Java system
 * logger.
 */
public class JavaSystemLogger implements ILogger {

    private final static LogLevel DEFAULT_LOG_LEVEL = LogLevel.ERROR;
    private final Handler handler;
    private final ILogger delegateLog;
    private final LogLevel maxLevel;

    /**
     * Constructor
     */
    public JavaSystemLogger() {
	this(null, null, DEFAULT_LOG_LEVEL);
    }

    /**
     * Constructor
     * 
     * @param maxLevel the max log level. Default is ERROR.
     */
    public JavaSystemLogger(LogLevel maxLevel) {
	this(null, null, maxLevel);
    }

    /**
     * Constructor
     * 
     * @param handler an handler added to the underlying Java Logger. Default is
     *                <code>null</code>.
     */
    public JavaSystemLogger(Handler handler) {
	this(handler, null, DEFAULT_LOG_LEVEL);
    }

    /**
     * Constructor
     * 
     * @param handler  an handler added to the underlying Java Logger. Default is
     *                 <code>null</code>.
     * @param maxLevel the max log level. Default is ERROR.
     */
    public JavaSystemLogger(Handler handler, LogLevel maxLevel) {
	this(handler, null, maxLevel);
    }

    /**
     * Constructor
     * 
     * @param delegateLog an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                    as delegate. Default is <code>null</code>.
     */
    public JavaSystemLogger(ILogger delegateLog) {
	this(null, delegateLog, DEFAULT_LOG_LEVEL);
    }

    /**
     * Constructor
     * 
     * @param delegateLog an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                    as delegate. Default is <code>null</code>.
     * @param maxLevel    the max log level. Default is ERROR.
     */
    public JavaSystemLogger(ILogger delegateLog, LogLevel maxLevel) {
	this(null, delegateLog, maxLevel);
    }

    /**
     * Constructor
     * 
     * @param handler     an handler added to the underlying Java Logger. Default is
     *                    <code>null</code>.
     * @param delegateLog an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                    as delegate. Default is <code>null</code>.
     * @param maxLevel    the max log level. Default is ERROR.
     */
    public JavaSystemLogger(Handler handler, ILogger delegateLog, LogLevel maxLevel) {
	this.handler = handler;
	this.delegateLog = delegateLog;
	this.maxLevel = maxLevel;
    }

    /**
     * Removes all predefined handlers.
     */
    public void removeSystemHandler() {
	Logger rootLogger = Logger.getLogger("");
	Handler[] handlers = rootLogger.getHandlers();
	for (Handler handler : handlers) {
	    rootLogger.removeHandler(handler);
	}
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
	if (tag == null) {
	    tag = Logger.GLOBAL_LOGGER_NAME;
	}
	Level javaLevel = mapToJavaSystemLevel(level);
	Logger logger = Logger.getLogger(tag);
	logger.setLevel(Level.ALL);
	if (handler != null) {
	    logger.addHandler(handler);
	}
	if (throwable != null) {
	    logger.log(javaLevel, message, throwable);
	} else {
	    logger.log(javaLevel, message);
	}
    }

    private Level mapToJavaSystemLevel(LogLevel level) {
	switch (level) {
	case VERBOSE:
	    return Level.FINEST;
	case DEBUG:
	    return Level.FINE;
	case INFO:
	    return Level.INFO;
	case WARN:
	    return Level.WARNING;
	case ERROR:
	    return Level.SEVERE;
	default:
	    return Level.SEVERE;
	}
    }

}
