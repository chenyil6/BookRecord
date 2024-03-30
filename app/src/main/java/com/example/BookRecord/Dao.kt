package com.example.BookRecord

import androidx.room.*
import androidx.lifecycle.LiveData

@Dao
interface BookDao {
    @Query("SELECT * FROM books")
    fun getAllBooks(): LiveData<List<Book>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Update
    suspend fun updateBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)
}

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Query("SELECT * FROM note") // 确保表名与你的 Note 实体中定义的一致
    fun getAllNotes(): LiveData<List<Note>>

    @Query("SELECT * FROM Note WHERE bookId = :bookId")
    fun getNotesByBookId(bookId: Int): LiveData<List<Note>>

    @Query("SELECT COUNT(*) FROM Note WHERE bookId = :bookId")
    fun getNoteCountByBookId(bookId: Int): LiveData<Int>

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM Note WHERE id = :noteId")
    suspend fun deleteById(noteId: Int)
}

