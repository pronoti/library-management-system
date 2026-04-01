package com.library;

import com.library.enums.Genre;
import com.library.exception.LibraryOperationException;
import com.library.persistence.LibDataStore;
import com.library.service.LibraryService;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Console workflow and grouped submenu navigation for the library system.
 * @author A00325358 Pronoti Saha
 */
public final class LibraryApp {

    public static final String BACK_TO_MAIN_MENU = "0. Back to Main Menu";
    private final LibraryService service;
    private final Scanner scanner;

    private LibraryApp(LibraryService libraryService, Scanner scanner) {
        this.service = libraryService;
        this.scanner = scanner;
    }

    public static LibraryApp getInstance(Scanner scanner){
        LibDataStore libDataStore = new LibDataStore(Path.of("data"));
        Library library = libDataStore.loadExistingOnStartup();
        LibraryService libraryService = new LibraryService(library, libDataStore);
        return new LibraryApp(libraryService, scanner);
    }

    public void run() {
        boolean running = true;
        while (running) {
            printSection("Main Menu");
            IO.println("1. Catalogue");
            IO.println("2. Operations");
            IO.println("3. Administration");
            IO.println("4. Reports");
            IO.println("0. Exit");

            switch (readMenuChoice(0, 4)) {
                case 1 -> showCatalogueMenu();
                case 2 -> showOperationsMenu();
                case 3 -> showAdministrationMenu();
                case 4 -> showReportsMenu();
                case 0 -> {
                    running = false;
                    IO.println("Exiting... your data is already saved in " + service.showStorageSummary());
                }
                default -> throw new IllegalStateException("Unexpected menu state.");
            }
        }
    }

    private void showCatalogueMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printSection("Catalogue");
            IO.println("1. View all items");
            IO.println("2. Search books by title");
            IO.println("3. Search books by author");
            IO.println("4. Browse catalogue");
            IO.println(BACK_TO_MAIN_MENU);

            switch (readMenuChoice(0, 4)) {
                case 1 -> runAction(service::showAllItems);
                case 2 -> runAction(() -> service.searchByTitle(promptRequired("Title keyword")));
                case 3 -> runAction(() -> service.searchByAuthor(promptRequired("Author keyword")));
                case 4 -> runAction(service::browseCatalogue);
                case 0 -> inMenu = false;
                default -> throw new IllegalStateException("Unexpected submenu state.");
            }
        }
    }

    private void showOperationsMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printSection("Operations");
            IO.println("1. Borrow a book");
            IO.println("2. Return a book");
            IO.println("3. View active borrow records");
            IO.println(BACK_TO_MAIN_MENU);

            switch (readMenuChoice(0, 3)) {
                case 1 -> runAction(() -> service.borrowBook(readPositiveInt(), promptRequired("Borrower name")));
                case 2 -> runAction(() -> service.returnBook(readPositiveInt()));
                case 3 -> runAction(service::viewBorrowRecords);
                case 0 -> inMenu = false;
                default -> throw new IllegalStateException("Unexpected submenu state.");
            }
        }
    }

    private void showReportsMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printSection("Reports");
            IO.println("1. Statistics dashboard");
            IO.println("2. Global opening hours");
            IO.println(BACK_TO_MAIN_MENU);

            switch (readMenuChoice(0, 2)) {
                case 1 -> runAction(service::showStatisticsDashboard);
                case 2 -> runAction(service::showOpeningHours);
                case 0 -> inMenu = false;
                default -> throw new IllegalStateException("Unexpected submenu state.");
            }
        }
    }

    private void showAdministrationMenu() {
        boolean inMenu = true;
        while (inMenu) {
            printSection("Administration");
            IO.println("1. Add a new book");
            IO.println("2. Add a new magazine");
            IO.println("3. Data store location");
            IO.println(BACK_TO_MAIN_MENU);

            switch (readMenuChoice(0, 3)) {
                case 1 -> runAction(this::addBookFlow);
                case 2 -> runAction(this::addMagazineFlow);
                case 3 -> runAction(() -> IO.println("Persistent data folder: " + service.showStorageSummary()));
                case 0 -> inMenu = false;
                default -> throw new IllegalStateException("Unexpected submenu state.");
            }
        }
    }

    private void addBookFlow() {
        String title = promptRequired("Book title");
        String author = promptRequired("Author");
        Genre genre = selectGenre();
        int generatedId = service.addBook(title, author, genre);
        IO.println("Generated book id: " + generatedId);
    }

    private void addMagazineFlow() {
        String title = promptRequired("Magazine title");
        Map<String, String> metadata = new LinkedHashMap<>();
        String publisher = promptOptional("Publisher");
        if (!publisher.isBlank()) {
            metadata.put("Publisher", publisher);
        }
        String issue = promptOptional("Issue / Edition");
        if (!issue.isBlank()) {
            metadata.put("Issue", issue);
        }
        service.addMagazine(title, metadata);
    }

    private Genre selectGenre() {
        printSection("Choose Genre");
        int option = 1;
        for (Genre genre : service.genres()) {
            IO.println(option + ". " + genre);
            option++;
        }
        int selected = readMenuChoice(1, service.genres().size());
        return service.genres().get(selected - 1);
    }

    private void runAction(Runnable action) {
        try {
            action.run();
        } catch (LibraryOperationException e) {
            IO.println("Problem: " + e.getMessage());
        } catch (RuntimeException e) {
            IO.println("Unexpected error: " + e.getMessage());
        }
        pause();
    }

    private int readMenuChoice(int min, int max) {
        while (true) {
            IO.print("Choose an option: ");
            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }
            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
            } catch (NumberFormatException _) {
                // Ignored
            }
            IO.println("Please enter a number between " + min + " and " + max + ".");
        }
    }

    private int readPositiveInt() {
        while (true) {
            IO.print("Book id: ");
            String input = scanner.nextLine().trim();
            try {
                int value = Integer.parseInt(input);
                if (value > 0) {
                    return value;
                }
            } catch (NumberFormatException _) {
                // Ignored
            }
            IO.println("Please enter a positive whole number.");
        }
    }

    private String promptRequired(String label) {
        while (true) {
            IO.print(label + ": ");
            String input = scanner.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }
            IO.println(label + " cannot be empty.");
        }
    }

    private String promptOptional(String label) {
        IO.print(label + " (optional): ");
        return scanner.nextLine().trim();
    }

    private void pause() {
        IO.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void printSection(String title) {
        IO.println("\n========================================");
        IO.println(title);
        IO.println("========================================");
    }
}
