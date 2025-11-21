package com.library.items;

/**
 * Borrowable interface
 * @author A00325358 Pronoti Saha
 */
public interface Borrowable {

    /**
     * Borrow
     * @param studentName Name of the student
     */
    void borrow(String studentName);

    /**
     * Return Book
     */
    void returnBook();

    /**
     * Default method
     */
    default void greet() {
        System.out.println("Welcome to the Library!");
    }

    /**
     * Static methods
     */
    static void libraryInfo() {
        System.out.println("Library System 2025");
    }
}
