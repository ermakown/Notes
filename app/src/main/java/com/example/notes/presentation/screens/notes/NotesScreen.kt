package com.example.notes.presentation.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.Green
import com.example.notes.presentation.ui.theme.Yellow200

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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Title(text = "All notes")
        }
        item {
            SearchBar(
                query = state.query,
                onQueryChange = {
                    viewModel.processCommand(NoteCommands.InputSearchQuery(it))
                }
            )
        }
        item {
            Subtitle(text = "Pinned")
        }
        item {
            LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(
                    items = state.pinnedNotes,
                    key = { it.id }
                ) {note ->
                    NoteCard(
                        note = note,
                        backGroundColor = Yellow200,
                        onClick = {
                            viewModel.processCommand(NoteCommands.EditNote(it))
                        },
                        onDoubleClick = {
                            viewModel.processCommand(NoteCommands.DeleteNote(it.id))
                        },
                        onLongClick = {
                            viewModel.processCommand(NoteCommands.SwitchPinnedStatus(it.id))
                        }
                    )
                }
            }
        }
        item {
            Subtitle(text = "Others")
        }
        items(
            items = state.otherNotes,
            key = { it.id }
        ) {note ->
            NoteCard(
                modifier = modifier.fillMaxWidth(),
                note = note,
                backGroundColor = Green,
                onClick = {
                    viewModel.processCommand(NoteCommands.EditNote(it))
                },
                onDoubleClick = {
                    viewModel.processCommand(NoteCommands.DeleteNote(it.id))
                },
                onLongClick = {
                    viewModel.processCommand(NoteCommands.SwitchPinnedStatus(it.id))
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

// Title function
@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

// SearchBar function
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onQueryChange: (String) -> Unit
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Note"
            )
        }
    )
}

// Subtitle function
@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// 1 click - edit, long - switch, 2 click = delete
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backGroundColor: Color,
    onClick: (Note) -> Unit,
    onDoubleClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit
    ) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backGroundColor)
            .combinedClickable(
                onClick = { onClick(note) },
                onDoubleClick = { onDoubleClick(note) },
                onLongClick = { onLongClick(note) }
            )
    )
    {
        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = note.updatedAt.toString(),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = note.content,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }

}