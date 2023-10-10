package net.ibbaa.phonelog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JavaSystemLoggerTest {

    private TestHandler handler;
    private JavaSystemLogger systemLogger;

    @BeforeEach
    public void beforeEachTestMethod() {
	handler = new TestHandler();
	systemLogger = new JavaSystemLogger(handler, null, LogLevel.VERBOSE);
	systemLogger.removeSystemHandler();
    }

    @Test
    public void testLogNull() {
	systemLogger.log("123", "message", null, null);
	assertNull(handler.getLastRecord());
	systemLogger.log("123", null, null, LogLevel.ERROR);
	assertNull(handler.getLastRecord());
    }

    @Test
    public void testLog() {
	systemLogger.log("123", "message", null, LogLevel.VERBOSE);
	LogRecord record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.FINEST, record.getLevel());
	assertNull(record.getThrown());
	systemLogger.log("123", "message", null, LogLevel.DEBUG);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.FINE, record.getLevel());
	assertNull(record.getThrown());
	systemLogger.log("123", "message", null, LogLevel.INFO);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.INFO, record.getLevel());
	assertNull(record.getThrown());
	systemLogger.log("123", "message", null, LogLevel.WARN);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.WARNING, record.getLevel());
	assertNull(record.getThrown());
	systemLogger.log("123", "message", null, LogLevel.ERROR);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.SEVERE, record.getLevel());
	assertNull(record.getThrown());
    }

    @Test
    public void testLogException() {
	systemLogger.log("123", "message", new NullPointerException(), LogLevel.VERBOSE);
	LogRecord record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.FINEST, record.getLevel());
	assertTrue(record.getThrown() instanceof NullPointerException);
	systemLogger.log("123", "message", new NullPointerException(), LogLevel.DEBUG);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.FINE, record.getLevel());
	assertTrue(record.getThrown() instanceof NullPointerException);
	systemLogger.log("123", "message", new NullPointerException(), LogLevel.INFO);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.INFO, record.getLevel());
	assertTrue(record.getThrown() instanceof NullPointerException);
	systemLogger.log("123", "message", new NullPointerException(), LogLevel.WARN);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.WARNING, record.getLevel());
	assertTrue(record.getThrown() instanceof NullPointerException);
	systemLogger.log("123", "message", new NullPointerException(), LogLevel.ERROR);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.SEVERE, record.getLevel());
	assertTrue(record.getThrown() instanceof NullPointerException);
    }

    @Test
    public void testLogLevel() {
	JavaSystemLogger logger = new JavaSystemLogger(handler, null, LogLevel.WARN);
	logger.log("123", "message", null, LogLevel.VERBOSE);
	assertNull(handler.getLastRecord());
	logger.log("123", "message", null, LogLevel.DEBUG);
	assertNull(handler.getLastRecord());
	logger.log("123", "message", null, LogLevel.WARN);
	LogRecord record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.WARNING, record.getLevel());
	assertNull(record.getThrown());
	logger.log("123", "message", null, LogLevel.ERROR);
	record = handler.getLastRecord();
	assertEquals("message", record.getMessage());
	assertEquals(Level.SEVERE, record.getLevel());
	assertNull(record.getThrown());
    }

    private class TestHandler extends Handler {

	private LogRecord lastRecord;

	@Override
	public void publish(LogRecord record) {
	    lastRecord = record;
	}

	@Override
	public void flush() {

	}

	@Override
	public void close() throws SecurityException {

	}

	public LogRecord getLastRecord() {
	    return lastRecord;
	}
    }
}
