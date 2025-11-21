# Library Management System – Java 25
## 1. Overview
### This project implements a Library Management System using Java 25 features. It allows:
- Adding books and Magazine
- Borrowing and returning books
- Displaying library statistics
- Demonstrating advanced Java features

## 2. Features Used
### Fundamental Java:
- Classes, objects, encapsulation
- Method overloading, varargs
- Inheritance, polymorphism
- Interfaces
- Arrays and Collections (ArrayList)
- Enums and String APIs

### Advanced Java:
- Records (LibraryRecord)
- Immutable class (Magazine)
- Lambdas and Predicates
- Method references
- Sealed classes/interfaces
- Flexible constructor bodies (JEP 513)
- Compact source files + instance main (JEP 512)

## Java 25 Features:
### 1. Compact Source + Instance Main (JEP 512)
- `void main()` declared at top level
- No static or public main needed
- Demonstrates quick testing without boilerplate
- Uses `IO.println(...)` for console output

### 2. Flexible Constructor Bodies (JEP 513)
- Constructor validation occurs before calling super or initializing fields
- Allows early error detection and fail-fast design

### 3. Notes on Compilation
- Requires Java 25 JDK
- Compile & run:
  javac Main.java
  java Main.java
- Compact source file auto-generates an implicit class for main
- IO.println requires explicit IO class

### 4. Conclusion
The project successfully demonstrates a Library System with modern Java features and is suitable for student learning. The Java 25 features reduce boilerplate and allow safer constructor logic.
