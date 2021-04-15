package com.android.notepad.ui.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.notepad.R;

import java.io.File;
import java.io.IOException;

public class RecordActivity extends AppCompatActivity implements View.OnTouchListener {

    public static boolean IS_PRESS = false;
    private TextView recordText;
    private ImageView recordImg;
    private MediaRecorder mediaRecorder = null;
    private MediaPlayer mediaPlayer = null;
    private String fileName = null;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels / 1.5);
        getWindow().setAttributes(layoutParams);

        recordText = (TextView) findViewById(R.id.record_text);
        recordImg = (ImageView) findViewById(R.id.record_img);

        recordImg.setOnTouchListener(this);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Log.d("RecordActivity", "down");
                try {
                    startRecord();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                recordText.setText("录制中...");
                recordImg.setImageResource(R.drawable.icon_record_press);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("RecordActivity", "up");
                stopRecord();
                recordText.setText("按住录音");
                recordImg.setImageResource(R.drawable.icon_record);
                Intent intent = new Intent();
                String soundFilePath = getExternalFilesDir(null) + "/sounds/" + fileName;
                intent.putExtra("soundFilePath", soundFilePath);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
        return true;
    }

    /**
     * 开始录制
     * @throws IOException
     */
    private void startRecord() throws IOException {
        if (mediaRecorder == null) {
            File dir = new File(getExternalFilesDir(null),  "sounds");
            if (!dir.exists()) {
                dir.mkdir();
            }
            fileName = System.currentTimeMillis() + ".amr";
            File soundFile = new File(dir, fileName);
            if (!soundFile.exists()) {
                soundFile.createNewFile();
            }
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);        // 音频输入源
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_WB);   // 输出格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);   // 设置编码格式
            mediaRecorder.setOutputFile(soundFile.getAbsolutePath());           // 设置输出文件
            mediaRecorder.prepare();
            mediaRecorder.start();
        }
    }

    /**
     * 停止录制
     */
    private void stopRecord() {
        if (mediaRecorder != null) {
            try {
                mediaRecorder.stop();
            } catch (IllegalStateException e) {
                mediaRecorder = null;
                mediaRecorder = new MediaRecorder();
            }
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }
}