package net.ibbaa.phonelog;

import java.nio.charset.Charset;

/**
 * Passthrough implementation for {@link net.ibbaa.phonelog.ILogFormatter}.
 */
public class PassthroughMessageLogFormatter implements ILogFormatter {

    /**
     * Format the log entry
     * 
     * @param entry the log entry
     * @return the formatted log entry as string
     */
    public String formatLogFileEntry(LogFileEntry entry) {
	return entry.getMessage() + System.lineSeparator();
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
