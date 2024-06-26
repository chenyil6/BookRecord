package com.example.BookRecord

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate


@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String

)
@Entity(
    tableName = "books",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)

data class Book(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var userId: String, // 添加这个字段来连接User
    var title: String,
    var image: String,
    var author: String,
    var pages: String,
    var status: BookStatus,
    var readpage: String,
    var press: String,
    var startTime: LocalDate
)


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = ["id"], // Book类的ID字段
            childColumns = ["bookId"], // Note类的bookId字段，作为外键
            onDelete = ForeignKey.CASCADE // 如果Book被删除，则相应的Note也被删除
        )
    ]
)

data class Note(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var content: String,
    val bookId: Int // 引用Book的ID
)

//创建一个实体来记录每日的阅读页数
@Entity(tableName = "reading_records",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ])
data class ReadingRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // 引用User的UID
    val date: LocalDate, // 记录阅读的日期
    val readPages: Int // 当天读的页数
)




