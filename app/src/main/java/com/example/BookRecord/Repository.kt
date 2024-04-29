package com.example.BookRecord

import androidx.lifecycle.LiveData

//这两个类的功能是作为应用的数据访问逻辑与数据访问对象（DAO）之间的中间层，提供清晰的API来处理数据。
class BookRepository(private val bookDao: BookDao) { //这个类封装了对书籍数据的各种操作，使用BookDao接口进行数据操作。
    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()
//通过BookDao获取所有书籍的实时数据（LiveData），这允许观察数据变化并在数据变化时更新UI。
    //下面都是成员函数
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

