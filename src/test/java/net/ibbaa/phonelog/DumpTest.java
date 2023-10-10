package net.ibbaa.phonelog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.ArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DumpTest {

    private MockDump mockDump;

    @BeforeEach
    public void beforeEachTestMethod() {
	mockDump = new MockDump();
	Dump.initialize(mockDump);
    }

    @AfterEach
    public void afterEachTestMethod() {
	Dump.initialize(null);
    }

    @Test
    public void testDump() {
	IDumpSource dumpSource = ArrayList::new;
	Dump.dump(dumpSource);
	assertEquals(1, mockDump.numberDumpCalls());
	MockDump.DumpCall dumpCall = mockDump.getDumpCall(0);
	assertNull(dumpCall.getTag());
	assertNull(dumpCall.getMessage());
	assertNull(dumpCall.getBaseFileName());
	assertSame(dumpSource, dumpCall.getDumpSource());
	Dump.dump("tag", "message", dumpSource);
	assertEquals(2, mockDump.numberDumpCalls());
	dumpCall = mockDump.getDumpCall(1);
	assertEquals("tag", dumpCall.getTag());
	assertEquals("message", dumpCall.getMessage());
	assertNull(dumpCall.getBaseFileName());
	assertSame(dumpSource, dumpCall.getDumpSource());
	Dump.dump("tag", "message", "file", dumpSource);
	assertEquals(3, mockDump.numberDumpCalls());
	dumpCall = mockDump.getDumpCall(2);
	assertEquals("tag", dumpCall.getTag());
	assertEquals("message", dumpCall.getMessage());
	assertEquals("file", dumpCall.getBaseFileName());
	assertSame(dumpSource, dumpCall.getDumpSource());
    }
}
