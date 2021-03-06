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
    // ????????????????????????
    private final int REQUEST_RECORD_AUDIO = 1;

    private Note note = null;   // ??????Note??????
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
    private String picFilePath = null;  // ???????????????????????????????????????
    private TimePickerView pvTime;      // ????????????????????????????????????
    private int alarmSendCode = 0;
//    private AlarmReceiver alarmReceiver;    // ???????????????


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

        // ??????toolbar???????????????
        setSupportActionBar(editToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // ????????????????????????
        Drawable soundDrawable = getResources().getDrawable(R.drawable.icon_sound);
        soundDrawable.setBounds(0, 0, 60, 60);
        playSoundBtn.setCompoundDrawables(null, soundDrawable, null, null);

        // ??????????????????????????????
        initTimePicker();

        // ???????????????????????????intent??????????????????
        if ("update".equals(tag)) {
            int noteId = getIntent().getIntExtra("noteId", 1);
            note = Repository.getInstance().queryNoteById(noteId);
            edit.setText(note.getContent());
            // ???????????????????????????imageView
            if (!TextUtils.isEmpty(note.getImage())) {
                Bitmap bitmap = BitmapFactory.decodeFile(note.getImage());
                imageView.setImageBitmap(bitmap);
                UiUtil.adjustImageView(this, imageView, bitmap);
                String[] imageFilePathArr = note.getImage().split("/");
                // ?????????????????????
                if ("paint".equals(imageFilePathArr[imageFilePathArr.length - 2])) {
                    Log.d("EditActivity", "is paint");
                    imageView.setOnClickListener(this);
                }
            }
            // ???????????????????????????
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
            timeAlterText.setText("???????????????" + note.getTime());
        } else {
            note = new Note();
        }

        // ???????????????????????????
        insertPhotoBtn.setOnClickListener(this);
        insertImageBtn.setOnClickListener(this);
        insertVoiceBtn.setOnClickListener(this);
        insertDrawBtn.setOnClickListener(this);
        playSoundBtn.setOnClickListener(this);
    }

    /**
     * ??????????????????????????????
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
     * ??????edit_toolbar.xml??????
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_toolbar, menu);
        return true;
    }

    /**
     * ??????toolBar????????????
     * @param item
     * @return
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {   // ?????????????????????
                Log.d("EditActivity", "home click");
                try {
                    saveNote();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                finish();
                return true;
            }
            case R.id.add_tag: {        // ??????????????????????????????
                int noteId = getIntent().getIntExtra("noteId", 0);
                Intent intent = new Intent(EditActivity.this, TagActivity.class);
                intent.putExtra("noteId", noteId);
                startActivity(intent);
                break;
            }
            case R.id.add_alarm: {      // ??????????????????????????????
                pvTime.show();
                break;
            }
            case R.id.delete_note: {    // ?????????????????????
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
            case R.id.insert_photo_btn:     // ??????????????????
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
                // ??????????????????
                Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(cameraIntent, TAKE_PHOTO);
                break;
            case R.id.insert_image_btn:     // ??????????????????
                // ?????????????????????
                Intent docIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                docIntent.addCategory(Intent.CATEGORY_OPENABLE);
                // ?????????????????????
                docIntent.setType("image/*");
                startActivityForResult(docIntent, FROM_ALBUM);
                break;
            case R.id.insert_voice_btn:     // ??????????????????
                // ????????????????????????
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[] { Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO);
                } else {
                    Intent recordIntent = new Intent(EditActivity.this, RecordActivity.class);
                    startActivityForResult(recordIntent, RECORD);
                }
                break;
            case R.id.insert_draw_btn:      // ??????????????????
                Intent paintIntent = new Intent(EditActivity.this, PaintActivity.class);
                startActivityForResult(paintIntent, PAINT);
                break;
            case R.id.play_sound_btn:       // ????????????????????????
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
            // ???????????????
            case TAKE_PHOTO: {
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    imageView.setImageBitmap(bitmap);
                    UiUtil.adjustImageView(this, imageView, bitmap);    // ?????????????????????
                    saveImgFile(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;
            }
            // ???????????????
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
            // ??????
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
            // ??????
            case PAINT: {
                Log.d("EditActivity", "Paint");
                if (resultCode == Activity.RESULT_OK && data != null) {
                    picFilePath = data.getStringExtra("paintFilePath");
//                    Log.d("EditActivity", "paintFilePath: " + picFilePath);
                    // ??????????????????Bitmap??????
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
     * ????????????????????????
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
                    Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * ????????????
     */
    private void saveNote() throws IOException {
        // ????????????
        Date date = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat ft = new SimpleDateFormat("yyyy???MM???dd???");
        long timestamp = System.currentTimeMillis();

        // ???????????????????????????
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
        Log.d("EditActivity", "?????????????????????????????????" + (System.currentTimeMillis() - timestamp));
    }

    /**
     * ?????????????????????????????????????????????
     * @param bitmap Bitmap??????
     * @throws IOException
     */
    private void saveImgFile(Bitmap bitmap) throws IOException {
        long timestamp = System.currentTimeMillis();
        // ??????????????????
        File imageFileDir = new File(getExternalFilesDir(null), "images");
        if (!imageFileDir.exists()) {
            imageFileDir.mkdir();
        }
        String imageFileName = null;
        String imageFilePath = null;
        if (bitmap != null) {
            // ?????????????????????
            imageFileName = timestamp + ".jpg";
            picFilePath = imageFileDir + "/" + imageFileName;

            // ???????????????????????????????????????
            File imageFile = new File(imageFileDir, imageFileName);
            FileOutputStream imageOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, imageOutputStream);

            imageOutputStream.flush();
            imageOutputStream.close();
        }
    }

    /**
     * ????????????????????????
     * @param soundFilePath ????????????
     * @throws IOException
     */
    private void initMediaPlayer(String soundFilePath) throws IOException {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(soundFilePath);
        mediaPlayer.prepare();
    }


    /**
     * ??????????????????????????????
     */
    private void initTimePicker() { // Dialog ???????????????????????????
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
                .setRangDate(Calendar.getInstance(), endCalender)  // ??????????????????
                .setTimeSelectChangeListener(new OnTimeSelectChangeListener() {
                    @Override
                    public void onTimeSelectChanged(Date date) {
                        Log.i("pvTime", "onTimeSelectChanged");
                    }
                })
                .setType(new boolean[]{true, true, true, true, true, false})
                .isDialog(true) //????????????false ??????????????????DecorView ????????????????????????
                .addOnCancelClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("pvTime", "onCancelClickListener");
                    }
                })
                .setItemVisibleCount(5) //?????????????????????????????????1???????????????6???????????????????????????7???
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
                dialogWindow.setWindowAnimations(com.bigkoo.pickerview.R.style.picker_view_slide_anim); //??????????????????
                dialogWindow.setGravity(Gravity.BOTTOM);    // ??????Bottom,????????????
                dialogWindow.setDimAmount(0.3f);
            }
        }
    }

    /**
     * ????????????
     * @param date ???????????????
     */
    private void setAlarm(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.set(Calendar.SECOND, 0);   // ?????????????????????????????????????????????????????????
        long alarmTime = calendar.getTimeInMillis();    // ??????????????????????????????

        Intent intent = new Intent(EditActivity.this, AlarmReceiver.class); // ??????????????????intent
        intent.setAction("com.android.notepad.alarm");

        // ???????????????note???id?????????
        int noteId = getIntent().getIntExtra("noteId", 0);
        Log.d("EditActivity", "noteId: " + noteId);
//        Note note = Repository.getInstance().queryNoteById(noteId);
        intent.putExtra("noteId", noteId);
        String alarmContent = note.getContent();
        if (note.getImage() != null) {      // ?????????????????????????????????
            alarmContent = alarmContent + "[??????]";
        }
        if (note.getSound() != null) {      // ????????????????????????
            alarmContent = alarmContent + "[??????]";
        }
        intent.putExtra("noteContent", alarmContent);
        PendingIntent pi = PendingIntent.getBroadcast(this, alarmSendCode++, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 2 * 1000, pi); // ?????????
    }
}