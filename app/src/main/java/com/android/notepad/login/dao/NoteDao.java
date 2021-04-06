package com.android.notepad.login.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.notepad.login.model.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteDao {
    private static NoteDao noteDao;
    NoteDatabaseHelper noteDatabaseHelper;
    SQLiteDatabase db;
//    List<Note> noteList = new ArrayList<>();
    public static NoteDao getInstance() {
        if (noteDao == null) {
            noteDao = new NoteDao();
        }
        return noteDao;
    }
    public void createNoteDatabase(Context context) {
        noteDatabaseHelper = new NoteDatabaseHelper(context, "note_store.db", null, 1);
        db = noteDatabaseHelper.getWritableDatabase();
    }

    public void insertNote(Note note) {
        ContentValues values = new ContentValues();
        values.put("content", note.getContent());
        values.put("time", note.getTime().toString());
        db.insert("Note", null, values);
    }

    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put("content", note.getContent());
        values.put("time", note.getTime().toString());
        db.update("Note", values, "id = ?", new String[] { String.valueOf(note.getId()) });
    }

    public void deleteNote(Note note) {
        db.delete("Note", "id = ?", new String[] { String.valueOf(note.getId()) });
    }

    public List<Note> queryNote() {
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = db.query("Note", null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                Note note = new Note(id, content, time);
                noteList.add(note);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return noteList;
    }
}
