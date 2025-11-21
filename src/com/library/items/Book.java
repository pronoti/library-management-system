package com.library.items;

import com.library.enums.BookStatus;
import com.library.exception.LibraryException;

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
    private boolean isBorrowed;
    private LocalDate dueDate;

    /**
     * Flexible constructor body Java 25 (JEP 513)
     * @param id Book Id
     * @param title Book Title
     * @param author Book Author
     */
    public Book(int id, String title, String author) {
        // validate before initializing fields
        if (id <= 0) throw new IllegalArgumentException("ID must be positive");
        if (title == null || title.isEmpty()) throw new IllegalArgumentException("Title cannot be empty");
        if (author == null || author.isEmpty()) throw new IllegalArgumentException("Author cannot be empty");

        this.id = id;
        this.title = title;
        this.author = author;
        this.isBorrowed = false;
    }

    /**
     * Book info
     */
    @Override
    public void info() {
        System.out.printf("%-3s | %-20s | %-20s | %s \n", id, title ,author , (isBorrowed ? BookStatus.BORROWED.value + ", Due: " + dueDate : BookStatus.AVAILABLE.value));
    }

    /**
     * Borrow a book for a Student
     * @param studentName Name of the Student
     */
    @Override
    public void borrow(String studentName) {
        if (!isBorrowed) {
            isBorrowed = true;
            dueDate = LocalDate.now().plusDays(7); // 7-day loan
            StringBuilder receipt = new StringBuilder();
            receipt.append("\n*** RECEIPT ***\n");
            receipt.append("Student: ").append(studentName).append("\n");
            receipt.append("Book: ").append(title).append("\n");
            receipt.append("Due Date: ").append(dueDate).append("\n");
            receipt.append("****************\n");
            System.out.println(receipt);
        } else {
            System.out.println(new LibraryException("Book id: "+this.id+" is already borrowed."));
        }
    }

    /**
     * Return a book
     */
    @Override
    public void returnBook() {
        if (!isBorrowed) {
            System.out.println("Book was not borrowed.");
            return;
        }
        LocalDate today = LocalDate.now();
        long lateDays = 0;
        if (today.isAfter(dueDate)) {
            lateDays = ChronoUnit.DAYS.between(dueDate, today);
        }
        isBorrowed = false;
        System.out.println("\n*** RETURN RECEIPT ***");
        System.out.println("Book Returned: " + title);
        System.out.println("Due Date: " + dueDate);
        System.out.println("Returned On: " + today);
        if (lateDays > 0) {
            System.out.println("Late by: " + lateDays + " days");
            System.out.println("Fine: €" + (lateDays * 1));
        } else {
            System.out.println("Returned on time. No fine.");
        }
        System.out.println("***********************\n");
    }

    /**
     * Is the book borrowed
     * @return isBorrowed
     */
    public boolean isBorrowed() {
        return isBorrowed;
    }

    /**
     * Get the title of the book
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the Author of the book
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Get the id of the book
     * @return id
     */
    public int getId() {
        return id;
    }
}