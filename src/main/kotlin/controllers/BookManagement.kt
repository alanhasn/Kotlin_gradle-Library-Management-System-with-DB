package controllers
// =============== Imports Modules =================
import data.Book
import java.sql.PreparedStatement
import java.sql.Statement
import kotlin.collections.iterator

class BookManagement(){

    // function for adding books to Database
    fun addBook(book: Book) {
        // Connect to the database
        val connection = Database.connect()
        try {
            // Try to retrieve the author ID from the database (if the author already exists)
            var authorId: Int? = null
            val findAuthorSql = "SELECT id FROM authors WHERE name = ?"
            val findAuthorStmt = connection.prepareStatement(findAuthorSql) // Prepare the SELECT query
            findAuthorStmt.setString(1, book.author.name) // Set the author name parameter
            val resultSet = findAuthorStmt.executeQuery() // Execute the query to find the author

            if (resultSet.next()) {
                // Author exists, retrieve their ID
                authorId = resultSet.getInt("id")
            } else {
                // Author does not exist, insert a new author into the 'authors' table
                val insertAuthorSql = "INSERT INTO authors (name) VALUES (?)"
                // Prepare the INSERT statement with RETURN_GENERATED_KEYS to get the new author ID
                val insertAuthorStmt = connection.prepareStatement(insertAuthorSql, Statement.RETURN_GENERATED_KEYS)
                insertAuthorStmt.setString(1, book.author.name) // Set the new author's name
                insertAuthorStmt.executeUpdate() // Execute the insertion

                // Retrieve the generated author ID
                val generatedKeys = insertAuthorStmt.generatedKeys
                if (generatedKeys.next()) {
                    authorId = generatedKeys.getInt(1) // Get the new author ID
                    println("New Author added '${book.author.name}' (ID: $authorId)")
                } else {
                    // Handle the case where no ID was generated (unexpected behavior)
                    throw Exception("Error while entering the author '${book.author.name}'")
                }
            }

            // Prepare the SQL statement to insert the new book
            val insertBookSql = """
            INSERT INTO books (title, author_id, description, type, publishDate, language, pages, price, stock, isAvailable)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

            val statement = connection.prepareStatement(insertBookSql)
            // Set each parameter for the book's details
            statement.setString(1, book.title) // Set book title
            statement.setInt(2, authorId!!) // Set the author ID
            statement.setString(3, book.description) // Set book description
            statement.setString(4, book.type.name) // Set book type
            statement.setString(5, book.publishDate) // Set publish date
            statement.setString(6, book.language) // Set book language
            statement.setInt(7, book.pages) // Set number of pages
            statement.setDouble(8, book.price) // Set price
            statement.setInt(9, book.stock) // Set stock quantity
            statement.setBoolean(10, book.isAvailable) // Set availability status

            statement.executeUpdate() // Execute the book insertion
            println("New Book added Successfully '${book.title}' and connected to '${book.author.name}' Author")

        } catch (e: Exception) {
            // Catch and print any unexpected errors
            println("Unexpected Error: $e")
        } finally {
            // Always close the database connection in the 'finally' block to avoid resource leaks
            connection.close()
        }
    }

    // Function to remove a book from the database by its title
    fun removeBook(bookTitle: String) {
        // Connect to the database
        val connection = Database.connect()
        var statement: PreparedStatement? = null

        try {
            // Prepare the SQL DELETE statement to remove the book by title
            statement = connection.prepareStatement("""
            DELETE FROM books WHERE title = ?
        """.trimIndent())

            // Set the title parameter in the prepared statement
            statement.setString(1, bookTitle)

            // Execute the delete operation and get the number of affected rows
            val affectedRows = statement.executeUpdate()

            if (affectedRows > 0) {
                // If at least one row was affected, the book was successfully removed
                println("The book titled \"$bookTitle\" was removed successfully.")
            } else {
                // If no rows were affected, no book with that title was found
                println("No book with title \"$bookTitle\" was found.")
            }

        } catch (e: Exception) {
            // Handle and print any unexpected errors that occur during the process
            println("Unexpected Error: $e")
        } finally {
            // Close the connection in the 'finally' block to ensure it always runs
            try {
                connection.close()
            } catch (e: Exception) {
                // Handle any error that occurs while closing the connection
                println("Failed to close connection: $e")
            }
        }
    }

    // Function to display all books with their details from the database
    fun showAllBooks() {
        // Connect to the database
        val connection = Database.connect()
        var statement: PreparedStatement? = null

        try {
            // Prepare the SQL SELECT query to fetch all book details
            // along with the author's name by joining 'books' and 'authors' tables
            statement = connection.prepareStatement("""
            SELECT 
                books.*, 
                authors.name AS author_name 
            FROM books
            JOIN authors ON books.author_id = authors.id
        """.trimIndent())

            // Execute the query and get the result set
            val resultSet = statement.executeQuery()

            // Iterate through each book record in the result set
            while (resultSet.next()) {
                // Retrieve each column's data for the current book
                val id = resultSet.getInt("id") // Book ID
                val title = resultSet.getString("title") // Book title
                val description = resultSet.getString("description") // Book description
                val type = resultSet.getString("type") // Book type
                val publishDate = resultSet.getString("publishDate") // Publish date
                val language = resultSet.getString("language") // Language of the book
                val pages = resultSet.getInt("pages") // Number of pages
                val price = resultSet.getDouble("price") // Price of the book
                val stock = resultSet.getInt("stock") // Stock quantity
                val isAvailable = resultSet.getBoolean("isAvailable") // Availability status
                val authorName = resultSet.getString("author_name") // Author's name

                // Print out all details of the book in a readable format
                println("Book: $id")
                println("Title: $title")
                println("Author: $authorName")
                println("Description: $description")
                println("Type: $type")
                println("Publish Date: $publishDate")
                println("Language: $language")
                println("Pages: $pages")
                println("Price: $price")
                println("Stock: $stock")
                println("Available: $isAvailable")
                println("-".repeat(40)) // Separator for readability
            }
        } catch (e: Exception) {
            // Catch and print any unexpected errors
            println("Unexpected Error: $e")
        } finally {
            // Always close the connection to avoid resource leaks
            connection.close()
        }
    }

    // Finds a book by its title and prints detailed information about it.
    fun findBookByTitle(title: String) {
        // Connect to the database
        val connection = Database.connect()
        var statement: PreparedStatement? = null

        try {
            // Prepare SQL query to search for the book by title (case-insensitive)
            statement = connection.prepareStatement("""
            SELECT 
                books.*, 
                authors.name AS author_name 
            FROM books
            JOIN authors ON books.author_id = authors.id
            WHERE LOWER(books.title) = LOWER(?)
        """.trimIndent())

            // Set the title parameter in the query
            statement.setString(1, title)
            val resultSet = statement.executeQuery()

            // Check if the book is found and print its details
            if (resultSet.next()) {
                println("Book found:")
                println("Title: ${resultSet.getString("title")}")
                println("Author: ${resultSet.getString("author_name")}")
                println("Description: ${resultSet.getString("description")}")
                println("Type: ${resultSet.getString("type")}")
                println("Publish Date: ${resultSet.getString("publishDate")}")
                println("Language: ${resultSet.getString("language")}")
                println("Pages: ${resultSet.getInt("pages")}")
                println("Price: ${resultSet.getDouble("price")}")
                println("Stock: ${resultSet.getInt("stock")}")
                println("Available: ${resultSet.getBoolean("isAvailable")}")
            } else {
                println("Book titled \"$title\" not found in the library.")
            }

        } catch (e: Exception) {
            // Handle any unexpected error
            println("Unexpected Error: $e")
        } finally {
            // Close the database connection to free resources
            connection.close()
        }
    }


    // Finds books by type and prints a simple summary for each.
    fun findBooksByType(type: String) {
        val connection = Database.connect()
        var statement: PreparedStatement? = null

        try {
            // Prepare SQL query to search books by type (case-insensitive)
            statement = connection.prepareStatement("""
            SELECT 
                books.*, 
                authors.name AS author_name 
            FROM books
            JOIN authors ON books.author_id = authors.id
            WHERE LOWER(books.type) = LOWER(?)
        """.trimIndent())

            // Set the type parameter in the query
            statement.setString(1, type)
            val resultSet = statement.executeQuery()

            var found = false
            // Iterate over the result set and print basic details
            while (resultSet.next()) {
                println("Book found:")
                println("Title: ${resultSet.getString("title")}")
                println("Author: ${resultSet.getString("author_name")}")
                println("Type: ${resultSet.getString("type")}")
                println("--------------")
                found = true
            }

            // If no books are found for the given type
            if (!found) {
                println("No books found for type \"$type\".")
            }

        } catch (e: Exception) {
            println("Unexpected Error: $e")
        } finally {
            connection.close()
        }
    }


    // Finds books by author name and prints a summary for each.
    fun findBooksByAuthor(authorName: String) {
        val connection = Database.connect()
        var statement: PreparedStatement? = null

        try {
            // Prepare SQL query to search books by author's name (case-insensitive)
            statement = connection.prepareStatement("""
            SELECT 
                books.*, 
                authors.name AS author_name 
            FROM books
            JOIN authors ON books.author_id = authors.id
            WHERE LOWER(authors.name) = LOWER(?)
        """.trimIndent())

            // Set the authorName parameter in the query
            statement.setString(1, authorName)
            val resultSet = statement.executeQuery()

            var found = false
            // Iterate over the result set and print book details
            while (resultSet.next()) {
                println("Book found:")
                println("Title: ${resultSet.getString("title")}")
                println("Author: ${resultSet.getString("author_name")}")
                println("--------------")
                found = true
            }

            // If no books are found for the given author
            if (!found) {
                println("No books found for author \"$authorName\".")
            }

        } catch (e: Exception) {
            println("Unexpected Error: $e")
        } finally {
            connection.close()
        }
    }


    // Finds books by language and prints a summary for each.
    fun findBooksByLanguage(language: String) {
        val connection = Database.connect()
        var statement: PreparedStatement? = null

        try {
            // Prepare SQL query to search books by language (case-insensitive)
            statement = connection.prepareStatement("""
            SELECT 
                books.*, 
                authors.name AS author_name 
            FROM books
            JOIN authors ON books.author_id = authors.id
            WHERE LOWER(books.language) = LOWER(?)
        """.trimIndent())

            // Set the language parameter in the query
            statement.setString(1, language)
            val resultSet = statement.executeQuery()

            var found = false
            // Iterate over the result set and print book details
            while (resultSet.next()) {
                println("Book found:")
                println("Title: ${resultSet.getString("title")}")
                println("Author: ${resultSet.getString("author_name")}")
                println("Language: ${resultSet.getString("language")}")
                println("--------------")
                found = true
            }

            // If no books are found for the given language
            if (!found) {
                println("No books found for language \"$language\".")
            }

        } catch (e: Exception) {
            println("Unexpected Error: $e")
        } finally {
            connection.close()
        }
    }

}