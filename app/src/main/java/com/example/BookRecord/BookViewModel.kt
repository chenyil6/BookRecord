package com.example.BookRecord


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State

enum class BookStatus {
    READING,
    READ,
    ON_HOLD
}

data class Book(
    val id: Int,
    var title: String,
    var image: String,
    var author: String,
    var pages: String,
    var status: BookStatus,
    var read_page :Int
)

class BookViewModel : ViewModel() {
    private var nextId = 1 // 简单的方式来生成唯一ID
    val books: MutableState<List<Book>> = mutableStateOf(emptyList())

    val numberOfBooks: Int
        get() = books.value.size

    val numberOfBooksByStatus: Map<BookStatus, Int>
        get() = books.value.groupingBy { it.status }.eachCount()

    fun addBook(
        bookTitle: String,
        bookImage: String,
        author: String,
        pages: String,
        status: BookStatus,
        read_page: Int
    ) {
        val newBook = Book(
            id = nextId++,
            title = bookTitle,
            image = bookImage,
            author = author,
            pages = pages,
            status = status,
            read_page = read_page
        )
        books.value = books.value + newBook
    }

    // 获取状态为正在读的书的列表
    fun getReadingBooks(): List<Book> {
        return books.value.filter { it.status == BookStatus.READING }
    }

    fun getHaveReadBooks(): List<Book> {
        return books.value.filter { it.status == BookStatus.READ }
    }

    fun getLayAsideBooks(): List<Book> {
        return books.value.filter { it.status == BookStatus.ON_HOLD }
    }

    fun deleteBook(bookId: Int) {
        books.value = books.value.filterNot { it.id == bookId }
    }

    fun updateBookStatus(bookId: Int, newStatus: BookStatus) {
        books.value = books.value.map { book ->
            if (book.id == bookId) {
                book.copy(status = newStatus)
            } else {
                book
            }
        }
    }


    fun updateBookReadPage(bookId: Int, readPage: Int) {
        books.value = books.value.map { book ->
            if (book.id == bookId) {
                book.copy(read_page = readPage)
            } else {
                book
            }
        }
    }
}

