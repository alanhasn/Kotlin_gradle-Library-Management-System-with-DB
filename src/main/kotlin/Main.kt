import controllers.BookManagement
import controllers.UserManagement
import data.Book
import data.User
import data.Author
import utils.BooksType

fun main() {
    val bookManagement = BookManagement()
    val userManagement = UserManagement()

    // Example: Adding a new book
    val author = Author(name = "J.K. Rowling")
    val book = Book(
        title = "Harry Potter and the Philosopher's Stone",
        author = author,
        description = "The first book in the Harry Potter series.",
        type = BooksType.FANTASY,
        publishDate = "1997-06-26",
        language = "English",
        pages = 223,
        price = 10.99,
        stock = 10,
        isAvailable = true
    )
    bookManagement.addBook(book)

    // Example: Removing a book
    bookManagement.removeBook("Harry Potter and the Philosopher's Stone")

    // Example: Displaying all books
    bookManagement.showAllBooks()

    // Example: Finding a book by title
    bookManagement.findBookByTitle("Harry Potter and the Philosopher's Stone")

    // Example: Finding books by type
    bookManagement.findBooksByType("FANTASY")

    // Example: Finding books by author
    bookManagement.findBooksByAuthor("J.K. Rowling")

    // Example: Finding books by language
    bookManagement.findBooksByLanguage("English")

    // Example: Registering a new user
    val user = User(
        username = "john_doe",
        email = "john.doe@example.com",
        password = "securepassword123",
        balance = 50.0
    )
    userManagement.register(user)

    // Example: Logging in a user
    userManagement.login("john_doe", "securepassword123")

    // Example: Listing all registered users
    userManagement.registeredUser()

    // Example: Purchasing a book
    userManagement.purchaseBook(userId = 1, bookId = 1, quantity = 2)

    // Example: Borrowing a book
    userManagement.borrowBook(userId = 1, bookId = 1)

    // Example: Returning a book
    userManagement.returnBook(bookTitle = "Harry Potter and the Philosopher's Stone", userId = 1)

    // Example: Showing borrowed books
    userManagement.showBorrowedBooks(userId = 1)
}
