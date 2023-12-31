package net.ibbaa.phonelog;

import java.util.ArrayList;
import java.util.List;

public class MockLogger implements ILogger {

    private final List<LogFileEntry> logEntries;

    public MockLogger() {
	logEntries = new ArrayList<>();
    }

    @Override
    public void log(String tag, String message, Throwable throwable, LogLevel level) {
	LogFileEntry logEntry = new LogFileEntry(1, "thread", level, tag, message, throwable);
	logEntries.add(logEntry);
    }

    public void reset() {
	logEntries.clear();
    }

    public boolean wasLogCalled() {
	return numberLogEntries() > 0;
    }

    public int numberLogEntries() {
	return logEntries.size();
    }

    public LogFileEntry getEntry(int index) {
	return logEntries.get(index);
    }
}
