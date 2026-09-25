package com.example.notes.presentation.screens.editing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil3.Uri
import com.example.notes.domain.ContentItem
import com.example.notes.domain.DeleteNoteUseCase
import com.example.notes.domain.EditNoteUseCase
import com.example.notes.domain.GetNoteUseCase
import com.example.notes.domain.Note
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = EditNoteViewModel.Factory::class)
class EditNoteViewModel @AssistedInject constructor(
    @Assisted("noteId") private val noteId: Int,
    private val editNoteUseCase: EditNoteUseCase,
    private val getNoteUseCase: GetNoteUseCase,
    private val deleteNoteUseCase: DeleteNoteUseCase
): ViewModel() {

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId = noteId)
                EditNoteState.Editing(note)
            }
        }
    }

    fun processCommand(command: EditNoteCommand) {
        when(command) {
            EditNoteCommand.Back -> {
                _state.update { EditNoteState.Finished }
            }
            EditNoteCommand.Delete -> {
                viewModelScope.launch {
                    _state.update {previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            deleteNoteUseCase(note.id)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }
            is EditNoteCommand.InputContent -> {
                _state.update {previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newContent = previousState.note.content
                            .mapIndexed { index, contentItem ->
                                if (index == command.index && contentItem is ContentItem.Text) {
                                    contentItem.copy(content = command.content)
                                } else {
                                    contentItem
                                }
                            }
                        previousState.copy(note = previousState.note.copy(content = newContent))
                    } else {
                        previousState
                    }
                }
            }
            is EditNoteCommand.InputTitle -> {
                _state.update {previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newNote = previousState.note.copy(title = command.title)
                        previousState.copy(note = newNote)
                    } else {
                        previousState
                    }
                }
            }
            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update {previousState ->
                        if (previousState is EditNoteState.Editing) {
                            editNoteUseCase(previousState.note)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            is EditNoteCommand.AddImage -> {
                _state.update {previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newItems = previousState.note.content.toMutableList()
                        if (newItems.isEmpty()) {
                            newItems.add(ContentItem.Text(""))
                        }
                        newItems.add(ContentItem.Image(command.uri.toString()))
                        newItems.add(ContentItem.Text(""))
                        previousState.copy(note = previousState.note.copy(content = newItems))
                    } else {
                        previousState
                    }
                }
            }
            is EditNoteCommand.DeleteImage -> {
                _state.update {previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newItems = previousState.note.content.toMutableList()

                        if (command.index in newItems.indices && newItems[command.index] is ContentItem.Image) {
                            newItems.removeAt(command.index)

                            val nextItem = newItems.getOrNull(command.index)
                            if (nextItem is ContentItem.Text && nextItem.content.isEmpty()) {
                                newItems.removeAt(command.index)
                            }
                        }
                        previousState.copy(note = previousState.note.copy(content = newItems))
                    } else {
                        previousState
                    }
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("noteId") noteId: Int): EditNoteViewModel
    }
}

sealed interface EditNoteCommand {

    data class InputTitle(val title: String): EditNoteCommand

    data class InputContent(val content: String, val index: Int): EditNoteCommand

    data class AddImage(val uri: android.net.Uri): EditNoteCommand

    data class DeleteImage(val index: Int): EditNoteCommand

    data object Delete: EditNoteCommand

    data object Back: EditNoteCommand

    data object Save: EditNoteCommand
}

sealed interface EditNoteState {

    data object Initial: EditNoteState

    data class Editing (
        val note: Note
    ): EditNoteState {

        val isSaveEnabled: Boolean
            get() {
                return when {
                    note.title.isBlank() -> false
                    note.content.isEmpty() -> false
                    else -> {
                        note.content.any {
                            it !is ContentItem.Text || it.content.isNotBlank()
                        }
                    }
                }
            }
    }

    data object Finished: EditNoteState
}