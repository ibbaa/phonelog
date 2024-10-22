package net.ibbaa.phonelog;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DefaultLogFormatterTest {

    private DefaultLogFormatter defaultLogFormatter;

    @BeforeEach
    public void beforeEachTestMethod() {
	defaultLogFormatter = new DefaultLogFormatter();
    }

    @Test
    public void testExceptionToString() {
	String exc = defaultLogFormatter.exceptionToString(new IllegalArgumentException(new NullPointerException()));
	assertTrue(exc.contains(IllegalArgumentException.class.getName()));
	assertTrue(exc.contains(NullPointerException.class.getName()));
	assertTrue(exc.contains("at net.ibbaa.phonelog.DefaultLogFormatterTest.testExceptionToString"));
	assertTrue(exc.contains("Caused by: " + NullPointerException.class.getName()));
    }

    @Test
    public void testFormatLogFileEntry() {
	LogFileEntry entry = getTestEntry(getTestTimestamp(), "thread", LogLevel.DEBUG, "tag", "message", null);
	String message = defaultLogFormatter.formatLogFileEntry(entry);
	assertEquals("1985-12-24 01:01:01.999 [thread] DEBUG tag: message" + System.lineSeparator(), message);
	assertArrayEquals(message.getBytes(Charsets.UTF8_CHARSET), defaultLogFormatter.formatLogFileEntry(entry, Charsets.UTF8_CHARSET));
	try {
	    throw new NullPointerException();
	} catch (Exception exc) {
	    entry = getTestEntry(getTestTimestamp(), "thread", LogLevel.DEBUG, "tag", "message", exc);
	    message = defaultLogFormatter.formatLogFileEntry(entry);
	    assertTrue(message.startsWith("1985-12-24 01:01:01.999 [thread] DEBUG tag: message"));
	    assertTrue(message.contains(NullPointerException.class.getName()));
	    assertTrue(message.contains("at net.ibbaa.phonelog.DefaultLogFormatterTest.testFormatLogFileEntry"));
	    assertArrayEquals(message.getBytes(Charsets.UTF8_CHARSET), defaultLogFormatter.formatLogFileEntry(entry, Charsets.UTF8_CHARSET));
	}
    }

    @Test
    public void testTagIsNull() {
	LogFileEntry entry = getTestEntry(getTestTimestamp(), "thread", LogLevel.DEBUG, null, "message", null);
	String message = defaultLogFormatter.formatLogFileEntry(entry);
	assertEquals("1985-12-24 01:01:01.999 [thread] DEBUG: message" + System.lineSeparator(), message);
	assertArrayEquals(message.getBytes(Charsets.UTF8_CHARSET), defaultLogFormatter.formatLogFileEntry(entry, Charsets.UTF8_CHARSET));
    }

    private long getTestTimestamp() {
	Calendar calendar = new GregorianCalendar(1985, Calendar.DECEMBER, 24, 1, 1, 1);
	calendar.set(Calendar.MILLISECOND, 999);
	return calendar.getTimeInMillis();
    }

    private LogFileEntry getTestEntry(long timestamp, String thread, LogLevel level, String tag, String message, Throwable exc) {
	return new LogFileEntry(timestamp, thread, level, tag, message, exc);
    }
}
