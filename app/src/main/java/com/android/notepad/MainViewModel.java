package com.android.notepad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;
import com.android.notepad.login.model.Tag;
import com.android.notepad.ui.UiUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {
    MutableLiveData<List<Note>> noteLiveData = new MutableLiveData<>();
    MutableLiveData<Map<Integer, Bitmap>> noteBitmapMapLiveData = new MutableLiveData<>();
    List<Note> noteList = new ArrayList<>();
    Map<Integer, Bitmap> noteBitmapMap = new HashMap<>();

    public void createNoteDatabase(Context context) {
        Repository.getInstance().createNoteDatabase(context);
    }

    /**
     * 插入记事
     * @param note
     */
    public void insertNote(Note note) {
        Repository.getInstance().insertNote(note);
    }

    /**
     * 查询记事
     * @return
     */
    public List<Note> queryNote() {
        return Repository.getInstance().queryNote();
    }

    /**
     * 通过id查询记事
     * @param noteId
     * @return
     */
    public Note queryNoteById(int noteId) {
        return Repository.getInstance().queryNoteById(noteId);
    }

    /**
     * 通过内容查询记事
     * @param content
     * @return
     */
    public List<Note> queryNoteByContent(String content) {
        return Repository.getInstance().queryNoteByContent(content);
    }

    /**
     * 通过标签查询记事
     * @param tagId
     * @return
     */
    public List<Note> queryTagByTag(int tagId) {
        return Repository.getInstance().queryNoteByTag(tagId);
    }

    /**
     * 查询所有标签
     * @return
     */
    public List<Tag> queryTag() {
        return Repository.getInstance().queryTag();
    }

    /**
     * 插入标签
     * @param tag
     */
    public void insertTag(Tag tag) {
        Repository.getInstance().insertTag(tag);
    }


    public void refreshNoteList(Context context) {

        noteList.clear();
        noteList.addAll(queryNote());
        noteLiveData.setValue(noteList);

        noteBitmapMap.clear();
        for (Note note : noteList) {
            if (note.getImage() != null) {
//                Bitmap bitmap = BitmapFactory.decodeFile(note.getImage());
                Bitmap bitmap = UiUtil.decodeSampledBitmapFromFile(note.getImage(), 200, 200);
                noteBitmapMap.put(note.getId(), bitmap);
            }
        }
        noteBitmapMapLiveData.setValue(noteBitmapMap);
    }

    public void refreshNoteListBySearch(Context context, String content) {
        noteList.clear();
        noteList.addAll(queryNoteByContent(content));
        noteLiveData.setValue(noteList);

        noteBitmapMap.clear();
        for (Note note : noteList) {
            if (note.getImage() != null) {
//                Bitmap bitmap = BitmapFactory.decodeFile(note.getImage());
                Bitmap bitmap = UiUtil.decodeSampledBitmapFromFile(note.getImage(), 200, 200);
                noteBitmapMap.put(note.getId(), bitmap);
            }
        }
        noteBitmapMapLiveData.setValue(noteBitmapMap);
    }

    public void refreshNoteListByTag(Context context, int tagId) {
        noteList.clear();
        noteList.addAll(queryTagByTag(tagId));
        noteLiveData.setValue(noteList);

        noteBitmapMap.clear();
        for (Note note : noteList) {
            if (note.getImage() != null) {
//                Bitmap bitmap = BitmapFactory.decodeFile(note.getImage());
                Bitmap bitmap = UiUtil.decodeSampledBitmapFromFile(note.getImage(), 200, 200);
                noteBitmapMap.put(note.getId(), bitmap);
            }
        }
        noteBitmapMapLiveData.setValue(noteBitmapMap);
    }
}
