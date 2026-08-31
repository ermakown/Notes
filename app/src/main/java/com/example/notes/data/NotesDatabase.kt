package com.example.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [NoteDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class NotesDatabase: RoomDatabase() {

    abstract fun notesDao(): NotesDao
}