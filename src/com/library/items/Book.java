package com.library.items;

import com.library.enums.BorrowableItemStatus;
import com.library.enums.Genre;
import com.library.exception.LibraryException;
import com.library.exception.LibraryOperationException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Book is a borrowable library item
 * @author A00325358 Pronoti Saha
 */
public final class Book implements LibraryItem, Borrowable {

    private final int id;
    private final String title;
    private final String author;
    private final Genre genre;
    private boolean borrowed;
    private LocalDate dueDate;
    private LocalDate borrowDate;

    /**
     * Flexible constructor body Java 25 (JEP 513)
     * @param id Book Id
     * @param title Book Title
     * @param author Book Author
     * @param genre Book Genre
     */
    public Book(int id, String title, String author, Genre genre) {
        if (id <= 0) throw new LibraryOperationException("ID must be positive");
        if (title == null || title.isBlank()) throw new LibraryOperationException("Title cannot be empty");
        if (author == null || author.isBlank()) throw new LibraryOperationException("Author cannot be empty");

        this.id = id;
        this.title = title.trim();
        this.author = author.trim();
        this.genre = genre != null ? genre : Genre.EDUCATION;
        this.borrowed = false;
        this.dueDate = null;
        this.borrowDate = null;
    }

    @Override
    public void info() {
        System.out.printf("%-3s | %-25s | %-20s | %-12s | %s %n",
                id,
                title,
                author,
                genre,
                borrowed ? BorrowableItemStatus.BORROWED.value + "Borrowed on: " + borrowDate + ", Due: " + dueDate : BorrowableItemStatus.AVAILABLE.value);
    }

    @Override
    public boolean borrowItem(String borrowerName) {
        if (borrowed) {
            System.out.println(new LibraryException("Book id: " + id + " is already borrowed."));
            return false;
        }

        borrowed = true;
        borrowDate = LocalDate.now();
        dueDate = borrowDate.plusDays(7);
        System.out.println("\n*** RECEIPT ***");
        System.out.println("Borrower: " + borrowerName);
        System.out.println("Book: " + title);
        System.out.println("Borrow Date: " + LocalDate.now());
        System.out.println("Due Date: " + dueDate);
        System.out.println("****************");
        return true;
    }

    @Override
    public boolean returnItem() {
        if (!borrowed) {
            System.out.println("Book was not borrowed.");
            return false;
        }

        LocalDate today = LocalDate.now();
        long lateDays = today.isAfter(dueDate) ? ChronoUnit.DAYS.between(dueDate, today) : 0;

        borrowed = false;
        IO.println("\n*** RETURN RECEIPT ***");
        IO.println("Book Returned: " + title);
        IO.println("Due Date: " + dueDate);
        IO.println("Returned On: " + today);
        if (lateDays > 0) {
            IO.println("Late by: " + lateDays + " days");
            IO.println("Fine: EUR " + lateDays);
        } else {
            IO.println("Returned on time. No fine.");
        }
        IO.println("***********************\n");
        dueDate = null;
        return true;
    }

    public void restoreBorrow(LocalDate restoredBorrowDate, LocalDate restoredDueDate) {
        borrowed = true;
        borrowDate = restoredBorrowDate;
        dueDate = restoredDueDate;
    }

    public boolean isBorrowed() {
        return borrowed;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getId() {
        return id;
    }

    public Genre getGenre() {
        return genre;
    }

    public LocalDate getDueDate() { return dueDate; }

    public LocalDate getBorrowedDate() { return borrowDate; }
}
