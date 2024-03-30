package com.example.BookRecord

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate

enum class BookStatus {
    READING,
    READ,
    ON_HOLD
}


class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val bookDao = AppDatabase.getDatabase(application).bookDao()
    private val noteDao = AppDatabase.getDatabase(application).noteDao()
    private val bookRepository: BookRepository
    private val noteRepository: NoteRepository

    // 使用LiveData来观察数据变化
    val allBooks: LiveData<List<Book>>
    val readingBooks: LiveData<List<Book>>
    val completeBooks: LiveData<List<Book>>
    val layasideBooks: LiveData<List<Book>>

    init {
        bookRepository = BookRepository(bookDao)
        noteRepository = NoteRepository(noteDao)

        allBooks = bookRepository.allBooks

        readingBooks = allBooks.map { books ->
            books.filter { it.status == BookStatus.READING }
        }

        completeBooks = allBooks.map { books -> // 注意属性名称修正
            books.filter { it.status == BookStatus.READ }
        }

        layasideBooks = allBooks.map { books ->
            books.filter { it.status == BookStatus.ON_HOLD }
        }
    }

    fun addBook(bookTitle: String, bookImage: String, author: String, pages: String, status: BookStatus, readPage: String, press: String,startTime: LocalDate) = viewModelScope.launch {
        val newBook = Book(
            title = bookTitle,
            image = bookImage,
            author = author,
            pages = pages,
            status = status,
            readpage = readPage,
            press = press,
            startTime = startTime // 设置当前日期为开始时间
        )
        bookRepository.insert(newBook)
    }

    fun deleteBook(book: Book) = viewModelScope.launch {
        bookRepository.delete(book)
    }

    fun updateBookStatus(book: Book, newStatus: BookStatus) = viewModelScope.launch {
        book.status = newStatus
        bookRepository.update(book)
    }

    fun updateBookReadPage(book: Book, readPage: String) = viewModelScope.launch {
        book.readpage = readPage // 确保属性名称与你的 Book 类一致
        bookRepository.update(book)
    }

    // 新增方法：获取指定书籍的笔记数量
    fun getNoteCountByBookId(bookId: Int): LiveData<Int> {
        return noteRepository.getNoteCountByBookId(bookId)
    }
}


