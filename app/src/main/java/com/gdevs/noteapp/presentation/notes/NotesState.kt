package com.gdevs.noteapp.presentation.notes

import com.gdevs.noteapp.feature_note.domain.model.Note
import com.gdevs.noteapp.feature_note.domain.util.NoteOrder
import com.gdevs.noteapp.feature_note.domain.util.OrderType

data class NotesState(

    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false

)
