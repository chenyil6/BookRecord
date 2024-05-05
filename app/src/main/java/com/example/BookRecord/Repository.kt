package com.example.BookRecord

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope


//这两个类的功能是作为应用的数据访问逻辑与数据访问对象（DAO）之间的中间层，提供清晰的API来处理数据。
class BookRepository(private val bookDao: BookDao, private val scope: CoroutineScope) {
    private val currentUserId = MutableLiveData<String?>()

    init {
        refreshCurrentUser()
    }

    fun refreshCurrentUser() {
        currentUserId.value = FirebaseAuth.getInstance().currentUser?.uid
    }

    val allBooks: LiveData<List<Book>> = currentUserId.switchMap { uid ->
        uid?.let { bookDao.getAllBooks(it) } ?: MutableLiveData(emptyList())
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

