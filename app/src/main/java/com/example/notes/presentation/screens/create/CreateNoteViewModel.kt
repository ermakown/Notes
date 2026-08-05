package com.example.notes.presentation.screens.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes.data.TestNotesRepositoryImpl
import com.example.notes.domain.AddNoteUseCase
import com.example.notes.presentation.screens.creation.CreateNoteState.Creation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel: ViewModel() {

    private val repository = TestNotesRepositoryImpl
    private val addNoteUseCase = AddNoteUseCase(repository)

    private val _state = MutableStateFlow<CreateNoteState>(Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {
        when(command) {
            CreateNoteCommand.Back -> {
                _state.update {
                    CreateNoteState.Finished
                }
            }
            is CreateNoteCommand.InputContent -> {
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
            is CreateNoteCommand.InputTitle -> {
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
            CreateNoteCommand.Save -> {
                viewModelScope.launch {
                    _state.update {previousState ->
                        if (previousState is Creation) {
                            val title = previousState.title
                            val content = previousState.content
                            addNoteUseCase(title, content)
                            CreateNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }
        }
    }
}

sealed interface CreateNoteCommand {

    data class InputTitle(val title: String): CreateNoteCommand

    data class InputContent(val content: String): CreateNoteCommand

    data object Save: CreateNoteCommand

    data object Back: CreateNoteCommand
}

sealed interface CreateNoteState {

    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnabled: Boolean = false
    ): CreateNoteState

    data object Finished: CreateNoteState
}