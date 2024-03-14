package com.example.BookRecord

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

data class Note(var content: String ,val id:Int)

class NoteViewModel: ViewModel() {
    private var nextId = 1 // 简单的方式来生成唯一ID
    val notes: MutableState<List<Note>> = mutableStateOf(emptyList())

    fun addNote(noteContent: String) {
        val newNote = Note(id = nextId++, content = noteContent)
        notes.value = notes.value + newNote
    }

    fun deleteNote(noteId: Int) {
        notes.value = notes.value.filterNot { it.id == noteId }
    }

    fun editNote(noteId: Int, newContent: String) {
        notes.value = notes.value.map {
            if (it.id == noteId) it.copy(content = newContent) else it
        }
    }
}
