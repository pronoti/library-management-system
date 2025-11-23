package com.library.exception;

/**
 * Unchecked Library Operation Exception
 * @author A00325358 Pronoti Saha
 */
public sealed class LibraryOperationException extends RuntimeException permits LibraryItemNotFoundException {

    /**
     * Constructor
     * @param message Error Message
     */
    public LibraryOperationException(String message) {
        super(message);
    }
}
