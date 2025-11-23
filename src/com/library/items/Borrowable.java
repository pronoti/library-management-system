package com.library.items;

/**
 * Borrowable interface
 * @author A00325358 Pronoti Saha
 */
public interface Borrowable {

    /**
     * Borrow item
     * @param studentName Name of the student
     */
    void borrowItem(String studentName);

    /**
     * Return item
     */
    void returnItem();

    /**
     * Default method
     */
    default void greet() {
        System.out.println("Welcome to the Library!");
        getName();
    }

    /**
     * Static methods
     */
    static void libraryInfo() {
        System.out.println("Library System 2025");
    }

    private void getName(){
        System.out.println("Borrowable");
    }
}


