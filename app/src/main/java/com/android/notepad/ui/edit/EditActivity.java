package com.android.notepad.ui.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.notepad.R;
import com.android.notepad.broadcast.AlarmReceiver;
import com.android.notepad.login.Repository;
import com.android.notepad.login.model.Note;
import com.android.notepad.ui.UiUtil;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectChangeListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

    private final int TAKE_PHOTO = 1;
    private final int FROM_ALBUM = 2;
    private final int RECORD = 3;
    private final int PAINT = 4;
    // 用于申请录音权限
    private final int REQUEST_RECORD_AUDIO = 1;

    private Note note = null;   // 全局Note对象
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
    private String soundFilePath = null;
    private String picFilePath = null;  // 图片文件名，包括照片和手绘
    private TimePickerView pvTime;      // 第三方库的时间选择器对象
    private int alarmSendCode = 0;
//    private AlarmReceiver alarmReceiver;    // 广播接收器


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
        playSoundBtn.setCompoundDrawables(null, soundDrawable, null, null);

        // 初始化时间选择器对象
        initTimePicker();

        // 若是个更新操作，将intent中的对象取出
        if ("update".equals(tag)) {
            int noteId = getIntent().getIntExtra("noteId", 1);
            note = Repository.getInstance().queryNoteById(noteId);
            edit.setText(note.getContent());
            // 若有存储图片则设置imageView
            if (!TextUtils.isEmpty(note.getImage())) {
                Bitmap bitmap = BitmapFactory.decodeFile(note.getImage());
                imageView.setImageBitmap(bitmap);
                UiUtil.adjustImageView(this, imageView, bitmap);
                String[] imageFilePathArr = note.getImage().split("/");
                // 若该图片是手绘
                if ("paint".equals(imageFilePathArr[imageFilePathArr.length - 2])) {
                    Log.d("EditActivity", "is paint");
                    imageView.setOnClickListener(this);
                }
            }
            // 若有录音则显示出来
            if (note.getSound() != null) {
                try {
                    initMediaPlayer(note.getSound());
                    playSoundBtn.setVisibility(View.VISIBLE);
                    int duration = mediaPlayer.getDuration();
                    playSoundBtn.setText(duration / 1000 + "''");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            timeAlterText.setText("修改时间：" + note.getTime());
        } else {
            note = new Note();
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
            case android.R.id.home: {   // 点击返回键事件
                Log.d("EditActivity", "home click");
                try {
                    saveNote();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                return true;
            }
            case R.id.add_tag: {        // 点击添加标签按钮事件
                int noteId = getIntent().getIntExtra("noteId", 0);
                Intent intent = new Intent(EditActivity.this, TagActivity.class);
                intent.putExtra("noteId", noteId);
                startActivity(intent);
                break;
            }
            case R.id.add_alarm: {      // 点击添加提醒按钮事件
                pvTime.show();
                break;
            }
            case R.id.delete_note: {    // 点击删除键事件
                int noteId = getIntent().getIntExtra("noteId", 0);
                if (noteId != 0) {
                    Repository.getInstance().deleteNote(noteId);
                }
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.insert_photo_btn:     // 点击相机按钮
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
            case R.id.insert_image_btn:     // 点击图片按钮
                // 打开文件选择器
                Intent docIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                docIntent.addCategory(Intent.CATEGORY_OPENABLE);
                // 指定只显示图片
                docIntent.setType("image/*");
                startActivityForResult(docIntent, FROM_ALBUM);
                break;
            case R.id.insert_voice_btn:     // 点击语音按钮
                // 动态申请录音权限
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[] { Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
                } else {
                    Intent recordIntent = new Intent(EditActivity.this, RecordActivity.class);
                    startActivityForResult(recordIntent, RECORD);
                }
                break;
            case R.id.insert_draw_btn:      // 点击画图按钮
                Intent paintIntent = new Intent(EditActivity.this, PaintActivity.class);
                startActivityForResult(paintIntent, PAINT);
                break;
            case R.id.play_sound_btn:       // 点击播放语音按钮
                if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                break;
            case R.id.image_view:
                Intent editPaintIntent = new Intent(EditActivity.this, PaintActivity.class);
                editPaintIntent.putExtra("paintFilePath", note.getImage());
                startActivityForResult(editPaintIntent, PAINT);
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
                    saveImgFile(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                            saveImgFile(bitmap);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            }
            // 录音
            case RECORD: {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    soundFilePath = data.getStringExtra("soundFilePath");
                    try {
                        initMediaPlayer(soundFilePath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (mediaPlayer != null) {
                        playSoundBtn.setVisibility(View.VISIBLE);
                        int duration = mediaPlayer.getDuration();
                        playSoundBtn.setText(duration / 1000 + "''");
                    }
                }
            }
            break;
            // 画图
            case PAINT: {
                Log.d("EditActivity", "Paint");
                if (resultCode == Activity.RESULT_OK && data != null) {
                    picFilePath = data.getStringExtra("paintFilePath");
//                    Log.d("EditActivity", "paintFilePath: " + picFilePath);
                    // 从文件名获取Bitmap对象
                    Bitmap bitmap = BitmapFactory.decodeFile(picFilePath);
                    if (bitmap == null) {
//                        Log.d("EditActivity", "Bitmap is null.");
                    }
                    imageView.setImageBitmap(bitmap);
                    UiUtil.adjustImageView(this, imageView, bitmap);
                }
                break;
            }
        }
    }

    /**
     * 权限申请回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent recordIntent = new Intent(EditActivity.this, RecordActivity.class);
                    startActivityForResult(recordIntent, RECORD);
                } else {
                    Toast.makeText(this, "请打开录音权限", Toast.LENGTH_SHORT).show();
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

        // 判断是插入还是更新
        if ("insert".equals(tag)) {
            if (!TextUtils.isEmpty(edit.getText())
                    || !TextUtils.isEmpty(picFilePath)
                    || !TextUtils.isEmpty(soundFilePath)) {
//                note = new Note();
                note.setContent(edit.getText().toString());
                note.setTime(ft.format(date));
                note.setTimestamp(timestamp);
                if (picFilePath != null) {
                    note.setImage(picFilePath);
                }
                if (soundFilePath != null) {
                    note.setSound(soundFilePath);
                }

                Repository.getInstance().insertNote(note);
//                    Log.d("EditActivity", "insert");
            }
        } else if ("update".equals(tag)) {
            note = Repository.getInstance().queryNoteById(getIntent().getIntExtra("noteId", 0));
            if (TextUtils.isEmpty(edit.getText())
                    && TextUtils.isEmpty(note.getImage())
                    && TextUtils.isEmpty(note.getSound())) {
                Repository.getInstance().deleteNote(note.getId());
//                    Log.d("EditActivity", "delete");
            } else {
                note.setContent(edit.getText().toString());
                note.setTime(ft.format(date));
                note.setTimestamp(timestamp);
                if (picFilePath != null) {
                    note.setImage(picFilePath);
                }
                if (soundFilePath != null) {
                    note.setSound(soundFilePath);
                }
                Repository.getInstance().updateNote(note);
//                    Log.d("EditActivity", "update");
            }
        }
        Log.d("EditActivity", "完成数据库存储，耗时：" + (System.currentTimeMillis() - timestamp));
    }

    /**
     * 保存通过相机或者图库选择的图片
     * @param bitmap Bitmap对象
     * @throws IOException
     */
    private void saveImgFile(Bitmap bitmap) throws IOException {
        long timestamp = System.currentTimeMillis();
        // 文件存储图片
        File imageFileDir = new File(getExternalFilesDir(null), "images");
        if (!imageFileDir.exists()) {
            imageFileDir.mkdir();
        }
        String imageFileName = null;
        String imageFilePath = null;
        if (bitmap != null) {
            // 命名图片文件名
            imageFileName = timestamp + ".jpg";
            picFilePath = imageFileDir + "/" + imageFileName;

            // 将图片压缩后放入文件输出流
            File imageFile = new File(imageFileDir, imageFileName);
            FileOutputStream imageOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, imageOutputStream);

            imageOutputStream.flush();
            imageOutputStream.close();
        }
    }

    /**
     * 初始化音频播放器
     * @param soundFilePath 声音文件
     * @throws IOException
     */
    private void initMediaPlayer(String soundFilePath) throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(soundFilePath);
        mediaPlayer.prepare();
    }


    /**
     * 初始化时间选择器对象
     */
    private void initTimePicker() { // Dialog 模式下，在底部弹出
        Calendar endCalender = Calendar.getInstance();
        endCalender.set(2100, 1, 1, 0, 0, 0);
        pvTime = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                Log.d("EditActivity", "onTimeSelect");
                setAlarm(date);
            }
        })
                .setTitleBgColor(Color.WHITE)
                .setSubmitColor(Color.BLACK)
                .setCancelColor(Color.BLACK)
                .setRangDate(Calendar.getInstance(), endCalender)  // 设置起止时间
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                .isDialog(true) //默认设置false ，内部实现将DecorView 作为它的父控件。
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                })
                .setItemVisibleCount(5) //若设置偶数，实际值会加1（比如设置6，则最大可见条目为7）
                .setLineSpacingMultiplier(2.0f)
                .isAlphaGradient(true)
                .build();

        Dialog mDialog = pvTime.getDialog();
        if (mDialog != null) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.BOTTOM);

            params.leftMargin = 0;
            params.rightMargin = 0;
            pvTime.getDialogContainerLayout().setLayoutParams(params);

            Window dialogWindow = mDialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim); //修改动画样式
                dialogWindow.setGravity(Gravity.BOTTOM);    // 改成Bottom,底部显示
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }

    /**
     * 设置提醒
     * @param date 提醒的时间
     */
    private void setAlarm(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.SECOND, 0);   // 将提醒时间的秒置零，提醒时间只精确到分
        long alarmTime = calendar.getTimeInMillis();    // 提醒时间转化为时间戳

        Intent intent = new Intent(EditActivity.this, AlarmReceiver.class); // 设置到广播的intent
        intent.setAction("com.android.notepad.alarm");

        // 为广播传递note的id和内容
        int noteId = getIntent().getIntExtra("noteId", 0);
        Log.d("EditActivity", "noteId: " + noteId);
//        Note note = Repository.getInstance().queryNoteById(noteId);
        intent.putExtra("noteId", noteId);
        String alarmContent = note.getContent();
        if (note.getImage() != null) {      // 当内容包含图片和手绘时
            alarmContent = alarmContent + "[图片]";
        }
        if (note.getSound() != null) {      // 当内容包含语音时
            alarmContent = alarmContent + "[语音]";
        }
        intent.putExtra("noteContent", alarmContent);
        PendingIntent pi = PendingIntent.getBroadcast(this, alarmSendCode++, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2 * 1000, pi); // 测试用
    }
}