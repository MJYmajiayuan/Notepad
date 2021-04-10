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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.notepad.R;
import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;

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

    private Toolbar editToolbar;
    private ImageView imageView;
    private EditText edit;
    private TextView timeAlterText;
    private ImageButton insertPhotoBtn;
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
                    adjustImageView(this, imageView, bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//                Bitmap bitmap = BitmapFactory.decodeByteArray(note.getImage(), 0, note.getImage().length);
            }

            timeAlterText.setText("修改时间：" + note.getTime());
        }

        // 设置控件的点击事件
        insertPhotoBtn.setOnClickListener(this);
        insertImageBtn.setOnClickListener(this);
        insertVoiceBtn.setOnClickListener(this);
        insertDrawBtn.setOnClickListener(this);

    }

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
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insert_photo_btn:
                // 以当前的年月日时分秒作为文件名
//                Date date = new Date();
//                @SuppressLint("SimpleDateFormat")
//                SimpleDateFormat ft = new SimpleDateFormat("IMG_yyyy-MM-dd-HH-mm-ss");
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
                break;
            case R.id.insert_draw_btn:
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
                    adjustImageView(this, imageView, bitmap);

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
                            Bitmap bitmap = getBitmapFromUri(imageUri);
                            imageView.setImageBitmap(bitmap);
                            adjustImageView(this, imageView, bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
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

        // 将图片转化成字节流
//        byte[] imageByteArray = null;
//        if (imageUri != null) {
//            Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//            final ByteArrayOutputStream os = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
//            imageByteArray = os.toByteArray();
//        }

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
            if (edit.getText() != null && !"".equals(edit.getText().toString())) {
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
            Note note = Repository.getInstance().queryNoteById(getIntent().getIntExtra("noteId", 1));
            if (edit.getText() == null || "".equals(edit.getText().toString())) {
                Repository.getInstance().deleteNote(note);
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

    /**
     * 获取bitmap
     * @param uri
     * @return
     * @throws FileNotFoundException
     */
    private Bitmap getBitmapFromUri(Uri uri) throws FileNotFoundException {
        Bitmap bitmap = null;
        if (getContentResolver().openFileDescriptor(uri, "r") != null) {
            bitmap = BitmapFactory.decodeFileDescriptor(
                    getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
        }
        return bitmap;
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    private int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    private int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    /**
     * 根据手机的分辨率从px转成为dp
     * @param context
     * @param pxValue
     * @return
     */
    private int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 调整ImageView的大小，使之根据图片的大小调整
     * @param context
     * @param imageView
     * @param bitmap
     */
    private void adjustImageView(Context context, ImageView imageView, Bitmap bitmap) {
        if (bitmap.getWidth() > getScreenWidth(this)) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        } else {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        float newHeight = (float) getScreenWidth(context) / (float) bitmap.getWidth() * bitmap.getHeight();
        layoutParams.height = (int) newHeight;
        imageView.setLayoutParams(layoutParams);
    }
}