package com.android.notepad.login.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.android.notepad.login.model.Tag;

import java.util.ArrayList;
import java.util.List;

public class TagDao {

    private static TagDao tagDao;
    private SQLiteDatabase db;

    public static TagDao getInstance() {
        if (tagDao == null) {
            tagDao = new TagDao();
        }
        return tagDao;
    }

    /**
     * 创建数据库
     * @param context
     */
    public void createNoteDatabase(Context context) {
        db = DatabaseUtils.getDatabase(context);
    }

    /**
     * 添加新标签
     * @param tag 标签对象
     */
    public void insertTag(Tag tag) {
        ContentValues values = new ContentValues();
        values.put("tag_name", tag.getTagName());
        values.put("tag_num", tag.getTagNum());
        db.insert("Tag", null, values);
    }

    /**
     * 查询所有标签
     * @return 标签对象列表
     */
    public List<Tag> queryTag() {
        List<Tag> tagList = new ArrayList<>();
        Cursor cursor = db.query("Tag", null, null, null, null, null, "tag_id");
        if (cursor.moveToFirst()) {
            do {
                int tagId = cursor.getInt(cursor.getColumnIndex("tag_id"));
                String tagName = cursor.getString(cursor.getColumnIndex("tag_name"));
                int tagNum = cursor.getInt(cursor.getColumnIndex("tag_num"));
                tagList.add(new Tag(tagId, tagName, tagNum));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return tagList;
    }
}
