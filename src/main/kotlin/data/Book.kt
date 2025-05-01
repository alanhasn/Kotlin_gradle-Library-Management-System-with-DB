package data

import utils.BooksType

data class Book(
    val title: String,
    val author: Author,
    val description: String,
    val type: BooksType,
    val publishDate: String,
    val language: String,
    val pages: Int,
    val price: Double,
    var stock: Int,
    var isAvailable: Boolean = true

){
    companion object {
        private var count = 0
        fun getCount(): String = "Total books in library: $count"

        // Increment book count each time a new book is created
        fun incrementCount() { count++ }
    }

    init {
        incrementCount()
    }

}