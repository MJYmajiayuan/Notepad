package com.android.notepad.ui.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;

import com.android.notepad.R;
import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        edit = (EditText) findViewById(R.id.edit);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (edit.getText() != null && !"".equals(edit.getText().toString())) {
                Note note = new Note();
                Date date = new Date();
                @SuppressLint("SimpleDateFormat")
                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                note.setContent(edit.getText().toString());
                note.setTime(ft.format(date));
                Repository.getInstance().insertNote(note);
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}