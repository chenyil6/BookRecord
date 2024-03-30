package com.example.BookRecord

import androidx.room.TypeConverter
import org.threeten.bp.LocalDate


class BookStatusConverter {
    @TypeConverter
    fun toBookStatus(value: String) = enumValueOf<BookStatus>(value)

    @TypeConverter
    fun fromBookStatus(status: BookStatus) = status.name
}




class DataConverters {
    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String? {
        return value?.toString()
    }

    @TypeConverter
    fun toLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }
}

