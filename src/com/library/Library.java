package com.library;

import com.library.exception.LibraryItemNotFoundException;
import com.library.items.Book;
import com.library.items.Magazine;
import com.library.records.LibraryRecord;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * library class
 * @author A00325358 Pronoti Saha
 */
public class Library{

    private final List<Book> books = new ArrayList<>();
    private final List<Magazine> magazines = new ArrayList<>();
    private final List<LibraryRecord> records = new ArrayList<>();

    /**
     * No argument constructor
     */
    public Library(){
        super();
    }

    /**
     * Add a book to the library inventory
     * @param book Book
     */
    public void addBook(Book book) {
        books.add(book);
    }

    /**
     * Add a magazine to the library inventory
     * @param magazine Magazine
     */
    public void addMagazine(Magazine magazine) {
        magazines.add(magazine);
    }

    /**
     * Show all items in the Library
     */
    public void showAllItems() {
        IO.println("*** Borrowable Items ***");
        System.out.printf("%-3s | %-20s | %-20s | %s \n", "Id", "Title" , "Author" , "Availability");
        books.forEach(Book::info);
        IO.println();
        IO.println("*** Non-Borrowable Items ***");
        magazines.forEach(Magazine::info);
    }

    /**
     * Search book by title using Predicate
     * @param title Title
     */
    public void searchByTitle(String title) {
        Predicate<Book> filter = b -> b.getTitle().toLowerCase().contains(title.toLowerCase());
        var matches = books.stream().filter(filter).toList();
        if (matches.isEmpty()) {
            IO.println(new LibraryItemNotFoundException("No book found with title: " + title));
        } else {
            // Java Lambda method reference
            matches.forEach(Book::info);
        }
    }

    /**
     * Search book by author
     * @param author Author
     */
    public void searchByAuthor(String author) {
        Predicate<Book> filter = b -> b.getAuthor().toLowerCase().contains(author.toLowerCase());
        // LVTI
        var matches = books.stream().filter(filter).toList();
        if (matches.isEmpty()) {
            IO.println(new LibraryItemNotFoundException("No book found having author: " + author));
        } else {
            matches.forEach(Book::info);
        }
    }

    /**
     * Borrow book by Id and Borrower Name
     * @param id Book Id
     * @param studentName Student Name
     */
    public void borrowBook(int id, String studentName) {
        // Java Stream
        books.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .ifPresentOrElse(b -> {
                    b.borrow(studentName);
                    records.add(new LibraryRecord(b.getId(), b.getTitle(), b.getAuthor(), LocalDate.now(), LocalDate.now().plusDays(7)));
                }, () -> System.out.println("Book not found!"));
    }

    /**
     * Return borrowed book by Id
     * @param id Book Id
     */
    public void returnBook(int id) {
        // Java Stream
        books.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .ifPresentOrElse(Book::returnBook, () -> System.out.println("Book not found!"));
    }

    /**
     * Show Statistics
     */
    public void showBooksStatistics() {
        long borrowed = books.stream().filter(Book::isBorrowed).count();
        long total = books.size();
        System.out.println("\n--- LIBRARY STATISTICS ---");
        System.out.println("Total Books: " + total);
        System.out.println("Borrowed Books: " + borrowed);
        System.out.println("Available Books: " + (total - borrowed));
        System.out.println("Available Magazines: " + magazines.size());
        System.out.println("--------------------------\n");
    }


}