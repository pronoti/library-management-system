# OOP2 Assignment Report

## 1. Introduction

This project is a **Library Management System** written in **Java 25** using preview features where required. The application allows a user to manage a small library catalogue, add books and magazines, borrow and return books, search the catalogue, view saved borrow records, and display reports.

The main purpose of this project is not to create a business-ready library platform, but to use the library domain as a vehicle for demonstrating the Java language features required by the OOP2 assignment brief. Because of that, some features are surfaced in menu options and reports specifically so they can be clearly shown and explained during the screencast.

The application uses **file-based persistence** instead of a database. Books, magazines, and active borrow records are stored in text files under the `data/` folder. On startup, the system loads the saved state back into memory and skips malformed rows instead of crashing.

## 2. Architecture Overview

The project follows a simple layered structure:

- `Main.java`
  - Entry point using Java 25 compact source style.
- `LibraryApp.java`
  - Handles the console menus, user input, and navigation between submenus.
- `Library.java`
  - Acts as the in-memory domain aggregate holding books, magazines, and borrow records.
- `service/LibraryService.java`
  - Contains application logic such as search, borrowing, returning, reporting, and feature demonstrations.
- `persistence/LibDataStore.java`
  - Handles NIO2-based file reading/writing for books, magazines, and records.
- `items/`
  - Contains the sealed item model: `LibraryItem`, `Borrowable`, `Book`, and `Magazine`.
- `records/LibraryRecord.java`
  - Record type used to represent an active borrow.
- `exception/`
  - Custom checked and unchecked exceptions.
- `enums/`
  - `Genre` and `BorrowableItemStatus`.

This structure separates responsibilities clearly:

- The **UI layer** is in `LibraryApp`.
- The **domain layer** is in `Library`, `Book`, `Magazine`, and `LibraryRecord`.
- The **service layer** is in `LibraryService`.
- The **persistence layer** is in `LibDataStore`.

This makes the project easier to explain during the demo and easier to extend if more features are added later.

## 3. User Stories Completed

The following user stories are currently completed in the application:

1. As a user, I want to view all catalogue items so I can see books and magazines in one place.
2. As a user, I want to search books by title so I can find a book quickly.
3. As a user, I want to search books by author so I can locate books written by a particular person.
4. As a user, I want to borrow a book so that it becomes unavailable to other borrowers.
5. As a user, I want to return a borrowed book so that it becomes available again.
6. As a user, I want to see active borrow records so that I can review who borrowed what and when it is due.
7. As a user, I want to add a new book so the catalogue can grow over time.
8. As a user, I want book IDs to be generated automatically so I do not need to manage IDs manually.
9. As a user, I want to add a magazine so non-borrowable items can also be stored.
10. As a user, I want catalogue insight reports so I can view grouped and partitioned information.
11. As a user, I want a statistics dashboard so I can see counts and Java stream examples in action.
12. As a user, I want opening hours shown in different locales so localisation can be demonstrated.
13. As a user, I want the application to save data to files so the state is preserved between runs.
14. As a user, I want malformed saved rows to be skipped so the system does not fail to start because of one bad line.

## 4. Java Features Demonstrated

The brief states that the application should be used to demonstrate Java features. The table below explains how the current code addresses those requirements.

### 4.1 Fundamentals

#### Sorting

Sorting is demonstrated in `LibraryService.showAllItems()` using `Comparator.comparing(...)` when ordering catalogue items by title.

#### Lambdas

The following lambda-based functional interfaces are used:

- `Predicate<Book>`
  - Used in search logic for filtering books by title or author.
- `Function<Book, String>`
  - Used to format search results.
- `Supplier<LibraryRecord>`
  - Used when creating a new borrow record.
- `Consumer<Book>`
  - Used during the return flow.

#### Stream Intermediate Operations

The search logic demonstrates:

- `filter()`
- `map()`
- `distinct()`
- `limit()`
- `sorted()`

#### Stream Terminal Operations

The statistics dashboard demonstrates:

- `min()`
- `max()`
- `count()`
- `findAny()`
- `findFirst()`
- `allMatch()`
- `anyMatch()`
- `noneMatch()`
- `forEach()`

#### Collectors

The catalogue insights option demonstrates:

- `Collectors.toMap()`
- `Collectors.groupingBy()`
- `Collectors.partitioningBy()`

#### Switch Expressions and Pattern Matching

`showAllItems()` switches across the sealed `LibraryItem` hierarchy and formats `Book` and `Magazine` differently.

#### Sealed Classes and Interfaces

The project uses:

