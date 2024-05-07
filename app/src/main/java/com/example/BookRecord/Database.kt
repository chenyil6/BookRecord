package com.example.BookRecord

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [User::class, Book::class, Note::class,ReadingRecord::class], version = 8, exportSchema = false)//数据库版本为，不导出数据库架构到文件中
@TypeConverters(DataConverters::class,BookStatusConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bookDao(): BookDao //定义一个抽象方法，用于获取BookDao的实例，这是访问Book表的数据访问对象。
    abstract fun noteDao(): NoteDao
    abstract fun ReadingRecordDao():ReadingRecordDao

    companion object { //这是一个伴生对象，允许AppDatabase类拥有类似于Java静态成员的功能。
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
                    .fallbackToDestructiveMigration() // 如果出现无法处理的迁移情况，允许数据库重建
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

