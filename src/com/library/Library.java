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

    public void addBook(Book book) {
        books.add(book);
    }

    public void addMagazine(Magazine magazine) {
        magazines.add(magazine);
    }

    public void addRecord(LibraryRecord libraryRecord) {
        records.add(libraryRecord);
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Magazine> getMagazines() {
        return magazines;
    }

    public List<LibraryRecord> getRecords() {
        return records;
    }

    public List<LibraryItem> getAllItems() {
        return Stream.concat(books.stream(), magazines.stream()).toList();
    }

    public Optional<Book> findBookById(int id) {
        return books.stream().filter(book -> book.getId() == id).findFirst();
    }

}
