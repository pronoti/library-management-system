# 10 Minute Demo Transcript

## Opening

Hello, my name is Pronoti Saha, and this is my OOP2 assignment demonstration.

My application is a **Library Management System** written in **Java 25**. The goal of the project is not mainly to build a business-ready system, but to use a simple domain to clearly demonstrate the Java features required in the assignment brief.

The application lets the user:

- view books and magazines
- search by title or author
- borrow and return books
- add new books and magazines
- save and reload data from files
- view reports that demonstrate stream operations, localisation, concurrency, and Java 25 features

I will first give a quick overview of the architecture, then I will demo the main user stories, and while doing that I will point to where the required Java features are used in the code.

## Architecture Overview

At a high level, the application is split into four parts.

First, `Main.java` is the entry point, and it uses the Java 25 compact source file style with a top-level `void main()`.

Second, `LibraryApp.java` handles the menu system and user input. This is the console UI layer.

Third, `LibraryService.java` contains the application logic such as searching, borrowing, returning, and generating reports.

Fourth, `LibDataStore.java` handles file-based persistence. It reads and writes the data in the `data` folder using the NIO2 API.

The in-memory domain model itself is represented by `Library.java`, `Book.java`, `Magazine.java`, and `LibraryRecord.java`.

## Start the Application

I will run the application now.

At startup, the application prints the current timestamp using `LocalDateTime` and `DateTimeFormatter`, which is one part of the Date/Time API requirement.

The app then loads data from the `data` folder. The persistence layer also skips malformed rows instead of crashing, so the startup is more robust.

Now I can see the main menu:

1. Catalogue
2. Operations
3. Administration
4. Reports
0. Exit

## User Story 1: Add a New Book

I will go to `Administration` and choose `Add a new book`.

Here I enter a title, an author, and a genre.

The book ID is auto-generated, so the user does not need to manage IDs manually.

This add-book flow demonstrates several things:

- object creation using the `Book` class
- constructor validation in `Book`, which is also where I demonstrate **Flexible Constructor Bodies**, a Java 25 feature
- enum usage through `Genre`
- persistence because the new book is immediately saved to file

Now the system confirms that the book has been added and saved.

## User Story 2: View All Items

Next, I’ll go to `Catalogue` and choose `View all items`.

Here I can see both books and magazines displayed in one list.

This screen demonstrates:

- **Sorting**, because catalogue items are ordered using `Comparator.comparing(...)`
- **Switch expressions and pattern matching**, because the code switches across the sealed `LibraryItem` hierarchy and formats `Book` and `Magazine` differently
- **Sealed interfaces**, because `LibraryItem` and `Borrowable` are sealed

It also demonstrates **Stream Gatherers** using `Gatherers.windowSliding(2)`, where adjacent book titles are shown as overlapping pairs underneath the main table.

That gives me a second Java 25 feature to point at during the demo.

## User Story 3: Search by Title and Author

Now I will use `Search books by title`.

This search is implemented with Java Streams and lambdas.

It demonstrates the following stream intermediate operations:

- `filter`
- `map`
- `distinct`
- `limit`
- `sorted`

It also demonstrates:

- `Predicate<Book>` for filtering
- `Function<Book, String>` for formatting the output

The same idea is used again in `Search books by author`.

## User Story 4: Borrow a Book

Next, I’ll go to `Operations` and choose `Borrow a book`.

I enter the book ID and borrower name.

This flow demonstrates:

- finding a book from the in-memory collection
- borrow state being updated in the `Book` object
- creation of a `LibraryRecord`
- immediate persistence to file
- a receipt being printed

This borrow flow also demonstrates:

- `Supplier<LibraryRecord>` for creating the borrow record
- **Scoped Values**, where the borrower context is scoped for the duration of the operation
- the Date/Time API again, because borrow and due dates are based on `LocalDate`

## User Story 5: View Borrow Records

Still in `Operations`, I’ll open `View active borrow records`.

This shows all active records currently stored in memory and also reads the raw `records.txt` content back from disk.

This screen demonstrates:

- **NIO2**, because the records are stored using file APIs
- **Record types**, because `LibraryRecord` is a Java record
- **Stream Gatherers.fold(...)`**, which is used to create a single summary string of the borrowed titles

So this menu option is very useful in the screencast because it ties persistence, records, and Java 25 gatherers together.

## User Story 6: Return a Book

Now I’ll return the borrowed book.

This demonstrates:

- updating the book state back to available
- removing the corresponding `LibraryRecord`
- saving the updated state to disk

It also demonstrates:

- `Consumer<Book>` in the return flow
- `ChronoUnit.DAYS.between(...)` for late-day calculation

So this is another clear place to point at the Date/Time API in the code.

## User Story 7: Browse Catalogue Report

Back in `Catalogue`, I’ll choose `Browse catalogue`.

This report is designed specifically to demonstrate `collect()` and collectors.

It shows:

- `Collectors.toMap()`
- `Collectors.groupingBy()`
- `Collectors.partitioningBy()`

This feature is useful because the assignment explicitly names those collectors.

## User Story 8: Statistics Dashboard

Now I’ll go to `Reports` and choose `Statistics dashboard`.

This section is very important because it demonstrates the required stream terminal operations directly.

Here I show:

- `count`
- `min`
- `max`
- `findFirst`
- `findAny`
- `allMatch`
- `anyMatch`
- `noneMatch`

This same dashboard also demonstrates **Localisation** by formatting today’s date for multiple countries.

And finally, it demonstrates **Concurrency** using:

- `ExecutorService`
- a list of `Callable<String>`
- `invokeAll(...)`

The output shows multiple worker threads auditing books in parallel.

That satisfies the advanced concurrency requirement from the assignment brief.

## User Story 9: Global Opening Hours

The final report screen is `Global opening hours`.

This is another simple but useful localisation example because it shows date output in multiple locales.

## Show Persistence Across Restarts

To demonstrate file-based persistence, I can now exit the program and run it again.

When the program starts again, it loads the saved books, magazines, and active borrow records from the files in the `data` folder.

This proves that the application state is not just in memory and that the persistence layer is working.

## Code Feature Summary

Before I finish, I want to summarize where the required feature groups appear.

- `Main.java`
  - compact source file and top-level `main`
- `Book.java`
  - flexible constructor body, Date/Time API, borrow/return logic
- `LibraryService.java`
  - sorting, streams, lambdas, collectors, switch expressions, scoped values, gatherers, concurrency, localisation
- `LibDataStore.java`
  - NIO2 and defensive file parsing
- `LibraryRecord.java`
  - record type
- sealed type hierarchy
  - `LibraryItem`, `Borrowable`, and `LibraryOperationException`

## Evaluation

In terms of evaluation, I believe the project adheres well to the brief because it focuses strongly on Java feature coverage and explanation rather than trying to be an overly complex business system.

The application domain is simple, but that actually helps make the features easier to demonstrate.

One challenge I encountered was ensuring that every required feature existed in live code instead of only in comments or planned examples. Another challenge was keeping persistence robust, especially when loading malformed saved rows. I addressed that by making the loader skip bad rows instead of crashing startup.

I also improved the structure over time by separating the menu, service, domain, and persistence responsibilities. That makes the project easier to explain and easier to maintain.

If I had more time, I would improve presentation further, possibly add tests, and polish the console UI more. But overall, the current version demonstrates the assignment requirements clearly and in executable code.

## Closing

That concludes my demonstration.

This project uses a library domain to demonstrate the OOP2 Java requirements including fundamentals, advanced topics, and Java 25 features, while also providing a working file-based application with persistent data and a structured console menu.

Thank you.
