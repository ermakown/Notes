package com.example.notes.presentation.screens.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.NotesRepositoryImpl
import com.example.notes.data.TestNotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.presentation.screens.creation.EditNoteState.Creation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateNoteViewModel @Inject constructor(
    private val addNoteUseCase: AddNoteUseCase
): ViewModel() {
    private val _state = MutableStateFlow<EditNoteState>(Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: EditNoteCommand) {
        when(command) {
            EditNoteCommand.Back -> {
                _state.update {
                    EditNoteState.Finished
                }
            }
            is EditNoteCommand.InputContent -> {
                _state.update {previousState ->
                    if (previousState is Creation) {
                        previousState.copy(
                            content = command.content,
                            isSaveEnabled = previousState.title.isNotBlank() && command.content.isNotBlank()
                        )
                    } else {
                        Creation(content = command.content)
                    }
                }
            }
            is EditNoteCommand.InputTitle -> {
                _state.update {previousState ->
                    if (previousState is Creation) {
                        previousState.copy(
                            title = command.title,
                            isSaveEnabled = command.title.isNotBlank() && previousState.content.isNotBlank()
                        )
                    } else {
                        Creation(title = command.title)
                    }
                }
            }
            EditNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update {previousState ->
                        if (previousState is Creation) {
                            val title = previousState.title
                            val content = previousState.content
                            addNoteUseCase(title, content)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }
        }
    }
}

sealed interface EditNoteCommand {

    data class InputTitle(val title: String): EditNoteCommand

    data class InputContent(val content: String): EditNoteCommand

    data object Save: EditNoteCommand

    data object Back: EditNoteCommand
}

sealed interface EditNoteState {

    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnabled: Boolean = false
    ): EditNoteState

    data object Finished: EditNoteState
}