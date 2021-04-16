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
            "sound text," +
            "tag_id integer," +
            "foreign key (tag_id) references Tag(tag_id))";

    private String createTag = "create table Tag (" +
            "tag_id integer primary key autoincrement," +
            "tag_name text," +
            "tag_num integer)";

    public NoteDatabaseHelper(@Nullable Context context, @Nullable String name,
                              @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTag);
        db.execSQL(createNote);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
