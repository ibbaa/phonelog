package net.ibbaa.phonelog.android;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler that delegates to Android system logging.
 */
public class AndroidSystemLoggingHandler extends Handler {

    /**
     * Constructor
     */
    public AndroidSystemLoggingHandler() {

    }

    /**
     * Publish a {@code LogRecord}.
     * 
     * @param record the log record
     */
    @Override
    public void publish(LogRecord record) {
	if (!super.isLoggable(record)) {
	    return;
	}
	int level = getAndroidLevel(record.getLevel());
	android.util.Log.println(level, record.getLoggerName(), record.getMessage());
	if (record.getThrown() != null) {
	    android.util.Log.println(level, record.getLoggerName(), android.util.Log.getStackTraceString(record.getThrown()));
	}
    }

    private int getAndroidLevel(Level level) {
	int value = level.intValue();
	if (value >= Level.SEVERE.intValue()) {
	    return 6;
	} else if (value >= Level.WARNING.intValue()) {
	    return 5;
	} else if (value >= Level.INFO.intValue()) {
	    return 4;
	} else {
	    return 3;
	}
    }

    /**
     * Does noting here.
     */
    @Override
    public void close() {
    }

    /**
     * Does noting here.
     */
    @Override
    public void flush() {
    }
}
