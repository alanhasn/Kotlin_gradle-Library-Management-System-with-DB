package org.controllers

import org.models.Book
import kotlin.collections.iterator

class BookManagement(){
    private val booksList = mutableListOf<Book>()
    fun addBook(book: Book){
        try {
            booksList.add(book)
            println("The ${book.title} book added to Library Successfully")
        }catch (e: Exception){
            println("Unexpected Error $e")
        }
    }

    // Remove book by title
    fun removeBook(bookTitle: String) {
        val book = booksList.find { it.title == bookTitle }
        if (book != null) {
            booksList.remove(book)
            println("Removed book: $bookTitle")
        } else {
            println("Book '$bookTitle' not found.")
        }
    }


    fun showAllBooks(){
        if (booksList.isNotEmpty()){
            println("ALL BOOKS\n===================")
            for (book in booksList){
                println("BOOK ${booksList.indexOf(book)+1}\n------------------------")
                println("title: ${book.title} (${book.type})")
                println("Author: ${book.author.name}, Age: ${book.author.age}, Nationality: ${book.author.nationality}")
                println("Description: ${book.description}")
                println("Stock: ${book.stock} copies, Available: ${if (book.isAvailable) "Yes" else "No"}")
                println("Published Date: ${book.publishDate}")
                println("Number Of pages: ${book.pages}")
                println("Language: ${book.language}")
                println("Price:${book.price}")
                println("-".repeat(40))
            }
        } else {
            println("No books found in the library.")
        }
    }
    fun hasBooks(): Boolean {
        return booksList.isNotEmpty()
    }

    fun findBookByTitle(title: String){
        val book = booksList.find { it.title.equals(title , ignoreCase = true) }
        if (book != null){
            println("The $title book found in the library")
        }
        else{
            println("$title not found")
        }
    }

    //class for filters to use DRY principle
    private fun printGroupedBooks(grouped: Map<String, List<Book>>, groupTitle: String) {
        for ((key, books) in grouped) {
            println("$groupTitle: $key")
            books.forEachIndexed { index, book ->
                println("  ${index + 1}. ${book.title}")
            }
            println("=".repeat(40))
        }
    }
    // filters method
    fun filterByType() {
        val grouped = booksList.groupBy { it.type.name }
        printGroupedBooks(grouped, "Type")
    }
    fun filterByAuthor() {
        val grouped = booksList.groupBy { it.author.name }
        printGroupedBooks(grouped, "Author")
    }
    fun filterByLanguage() {
        val grouped = booksList.groupBy { it.language.uppercase() }
        printGroupedBooks(grouped, "Language")
    }

}