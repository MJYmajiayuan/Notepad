package com.android.notepad;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel {
    MutableLiveData<List<Note>> noteLiveData = new MutableLiveData<>();
    List<Note> noteList = new ArrayList<>();

    public void createNoteDatabase(Context context) {
        Repository.getInstance().createNoteDatabase(context);
    }

    public void insertNote(Note note) {
        Repository.getInstance().insertNote(note);
    }

    public List<Note> queryNote() {
        return Repository.getInstance().queryNote();
    }

    public void refreshNoteList() {
        noteList.clear();
        noteList.addAll(queryNote());
        noteLiveData.setValue(noteList);
    }
}
