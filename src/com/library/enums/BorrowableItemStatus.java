package com.library.enums;

/**
 * Status of a Borrowable Item
 * @author A00325358 Pronoti Saha
 */
public enum BorrowableItemStatus {
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
    BorrowableItemStatus(String value) {
        this.value = value;
    }
}

