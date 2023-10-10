package net.ibbaa.phonelog;

import java.util.List;

/**
 * Interface for supplying objects to dump.
 */
@FunctionalInterface
public interface IDumpSource {
    /**
     * Returns the objects to dump
     * 
     * @return the list of objects to dump
     */
    List<?> objectsToDump();
}
