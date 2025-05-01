package org
//
//import org.controllers.BookManagement
//import org.controllers.UserManagement
//import org.models.Author
//import org.models.Book
//import org.models.User
//import org.utils.BooksType // Assuming BooksType has enums like SCIENCE_FICTION, FANTASY, MYSTERY, HISTORY, THRILLER, ROMANCE etc.
//
//fun main() {
//    // --- Create Authors ---
//    val author1 = Author("Alan Kurdish", 45, "Kurdish")
//    val author2 = Author("Jane Doe", 38, "American")
//    val author3 = Author("Ken Follett", 74, "British")
//    val author4 = Author("Haruki Murakami", 75, "Japanese")
//    val author5 = Author("Isabel Allende", 81, "Chilean")
//    val author6 = Author("George Orwell", 46, "British") // Note: Using age at death for historical authors if desired
//
//    // --- Create Books (Updated: publishDate, stock) ---
//    val book1 = Book(
//        title = "The SciFi Kurdish Tale",
//        author = author1,
//        description = "A science fiction story rooted in Kurdish folklore.",
//        type = BooksType.SCIENCE_FICTION,
//        publishDate = "2020-05-15", // Using ISO format is often better
//        language = "Kurdish",
//        pages = 350,
//        price = 19.99,
//        stock = 20 // Added stock
//    )
//
//    val book2 = Book(
//        title = "Mystery on Main Street",
//        author = author2,
//        description = "A puzzling crime shakes a small town.",
//        type = BooksType.MYSTERY,
//        publishDate = "2022-11-01",
//        language = "English",
//        pages = 280,
//        price = 15.50,
//        stock = 30
//    )
//
//    val book3 = Book(
//        title = "Pillars of the Earth",
//        author = author3,
//        description = "Epic tale of building a cathedral in medieval England.",
//        type = BooksType.HISTORY, // Or Historical Fiction depending on your enum
//        publishDate = "1989-09-12",
//        language = "English",
//        pages = 973,
//        price = 25.00,
//        stock = 10
//    )
//
//    val book4 = Book(
//        title = "Kafka on the Shore",
//        author = author4,
//        description = "A surreal and complex journey involving cats, spirits, and more.",
//        type = BooksType.FANTASY, // Or Magical Realism
//        publishDate = "2002-09-12",
//        language = "Japanese", // Original language
//        pages = 615,
//        price = 22.75,
//        stock = 9
//    )
//
//    val book5 = Book(
//        title = "The House of the Spirits",
//        author = author5,
//        description = "A multi-generational family saga in Latin America.",
//        type = BooksType.FANTASY, // Or Magical Realism
//        publishDate = "1982-11-11",
//        language = "Spanish", // Original language
//        pages = 448,
//        price = 18.00,
//        stock = 5
//    )
//
//    val book6 = Book(
//        title = "Downtown Thriller",
//        author = author2, // Same author as book2
//        description = "High-stakes action unfolds in the city center.",
//        type = BooksType.THRILLER,
//        publishDate = "2023-08-20",
//        language = "English",
//        pages = 320,
//        price = 16.99,
//        stock = 6
//    )
//
//    val book7 = Book(
//        title = "Galactic Empires",
//        author = author1, // Same author as book1
//        description = "War among the stars for control of the galaxy.",
//        type = BooksType.SCIENCE_FICTION,
//        publishDate = "2021-03-10",
//        language = "English", // Different language than book1
//        pages = 500,
//        price = 24.95,
//        stock = 8
//    )
//
//    val book8 = Book(
//        title = "Nineteen Eighty-Four",
//        author = author6,
//        description = "A dystopian novel about totalitarianism and surveillance.",
//        type = BooksType.SCIENCE_FICTION, // Often classified as Dystopian/Sci-Fi
//        publishDate = "1949-06-08",
//        language = "English",
//        pages = 328,
//        price = 12.50,
//        stock = 2
//    )
//
//    val book9 = Book(
//        title = "World Without End",
//        author = author3, // Same author as book3
//        description = "Takes place 150 years after Pillars of the Earth.",
//        type = BooksType.HISTORY, // Or Historical Fiction
//        publishDate = "2007-10-09",
//        language = "English",
//        pages = 1024,
//        price = 26.50,
//        stock = 9
//    )
//
//    // --- Initialize Book Management ---
//    val bookManager = BookManagement() // Renamed to avoid conflict with 'manager' concept if UserManagement also manages
//
//    // --- Add Books to Management ---
//    println("Adding books to the book manager...")
//    bookManager.addBook(book1)
//    bookManager.addBook(book2)
//    bookManager.addBook(book3)
//    bookManager.addBook(book4)
//    bookManager.addBook(book5)
//    bookManager.addBook(book6)
//    bookManager.addBook(book7)
//    bookManager.addBook(book8)
//    bookManager.addBook(book9)
//    println("Finished adding books.")
//
//    // --- Use Book Management Functions ---
//    println("\nChecking if book manager has books:")
//    val hasBooksResult = bookManager.hasBooks() // Assuming hasBooks returns boolean and prints internally, or just returns
//    println("Book manager has books: $hasBooksResult") // Example if it returns boolean
//
//    println("\nShow all books:")
//    bookManager.showAllBooks()
//
//    println("\nFilter by Author (will likely prompt):")
//    bookManager.filterByAuthor()
//
//    println("\nFilter by Language (will likely prompt):")
//    bookManager.filterByLanguage()
//
//    println("\nFilter by Type (will likely prompt):")
//    bookManager.filterByType()
//
//    println("\nFinding 'Downtown Thriller':")
//    bookManager.findBookByTitle("Downtown Thriller") // Assumes this method prints the found book or null/not found
//
//    println("\nRemoving 'Downtown Thriller':")
//    bookManager.removeBook("Downtown Thriller") // Assumes this method confirms removal
//
//    println("\nFinding 'Downtown Thriller' again:")
//    bookManager.findBookByTitle("Downtown Thriller") // Should now indicate not found
//
//    // --- Initialize User Management ---
//    val userManagement = UserManagement()
//
//    // --- Create and Register User ---
//    val user1 = User("alan", "alan@gmail.com", "12345", 200.2) // Added balance
//    println("\nRegistering user ${user1.username}:")
//    userManagement.register(user1) // Assumes this adds user1 to UserManagement
//
//    println("\nShow registered users:")
//    userManagement.registeredUser() // Assumes this lists registered users
//
//    // --- User Actions ---
//    println("\nUser '${user1.username}' borrowing '${book1.title}':")
//    user1.borrowBook(book1) // Assumes this adds book1 to user's borrowed list and potentially affects stock/availability
//
//    // Potential Issue: Purchasing 20 copies when stock is 9 and balance might be insufficient.
//    val purchaseQuantity = 1 // Changed quantity to 1 for realism
//    println("\nUser '${user1.username}' purchasing $purchaseQuantity of '${book4.title}':")
//    // Need logic in purchaseBook to check stock and balance
//    user1.purchaseBook(book4, purchaseQuantity) // Assumes this adds book4 to user's purchased list, adjusts user balance, and book stock
//
//    println("\nUser '${user1.username}' showing borrowed books:")
//    user1.showBorrowedBooks() // Assumes this lists books currently borrowed by user1
//
//    println("\nUser '${user1.username}' returning '${book1.title}':")
//    user1.returnBook(book1.title) // Assumes this removes book1 from user's borrowed list and potentially adjusts stock/availability
//
//    println("\nUser '${user1.username}' showing borrowed books again:")
//    user1.showBorrowedBooks() // Should not list book1 anymore
//
//    println("\n--- End of Main ---")
//}

import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

fun main() {
    val url = "jdbc:sqlite:test.db"

    // Establish connection
    val connection: Connection = DriverManager.getConnection(url)
    connection.use { conn ->
        println("Connection to SQLite has been established.")
    }
}