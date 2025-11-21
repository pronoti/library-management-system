package com.library.enums;

/**
 * Status of a Book
 * @author A00325358 Pronoti Saha
 */
public enum BookStatus {
    /**
     * Status - Available
     */
    AVAILABLE("Available"),
    /**
     * Status - Borrowed
     */
    BORROWED("Borrowed"),
    /**
     * Status - Reserved
     */
    RESERVED("Reserved");

    /**
     * Status value
     */
    public final String value;

    /**
     * @param value Status
     */
    private BookStatus(String value) {
        this.value = value;
    }
}