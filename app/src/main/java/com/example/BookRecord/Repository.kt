package com.example.BookRecord

import androidx.lifecycle.LiveData

class BookRepository(private val bookDao: BookDao) {
    val allBooks: LiveData<List<Book>> = bookDao.getAllBooks()

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

