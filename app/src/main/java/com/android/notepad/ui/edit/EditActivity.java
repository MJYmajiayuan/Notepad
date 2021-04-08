package com.android.notepad.ui.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.notepad.R;
import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    final int FROM_ALBUM = 1;

    private Toolbar editToolbar;
    private ImageView imageView;
    private EditText edit;
    private TextView timeAlterText;
    private ImageButton insertImageBtn;
    private ImageButton insertVoiceBtn;
    private ImageButton insertDrawBtn;
    private String tag;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        imageView = (ImageView) findViewById(R.id.image_view);
        edit = (EditText) findViewById(R.id.edit);
        timeAlterText = (TextView) findViewById(R.id.time_alter_text);
        insertImageBtn = (ImageButton) findViewById(R.id.insert_image_btn);
        insertVoiceBtn = (ImageButton) findViewById(R.id.insert_voice_btn);
        insertDrawBtn = (ImageButton) findViewById(R.id.insert_draw_btn);
        tag = getIntent().getStringExtra("tag");

        // 设置toolbar的返回按钮
        setSupportActionBar(editToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 若是个更新操作，将intent中的对象取出
        if ("update".equals(tag)) {
            Note note = (Note) getIntent().getSerializableExtra("note");
            edit.setText(note.getContent());
            timeAlterText.setText("修改时间：" + note.getTime());
        }

        // 设置控件的点击事件
        insertImageBtn.setOnClickListener(this);
        insertVoiceBtn.setOnClickListener(this);
        insertDrawBtn.setOnClickListener(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saveNote();
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d("EditActivity", "home click");
                saveNote();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insert_image_btn:
                // 打开文件选择器
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                // 指定只显示图片
                intent.setType("image/*");
                startActivityForResult(intent, FROM_ALBUM);
                break;
            case R.id.insert_voice_btn:
                break;
            case R.id.insert_draw_btn:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 从相册获取
            case FROM_ALBUM:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageUri = data.getData();
                    if (imageUri != null) {
                        try {
                            Bitmap bitmap = getBitmapFromUri(imageUri);
                            imageView.setImageBitmap(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                }
                break;
        }
    }

    /**
     * 保存笔记
     */
    private void saveNote() {
        // 获取时间
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat ft = new SimpleDateFormat("yyyy年MM月dd日");
        long timestamp = System.currentTimeMillis();

        // 判断是插入还是更新
        if ("insert".equals(tag)) {
            if (edit.getText() != null && !"".equals(edit.getText().toString())) {
                Note note = new Note();
                note.setContent(edit.getText().toString());
                note.setTime(ft.format(date));
                note.setTimestamp(timestamp);
                Repository.getInstance().insertNote(note);
//                    Log.d("EditActivity", "insert");
            }
        } else if ("update".equals(tag)) {
            Note note = (Note) getIntent().getSerializableExtra("note");
            if (edit.getText() == null || "".equals(edit.getText().toString())) {
                Repository.getInstance().deleteNote(note);
//                    Log.d("EditActivity", "delete");
            } else {
                note.setContent(edit.getText().toString());
                note.setTime(ft.format(date));
                note.setTimestamp(timestamp);
                Repository.getInstance().updateNote(note);
//                    Log.d("EditActivity", "update");
            }
        }
    }

    // 获取Bitmap
    private Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException {
        Bitmap bitmap = null;
        if (getContentResolver().openFileDescriptor(uri, "r") != null) {
            bitmap = BitmapFactory.decodeFileDescriptor(
                    getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
        }
        return bitmap;
    }

}