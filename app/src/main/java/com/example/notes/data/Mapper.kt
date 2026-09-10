package com.example.notes.data

import com.example.notes.domain.ContentItem
import com.example.notes.domain.Note
import kotlinx.serialization.json.Json

fun Note.toDbModel(): NoteDbModel {
    val contentAsString = Json.encodeToString(content.toContentItemDbModel())
    return NoteDbModel(id, title, contentAsString, updatedAt, isPinned)
}

fun List<ContentItem>.toContentItemDbModel(): List<ContentItemDbModel> {
    return map {contentItem ->
        when(contentItem) {
            is ContentItem.Image -> {
                ContentItemDbModel.Image(contentItem.url)
            }
            is ContentItem.Text -> {
                ContentItemDbModel.Text(contentItem.content)
            }
        }
    }
}

fun List<ContentItemDbModel>.toContentItem(): List<ContentItem> {
    return map {contentItemDbModel ->
        when(contentItemDbModel) {
            is ContentItemDbModel.Image -> {
                ContentItem.Image(contentItemDbModel.url)
            }
            is ContentItemDbModel.Text -> {
                ContentItem.Text(contentItemDbModel.content)
            }
        }
    }
}

fun NoteDbModel.toEntity(): Note {
    val contentItem = Json.decodeFromString<List<ContentItemDbModel>>(content).toContentItem()
    return Note(id, title, contentItem, updatedAt, isPinned)
}

fun List<NoteDbModel>.toEntities(): List<Note> {
    return this.map { it.toEntity() }
}