import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Database {
    private const val  URL = "jdbc:sqlite:library_management.db"

    // Connect to the database
    fun connect(): Connection {
        return try {
            DriverManager.getConnection(URL)
        } catch (e: SQLException) {
            throw SQLException("Failed to connect to the database: ${e.message}")
        }
    }

    // Create necessary tables if they don't exist
    fun createTables() {
        val connection = connect()
        try {
            val statement = connection.createStatement()
            // Create books table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS books (
                    "id"	INTEGER,
                    "title"	TEXT NOT NULL,
                    "author_id"	INTEGER NOT NULL,
                    "description"	TEXT NOT NULL,
                    "type"	TEXT NOT NULL,
                    "publishDate"	TEXT NOT NULL,
                    "language"	TEXT NOT NULL,
                    "pages"	INTEGER NOT NULL,
                    "price"	REAL NOT NULL,
                    "stock"	INTEGER NOT NULL,
                    "isAvailable"	INTEGER NOT NULL DEFAULT 1,
                    PRIMARY KEY("id"),
                    FOREIGN KEY("author_id") REFERENCES "authors"("id")
                );         
                """.trimIndent())

            // Create Authors Table
            statement.executeQuery("""
                 CREATE TABLE IF NOT EXISTS authors(
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL
                 );
            """.trimIndent())

            // Create users table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY,
                    username TEXT NOT NULL UNIQUE,
                    email TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    balance REAL NOT NULL DEFAULT 0.0
                );
            """.trimIndent())

            statement.execute("""
                CREATE TABLE IF NOT EXISTS book_borrows (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    book_id INTEGER,
                    borrow_date TEXT,
                    return_date TEXT,
                    expected_return_date TEXT,
                    is_returned BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    FOREIGN KEY (book_id) REFERENCES books(id)
                );
            """.trimIndent())

        } catch (e: SQLException) {
            println("Error during table creation: ${e.message}")
        } finally {
            connection.close()
        }
    }
}

fun main() {
    Database.createTables()
}
