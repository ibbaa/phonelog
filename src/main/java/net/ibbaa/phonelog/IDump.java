package net.ibbaa.phonelog;

/**
 * Interface for dump implementations.
 */
public interface IDump {

    /**
     * Dump method
     * 
     * @param tag          the tag
     * @param message      the message
     * @param baseFileName the file name to write dump to
     * @param source       the dump source implementation
     */
    void dump(String tag, String message, String baseFileName, IDumpSource source);
}
