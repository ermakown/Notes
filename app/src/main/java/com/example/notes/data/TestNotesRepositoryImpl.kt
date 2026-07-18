package com.example.notes.data

import androidx.compose.runtime.MutableState
import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.collections.map

object TestNotesRepositoryImpl : NotesRepository {

    private val notesListFlow = MutableStateFlow<List<Note>>(listOf())

    override fun addNote(note: Note){
        notesListFlow.update {
            it + note
        }
    }

    override fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf { it ->
                    it.id == noteId
                }
            }
        }
    }

    override fun editNote(note: Note) {
        notesListFlow.update {oldList ->
            oldList.map { it ->
                if (it.id == note.id) { note }
                else { it }
            }
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override fun getNote(noteId: Int): Note {
        return notesListFlow.value.first {it ->
            it.id == noteId
        }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map {currentList ->
            currentList.filter {it ->
                it.title.contains(query) || it.content.contains(query)
            }
        }
    }

    override fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.map {it ->
                if (it.id == noteId) {
                    it.copy(isPinned = !it.isPinned)
                }
                else {
                    it
                }
            }
        }
    }
}