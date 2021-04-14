package com.android.notepad.ui.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.android.notepad.R;

public class PaintActivity extends AppCompatActivity {

    private Toolbar paintToolBar;
    private PaintView paintView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        paintToolBar = (Toolbar) findViewById(R.id.paint_toolbar);
        paintView = (PaintView) findViewById(R.id.paint_view);

        // 设置toolbar的返回按钮
        setSupportActionBar(paintToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paint_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                paintView.dropPaintFile();
                finish();
                return true;
            }
            case R.id.check_paint: {
                Intent intent = new Intent();
                intent.putExtra("paintFilePath", paintView.getPaintFilePath());
                setResult(RESULT_OK, intent);
                paintView.savePaintFile();
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}