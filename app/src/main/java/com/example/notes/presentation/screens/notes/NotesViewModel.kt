@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
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

    private val scope = CoroutineScope(Dispatchers.IO)
    init {
        query
            .flatMapLatest{
                if(it.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(it)
                }
            }
            .onEach {
                val pinnedNotes = it.filter {note ->
                    note.isPinned
                }
                val otherNotes = it.filter {note ->
                    !note.isPinned
                }
                _state.update {state ->
                    state.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes)
                }
            }
            .launchIn(scope)
    }

    fun processCommand(command: NoteCommands) {
        when(command) {
            is NoteCommands.DeleteNote -> {
                deleteNoteUseCase(command.noteId)
            }

            is NoteCommands.EditNote -> {
                val title = command.note.title
                editNoteUseCase(command.note.copy(title = "$title edited"))
            }

            is NoteCommands.InputSearchQuery -> {

            }

            is NoteCommands.SwitchPinnedStatus -> {
                switchPinnedStatusUseCase(command.noteId)
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