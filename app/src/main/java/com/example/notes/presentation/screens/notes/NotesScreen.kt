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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notes.R
import com.example.notes.domain.Note
import com.example.notes.presentation.ui.theme.Brown
import com.example.notes.presentation.ui.theme.NotesTheme
import com.example.notes.presentation.ui.theme.OtherNotesColors
import com.example.notes.presentation.ui.theme.PinnedNotesColors
import com.example.notes.presentation.utils.DateFormatter

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    viewModel: NotesViewModel = viewModel(),
    onNoteClick: (Note) -> Unit,
    onFloatingActionButtonClick: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFloatingActionButtonClick,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_add_note),
                    contentDescription = "Button add note"
                )
            }
        }
    ) { innerPadding ->
        if (state.otherNotes.isEmpty() && state.pinnedNotes.isEmpty()) {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize(),
                contentPadding = innerPadding
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
                        modifier = Modifier.height(225.dp)
                    )
                }
                item {
                    Text(
                        modifier = Modifier
                            .padding(24.dp),
                        text = "No notes yet. Add the first one.",
                        textAlign = TextAlign.Center,
                        fontSize = 35.sp,
                        color = Brown,
                        lineHeight = 40.sp
                    )
                }
            }
        }
        else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize(),
                contentPadding = innerPadding
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
                item{
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(24.dp)
                    ){
                        itemsIndexed(
                            items = state.pinnedNotes,
                            key = { _, note -> note.id }
                        ) {index, note ->
                            NoteCard(
                                modifier = Modifier
                                    .widthIn(max = 160.dp),
                                note = note,
                                onShortClick = onNoteClick,
                                onLongClick = {
                                    viewModel.processCommand(NoteCommands.SwitchPinnedStatus(it.id))
                                },
                                backgroundColor = PinnedNotesColors[index % PinnedNotesColors.size]
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        note = note,
                        onShortClick = onNoteClick,
                        onLongClick = {
                            viewModel.processCommand(NoteCommands.SwitchPinnedStatus(it.id))
                        },
                        backgroundColor = OtherNotesColors[index % OtherNotesColors.size]
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNotesScreen() {
    NotesTheme {
        NotesScreen(
            onNoteClick = {},
            onFloatingActionButtonClick = {}
        )
    }
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

// Note search field function
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onQueryChange,
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
                contentDescription = "Search Notes",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}

// Subtitle function
@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String
){
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// Card with note function
@Composable
fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    onShortClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .combinedClickable(
                onClick = { onShortClick(note) },
                onLongClick = { onLongClick(note) }
            )
            .padding(16.dp)
    ) {
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
            text = DateFormatter.formatDateToString(note.updatedAt),
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