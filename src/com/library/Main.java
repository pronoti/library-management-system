import com.library.Library;
import com.library.exception.LibraryOperationException;
import com.library.items.Book;
import com.library.items.Borrowable;
import com.library.items.Magazine;

import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;

/**
 * Compact source main method (JEP 512)
 * @author A00325358 Pronoti Saha
 * @param args Arguments
 */
void main(String[] args) {
    try (Scanner sc = new Scanner(System.in)) {
        IO.println("Starting Library System");
        var library = new Library();
        loadLibraryItems(library);

        // static interface method
        Borrowable.libraryInfo();

        int choice;
        do {
            try {
                showMenu();
                choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1 -> {
                        library.showAllItems();
                        waitForEnter(sc);
                    }
                    case 2 -> {
                        IO.print("Enter title to search: ");
                        String title = sc.nextLine();
                        library.searchByTitle(title);
                        waitForEnter(sc);
                    }
                    case 3 -> {
                        IO.print("Enter author to search: ");
                        String author = sc.nextLine();
                        library.searchByAuthor(author);
                        waitForEnter(sc);
                    }
                    case 4 -> {
                        IO.print("Enter book ID to borrow: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        IO.print("Enter your name: ");
                        String name = sc.nextLine();
                        library.borrowBook(id, name);
                        waitForEnter(sc);
                    }
                    case 5 -> {
                        IO.print("Enter book ID to return: ");
                        int id = sc.nextInt();
                        sc.nextLine();
                        library.returnBook(id);
                        waitForEnter(sc);
                    }
                    case 6 -> {
                        library.showBooksStatistics();
                        waitForEnter(sc);
                    }
                    case 7 -> IO.println("Exiting...");
                    default -> {
                        IO.println("Invalid choice!");
                        waitForEnter(sc);
                    }
                }
            }
            catch (InputMismatchException e) {
                IO.println("#### Exception: "+new LibraryOperationException("Invalid input."));
                choice = 0;
                sc.nextLine();
            }
        } while (choice != 7);
    }
}

/**
 * Load library with books and magazines
 * @param library The Library System
 */
private static void loadLibraryItems(Library library) {
    // Adding sample books
    library.addBook(new Book(1, "Java Programming", "James Gosling"));
    library.addBook(new Book(2, "Data Structures", "Robert Lafore"));
    library.addBook(new Book(3, "Design Patterns", "Erich Gamma"));

    Map<String, String> metaData = new HashMap<>();
    metaData.put("Publisher", "National Geographic Partners");
    library.addMagazine(new Magazine("National Geographic", metaData));
    library.addMagazine(new Magazine("Nature"));
}

/**
 * Program Menu
 */
private static void showMenu() {
    IO.println("---- LIBRARY MENU ----");
    IO.println("1. Show All Items");
    IO.println("2. Search Books by Title");
    IO.println("3. Search Books by Author");
    IO.println("4. Borrow Book");
    IO.println("5. Return Book");
    IO.println("6. Show Books Statistics");
    IO.println("7. Exit");
    IO.print("Enter choice: ");
}

/**
 * Wait for Enter to display Menu
 * @param sc Scanner
 */
private static void waitForEnter(Scanner sc) {
    IO.println("\nPress Enter to return to the main menu...");
    sc.nextLine();
}


