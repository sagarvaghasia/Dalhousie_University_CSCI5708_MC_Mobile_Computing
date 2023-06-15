package com.example.labassignment03.persistence

import com.example.labassignment03.model.Note

object Database {

    init {
        println("Database created....")
    }

    public var notes: MutableList<Note> = mutableListOf()

    public fun addNotesToList(note: Note){
        notes.add(note)
    }
}