package com.android.notepad.ui.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.notepad.R;
import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;
import com.android.notepad.ui.UiUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private final int TAKE_PHOTO = 1;
    private final int FROM_ALBUM = 2;
    private final int RECORD = 3;

    private Toolbar editToolbar;
    private ImageView imageView;
    private EditText edit;
    private Button playSoundBtn;
    private TextView timeAlterText;
    private ImageButton insertPhotoBtn;
    private ImageButton insertImageBtn;
    private ImageButton insertVoiceBtn;
    private ImageButton insertDrawBtn;
    private String tag;
    private Uri imageUri;
    private MediaPlayer mediaPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        editToolbar = (Toolbar) findViewById(R.id.edit_toolbar);
        imageView = (ImageView) findViewById(R.id.image_view);
        edit = (EditText) findViewById(R.id.edit);
        playSoundBtn = (Button) findViewById(R.id.play_sound_btn);
        timeAlterText = (TextView) findViewById(R.id.time_alter_text);
        insertPhotoBtn = (ImageButton) findViewById(R.id.insert_photo_btn);
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

        // 设置播放录音样式
        Drawable soundDrawable = getResources().getDrawable(R.drawable.icon_sound);
        soundDrawable.setBounds(0, 0, 60, 60);
        playSoundBtn.setCompoundDrawables(soundDrawable, null, null, null);

        // 若是个更新操作，将intent中的对象取出
        if ("update".equals(tag)) {
            int noteId = getIntent().getIntExtra("noteId", 1);
            Note note = Repository.getInstance().queryNoteById(noteId);
            edit.setText(note.getContent());
            // 若有存储图片则设置imageView
            if (note.getImage() != null) {
                try {
                    // 通过note.getImage()获取文件名，打开文件输入流
                    FileInputStream inputImage = openFileInput(note.getImage());
                    Bitmap bitmap = BitmapFactory.decodeStream(inputImage);
                    imageView.setImageBitmap(bitmap);
//                    Log.d("EditActivity", "imageView.width = " + imageView.getWidth());
                    UiUtil.adjustImageView(this, imageView, bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            timeAlterText.setText("修改时间：" + note.getTime());
        }

        // 设置控件的点击事件
        insertPhotoBtn.setOnClickListener(this);
        insertImageBtn.setOnClickListener(this);
        insertVoiceBtn.setOnClickListener(this);
        insertDrawBtn.setOnClickListener(this);
        playSoundBtn.setOnClickListener(this);
    }

    /**
     * 按下返回键的保存记事
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            try {
                saveNote();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 加载edit_toolbar.xml布局
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return true;
    }

    /**
     * 点击toolBar按钮事件
     * @param item
     * @return
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:     // 点击返回键事件
                Log.d("EditActivity", "home click");
                try {
                    saveNote();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                return true;
            case R.id.delete_note:      // 点击删除键事件
                int noteId = getIntent().getIntExtra("noteId", 0);
                if (noteId != 0) {
                    Repository.getInstance().deleteNote(noteId);
                }
                finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insert_photo_btn:
                File cameraImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (cameraImage.exists()) {
                        cameraImage.delete();
                    }
                    cameraImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    imageUri = FileProvider.getUriForFile(this,
                            "com.android.notepad.fileprovider", cameraImage);
                } else {
                    imageUri = Uri.fromFile(cameraImage);
                }
                // 启动相机程序
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, TAKE_PHOTO);
                break;
            case R.id.insert_image_btn:
                // 打开文件选择器
                Intent docIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                docIntent.addCategory(Intent.CATEGORY_OPENABLE);
                // 指定只显示图片
                docIntent.setType("image/*");
                startActivityForResult(docIntent, FROM_ALBUM);
                break;
            case R.id.insert_voice_btn:
                Intent recordIntent = new Intent(EditActivity.this, RecordActivity.class);
                startActivityForResult(recordIntent, RECORD);
                break;
            case R.id.insert_draw_btn:
                break;
            case R.id.play_sound_btn:
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // 从相机获取
            case TAKE_PHOTO: {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    imageView.setImageBitmap(bitmap);
                    UiUtil.adjustImageView(this, imageView, bitmap);    // 调整图片的大小
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            }
            // 从相册获取
            case FROM_ALBUM: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    imageUri = data.getData();
                    if (imageUri != null) {
                        try {
                            Bitmap bitmap = UiUtil.getBitmapFromUri(this, imageUri);
                            imageView.setImageBitmap(bitmap);
                            UiUtil.adjustImageView(this, imageView, bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            case RECORD: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    playSoundBtn.setVisibility(View.VISIBLE);
                    String soundFilePath = data.getStringExtra("soundFilePath");
                    try {
                        initMediaPlayer(soundFilePath);
                    } catch (IOException e) {
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
    private void saveNote() throws IOException {
        // 获取时间
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat ft = new SimpleDateFormat("yyyy年MM月dd日");
        long timestamp = System.currentTimeMillis();

        // 文件存储图片
        String imageFileName = null;
        if (imageUri != null) {
            imageFileName = String.valueOf(timestamp);
            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            FileOutputStream outputImage = openFileOutput(imageFileName, Context.MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputImage);
            outputImage.flush();
            outputImage.close();
        }


        // 判断是插入还是更新
        if ("insert".equals(tag)) {
            if (TextUtils.isEmpty(edit.getText())
                    || imageUri != null) {
                Note note = new Note();
                note.setContent(edit.getText().toString());
                note.setTime(ft.format(date));
                note.setTimestamp(timestamp);
                if (imageFileName != null) {
                    note.setImage(imageFileName);
                }

                Repository.getInstance().insertNote(note);
//                    Log.d("EditActivity", "insert");
            }
        } else if ("update".equals(tag)) {
            Note note = Repository.getInstance().queryNoteById(getIntent().getIntExtra("noteId", 0));
            if (TextUtils.isEmpty(edit.getText())
                    && note.getImage() == null) {
                Repository.getInstance().deleteNote(note.getId());
//                    Log.d("EditActivity", "delete");
            } else {
                note.setContent(edit.getText().toString());
                note.setTime(ft.format(date));
                note.setTimestamp(timestamp);
                if (imageFileName != null) {
                    note.setImage(imageFileName);
                }
                Repository.getInstance().updateNote(note);
//                    Log.d("EditActivity", "update");
            }
        }
    }

    private void initMediaPlayer(String soundFilePath) throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(soundFilePath);
        mediaPlayer.prepare();
    }

}