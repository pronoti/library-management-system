package com.library.persistence;

import com.library.Library;
import com.library.enums.Genre;
import com.library.items.Book;
import com.library.items.Magazine;
import com.library.records.LibraryRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * File-based persistence using plain text files and NIO2.
 * @author A00325358 Pronoti Saha
 */
public final class LibDataStore {

    private final Path dataDirectory;
    private final Path booksFile;
    private final Path magazinesFile;
    private final Path recordsFile;

    /**
     * Constructs a LibDataStore rooted at the given directory.
     * @param dataDirectory path to the folder containing the data files
     */
    public LibDataStore(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
        this.booksFile = dataDirectory.resolve("books.txt");
        this.magazinesFile = dataDirectory.resolve("magazines.txt");
        this.recordsFile = dataDirectory.resolve("records.txt");
    }

    /**
     * Loads the library from file storage on application startup.
     * Creates and seeds files if they do not yet exist.
     * @return a fully populated Library instance
     */
    public Library loadExistingOnStartup() {
        try {
            initialiseStorage();
            seedIfEmpty();

            Library library = new Library();
            loadBooks(library);
            loadMagazines(library);
            loadRecords(library);
            IO.println("Loaded data from " + dataDirectory.toAbsolutePath());
            return library;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load storage: " + e.getMessage(), e);
        }
    }

    /**
     * Persists the full library state (books, magazines, records) to disk.
     * @param library the library instance to save
     * @throws IOException if any file write fails
     */
    public void save(Library library) throws IOException {
        initialiseStorage();
        saveBooks(library.getBooks());
        saveMagazines(library.getMagazines());
        saveRecords(library.getRecords());
    }

    /**
     * Reads all raw lines from the records file.
     * @return list of raw text lines from records.txt
     * @throws IOException if the file cannot be read
     */
    public List<String> readRecordLines() throws IOException {
        initialiseStorage();
        return Files.readAllLines(recordsFile, StandardCharsets.UTF_8);
    }

    /**
     * Returns the path to the data directory.
     * @return data directory path
     */
    public Path getDataDirectory() {
        return dataDirectory;
    }

    /**
     * Returns the path to the records file.
     * @return records.txt path
     */
    public Path getRecordsFile() {
        return recordsFile;
    }

    private void initialiseStorage() throws IOException {
        Files.createDirectories(dataDirectory);
        createFileIfMissing(booksFile);
        createFileIfMissing(magazinesFile);
        createFileIfMissing(recordsFile);
    }

    private void createFileIfMissing(Path file) throws IOException {
        if (Files.notExists(file)) {
            Files.write(file, List.of(), StandardCharsets.UTF_8, StandardOpenOption.CREATE_NEW);
        }
    }

