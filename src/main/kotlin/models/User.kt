package org.models


class User(
    val username: String,
    val email: String,
    val password: String,
    var balance: Double
) {
    val boughtBook = mutableListOf<Book>()
    val borrowedBooks = mutableListOf<BorrowedBook>()

    companion object {
        private var count = 0
        fun getCount(): String = "Total users: $count"

        fun incrementCount() { count++ }
    }

    init {
        incrementCount()
    }

    fun purchaseBook(book: Book, quantity: Int) {
        val totalPrice = book.price * quantity
        if (book.isAvailable && book.stock >= quantity) {
            if (balance < totalPrice) {
                println("Insufficient balance for $quantity copies of '${book.title}'.")
            } else {
                balance -= totalPrice
                book.stock -= quantity  // Reduce stock after purchase
                book.updateAvailability()  // Update availability
                repeat(quantity) { boughtBook.add(book) }
                println("Successfully bought $quantity copies of '${book.title}'. Remaining balance: $balance$")
            }
        } else {
            println("Not enough stock or book is unavailable for purchase.")
        }
    }

    fun borrowBook(book:Book){
        if (borrowedBooks.any { it.book.title == book.title }){
            println("You already borrowed '${book.title}' and haven't returned it yet.")
            return
        }
        else{
            if (book.isAvailable) {
                val borrowed = BorrowedBook(book)
                borrowedBooks.add(borrowed)
                book.stock--  // Reduce stock after borrowing
                book.updateAvailability()  // Update availability
                println("You borrowed '${book.title}'. Return by: ${borrowed.returnDate}")
            } else {
                println("The book '${book.title}' is not available for borrowing.")
            }

        }
    }

    fun returnBook(bookTitle: String) {
        val borrowedBook = borrowedBooks.find { it.book.title.equals(bookTitle, ignoreCase = true) }
        if (borrowedBook != null) {
            borrowedBooks.remove(borrowedBook)
            borrowedBook.book.stock++ // Increase stock on return
            borrowedBook.book.updateAvailability() // Recalculate availability
            println("You successfully returned '${borrowedBook.book.title}'.")
        } else {
            println("You haven't borrowed a book titled '$bookTitle'.")
        }
    }
    fun showBorrowedBooks() {
        if (borrowedBooks.isEmpty()) {
            println(" No books currently borrowed.")
        } else {
            println(" Borrowed Books:")
            borrowedBooks.forEach {
                println("- ${it.book.title}, due: ${it.returnDate}")
            }
        }
    }

}

