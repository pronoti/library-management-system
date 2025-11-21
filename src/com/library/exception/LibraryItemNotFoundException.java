package com.library.exception;

/**
 * Item Not Found Exception
 * @author A00325358 Pronoti Saha
 */
public class LibraryItemNotFoundException extends LibraryOperationException{
    /**
     * Constructor
     * @param message Error Message
     */
    public LibraryItemNotFoundException(String message) {
        super(message);
    }
}
