package net.ibbaa.phonelog;

import java.nio.charset.Charset;

/**
 * Interface for logf formatters.
 */
public interface ILogFormatter {

    /**
     * Format the log entry
     * 
     * @param entry the log entry
     * @return the formatted log entry as string
     */
    String formatLogFileEntry(LogFileEntry entry);

    /**
     * Format the log entry
     * 
     * @param entry    the log entry
     * @param encoding the encoding
     * @return the formatted log entry as byte array
     */
    byte[] formatLogFileEntry(LogFileEntry entry, Charset encoding);
}
