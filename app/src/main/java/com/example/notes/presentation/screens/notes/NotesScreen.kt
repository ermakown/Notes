package com.example.notes.presentation.screens.notes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.Green
import com.example.notes.presentation.ui.theme.OtherNotesColors
import com.example.notes.presentation.ui.theme.PinnedNotesColors
import com.example.notes.presentation.ui.theme.Yellow200
import com.example.notes.presentation.utils.DateFormatter

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
    ) {
        item {
            Title(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "All notes"
            )
        }
        item {
            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
        item {
            SearchBar(
                modifier = Modifier.padding(horizontal = 24.dp),
                query = state.query,
                onQueryChange = {
                    viewModel.processCommand(NoteCommands.InputSearchQuery(it))
                }
            )
        }
        item {
            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
        item {
            Subtitle(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Pinned"
            )
        }
        item {
            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
        item {
            LazyRow(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(24.dp)
            ) {
                itemsIndexed(
                    items = state.pinnedNotes,
                    key = { _, note -> note.id }
                ) {index, note ->
                    NoteCard(
                        modifier = Modifier
                            .widthIn(max = 160.dp),
                        note = note,
                        onClick = {
                            viewModel.processCommand(NoteCommands.EditNote(it))
                        },
                        onDoubleClick = {
                            viewModel.processCommand(NoteCommands.DeleteNote(it.id))
                        },
                        onLongClick = {
                            viewModel.processCommand(NoteCommands.SwitchPinnedStatus(it.id))
                        },
                        backGroundColor = PinnedNotesColors[index % PinnedNotesColors.size]
                    )
                }
            }
        }
        item {
            Spacer(
                modifier = Modifier.height(24.dp)
            )
        }
        item {
            Subtitle(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = "Others"
            )
        }
        item {
            Spacer(
                modifier = Modifier.height(16.dp)
            )
        }
        itemsIndexed(
            items = state.otherNotes,
            key = { _, note -> note.id }
        ) {index, note ->
            NoteCard(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                note = note,
                backGroundColor = OtherNotesColors[index % OtherNotesColors.size],
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
            Spacer(
                modifier = Modifier.height(8.dp)
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
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
        value = query,
        onValueChange = onQueryChange,
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
                contentDescription = "Search Note",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor= MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,

        )
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
            .padding(16.dp)
    )
    {
        Text(
            text = note.title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(
            modifier = Modifier.height(8.dp)
        )
        Text(
            text = DateFormatter.formatToString(note.updatedAt),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = note.content,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }

}