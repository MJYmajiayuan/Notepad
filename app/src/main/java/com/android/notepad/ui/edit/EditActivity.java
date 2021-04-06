package com.android.notepad.ui.edit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import com.android.notepad.R;
import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity {

    private EditText edit;
    String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        edit = (EditText) findViewById(R.id.edit);
        tag = getIntent().getStringExtra("tag");

        // 若是个更新操作，将intent中的对象取出
        if ("update".equals(tag)) {
            Note note = (Note) getIntent().getSerializableExtra("note");
            edit.setText(note.getContent());
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            // 获取时间
            Date date = new Date();
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");

            // 判断是插入还是更新
            if ("insert".equals(tag)) {
                if (edit.getText() != null && !"".equals(edit.getText().toString())) {
                    Note note = new Note();
                    note.setContent(edit.getText().toString());
                    note.setTime(ft.format(date));
                    Repository.getInstance().insertNote(note);
//                    Log.d("EditActivity", "insert");
                }
            } else if ("update".equals(tag)) {
                Note note = (Note) getIntent().getSerializableExtra("note");
                if (edit.getText() == null || "".equals(edit.getText().toString())) {
                    Repository.getInstance().deleteNote(note);
                    Log.d("EditActivity", "delete");
                } else {
                    note.setContent(edit.getText().toString());
                    note.setTime(ft.format(date));
                    Repository.getInstance().updateNote(note);
//                    Log.d("EditActivity", "update");
                }
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}