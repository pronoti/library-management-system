package com.library.exception;

/**
 * Unchecked Library Operation Exception
 * @author A00325358 Pronoti Saha
 */
public class LibraryOperationException extends RuntimeException {

    /**
     * Constructor
     * @param message Error Message
     */
    public LibraryOperationException(String message) {
        super(message);
    }
}
