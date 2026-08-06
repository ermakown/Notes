package com.example.notes.data

import com.example.notes.domain.Note
import com.example.notes.domain.NotesRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.collections.map
import kotlin.time.Duration.Companion.milliseconds

object TestNotesRepositoryImpl : NotesRepository {

    private val testList = mutableListOf<Note>().apply {
        repeat(10) {
            add(Note(id = it, title = "Title $it", content = "Content $it", updatedAt = System.currentTimeMillis(), isPinned = false))
        }
    }
    private val notesListFlow = MutableStateFlow<List<Note>>(testList)

    override suspend fun addNote(title: String, content: String, isPinned: Boolean, updatedAt: Long) {
        notesListFlow.update {oldList ->
            val note = Note(
                id = oldList.size,
                title = title,
                content = content,
                updatedAt = updatedAt,
                isPinned = isPinned
            )
            oldList + note
        }
    }

    override suspend fun deleteNote(noteId: Int) {
        notesListFlow.update { oldList ->
            oldList.toMutableList().apply {
                removeIf { it ->
                    it.id == noteId
                }
            }
        }
    }

    override suspend fun editNote(note: Note) {
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

    override suspend fun getNote(noteId: Int): Note {
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

    override suspend fun switchPinnedStatus(noteId: Int) {
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
