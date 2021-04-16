package com.android.notepad.login.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseUtils {
    /**
     * 创建数据库
     * @param context
     */
    public static SQLiteDatabase getDatabase(Context context) {
        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(context, "note_store.db", null, 1);
        return noteDatabaseHelper.getWritableDatabase();
    }
}
