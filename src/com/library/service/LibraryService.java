package com.library.service;

import com.library.Library;
import com.library.enums.Genre;
import com.library.exception.LibraryItemNotFoundException;
import com.library.exception.LibraryOperationException;
import com.library.items.Book;
import com.library.items.LibraryItem;
import com.library.items.Magazine;
import com.library.persistence.LibDataStore;
import com.library.records.LibraryRecord;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;

/**
 * Library service coordinating the domain model, persistence, and console reports.
 * @author A00325358 Pronoti Saha
 */
public final class LibraryService {

    private final Library library;
    private final LibDataStore storage;

    /**
     * Constructs a LibraryService with the given library and storage.
     * @param library the in-memory library domain object
     * @param storage the file-based data store
     */
    public LibraryService(Library library, LibDataStore storage) {
        this.library = library;
        this.storage = storage;
    }

    /**
     * Displays all catalogue items sorted by title, with a sliding-window gatherer footer.
     */
    public void showAllItems() {
        IO.println("\n========== COMPLETE CATALOGUE ==========");
        System.out.printf("%-3s | %-25s | %-20s | %-12s | %s %n", "Id", "Title", "Author", "Genre", "Availability");

        List<LibraryItem> sortedItems = library.getAllItems()
                                            .stream()
                                            .sorted(Comparator.comparing(LibraryItem::getTitle))
                                            .toList();

        sortedItems.stream()
                .map(item ->
                    switch (item) {
                        case Book book -> String.format("%-3s | %-25s | %-20s | %-12s | %s",
                                book.getId(),
                                book.getTitle(),
                                book.getAuthor(),
                                book.getGenre(),
                                book.isBorrowed() ? "Borrowed on: " + book.getBorrowedDate() + ", Due: " + book.getDueDate() : "Available");
                        case Magazine magazine -> String.format("%-3s | %-25s | %-20s | %-12s | %s",
                                "-",
                                magazine.getTitle(),
                                "Magazine",
                                "PERIODICAL",
                                "Reference only");
                    })
                .forEach(IO::println);

        List<String> adjacentTitles = library.getBooks().stream()
                .sorted(Comparator.comparing(Book::getTitle).thenComparing(Book::getAuthor))
                .map(Book::getTitle)
                .gather(Gatherers.windowSliding(2))
                .map(window -> String.join(" -> ", window))
                .toList();

        IO.println("\n[Gatherers.windowSliding] Adjacent book titles");
        if (adjacentTitles.isEmpty()) {
            IO.println("  Not enough books for adjacent title pairs.");
        } else {
            adjacentTitles.forEach(pair -> IO.println("  " + pair));
        }

        IO.println("========================================");
    }

    /**
     * Searches books by title keyword.
     * @param title the search keyword
     */
    public void searchByTitle(String title) {
        String query = validateQuery(title);
        Predicate<Book> predicate = book -> book.getTitle().toLowerCase().contains(query.toLowerCase());
        search(query, predicate);
    }

    /**
     * Searches books by author keyword.
     * @param author the search keyword
     */
    public void searchByAuthor(String author) {
        String query = validateQuery(author);
        Predicate<Book> predicate = book -> book.getAuthor().toLowerCase().contains(query.toLowerCase());
        search(query, predicate);
    }

    /**
     * Borrows a book for the given borrower, using a Scoped Value for the session context.
     * @param id the id of the book to borrow
     * @param borrowerName the name of the borrower
     */
    public void borrowBook(int id, String borrowerName) {
        String safeBorrower = borrowerName == null || borrowerName.isBlank() ? "Guest Reader" : borrowerName.trim();
        ScopedValue<String> currentBorrower = ScopedValue.newInstance();
        ScopedValue.where(currentBorrower, safeBorrower).run(() -> library.findBookById(id)
                .ifPresentOrElse(book -> {
                    IO.println("[Scoped Values] Borrow session for " + currentBorrower.get());
                    if (book.borrowItem(currentBorrower.get())) {
                        Supplier<LibraryRecord> createRecord = () -> new LibraryRecord(
                                book.getId(),
                                book.getTitle(),
                                book.getAuthor(),
                                currentBorrower.get(),
                                LocalDate.now(),
                                LocalDate.now().plusDays(7)
                        );
                        library.getRecords().removeIf(libraryRecord -> libraryRecord.id() == id);
                        library.addRecord(createRecord.get());
                        persist();
                    }
                }, () -> {
                    throw new LibraryItemNotFoundException("Book id " + id + " was not found.");
                }));
    }

