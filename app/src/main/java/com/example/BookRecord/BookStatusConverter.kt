package com.example.BookRecord

import androidx.room.TypeConverter

class BookStatusConverter {
    @TypeConverter
    fun toBookStatus(value: String) = enumValueOf<BookStatus>(value)

    @TypeConverter
    fun fromBookStatus(status: BookStatus) = status.name
}
