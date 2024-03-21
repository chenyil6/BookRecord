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
    var status: BookStatus
)

class BookViewModel : ViewModel() {
    private var nextId = 1 // 简单的方式来生成唯一ID
    private val _books: MutableState<List<Book>> = mutableStateOf(emptyList())
    val books: State<List<Book>> = _books

    val numberOfBooks: Int
        get() = _books.value.size

    val numberOfBooksByStatus: Map<BookStatus, Int>
        get() = _books.value.groupingBy { it.status }.eachCount()

    fun addBook(
        bookTitle: String,
        bookImage: String,
        author: String,
        pages: String,
        status: BookStatus
    ) {
        val newBook = Book(
            id = nextId++,
            title = bookTitle,
            image = bookImage,
            author = author,
            pages = pages,
            status = status
        )
        _books.value = _books.value + newBook
    }

    // 获取状态为正在读的书的列表
    fun getReadingBooks(): List<Book> {
        return _books.value.filter { it.status == BookStatus.READING }
    }

    fun getHaveReadBooks(): List<Book> {
        return _books.value.filter { it.status == BookStatus.READ }
    }

    fun getLayAsideBooks(): List<Book> {
        return _books.value.filter { it.status == BookStatus.ON_HOLD }
    }

    fun deleteBook(bookId: Int) {
        _books.value = _books.value.filterNot { it.id == bookId }
    }
}

