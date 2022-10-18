package com.gdevs.noteapp.di

import android.app.Application
import androidx.room.Room
import com.gdevs.noteapp.feature_note.data.data_source.NoteDataBase
import com.gdevs.noteapp.feature_note.data.repository.NoteRepositoryImplementation
import com.gdevs.noteapp.feature_note.domain.repository.NoteRepository
import com.gdevs.noteapp.feature_note.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Singleton
    fun provideNoteDataBase(app: Application): NoteDataBase {
        return Room.inMemoryDatabaseBuilder(
            app,
            NoteDataBase::class.java,
        ).build()
    }

    @Provides
    @Singleton
    fun provideNoteRepository(db: NoteDataBase): NoteRepository {
        return NoteRepositoryImplementation(db.noteDao)
    }

    @Provides
    @Singleton
    fun provideNoteUseCases(repository: NoteRepository): NoteUseCases {
        return NoteUseCases(
            getNotes = GetNotes(repository),
            deleteNote = DeleteNote(repository),
            addNote = AddNote(repository),
            getNote = GetNote(repository)
        )
    }
}