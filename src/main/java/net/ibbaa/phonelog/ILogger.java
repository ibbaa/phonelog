package net.ibbaa.phonelog;

/**
 * Interface for logger implementations.
 */
public interface ILogger {

    /**
     * log method
     * 
     * @param tag       the tag
     * @param message   the message
     * @param throwable the Throwable (can be null)
     * @param level     the log level
     */
    void log(String tag, String message, Throwable throwable, LogLevel level);
}
