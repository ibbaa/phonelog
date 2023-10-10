package net.ibbaa.phonelog;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PassthroughMessageLogFormatterTest {

    private PassthroughMessageLogFormatter logFormatter;

    @BeforeEach
    public void beforeEachTestMethod() {
	logFormatter = new PassthroughMessageLogFormatter();
    }

    @Test
    public void testFormatLogFileEntry() {
	LogFileEntry entry = getTestEntry(getTestTimestamp(), "thread", LogLevel.DEBUG, "tag", "message", null);
	String message = logFormatter.formatLogFileEntry(entry);
	assertEquals("message" + System.lineSeparator(), message);
	assertArrayEquals(message.getBytes(Charsets.UTF8_CHARSET),
		logFormatter.formatLogFileEntry(entry, Charset.forName("UTF-8")));
	entry = getTestEntry(1, null, LogLevel.DEBUG, null, "message", null);
	message = logFormatter.formatLogFileEntry(entry);
	assertEquals("message" + System.lineSeparator(), message);
	assertArrayEquals(message.getBytes(Charsets.UTF8_CHARSET),
		logFormatter.formatLogFileEntry(entry, Charset.forName("UTF-8")));
    }

    private long getTestTimestamp() {
	Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
	calendar.set(Calendar.MILLISECOND, 999);
	return calendar.getTimeInMillis();
    }

    private LogFileEntry getTestEntry(long timestamp, String thread, LogLevel level, String tag, String message,
	    Throwable exc) {
	return new LogFileEntry(timestamp, thread, level, tag, message, exc);
    }
}
