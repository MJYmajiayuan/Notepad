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

    /**
     * 创建数据库
     * @param context
     */
    public void createNoteDatabase(Context context) {
        noteDatabaseHelper = new NoteDatabaseHelper(context, "note_store.db", null, 2);
        db = noteDatabaseHelper.getWritableDatabase();
    }

    /**
     * 插入数据
     * @param note
     */
    public void insertNote(Note note) {
        ContentValues values = new ContentValues();
        values.put("content", note.getContent());
        values.put("time", note.getTime().toString());
        values.put("timestamp", note.getTimestamp());
        db.insert("Note", null, values);
    }

    /**
     * 更新数据
     * @param note
     */
    public void updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put("content", note.getContent());
        values.put("time", note.getTime().toString());
        values.put("timestamp", note.getTimestamp());
        db.update("Note", values, "id = ?", new String[] { String.valueOf(note.getId()) });
    }

    /**
     * 删除数据
     * @param note
     */
    public void deleteNote(Note note) {
        db.delete("Note", "id = ?", new String[] { String.valueOf(note.getId()) });
    }

    /**
     * 查询数据
     * @return
     */
    public List<Note> queryNote() {
        List<Note> noteList = new ArrayList<>();
        Cursor cursor = db.query("Note", null, null, null, null, null, "timestamp");
        if (cursor.moveToLast()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                String time = cursor.getString(cursor.getColumnIndex("time"));
                long timestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
                Note note = new Note(id, content, time, timestamp);
                noteList.add(note);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return noteList;
    }
}
