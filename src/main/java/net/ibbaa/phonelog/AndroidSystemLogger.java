package net.ibbaa.phonelog;

import java.util.function.BiConsumer;

/**
 * Template for {@link net.ibbaa.phonelog.ILogger} implementation delegating to
 * Android system logger.
 */
public class AndroidSystemLogger implements ILogger {

    private final static LogLevel DEFAULT_LOG_LEVEL = LogLevel.ERROR;
    private final ILogger delegateLog;
    private final LogLevel maxLevel;

    /**
     * Constructor
     */
    public AndroidSystemLogger() {
	this(null, DEFAULT_LOG_LEVEL);
    }

    /**
     * Constructor
     * 
     * @param maxLevel the max log level. Default is ERROR.
     */
    public AndroidSystemLogger(LogLevel maxLevel) {
	this(null, maxLevel);
    }

    /**
     * Constructor
     * 
     * @param delegateLog an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                    as delegate. Default is <code>null</code>.
     */
    public AndroidSystemLogger(ILogger delegateLog) {
	this(delegateLog, DEFAULT_LOG_LEVEL);
    }

    /**
     * Constructor
     * 
     * @param delegateLog an {@link net.ibbaa.phonelog.ILogger} implementation used
     *                    as delegate. Default is <code>null</code>.
     * @param maxLevel    the max log level. Default is ERROR.
     */
    public AndroidSystemLogger(ILogger delegateLog, LogLevel maxLevel) {
	this.delegateLog = delegateLog;
	this.maxLevel = maxLevel;
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
	switch (level) {
	case VERBOSE:
	    // call(android.util.Log::v, android.util.Log::v, tag, message, throwable);
	    break;
	case DEBUG:
	    // call(android.util.Log::d, android.util.Log::d, tag, message, throwable);
	    break;
	case INFO:
	    // call(android.util.Log::i, android.util.Log::i, tag, message, throwable);
	    break;
	case WARN:
	    // call(android.util.Log::w, android.util.Log::w, tag, message, throwable);
	    break;
	case ERROR:
	    // call(android.util.Log::e, android.util.Log::e, tag, message, throwable);
	    break;
	}

    }

    private void call(BiConsumer<String, String> consumer1, ThreeParameterConsumer<String, String, Throwable> consumer2,
	    String tag, String message, Throwable throwable) {
	if (throwable != null) {
	    consumer2.accept(tag, message, throwable);
	} else {
	    consumer1.accept(tag, message);
	}
    }

    @FunctionalInterface
    private interface ThreeParameterConsumer<T, U, V> {
	public void accept(T t, U u, V v);
    }
}
