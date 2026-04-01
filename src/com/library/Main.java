import com.library.LibraryApp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Compact source main method (JEP 512)
 * @author A00325358 Pronoti Saha
 */
void main() {
    IO.println("Library Management System 2026");
    IO.println("Staring: " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));
    try (Scanner scanner = new Scanner(System.in)) {
        LibraryApp.getInstance(scanner).run();
    } catch (Exception e) {
        IO.println("Application failed start: " + e.getMessage());
    }
}

