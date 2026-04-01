# Library Management System - Java 25

## Overview
This project is a file-based Library Management System built in Java 25 preview style for the OOP2 assignment.

The current application:
- loads and saves books, magazines, and active borrow records from files in `data/`
- uses a grouped console menu with submenus
- auto-generates book IDs when adding new books
- restores active loans on startup
- skips malformed persisted rows instead of crashing startup

## Current Project Structure

```text
src/
\-- com/library/
    +-- enums/
    |   +-- BorrowableItemStatus.java
    |   \-- Genre.java
    +-- exception/
    |   +-- LibraryException.java
    |   +-- LibraryItemNotFoundException.java
    |   \-- LibraryOperationException.java
    +-- items/
    |   +-- Book.java
    |   +-- Borrowable.java
    |   +-- LibraryItem.java
    |   \-- Magazine.java
    +-- persistence/
    |   \-- LibDataStore.java
    +-- records/
    |   \-- LibraryRecord.java
    +-- service/
    |   \-- LibraryService.java
    +-- Library.java
    +-- LibraryApp.java
    \-- Main.java
```

## Data Files

The application persists data in:

- `data/books.txt`
- `data/magazines.txt`
- `data/records.txt`

`LibDataStore` creates these files if they do not exist, seeds starter catalogue data when needed, and skips malformed rows instead of crashing startup.

## Menu Structure

### Main Menu

1. `Catalogue`
2. `Operations`
3. `Administration`
4. `Reports`
0. `Exit`

### Catalogue

1. `View all items`
2. `Search books by title`
3. `Search books by author`
4. `Browse catalogue`
0. `Back to Main Menu`

### Operations

1. `Borrow a book`
2. `Return a book`
3. `View active borrow records`
0. `Back to Main Menu`

### Administration

1. `Add a new book`
2. `Add a new magazine`
3. `Data store location`
0. `Back to Main Menu`

### Reports

1. `Statistics dashboard`
2. `Global opening hours`
0. `Back to Main Menu`

## What The Current Code Demonstrates

### Fundamentals

- `Sorting`
  - `LibraryService.showAllItems()` sorts the catalogue with `Comparator.comparing(...)`
- `Lambdas`
  - `Predicate<Book>` in search
  - `Function<Book, String>` in search result formatting
  - `Supplier<LibraryRecord>` in the borrow flow
  - `Consumer<Book>` in the return flow
- `Streams`
  - Intermediate operations: `filter`, `map`, `distinct`, `limit`, `sorted`
  - Terminal operations: `min`, `max`, `count`, `findAny`, `findFirst`, `allMatch`, `anyMatch`, `noneMatch`, `forEach`
- `Collectors`
  - `Collectors.toMap`, `Collectors.groupingBy`, `Collectors.partitioningBy` in `browseCatalogue()`
- `Switch expressions and pattern matching`
  - `showAllItems()` switches across sealed `LibraryItem` implementations
- `Sealed classes and interfaces`
  - `LibraryItem`, `Borrowable`, and `LibraryOperationException`
- `Date/Time API`
  - `LocalDate`, `LocalDateTime`, `DateTimeFormatter`, `ChronoUnit`
- `Records`
  - `LibraryRecord`

### Advanced / Java 25 Features In The Current Code

- `NIO2`
  - file creation, reading, and writing in `LibDataStore`
- `Concurrency`
  - `ExecutorService` and `Callable<String>` tasks in `showStatisticsDashboard()`
- `Localisation`
  - locale-aware date formatting in `showOpeningHours()` and the statistics section
- `Compact source files + instance main methods (JEP 512)`
  - top-level `void main()` entry point in `Main.java`
- `Flexible constructor bodies (JEP 513)`
  - validation before assignment in `Book`
- `Scoped Values`
  - borrower context in `borrowBook(...)`
- `Stream Gatherers`
  - `Gatherers.windowSliding(2)` in `showAllItems()`
  - `Gatherers.fold(...)` in `viewBorrowRecords()`

## Compile and Run

Requires JDK 25 with preview features enabled.

```bash
javac --enable-preview --source 25 -d out ^
  src/com/library/enums/*.java ^
  src/com/library/exception/*.java ^
  src/com/library/records/*.java ^
  src/com/library/items/*.java ^
  src/com/library/persistence/*.java ^
  src/com/library/service/*.java ^
  src/com/library/Library.java ^
  src/com/library/LibraryApp.java ^
  src/com/library/Main.java

java --enable-preview -cp out Main
```

## Suggested Demo Flow

1. Start the application and show the grouped main menu.
2. Open `Administration` and add a new book.
3. Open `Catalogue` and show the book immediately in `View all items`.
4. Borrow the book in `Operations`.
5. Open `View active borrow records` to show persisted loan data.
6. Open `Reports -> Statistics dashboard` to explain terminal operations, localisation, and concurrency.
7. Exit and restart the app to show that file-based persistence restores the saved state.
