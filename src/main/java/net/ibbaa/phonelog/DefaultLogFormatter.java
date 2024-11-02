package net.ibbaa.phonelog;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Default implementation for {@link net.ibbaa.phonelog.ILogFormatter}.
 */
public class DefaultLogFormatter implements ILogFormatter {

    private final static String LOG_TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * Constructor
     */
    public DefaultLogFormatter() {

    }
    
    /**
     * Format the log entry
     * 
     * @param entry the log entry
     * @return the formatted log entry as string
     */
    public String formatLogFileEntry(LogFileEntry entry) {
	SimpleDateFormat logTimestampDateFormat = new SimpleDateFormat(LOG_TIMESTAMP_PATTERN, Locale.US);
	StringBuilder messageBuilder = new StringBuilder();
	messageBuilder.append(logTimestampDateFormat.format(new Date(entry.getTimestamp())));
	String threadName = entry.getThread();
	if (threadName != null && !threadName.isEmpty()) {
	    messageBuilder.append(" [");
	    messageBuilder.append(threadName);
	    messageBuilder.append("]");
	}
	messageBuilder.append(" ");
	messageBuilder.append(entry.getLevel().name());
	String tag = entry.getTag();
	if (tag != null && !tag.isEmpty()) {
	    messageBuilder.append(" ");
	    messageBuilder.append(tag);
	}
	messageBuilder.append(": ");
	messageBuilder.append(entry.getMessage());
	Throwable exception = entry.getThrowable();
	if (exception != null) {
	    messageBuilder.append(System.lineSeparator());
	    messageBuilder.append(exceptionToString(exception));
	}
	messageBuilder.append(System.lineSeparator());
	return messageBuilder.toString();
    }

    /**
     * Returns a string representation of the exception
     * 
     * @param exc the exception
     * @return th exception as string
     */
    public String exceptionToString(Throwable exc) {
	StringWriter stringWriter = new StringWriter();
	PrintWriter printWriter = new PrintWriter(stringWriter);
	exc.printStackTrace(printWriter);
	return stringWriter.toString();
    }

    /**
     * Format the log entry
     * 
     * @param entry    the log entry
     * @param encoding the encoding
     * @return the formatted log entry as byte array
     */
    public byte[] formatLogFileEntry(LogFileEntry entry, Charset encoding) {
	return formatLogFileEntry(entry).getBytes(encoding);
    }
}
