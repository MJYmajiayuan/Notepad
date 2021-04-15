package com.android.notepad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;

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

    public void insertNote(Note note) {
        Repository.getInstance().insertNote(note);
    }

    public List<Note> queryNote() {
        return Repository.getInstance().queryNote();
    }

    public Note queryNoteById(int noteId) {
        return Repository.getInstance().queryNoteById(noteId);
    }

    public List<Integer> queryNoteByContent(String content) {
        return Repository.getInstance().queryNoteByContent(content);
    }

    public void refreshNoteList(Context context, int noteId) {

        noteList.clear();
        noteList.addAll(queryNote());
        noteLiveData.setValue(noteList);

        noteBitmapMap.clear();
        for (Note note : noteList) {
            if (note.getImage() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(note.getImage());
                noteBitmapMap.put(note.getId(), bitmap);
            }
        }
        noteBitmapMapLiveData.setValue(noteBitmapMap);
    }
}
