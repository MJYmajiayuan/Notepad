package com.android.notepad.login.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.notepad.login.model.Note;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NoteDao {
    private static NoteDao noteDao;
    NoteDatabaseHelper noteDatabaseHelper;
    SQLiteDatabase db;
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
        values.put("image", note.getImage());
        values.put("sound", note.getSound());
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
        values.put("image", note.getImage());
        values.put("sound", note.getSound());
        db.update("Note", values, "id = ?", new String[] { String.valueOf(note.getId()) });
    }

    /**
     * 删除数据
     * @param noteId
     */
    public void deleteNote(int noteId) {
        // 这里需要添加删除图片文件的功能
        db.delete("Note", "id = ?", new String[] { String.valueOf(noteId) });
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
                String image = cursor.getString(cursor.getColumnIndex("image"));
                String sound = cursor.getString(cursor.getColumnIndex("sound"));
                Note note = new Note(id, content, time, timestamp, image, sound);
                noteList.add(note);
            } while (cursor.moveToPrevious());
        }
        cursor.close();
        return noteList;
    }

    /**
     * 通过id查询
     * @param id
     * @return
     */
    public Note queryNoteById(int id) {
        Note note = new Note();
        Cursor cursor = db.query("Note", null, "id=?",
                new String[] { String.valueOf(id) }, null, null, null);
        if (cursor.moveToFirst()) {
            note.setId(cursor.getInt(cursor.getColumnIndex("id")));
            note.setContent(cursor.getString(cursor.getColumnIndex("content")));
            note.setTime(cursor.getString(cursor.getColumnIndex("time")));
            note.setTimestamp(cursor.getLong(cursor.getColumnIndex("timestamp")));
            note.setImage(cursor.getString(cursor.getColumnIndex("image")));
            note.setSound(cursor.getString(cursor.getColumnIndex("sound")));
        }
        cursor.close();
        return note;
    }

    /**
     * 使用FTS3查询指定内容
     * @param content
     * @return
     */
    public List<Integer> queryNoteByContent(String content) {
        List<Integer> noteIdList = new ArrayList<>();
        Cursor cursor;
        try {
            db.execSQL("delete from VirtualNote");
            db.execSQL("insert into VirtualNote(id, content) select id, content from Note");
            cursor = db.rawQuery("select id from VirtualNote" ,null);
            cursor = db.rawQuery("select id from VirtualNote where content like ?", new String[] { "%" + content + "%" });
//            cursor = db.query("VirtualNote", null, "content match ?", new String[] { content }, null, null, null);
        } catch (SQLException e) {
            db.execSQL("create virtual table VirtualNote using fts3 (id, content)");
            db.execSQL("insert into VirtualNote(id, content) select id, content from Note");
            cursor = db.rawQuery("select id from VirtualNote where content like ?", new String[] { "%" + content + "%" });
//            cursor = db.query("VirtualNote", null, "content match ?", new String[] { content }, null, null, null);
        }
        if (cursor.moveToFirst()) {
            do {
                noteIdList.add(cursor.getInt(cursor.getColumnIndex("id")));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return noteIdList;
    }
}
