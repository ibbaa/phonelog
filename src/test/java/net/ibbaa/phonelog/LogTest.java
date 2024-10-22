package net.ibbaa.phonelog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LogTest {

    private MockLogger mockLogger;

    @BeforeEach
    public void beforeEachTestMethod() {
	mockLogger = new MockLogger();
	Log.initialize(mockLogger);
    }

    @AfterEach
    public void afterEachTestMethod() {
	Log.initialize(null);
    }

    @Test
    public void testLog() {
	Log.i("tag1", "message1");
	assertEquals(1, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(0), "tag1", "message1", LogLevel.INFO, null);
	NullPointerException exc = new NullPointerException();
	Log.i("tag2", "message2", exc);
	assertEquals(2, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(1), "tag2", "message2", LogLevel.INFO, exc);
	Log.v("tag3", "message3");
	assertEquals(3, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(2), "tag3", "message3", LogLevel.VERBOSE, null);
	Log.v("tag4", "message4", exc);
	assertEquals(4, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(3), "tag4", "message4", LogLevel.VERBOSE, exc);
	Log.d("tag5", "message5");
	assertEquals(5, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(4), "tag5", "message5", LogLevel.DEBUG, null);
	Log.d("tag6", "message6", exc);
	assertEquals(6, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(5), "tag6", "message6", LogLevel.DEBUG, exc);
	Log.e("tag7", "message7");
	assertEquals(7, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(6), "tag7", "message7", LogLevel.ERROR, null);
	Log.e("tag8", "message8", exc);
	assertEquals(8, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(7), "tag8", "message8", LogLevel.ERROR, exc);
	Log.w("tag9", "message9");
	assertEquals(9, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(8), "tag9", "message9", LogLevel.WARN, null);
	Log.w("tag10", "message10", exc);
	assertEquals(10, mockLogger.numberLogEntries());
	assertLogEntryEquals(mockLogger.getEntry(9), "tag10", "message10", LogLevel.WARN, exc);
    }

    private void assertLogEntryEquals(LogFileEntry logEntry, String tag, String message, LogLevel level, Throwable exc) {
	assertEquals(tag, logEntry.getTag());
	assertEquals(message, logEntry.getMessage());
	assertEquals(level, logEntry.getLevel());
	assertEquals(exc, logEntry.getThrowable());
    }
}
