package com.android.notepad.login.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    private String createBook = "create table Note (" +
            "id integer primary key autoincrement," +
            "content text," +
            "time text," +
            "timestamp bigint," +
            "image blob)";

    public NoteDatabaseHelper(@Nullable Context context, @Nullable String name,
                              @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createBook);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion <= 1) {
            db.execSQL("alter table Note add column timestamp bigint");
        }
        if (oldVersion <= 2) {
            db.execSQL("alter table Note add column image blob");
        }
    }
}