- `sealed interface LibraryItem`
- `sealed interface Borrowable`
- `sealed class LibraryOperationException`

#### Date/Time API

The application uses:

- `LocalDate`
- `LocalDateTime`
- `DateTimeFormatter`
- `ChronoUnit`

These appear in borrowing, returning, due dates, startup timestamp, localisation output, and fine calculation.

#### Records

`LibraryRecord` is implemented as a Java record to represent active borrows.

### 4.2 Advanced Features

#### Concurrency

The statistics dashboard contains a real concurrency example using:

- `ExecutorService`
- `Callable<String>`
- `invokeAll(...)`

This demonstrates processing a list of tasks in parallel and printing the results.

#### NIO2

`LibDataStore` uses the NIO2 API for file persistence through:

- `Files.createDirectories(...)`
- `Files.readAllLines(...)`
- `Files.write(...)`
- `Path`
- `StandardOpenOption`

#### Localisation

Localised date output is shown in the reports section for:

- UK
- US
- France
- Germany
- Japan

### 4.3 Java 25 Features

#### Compact Source Files and Instance Main Methods (JEP 512)

The entry point is written as a top-level `void main()` in `Main.java`.

#### Flexible Constructor Bodies (JEP 513)

`Book` validates constructor arguments before assigning fields.

#### Scoped Values

Scoped values are used in the borrow workflow to demonstrate context propagation without explicitly passing state through multiple methods.

#### Stream Gatherers

The project currently demonstrates:

- `Gatherers.windowSliding(2)` in `showAllItems()`
- `Gatherers.fold(...)` in `viewBorrowRecords()`

## 5. Evaluation

Overall, I believe this project adheres well to the OOP2 project brief because it uses one coherent application domain to demonstrate both core and advanced Java features. The library domain is simple, but that simplicity is helpful because it keeps the focus on the code and the feature demonstrations rather than on complicated business rules. This is in line with the assignment guidance, which explicitly states that the application is mainly a vehicle for demonstrating Java language features.

One of the strongest parts of the project is that the code now covers the full set of required features in running code rather than only in comments or planned sections. Earlier in development, some features such as concurrency and some stream terminal operations were either missing or only partially represented. These were later added into live menu/report flows so they could be demonstrated directly during execution. This is important for marks because the rubric places value not only on using features, but also on being able to explain and highlight where they appear in the code.

The project also improved significantly by moving to file-based persistence. Instead of keeping everything in memory only, the application now saves books, magazines, and active borrow records in separate text files. This makes the application feel more complete and also gives a natural place to demonstrate NIO2. A further improvement was defensive loading of saved data. Malformed rows are skipped and reported instead of crashing startup, which makes the application more robust and addresses a real correctness issue that existed during development.

From a design point of view, the structure is reasonably clear. `LibraryApp` handles user interaction, `LibraryService` contains the operational and reporting logic, `Library` holds in-memory state, and `LibDataStore` handles persistence. This is not an enterprise architecture, but it is appropriate for the size of the assignment and makes the project easier to explain in a 10-minute screencast.

The menu system was also improved to make the application easier to navigate. Grouping features into `Catalogue`, `Operations`, `Administration`, and `Reports` makes the flow clearer than having a flat list of unrelated options. This is not a marking requirement by itself, but it improves the quality of the demonstration and helps present the application more professionally.

There were also some challenges during development. One challenge was keeping the README and project explanation aligned with the code as the structure evolved. Another was ensuring that all required Java features were actually present in live code rather than only planned or commented examples. There were also bugs around exit handling, borrow record persistence, and malformed file parsing that had to be fixed. Solving those issues improved both correctness and demonstration quality.

Even though the project now meets the feature brief more effectively, there are still areas where it could be improved. For example, the application is still intentionally small and console-based, and some report outputs are clearly designed for showcasing features rather than for a realistic production workflow. However, that is acceptable for this module because the brief explicitly prioritises Java feature coverage and explanation over a business-ready application.

If more time were available, I would improve presentation further by polishing console output, reducing repetitive output in some report sections, and perhaps adding a clearer on-screen mapping from each menu option to the Java features it demonstrates. I would also consider adding lightweight automated tests. Even so, in its current form the project demonstrates the assignment requirements well and is suitable for the report and screencast submission.

## 6. Conclusion

This project successfully uses a library domain to demonstrate the main Java features required for the OOP2 assignment. The final version includes a structured menu system, file-based persistence, clearer separation of responsibilities, and live demonstrations of both core and advanced Java features, including Java 25 features. The project is therefore suitable both as a working application and as a teaching/demo artifact for explaining the required concepts.
