package com.android.notepad.login.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private String createNote = "create table Note (" +
            "id integer primary key autoincrement," +
            "content text," +
            "time text," +
            "timestamp bigint," +
            "image text," +
            "sound text)";

    private String createVirtualTable = "create virtual table VirtualNote using fts3 (id, content)";

    private String initVirtualTable = "insert into VirtualNote(id, content)" +
            "select id, content from Note";

    public NoteDatabaseHelper(@Nullable Context context, @Nullable String name,
                              @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createNote);
//        db.execSQL(createVirtualTable);
//        db.execSQL(initVirtualTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 1) {
            db.execSQL("alter table Note add column sound text");
        }
    }
}
