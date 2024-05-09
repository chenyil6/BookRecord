package com.example.BookRecord

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.BookRecord.network.NetworkBookItem
import com.example.BookRecord.network.NetworkBookResponse
import com.example.BookRecord.network.RetrofitClient
import com.example.BookRecord.network.RetrofitClient.booksApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDate
import retrofit2.Response


enum class BookStatus {
    READING, // 正在阅读
    READ, // 已阅读
    ON_HOLD // 搁置
}

//在类的初始化块中，通过传入应用程序的 Application 对象，获取了数据库的访问对象 bookDao 和 noteDao
class BookViewModel(application: Application) : AndroidViewModel(application) {
    private val bookDao = AppDatabase.getDatabase(application).bookDao()
    private val noteDao = AppDatabase.getDatabase(application).noteDao()
    private val bookRepository = BookRepository(bookDao, viewModelScope)
    private val noteRepository = NoteRepository(noteDao)
    private val booksApi = RetrofitClient.booksApi

    var currentUserUID = FirebaseAuth.getInstance().currentUser?.uid

    val allBooks: LiveData<List<Book>> = bookRepository.allBooks
    val bookCounts = MutableLiveData<Map<String, Int>>()

    // 使用 MediatorLiveData 替代 Transformations.map
    val readingBooks = MediatorLiveData<List<Book>>()
    val completeBooks = MediatorLiveData<List<Book>>()
    val layasideBooks = MediatorLiveData<List<Book>>()

    val searchResults = MutableLiveData<List<Book>>()

    init {
        readingBooks.addSource(allBooks) { books ->
            readingBooks.value = books.filter { it.status == BookStatus.READING }
        }

        completeBooks.addSource(allBooks) { books ->
            completeBooks.value = books.filter { it.status == BookStatus.READ }
        }

        layasideBooks.addSource(allBooks) { books ->
            layasideBooks.value = books.filter { it.status == BookStatus.ON_HOLD }
        }
        // Update book counts
        updateBookCounts()
    }


    private fun updateBookCounts() {
        val counts = mutableMapOf("have read" to 0, "lay aside" to 0, "reading" to 0)
        allBooks.observeForever { books ->
            counts["have read"] = books.count { it.status == BookStatus.READ }
            counts["lay aside"] = books.count { it.status == BookStatus.ON_HOLD }
            counts["reading"] = books.count { it.status == BookStatus.READING }
            bookCounts.value = counts
        }
    }

    // 添加新书籍
    fun addBook(bookTitle: String, bookImage: String, author: String, pages: String, status: BookStatus, readPage: String, press: String,startTime: LocalDate) = viewModelScope.launch {
        val newBook = Book(
            userId = currentUserUID ?: "",//确保不会空，处理未登陆的情况
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

    // 删除书籍
    fun deleteBook(book: Book) = viewModelScope.launch {
        bookRepository.delete(book)
    }

    // 更新书籍状态
    fun updateBookStatus(book: Book, newStatus: BookStatus) = viewModelScope.launch {
        book.status = newStatus
        bookRepository.update(book)
    }

    // 更新已阅读页数
    fun updateBookReadPage(book: Book, readPage: String) = viewModelScope.launch {
        book.readpage = readPage // 确保属性名称与你的 Book 类一致
        bookRepository.update(book)
    }

    // 获取指定书籍的笔记数量
    fun getNoteCountByBookId(bookId: Int): LiveData<Int> {
        return noteRepository.getNoteCountByBookId(bookId)
    }

    //搜索逻辑，然后在AddBooks里面调用
    fun searchBooks(query: String) = viewModelScope.launch {
        try {
            val response: Response<NetworkBookResponse> = booksApi.searchBooks(query)
            if (response.isSuccessful && response.body() != null) {
                searchResults.postValue(response.body()?.items?.map { it.toBook() } ?: emptyList())
            } else {
                searchResults.postValue(emptyList())
            }
        } catch (e: Exception) {
            searchResults.postValue(emptyList())
        }
    }

    private fun NetworkBookItem.toBook(): Book {
        return Book(
            userId ="the best",
            title = this.volumeInfo.title ?: "No title available",
            author = this.volumeInfo.authors?.joinToString(", ") ?: "Unknown",
            image = this.volumeInfo.imageLinks?.thumbnail ?: "",
            pages = this.volumeInfo.pageCount?.toString() ?: "N/A",
            status = BookStatus.READING, // 假设状态
            readpage = "0", // 默认已读页数
            press = this.volumeInfo.publisher ?: "Unknown",
            startTime = LocalDate.now() // 默认开始阅读时间
        )
    }

}



//定义了一个名为BookViewModel的view model类，用于管理书籍相关的数据和操作
//

