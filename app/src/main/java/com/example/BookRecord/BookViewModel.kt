package com.example.BookRecord

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch




class BookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BookRepository
    // 使用LiveData来观察数据变化
    val allBooks: LiveData<List<Book>>

    // 添加用于观察正在读的书籍的LiveData
    val readingBooks: LiveData<List<Book>>

    init {
        val bookDao = AppDatabase.getDatabase(application).bookDao()
        repository = BookRepository(bookDao)
        allBooks = repository.allBooks

        // 使用 map 扩展函数来创建新的 LiveData 实例
        readingBooks = allBooks.map { books ->
            books.filter { it.status == BookStatus.READING }
        }
    }

    // 添加书籍
    fun addBook(bookTitle: String, bookImage: String, author: String, pages: String, status: BookStatus, readPage: String, press: String) = viewModelScope.launch {
        val newBook = Book(title = bookTitle, image = bookImage, author = author, pages = pages, status = status, readpage = readPage, press = press)
        repository.insert(newBook)
    }

    // 删除书籍
    fun deleteBook(book: Book) = viewModelScope.launch {
        repository.delete(book)
    }

    // 更新书籍状态
    fun updateBookStatus(book: Book, newStatus: BookStatus) = viewModelScope.launch {
        book.status = newStatus
        repository.update(book)
    }

    // 更新已读页数
    fun updateBookReadPage(book: Book, readPage: String) = viewModelScope.launch {
        book.readpage = readPage
        repository.update(book)
    }
}


