package com.library;

import com.library.items.Book;
import com.library.items.LibraryItem;
import com.library.items.Magazine;
import com.library.records.LibraryRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Aggregate root for the library domain.
 * Holds the catalogue and active borrow records in memory.
 *
 * @author A00325358 Pronoti Saha
 */
public final class Library {

    private final List<Book> books = new ArrayList<>();
    private final List<Magazine> magazines = new ArrayList<>();
    private final List<LibraryRecord> records = new ArrayList<>();

    /**
     * Adds a book to the catalogue.
     * @param book the book to add
     */
    public void addBook(Book book) {
        books.add(book);
    }

    /**
     * Adds a magazine to the catalogue.
     * @param magazine the magazine to add
     */
    public void addMagazine(Magazine magazine) {
        magazines.add(magazine);
    }

    /**
     * Adds an active borrow record.
     * @param libraryRecord the record to add
     */
    public void addRecord(LibraryRecord libraryRecord) {
        records.add(libraryRecord);
    }

    /**
     * Returns all books in the catalogue.
     * @return mutable list of books
     */
    public List<Book> getBooks() {
        return books;
    }

    /**
     * Returns all magazines in the catalogue.
     * @return mutable list of magazines
     */
    public List<Magazine> getMagazines() {
        return magazines;
    }

    /**
     * Returns all active borrow records.
     * @return mutable list of records
     */
    public List<LibraryRecord> getRecords() {
        return records;
    }

    /**
     * Returns a combined view of all books and magazines.
     * @return unmodifiable list of all library items
     */
    public List<LibraryItem> getAllItems() {
        return Stream.concat(books.stream(), magazines.stream()).toList();
    }

    /**
     * Finds a book by its unique id.
     * @param id the book id to look up
     * @return an Optional containing the book, or empty if not found
     */
    public Optional<Book> findBookById(int id) {
        return books.stream().filter(book -> book.getId() == id).findFirst();
    }

}
