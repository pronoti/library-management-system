package com.library.records;

import java.time.LocalDate;

/**
 * Library Record is used to maintain records of borrowed library items
 * 
 * @author A00325358 Pronoti Saha
 * @param id Id
 * @param title Title
 * @param author Author
 * @param borrowDate Borrow Date
 * @param dueDate Due Date
 */
public record LibraryRecord(int id, String title, String author, LocalDate borrowDate, LocalDate dueDate) {
}