    /**
     * Returns a borrowed book and removes its active borrow record.
     * @param id the id of the book to return
     */
    public void returnBook(int id) {
        Consumer<Book> completeReturn = book -> {
            if (book.returnItem()) {
                library.getRecords().removeIf(libraryRecord -> libraryRecord.id() == id);
                persist();
            }
        };

        library.findBookById(id)
                .ifPresentOrElse(completeReturn,
                        () -> {
                            throw new LibraryItemNotFoundException("Book id " + id + " was not found.");
                        });
    }

    /**
     * Returns the next available auto-generated book id.
     * @return next book id
     */
    public int nextBookId() {
        return library.getBooks().stream()
                .mapToInt(Book::getId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Adds a new book to the catalogue and persists it.
     * @param title book title
     * @param author book author
     * @param genre book genre
     * @return the auto-generated id assigned to the new book
     */
    public int addBook(String title, String author, Genre genre) {
        int id = nextBookId();
        library.addBook(new Book(id, title, author, genre));
        persist();
        IO.println("Book added with auto-generated id " + id + " and saved to file storage.");
        return id;
    }

    /**
     * Adds a new magazine to the catalogue and persists it.
     * @param title magazine title
     * @param metadata key-value metadata map (e.g. Publisher, Issue)
     */
    public void addMagazine(String title, Map<String, String> metadata) {
        library.addMagazine(new Magazine(title, metadata));
        persist();
        IO.println("Magazine added and saved to file storage.");
    }

    /**
     * Displays the catalogue grouped by genre and partitioned by availability.
     */
    public void browseCatalogue() {
        IO.println("\n========== CATALOGUE INSIGHTS ==========");

        IO.println("[Collectors.toMap] ID -> Title");
        library.getBooks().stream()
                .collect(Collectors.toMap(Book::getId, Book::getTitle))
                .forEach((id, itemTitle) -> IO.println("  " + id + " -> " + itemTitle));

        IO.println("\n[Collectors.groupingBy] Books by genre");
        library.getBooks().stream()
                .collect(Collectors.groupingBy(Book::getGenre))
                .forEach((genre, books) -> IO.println("  " + genre + ": " + books.stream().map(Book::getTitle).toList()));

        IO.println("\n[Collectors.partitioningBy] Borrowed vs available");
        Map<Boolean, List<Book>> partitioned = library.getBooks().stream()
                .collect(Collectors.partitioningBy(Book::isBorrowed));
        IO.println("  On loan : " + partitioned.get(true).stream().map(Book::getTitle).toList());
        IO.println("  On shelf: " + partitioned.get(false).stream().map(Book::getTitle).toList());
        IO.println("=========================================");
    }

    /**
     * Displays the statistics dashboard including stream terminal ops,
     * localisation, and a parallel catalogue audit.
     */
    public void showStatisticsDashboard() {
        IO.println("\n========== LIBRARY DASHBOARD ==========");

        long count = library.getBooks().size();
        long borrowed = library.getBooks().stream().filter(Book::isBorrowed).count();
        IO.println("Total books      : " + count);
        IO.println("Borrowed books   : " + borrowed);
        IO.println("Available books  : " + (count - borrowed));
        IO.println("Magazine count   : " + library.getMagazines().size());

        IO.println("\n[Stream terminal operations]");
        library.getBooks().stream()
                .min(Comparator.comparing(Book::getTitle))
                .ifPresent(book -> IO.println("min(title)       : " + book.getTitle()));
        library.getBooks().stream()
                .max(Comparator.comparing(Book::getTitle))
                .ifPresent(book -> IO.println("max(title)       : " + book.getTitle()));
        IO.println("findFirst        : " + library.getBooks().stream()
                .findFirst()
                .map(Book::getTitle)
                .orElse("n/a"));
        IO.println("findAny          : " + library.getBooks().stream()
                .findAny()
                .map(Book::getTitle)
                .orElse("n/a"));
        IO.println("allMatch(id > 0) : " + library.getBooks().stream().allMatch(book -> book.getId() > 0));
        IO.println("anyMatch(borrow) : " + library.getBooks().stream().anyMatch(Book::isBorrowed));
        IO.println("noneMatch(blank) : " + library.getBooks().stream().noneMatch(book -> book.getTitle().isBlank()));

        showLocalisedDates();
        runParallelAudit();
        IO.println("=======================================");
    }

    /**
     * Displays all active borrow records with a Gatherers fold summary and NIO2 file view.
     */
    public void viewBorrowRecords() {
        IO.println("\n========== ACTIVE BORROW RECORDS ==========");

        if (library.getRecords().isEmpty()) {
            IO.println("No books are currently on loan.");
        } else {
            library.getRecords().forEach(libraryRecord ->
                    IO.println(libraryRecord.id() + " | " + libraryRecord.title() + " | " + libraryRecord.borrower()
                            + " | Borrowed: " + libraryRecord.borrowDate() + " | Due: " + libraryRecord.dueDate()));
        }

        String summary = library.getRecords().stream()
                .map(LibraryRecord::title)
                .gather(Gatherers.fold(() -> "", (accumulator, title) ->
                        accumulator.isEmpty() ? title : accumulator + " | " + title))
                .filter(result -> !result.isBlank())
                .findFirst()
                .orElse("none");
        IO.println("[Gatherers.fold] Titles on loan: " + summary);

        IO.println("\n[NIO2] Raw file view from " + storage.getRecordsFile().toAbsolutePath());
        try {
            List<String> rawLines = storage.readRecordLines().stream()
                    .filter(line -> !line.isBlank())
                    .toList();
            if (rawLines.isEmpty()) {
                IO.println("  records.txt is currently empty.");
            } else {
                rawLines.forEach(line -> IO.println("  " + line));
            }
        } catch (IOException e) {
            throw new LibraryOperationException("Unable to read borrow records: " + e.getMessage());
        }

        IO.println("===========================================");
    }

    /**
     * Displays global opening hours with today's date formatted per locale.
     */
    public void showOpeningHours() {
        IO.println("\n========== GLOBAL OPENING HOURS ==========");
        LocalDate today = LocalDate.now();
        List.of(Locale.UK, Locale.US, Locale.FRANCE, Locale.GERMANY, Locale.JAPAN)
                .forEach(locale -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale);
                    System.out.printf("  %-15s %s | Open 10:00 - 17:00%n",
                            locale.getDisplayCountry(Locale.ENGLISH), today.format(formatter));
                });
        IO.println("==========================================");
    }

