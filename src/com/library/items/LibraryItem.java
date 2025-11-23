package com.library.items;

/**
 * LibraryItem is a sealed interface
 * Permits Book and Magazine
 * @author A00325358 Pronoti Saha
 */
public sealed interface LibraryItem permits Book, Magazine {
    /**
     * Library item info
     */
    void info();
}

