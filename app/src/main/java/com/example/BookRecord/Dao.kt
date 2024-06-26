package com.example.BookRecord

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import org.threeten.bp.LocalDate


// 定义 BookDao 接口，用于访问与 Book 实体相关的数据库操作

@Dao
interface UserDao {
    // 插入用户，如果用户已存在则忽略
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    // 根据UID查询用户
    @Query("SELECT * FROM users WHERE uid = :uid")
    fun findUserById(uid: String): User?

    // 可选：获取所有用户
    @Query("SELECT * FROM users")
    fun getAllUsers(): List<User>

    // 可选：删除用户
    @Query("DELETE FROM users WHERE uid = :uid")
    fun deleteUserById(uid: String)
}

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE userId = :userId")
    fun getAllBooks(userId: String): LiveData<List<Book>>

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


@Dao
interface ReadingRecordDao {
    // 插入阅读记录
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadingRecord(readingRecord: ReadingRecord)

    // 查询特定用户在某日期的阅读页数
    @Query("SELECT SUM(readPages) FROM reading_records WHERE userId = :userId AND date = :date")
    fun getDailyReadPages(userId: String, date: LocalDate): LiveData<Int>

    // 查询特定用户在指定日期范围内的阅读页数总和
    @Query("SELECT SUM(readPages) FROM reading_records WHERE userId = :userId AND date BETWEEN :startDate AND :endDate")
    fun getTotalReadPages(userId: String, startDate: LocalDate, endDate: LocalDate): LiveData<Int>

    // 查询特定用户在指定日期范围内每天的阅读页数
    @Query("SELECT date, SUM(readPages) as totalPages FROM reading_records WHERE userId = :userId AND date BETWEEN :startDate AND :endDate GROUP BY date ORDER BY date")
    fun getPagesReadPerDay(userId: String, startDate: LocalDate, endDate: LocalDate): LiveData<List<DailyReading>>

    data class DailyReading(
        val date: LocalDate,
        val totalPages: Int
    )

}





