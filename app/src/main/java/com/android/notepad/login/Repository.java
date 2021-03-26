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

    public List<Note> queryNote() {
        return NoteDao.getInstance().queryNote();
    }
}
