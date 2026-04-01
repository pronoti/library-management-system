package com.library.items;

/**
 * Borrowable sealed interface - permits only Book
 * @author A00325358 Pronoti Saha
 */
public sealed interface Borrowable permits Book {

    /**
     * Borrow item
     * @param studentName Name of the student
     * @return true when the item was borrowed successfully
     */
    boolean borrowItem(String studentName);

    /**
     * Return item
     * @return true when the item was returned successfully
     */
    boolean returnItem();

}
