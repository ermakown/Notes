@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.notes.presentation.screens.notes

import android.R.id.input
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.TestNotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.EditNoteUseCase
import com.example.notes.domain.GetAllNotesUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import com.example.notes.domain.SearchNotesUseCase
import com.example.notes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NotesViewModel: ViewModel() {

    private val repository = TestNotesRepositoryImpl

    private val addNoteUseCase = AddNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)
    private val query = MutableStateFlow("")
    private val _state = MutableStateFlow(NoteScreenState())
    val state = _state.asStateFlow()

    init {
        addSomeNotes()
        query
            .onEach {input ->
                _state.update { it.copy(query = input) }
            }
            .flatMapLatest {input ->
                if (input.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(input)
                }
            }
            .onEach {notes ->
                val pinnedNotes = notes.filter {it ->
                    it.isPinned
                }
                val otherNotes = notes.filter {it ->
                    !it.isPinned
                }
                _state.update {state ->
                    state.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes)
                }
            }
            .launchIn(viewModelScope)
    }

    // TODO: don't forget to delete it

    private fun addSomeNotes() {
        viewModelScope.launch {
            repeat(50) {
                addNoteUseCase(
                    title = "Title №$it Title №$it Title №$it Title №$it Title №$it Title №$it Title №$it Title №$it Title №$it Title №$it ",
                    content = "Content №$it Content №$it Content №$it Content №$it Content №$it Content №$it Content №$it Content №$it Content №$it Content №$it "
                )
            }
        }

    }

    fun processCommand(command: NoteCommands) {
        viewModelScope.launch {
            when(command) {
                is NoteCommands.DeleteNote -> {
                    deleteNoteUseCase(command.noteId)
                }

                is NoteCommands.EditNote -> {
                    val note = getNoteUseCase(command.note.id)
                    val title = command.note.title
                    editNoteUseCase(note.copy(title = "$title edited"))
                }

                is NoteCommands.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }

                is NoteCommands.SwitchPinnedStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }
            }
        }

    }
}

sealed interface NoteCommands {

    data class InputSearchQuery(val query: String): NoteCommands

    data class SwitchPinnedStatus(val noteId: Int): NoteCommands

    // Temp

    data class DeleteNote(val noteId: Int): NoteCommands

    data class EditNote(val note: Note): NoteCommands
}


data class NoteScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf()
)