    /**
     * Returns the absolute path of the data storage directory.
     * @return storage directory path as a string
     */
    public String showStorageSummary() {
        return storage.getDataDirectory().toAbsolutePath().toString();
    }

    /**
     * Returns all available genres.
     * @return list of Genre values
     */
    public List<Genre> genres() {
        return List.of(Genre.values());
    }

    private String validateQuery(String query) {
        if (query == null || query.isBlank()) {
            throw new LibraryOperationException("Search text cannot be empty.");
        }
        return query.trim();
    }

    private void search(String content, Predicate<Book> filter) {
        Function<Book, String> format = book ->
                String.format("%-3d | %-25s | %-20s | %-12s | %s",
                        book.getId(), book.getTitle(), book.getAuthor(), book.getGenre(),
                        book.isBorrowed() ? "On loan until " + book.getDueDate() : "Available");

        List<String> results = library.getBooks().stream()
                .filter(filter)
                .map(format)
                .distinct()
                .limit(10)
                .sorted()
                .toList();

        if (results.isEmpty()) {
            IO.println(new LibraryItemNotFoundException("No book found for: " + content));
            return;
        }

        IO.println("\nSearch results:");
        results.forEach(IO::println);
    }

    private void showLocalisedDates() {
        IO.println("\n[Localisation] Today's date in library partner regions");
        List.of(Locale.UK, Locale.US, Locale.FRANCE, Locale.GERMANY, Locale.JAPAN)
                .forEach(locale -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale);
                    IO.println("  " + locale.getDisplayCountry(Locale.ENGLISH) + ": " + LocalDate.now().format(formatter));
                });
    }

    private void runParallelAudit() {
        IO.println("\n[Concurrency] ExecutorService catalogue audit");
        if (library.getBooks().isEmpty()) {
            IO.println("  No books available for audit.");
            return;
        }

        List<Callable<String>> tasks = library.getBooks().stream()
                .map(book -> (Callable<String>) () ->
                        "[" + Thread.currentThread().getName() + "] audited " + book.getTitle())
                .toList();

        try(ExecutorService executorService = Executors.newFixedThreadPool(Math.min(3, library.getBooks().size()))) {
            executorService.invokeAll(tasks)
                    .forEach(future -> {
                        try {
                            IO.println("  " + future.get());
                        } catch (InterruptedException _) {
                            Thread.currentThread().interrupt();
                            throw new LibraryOperationException("Parallel audit interrupted.");
                        } catch (ExecutionException e) {
                            throw new LibraryOperationException("Parallel audit failed: " + e.getMessage());
                        }
                    });
        } catch (InterruptedException _) {
            Thread.currentThread().interrupt();
            throw new LibraryOperationException("Parallel audit interrupted.");
        }
    }

    private void persist() {
        try {
            storage.save(library);
        } catch (IOException e) {
            throw new LibraryOperationException("Failed to save library data: " + e.getMessage());
        }
    }
}
