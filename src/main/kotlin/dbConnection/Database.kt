import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

object Database {
    private const val URL = "jdbc:sqlite:library_management.db"

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
                    id INTEGER PRIMARY KEY,
                    title TEXT NOT NULL,
                    author TEXT NOT NULL,
                    description TEXT NOT NULL,
                    type TEXT NOT NULL,
                    publishDate TEXT NOT NULL,
                    language TEXT NOT NULL,
                    pages INTEGER NOT NULL,
                    price REAL NOT NULL,
                    stock INTEGER NOT NULL,
                    isAvailable INTEGER NOT NULL DEFAULT 1
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
