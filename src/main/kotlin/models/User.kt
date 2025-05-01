package models

import data.Book
import java.sql.PreparedStatement


class User(
    val username: String,
    val email: String,
    val password: String,
    var balance: Double
) {
    val boughtBook = mutableListOf<Book>()

    companion object {
        private var count = 0
        fun getCount(): String = "Total users: $count"

        fun incrementCount() { count++ }
    }

    init {
        incrementCount()
    }
}

