package com.android.notepad.login;

import android.content.Context;

import com.android.notepad.login.dao.NoteDao;
import com.android.notepad.login.model.Note;

import java.util.List;

public class Repository {
    private static Repository repository = null;
    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public void createNoteDatabase(Context context) {
        NoteDao.getInstance().createNoteDatabase(context);
    }

    public void insertNote(Note note) {
        NoteDao.getInstance().insertNote(note);
    }

    public void updateNote(Note note) {
        NoteDao.getInstance().updateNote(note);
    }

    public void deleteNote(int noteId) {
        NoteDao.getInstance().deleteNote(noteId);
    }

    public List<Note> queryNote() {
        return NoteDao.getInstance().queryNote();
    }

    public Note queryNoteById(int id) {
        return NoteDao.getInstance().queryNoteById(id);
    }
}
