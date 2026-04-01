classDiagram
direction BT
class Book {
  + Book(int, String, String, Genre) 
  - String author
  - String title
  - LocalDate dueDate
  - LocalDate borrowDate
  - int id
  - Genre genre
  - boolean borrowed
  + isBorrowed() boolean
  + info() void
  + getBorrowedDate() LocalDate
  + restoreBorrow(LocalDate, LocalDate) void
  + getGenre() Genre
  + getAuthor() String
  + getId() int
  + getDueDate() LocalDate
  + returnItem() boolean
  + borrowItem(String) boolean
  + getTitle() String
}
class Borrowable {
<<Interface>>
  + borrowItem(String) boolean
  + returnItem() boolean
}
class BorrowableItemStatus {
<<enumeration>>
  - BorrowableItemStatus(String) 
  +  AVAILABLE
  +  BORROWED
  + String value
  + values() BorrowableItemStatus[]
  + valueOf(String) BorrowableItemStatus
}
class Genre {
<<enumeration>>
  + Genre() 
  +  EDUCATION
  +  OTHER
  +  TECHNOLOGY
  +  HISTORY
  +  SCIENCE
  +  FICTION
  + valueOf(String) Genre
  + values() Genre[]
}
class LibDataStore {
  + LibDataStore(Path) 
  - Path magazinesFile
  - Path dataDirectory
  - Path booksFile
  - Path recordsFile
  - parseMetadata(String) Map~String, String~
  + save(Library) void
  - initialiseStorage() void
  + getRecordsFile() Path
  + loadExistingOnStartup() Library
  - loadMagazines(Library) void
  - formatMetadata(Map~String, String~) String
  - saveRecords(List~LibraryRecord~) void
  + readRecordLines() List~String~
  - loadBooks(Library) void
  - clean(String) String
  + getDataDirectory() Path
  - parseOptionalParams(String) String[]
  - seedIfEmpty() void
  - saveBooks(List~Book~) void
  - saveMagazines(List~Magazine~) void
  - createFileIfMissing(Path) void
  - loadRecords(Library) void
}
class Library {
  + Library() 
  - List~LibraryRecord~ records
  - List~Book~ books
  - List~Magazine~ magazines
  + addMagazine(Magazine) void
  + getRecords() List~LibraryRecord~
  + getMagazines() List~Magazine~
  + getAllItems() List~LibraryItem~
  + addBook(Book) void
  + getBooks() List~Book~
  + findBookById(int) Optional~Book~
  + addRecord(LibraryRecord) void
}
class LibraryApp {
  - LibraryApp(LibraryService, Scanner) 
  + String BACK_TO_MAIN_MENU
  - Scanner scanner
  - LibraryService service
  - runAction(Runnable) void
  - promptRequired(String) String
  - printSection(String) void
  - readMenuChoice(int, int) int
  - addBookFlow() void
  + getInstance(Scanner) LibraryApp
  - pause() void
  - showReportsMenu() void
  - addMagazineFlow() void
  - selectGenre() Genre
  - showOperationsMenu() void
  - showCatalogueMenu() void
  - readPositiveInt() int
  + run() void
  - showAdministrationMenu() void
  - promptOptional(String) String
}
class LibraryException {
  + LibraryException(String) 
}
class LibraryItem {
<<Interface>>
  + getTitle() String
  + info() void
}
class LibraryItemNotFoundException {
  + LibraryItemNotFoundException(String) 
}
class LibraryOperationException {
  + LibraryOperationException(String) 
}
class LibraryRecord {
  + LibraryRecord(int, String, String, String, LocalDate, LocalDate) 
  - int id
  - String title
  - String borrower
  - LocalDate dueDate
  - String author
  - LocalDate borrowDate
  + id() int
  + title() String
  + author() String
  + dueDate() LocalDate
  + borrower() String
  + borrowDate() LocalDate
}
class LibraryService {
  + LibraryService(Library, LibDataStore) 
  - Library library
  - LibDataStore storage
  + addBook(String, String, Genre) int
  + showStatisticsDashboard() void
  + genres() List~Genre~
  + returnBook(int) void
  + borrowBook(int, String) void
  + showStorageSummary() String
  - validateQuery(String) String
  + nextBookId() int
  - search(String, Predicate~Book~) void
  - showLocalisedDates() void
  + searchByTitle(String) void
  + searchByAuthor(String) void
  - runParallelAudit() void
  + browseCatalogue() void
  - persist() void
  + showAllItems() void
  + addMagazine(String, Map~String, String~) void
  + viewBorrowRecords() void
  + showOpeningHours() void
}
class Magazine {
  + Magazine(String, Map~String, String~, String[]) 
  + Magazine(String, Map~String, String~) 
  + Magazine(String) 
  - String[] optionalParams
  - String title
  - Map~String, String~ metaData
  + info() void
  + getOptionalParams() String[]
  + getTitle() String
  + getMetadata() Map~String, String~
}
class Main {
  + Main() 
  ~ main() void
}

