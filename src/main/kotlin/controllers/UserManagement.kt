package controllers

import data.ValidationResult
import models.User
import at.favre.lib.crypto.bcrypt.BCrypt
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException

class UserManagement {
    // Function to validate the user before registration
    private fun isValidUser(user: User): ValidationResult {
        val connection = Database.connect()

        return try {
            // Prepare the SQL query to check if the username already exists
            val statement = connection.prepareStatement("""
            SELECT COUNT(*) AS count FROM users WHERE username = ?
        """.trimIndent())
            statement.setString(1, user.username)

            val resultSet = statement.executeQuery()
            var usernameExists = false

            if (resultSet.next()) {
                usernameExists = resultSet.getInt("count") > 0
            }

            // Check if the email already exists too
            val emailStatement = connection.prepareStatement("""
                SELECT COUNT(*) AS count FROM users WHERE email = ? -- Get the count of users with same username
            """.trimIndent())
            emailStatement.setString(1, user.email)

            val emailResultSet = emailStatement.executeQuery()
            var emailExists = false

            if (emailResultSet.next()) {
                emailExists = emailResultSet.getInt("count") > 0
            }

            // Now perform all validation checks
            when {
                user.username.isBlank() -> {
                    // Check if the username is empty
                    ValidationResult(false, "Please add a username.")
                }
                user.password.length < 8 -> {
                    // Check if the password is less than 8 characters
                    ValidationResult(false, "Your password must contain at least 8 characters.")
                }
                usernameExists -> {
                    // Check if the username is already taken
                    ValidationResult(false, "This username already exists.")
                }
                emailExists -> {
                    // Check if the email is already taken
                    ValidationResult(false , "This email already taken" )
                }
                !user.email.contains("@") -> {
                    // Check if the email contains the '@' symbol
                    ValidationResult(false, "The email must contain '@' symbol.")
                }
                !user.email.contains(".") -> {
                    // Check if the email contains a dot '.'
                    ValidationResult(false, "The email must contain '.' symbol.")
                }
                else -> ValidationResult(true)
            }
        } catch (e: Exception) {
            ValidationResult(false, "Error during validation: ${e.message}")
        } finally {
            // Always close the connection
            connection.close()
        }
    }

    // Function to register a new user
    fun register(user: User) {
        val connection = Database.connect()
        // Hash the user's password using Bcrypt
        val hashedPassword = BCrypt.withDefaults().hashToString(12, user.password.toCharArray())

        try {
            val result = isValidUser(user)
            if (result.isValid) {
                // Prepare the SQL insert statement
                val statement = connection.prepareStatement("""
                INSERT INTO users (username, email, password, balance)
                VALUES (?, ?, ?, ?)
                """.trimIndent()
                )

                // Set the values in the prepared statement
                statement.setString(1, user.username)
                statement.setString(2, user.email)
                statement.setString(3, hashedPassword)
                statement.setDouble(4, user.balance)

                // Execute the insert operation
                statement.executeUpdate()
                println("The user '${user.username}' registered successfully.")

                statement.close()
            } else {
                // Print the validation error message
                println("Validation Error: ${result.errorMessage}")
            }
        } catch (e: Exception) {
            // Handle SQL-specific and general exceptions
            when (e) {
                is SQLException -> println("Error while registering the user: this email '${user.email}' is already taken")
                else -> println("ERROR: $e")
            }
        } finally {
            // Always close the database connection
            connection.close()
        }
    }

    // Function to log in a user
    fun login(username: String, password: String) {
        val connection = Database.connect()
        try {
            // Prepare a SQL query to find the user by username
            val statement = connection.prepareStatement("""
               SELECT * FROM users WHERE username = ?
            """.trimIndent())
            statement.setString(1, username)

            val resultSet: ResultSet = statement.executeQuery()
            if (resultSet.next()) {
                // If user exists, retrieve the hashed password and other details
                val storedHashedPassword = resultSet.getString("password")
                val username = resultSet.getString("username")
                val balance = resultSet.getDouble("balance")

                // Verify the provided password against the hashed one
                val result = BCrypt.verifyer().verify(password.toCharArray(), storedHashedPassword)
                if (result.verified) {
                    println("Login Successful! Welcome $username. Your balance: $balance")
                } else {
                    println("Incorrect password. Please try again.")
                }
            } else {
                println("No user found with this username")
            }
        } catch (e: Exception) {
            // Handle any exceptions during login
            println("Error while logging in: ${e.message}")
        } finally {
            // Always close the database connection
            connection.close()
        }
    }

