package net.ibbaa.phonelog;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Dump custom data.
 */
public class Dump {

    private static final ReentrantReadWriteLock dumpLock = new ReentrantReadWriteLock();

    private static IDump dump;

    /**
     * Sets the {@link net.ibbaa.phonelog.IDump} implementation
     * 
     * @param dump the {@link net.ibbaa.phonelog.IDump} implementation
     */
    public static void initialize(IDump dump) {
	dumpLock.writeLock().lock();
	Dump.dump = dump;
	dumpLock.writeLock().unlock();
    }

    /**
     * Returns the {@link net.ibbaa.phonelog.IDump} implementation
     * 
     * @return the {@link net.ibbaa.phonelog.IDump} implementation
     */
    public static IDump getDump() {
	dumpLock.readLock().lock();
	try {
	    return dump;
	} finally {
	    dumpLock.readLock().unlock();
	}
    }

    /**
     * Dump method
     * 
     * @param tag          the tag
     * @param message      the message
     * @param baseFileName the file name to write dump to
     * @param source       the dump source implementation
     */
    public static void dump(String tag, String message, String baseFileName, IDumpSource source) {
	IDump dump = getDump();
	if (dump != null) {
	    dump.dump(tag, message, baseFileName, source);
	}
    }

    /**
     * Dump method
     * 
     * @param tag     the tag
     * @param message the message
     * @param source  the dump source implementation
     */
    public static void dump(String tag, String message, IDumpSource source) {
	dump(tag, message, null, source);
    }

    /**
     * Dump method
     * 
     * @param source the dump source implementation
     */
    public static void dump(IDumpSource source) {
	dump(null, null, null, source);
    }
}
