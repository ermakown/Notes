package com.example.notes.presentation.screens.notes

import android.R.attr.text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.domain.Note

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        item {
            LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = state.pinnedNotes,
                    key = { it.id }
                ) {note ->
                    NotesCard(
                        note = note,
                        onNoteClick = {
                            viewModel.processCommand(NoteCommands.SwitchPinnedStatus(note.id))
                        }
                    )
                }
            }
        }
        items(
            items = state.otherNotes,
            key = { it.id }
        ) {note ->
            NotesCard(
                note = note,
                onNoteClick = {
                    viewModel.processCommand(NoteCommands.SwitchPinnedStatus(note.id))
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNoteScreen() {
    NotesScreen()
}

@Composable
fun NotesCard(
    modifier: Modifier = Modifier,
    note: Note,
    onNoteClick: (Note) -> Unit
    ) {
    Text(
        modifier = modifier
            .clickable {
                onNoteClick(note)
            },
        text = "${note.title} – ${note.content}",
        fontSize = if (note.isPinned) 30.sp else 24.sp
    )
}