    // Function to list all registered users
    fun registeredUser() {
        val connection = Database.connect()
        val statement = connection.createStatement()
        val query = statement.executeQuery("SELECT * FROM users")

        // Loop through each user and print their details
        while (query.next()) {
            val id = query.getInt("id")
            val username = query.getString("username")
            val email = query.getString("email")
            val password = query.getString("password")
            val balance = query.getDouble("balance")

            println("User $id")
            println("-".repeat(20))
            println("Username: $username,\nEmail: $email,\nPassword: $password,\nBalance: $balance\n")
        }
    }


    //==================== Borrow and Buy Books ======================
    fun purchaseBook(userId: Int, bookId: Int, quantity: Int) {
        val connection = Database.connect()
        var statement: PreparedStatement? = null

        try {
            // Get Book from DB for checking
            statement = connection.prepareStatement("""
            SELECT * FROM books WHERE id = ?
        """)
            statement.setInt(1, bookId)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val bookTitle = resultSet.getString("title")
                val bookPrice = resultSet.getDouble("price")
                var bookStock = resultSet.getInt("stock")
                val isAvailable = resultSet.getBoolean("isAvailable")

                // check if the book is available
                if (!isAvailable) {
                    println("Book '$bookTitle' is currently unavailable.")
                    return
                }

                // check the stock
                if (bookStock < quantity) {
                    println("Not enough stock for $quantity copies of '$bookTitle'. Available stock: $bookStock")
                    return
                }

                // get total price
                val totalPrice = bookPrice * quantity

                // check for available balance
                statement = connection.prepareStatement("""
                SELECT balance FROM users WHERE id = ?
            """)
                statement.setInt(1, userId)
                val userResultSet = statement.executeQuery()

                if (userResultSet.next()) {
                    val balance = userResultSet.getDouble("balance")

                    if (balance < totalPrice) {
                        println("Insufficient balance for $quantity copies of '$bookTitle'.")
                        return
                    }

                    // Update the balance in Database
                    val newBalance = balance - totalPrice
                    statement = connection.prepareStatement("""
                    UPDATE users SET balance = ? WHERE id = ?
                """)
                    statement.setDouble(1, newBalance)
                    statement.setInt(2, userId)
                    statement.executeUpdate()

                    // Update the stock in the database
                    bookStock -= quantity
                    statement = connection.prepareStatement("""
                    UPDATE books SET stock = ?, isAvailable = ? WHERE id = ?
                """)
                    statement.setInt(1, bookStock)
                    statement.setBoolean(2, bookStock > 0)  // if the stock > 0 set it to false
                    statement.setInt(3, bookId)
                    statement.executeUpdate()

                    println("Successfully bought $quantity copies of '$bookTitle'. Remaining balance: $newBalance")
                } else {
                    println("User with ID $userId not found.")
                }
            } else {
                println("Book with ID $bookId not found.")
            }
        } catch (e: Exception) {
            println("Unexpected Error: $e")
        } finally {
            connection.close()
        }
    }

    fun borrowBook(userId: Int, bookId: Int) {
        val connection = Database.connect()
        var statement: PreparedStatement? = null

        try {
            // get the books from the DB to get the details
            statement = connection.prepareStatement("""
            SELECT * FROM books WHERE id = ?
        """)
            statement.setInt(1, bookId)
            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                val bookTitle = resultSet.getString("title")
                val bookStock = resultSet.getInt("stock")
                val isAvailable = resultSet.getBoolean("isAvailable")

                // check if the book is already borrowed
                statement = connection.prepareStatement("""
                SELECT * FROM book_borrows WHERE user_id = ? AND book_id = ? AND return_date IS NULL
            """)
                statement.setInt(1, userId)
                statement.setInt(2, bookId)
                val borrowedResult = statement.executeQuery()

                if (borrowedResult.next()) {
                    println("You already borrowed '$bookTitle' and haven't returned it yet.")
                    return
                }

                // check the borrowed books
                if (bookStock > 0 && isAvailable) {
                    // add record in borrowed books
                    statement = connection.prepareStatement("""
                    INSERT INTO book_borrows (user_id, book_id, borrow_date, return_date) 
                    VALUES (?, ?, ?, ?)
                """)
                    val borrowDate = java.sql.Date(System.currentTimeMillis())
                    val returnDate = java.sql.Date(System.currentTimeMillis() + 14L * 24 * 60 * 60 * 1000) // return date is after 14 days
                    statement.setInt(1, userId)
                    statement.setInt(2, bookId)
                    statement.setDate(3, borrowDate)
                    statement.setDate(4, returnDate)
                    statement.executeUpdate()

                    // update the stock in the database
                    statement = connection.prepareStatement("""
                    UPDATE books SET stock = stock - 1 WHERE id = ?
                """)
                    statement.setInt(1, bookId)
                    statement.executeUpdate()

                    // Update the availability in database
                    if (bookStock - 1 == 0) {
                        statement = connection.prepareStatement("""
                        UPDATE books SET isAvailable = FALSE WHERE id = ?
                    """)
                        statement.setInt(1, bookId)
                        statement.executeUpdate()
                    }

                    // print message
                    println("You borrowed '$bookTitle'. Return by: $returnDate")
                } else {
                    println("The book '$bookTitle' is not available for borrowing.")
                }
            } else {
                println("Book with ID $bookId not found.")
            }
        } catch (e: Exception) {
            println("Unexpected Error: $e")
        } finally {
            connection.close()
        }
    }

    fun returnBook(bookTitle: String, userId: Int) {
        val connection = Database.connect()

        try {
            // Find the borrowed book for the user
            val statement = connection.prepareStatement("""
            SELECT bb.id, b.id AS book_id, b.title, b.stock
            FROM book_borrows bb
            JOIN books b ON bb.book_id = b.id
            WHERE LOWER(b.title) = LOWER(?) AND bb.user_id = ? AND bb.is_returned = FALSE
            LIMIT 1
        """)
            statement.setString(1, bookTitle)
            statement.setInt(2, userId)

            val resultSet = statement.executeQuery()

            if (resultSet.next()) {
                // Mark the book as returned
                val borrowId = resultSet.getInt("id")
                val bookId = resultSet.getInt("book_id")

                val updateBorrow = connection.prepareStatement("""
                UPDATE book_borrows SET is_returned = TRUE, return_date = CURRENT_DATE WHERE id = ?
            """)
                updateBorrow.setInt(1, borrowId)
                updateBorrow.executeUpdate()

                // Increase the book stock
                val updateBook = connection.prepareStatement("""
                UPDATE books SET stock = stock + 1 WHERE id = ?
            """)
                updateBook.setInt(1, bookId)
                updateBook.executeUpdate()

                println("Successfully returned '$bookTitle'.")
                updateBorrow.close()
                updateBook.close()
            } else {
                println("You haven't borrowed a book titled '$bookTitle' or it's already returned.")
            }

            statement.close()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            connection.close()
        }
    }

    fun showBorrowedBooks(userId: Int) {
        val connection = Database.connect()

        try {
            val statement = connection.prepareStatement("""
            SELECT b.title, bb.borrow_date, bb.expected_return_date
            FROM book_borrows bb
            JOIN books b ON bb.book_id = b.id
            WHERE bb.user_id = ? AND bb.is_returned = FALSE
        """)
            statement.setInt(1, userId)

            val resultSet = statement.executeQuery()
            var hasBooks = false

            while (resultSet.next()) {
                if (!hasBooks) {
                    println("Borrowed Books:")
                    hasBooks = true
                }
                val title = resultSet.getString("title")
                val borrowDate = resultSet.getString("borrow_date")
                val expectedReturnDate = resultSet.getString("expected_return_date")

                println("- $title, Borrowed: $borrowDate, Expected return: $expectedReturnDate")
            }

            if (!hasBooks) {
                println("No borrowed books.")
            }

            statement.close()
        } catch (e: Exception) {
            println("Error: ${e.message}")
        } finally {
            connection.close()
        }
    }

}
