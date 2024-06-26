package com.example.BookRecord

import androidx.compose.runtime.staticCompositionLocalOf

// 提供默认的 ViewModel 实例或错误提示
val LocalNotesViewModel = staticCompositionLocalOf<NoteViewModel> {
    error("No NotesViewModel provided")
}

// 提供默认的 ViewModel 实例或错误提示
val LocalBooksViewModel = staticCompositionLocalOf<BookViewModel> {
    error("No NotesViewModel provided")
}


val LocalreadingRecordViewModel = staticCompositionLocalOf<ReadingRecordViewModel> {
    error("No NotesViewModel provided")
}