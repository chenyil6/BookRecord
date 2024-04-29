package com.example.BookRecord

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: NoteRepository

    // 使用 MutableLiveData 来跟踪当前选中的 bookId
    private val _currentBookId = MutableLiveData<Int?>()

    init {
        val appDatabase = AppDatabase.getDatabase(application)
        val notesDao = appDatabase.noteDao()
        repository = NoteRepository(notesDao)
    }

    // 使用 switchMap 来根据当前的 bookId 获取对应的笔记列表
    val notesByBookId: LiveData<List<Note>> = _currentBookId.switchMap { bookId ->
        bookId?.let {
            repository.getNotesByBookId(it)
        } ?: MutableLiveData(emptyList())
    }

    fun setBookId(bookId: Int) {
        _currentBookId.value = bookId
    }


    // 添加笔记需要提供bookId
    fun addNote(noteContent: String, bookId: Int) = viewModelScope.launch {
        val newNote = Note(content = noteContent, bookId = bookId)
        repository.insert(newNote)
    }

    // 删除笔记
    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.delete(note)
    }

    // 编辑笔记
    fun editNote(noteId: Int, noteContent: String, bookId: Int) = viewModelScope.launch {
        val noteToUpdate = Note(id = noteId, content = noteContent, bookId = bookId)
        repository.update(noteToUpdate)
    }
}
