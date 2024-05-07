package com.example.BookRecord

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import org.threeten.bp.LocalDate


//这两个类的功能是作为应用的数据访问逻辑与数据访问对象（DAO）之间的中间层，提供清晰的API来处理数据。
class BookRepository(private val bookDao: BookDao, private val scope: CoroutineScope) {
    private val currentUserId = MutableLiveData<String?>()

    init {
        // 添加 FirebaseAuth 状态监听器
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            // 异步更新当前用户ID
            val user = firebaseAuth.currentUser
            currentUserId.postValue(user?.uid)
        }

        // 初始设置当前用户ID
        refreshCurrentUser()
    }

    // 使用 currentUserId 来生成 allBooks 的 LiveData
    val allBooks: LiveData<List<Book>> = currentUserId.switchMap { uid ->
        if (uid == null) MutableLiveData(emptyList())
        else bookDao.getAllBooks(uid)
    }

    // 用于刷新当前用户的方法，可以在必要时显式调用
    fun refreshCurrentUser() {
        currentUserId.value = FirebaseAuth.getInstance().currentUser?.uid
    }

    suspend fun insert(book: Book) {
        bookDao.insertBook(book)
    }

    suspend fun update(book: Book) {
        bookDao.updateBook(book)
    }

    suspend fun delete(book: Book) {
        bookDao.deleteBook(book)
    }
}




class NoteRepository(private val noteDao: NoteDao) {
    fun getNotesByBookId(bookId: Int): LiveData<List<Note>> = noteDao.getNotesByBookId(bookId)

    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    fun getNoteCountByBookId(bookId: Int): LiveData<Int> {
        return noteDao.getNoteCountByBookId(bookId)
    }

    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    suspend fun deleteById(noteId: Int) {
        noteDao.deleteById(noteId)
    }
}

class ReadingRecordRepository(private val readingRecordDao: ReadingRecordDao) {
    private val currentUserId = MutableLiveData<String?>()

    init {
        // 添加 FirebaseAuth 状态监听器
        FirebaseAuth.getInstance().addAuthStateListener { firebaseAuth ->
            // 异步更新当前用户ID
            val user = firebaseAuth.currentUser
            currentUserId.postValue(user?.uid)
        }
    }

    // 插入新的阅读记录
    suspend fun insertReadingRecord(readingRecord: ReadingRecord) {
        readingRecordDao.insertReadingRecord(readingRecord)
    }

    // 获取特定用户在指定日期范围内每天的阅读页数
    fun getPagesReadPerDay(startDate: LocalDate, endDate: LocalDate): LiveData<List<ReadingRecordDao.DailyReading>> = currentUserId.switchMap { uid ->
        if (uid == null) MutableLiveData(emptyList())
        else readingRecordDao.getPagesReadPerDay(uid, startDate, endDate)
    }
}