    private void seedIfEmpty() throws IOException {
        boolean booksEmpty = Files.readAllLines(booksFile, StandardCharsets.UTF_8).isEmpty();
        boolean magazinesEmpty = Files.readAllLines(magazinesFile, StandardCharsets.UTF_8).isEmpty();
        if (!booksEmpty || !magazinesEmpty) {
            return;
        }

        Files.write(booksFile, List.of(
                "1\tJava Programming\tJames Gosling\tTECHNOLOGY",
                "2\tData Structures\tRobert Lafore\tEDUCATION",
                "3\tDesign Patterns\tErich Gamma\tTECHNOLOGY",
                "4\tA Brief History of Time\tStephen Hawking\tSCIENCE",
                "5\tClean Code\tRobert Martin\tEDUCATION"
        ), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);

        Files.write(magazinesFile, List.of(
                "National Geographic\tPublisher=National Geographic Partners\tMonthly",
                "Nature\tPublisher=Springer Nature\tWeekly"
        ), StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void loadBooks(Library library) throws IOException {
        for (String line : Files.readAllLines(booksFile, StandardCharsets.UTF_8)) {
            String[] parts = line.split("\t", -1);
            if (parts.length != 4) {
                IO.println("Skipping malformed book row: " + line);
                continue;
            }
            try {
                library.addBook(new Book(
                        Integer.parseInt(parts[0].trim()),
                        parts[1].trim(),
                        parts[2].trim(),
                        Genre.valueOf(parts[3].trim())
                ));
            } catch (RuntimeException _) {
                IO.println("Skipping malformed book row: " + line);
            }
        }
    }

    private void loadMagazines(Library library) throws IOException {
        for (String line : Files.readAllLines(magazinesFile, StandardCharsets.UTF_8)) {
            String[] parts = line.split("\t", -1);
            if (parts.length < 3) {
                IO.println("Skipping malformed magazine row: " + line);
                continue;
            }
            library.addMagazine(new Magazine(parts[0].trim(), parseMetadata(parts[1]), parseOptionalParams(parts[2])));
        }
    }

    private void loadRecords(Library library) throws IOException {
        for (String line : Files.readAllLines(recordsFile, StandardCharsets.UTF_8)) {
            String[] parts = line.split("\t", -1);
            if (parts.length != 6) {
                IO.println("Skipping malformed record row: " + line);
                continue;
            }
            try {
                int id = Integer.parseInt(parts[0].trim());
                String title = parts[1].trim();
                String author = parts[2].trim();
                String borrower = parts[3].trim();
                LocalDate borrowDate = LocalDate.parse(parts[4].trim());
                LocalDate dueDate = LocalDate.parse(parts[5].trim());
                library.findBookById(id).ifPresent(book -> {
                    book.restoreBorrow(borrowDate, dueDate);
                    library.addRecord(new LibraryRecord(id, title, author, borrower, borrowDate, dueDate));
                });
            } catch (RuntimeException _) {
                IO.println("Skipping malformed record row: " + line);
            }
        }
    }

    private void saveBooks(List<Book> books) throws IOException {
        List<String> lines = books.stream()
                .sorted((left, right) -> Integer.compare(left.getId(), right.getId()))
                .map(book -> String.join("\t",
                        String.valueOf(book.getId()),
                        clean(book.getTitle()),
                        clean(book.getAuthor()),
                        book.getGenre().name()))
                .toList();
        Files.write(booksFile, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void saveMagazines(List<Magazine> magazines) throws IOException {
        List<String> lines = magazines.stream()
                .map(magazine -> String.join("\t",
                        clean(magazine.getTitle()),
                        clean(formatMetadata(magazine.getMetadata())),
                        clean(String.join(";", magazine.getOptionalParams()))))
                .toList();
        Files.write(magazinesFile, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private void saveRecords(List<LibraryRecord> records) throws IOException {
        List<String> lines = records.stream()
                .map(libraryRecord -> String.join("\t",
                        String.valueOf(libraryRecord.id()),
                        clean(libraryRecord.title()),
                        clean(libraryRecord.author()),
                        clean(libraryRecord.borrower()),
                        libraryRecord.borrowDate().toString(),
                        libraryRecord.dueDate().toString()))
                .toList();
        Files.write(recordsFile, lines, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
    }

    private Map<String, String> parseMetadata(String source) {
        Map<String, String> metadata = new LinkedHashMap<>();
        if (source == null || source.isBlank()) {
            return metadata;
        }
        Arrays.stream(source.split(";"))
                .map(String::trim)
                .filter(entry -> !entry.isBlank())
                .forEach(entry -> {
                    String[] keyValue = entry.split("=", 2);
                    if (keyValue.length == 2) {
                        metadata.put(keyValue[0].trim(), keyValue[1].trim());
                    }
                });
        return metadata;
    }

    private String[] parseOptionalParams(String source) {
        if (source == null || source.isBlank()) {
            return new String[0];
        }
        return Arrays.stream(source.split(";"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .toArray(String[]::new);
    }

    private String formatMetadata(Map<String, String> metadata) {
        return metadata.entrySet().stream()
                .map(entry -> clean(entry.getKey()) + "=" + clean(entry.getValue()))
                .collect(Collectors.joining(";"));
    }

    private String clean(String value) {
        return value == null ? "" : value.replace("\t", " ").replace("\r", " ").replace("\n", " ").trim();
    }
}
