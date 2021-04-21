package com.android.notepad.ui.edit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;

import com.android.notepad.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaintActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar paintToolBar;
    private PaintView paintView;
    private ImageButton colorSelectBtn;
    private ImageButton eraserBtn;
    private ImageButton resetBtn;

    private List<CircleView> circleViewList;    // 颜色列表
    private PopupWindow colorPopupWindow = null;    // 颜色选择弹出窗口

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint);

        paintToolBar = (Toolbar) findViewById(R.id.paint_toolbar);
        paintView = (PaintView) findViewById(R.id.paint_view);
        colorSelectBtn = (ImageButton) findViewById(R.id.color_select_btn);
        eraserBtn = (ImageButton) findViewById(R.id.eraser_btn);
        resetBtn = (ImageButton) findViewById(R.id.reset_paint_btn);
        colorSelectBtn.setOnClickListener(this);
        eraserBtn.setOnClickListener(this);
        resetBtn.setOnClickListener(this);


        // 设置toolbar的返回按钮
        setSupportActionBar(paintToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        String paintFilePath = getIntent().getStringExtra("paintFilePath");
        if (!TextUtils.isEmpty(paintFilePath)) {
            Bitmap bitmap = BitmapFactory.decodeFile(paintFilePath).copy(Bitmap.Config.ARGB_8888, true);
            paintView.setBitmap(bitmap);
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.color_select_btn:
                colorPopupWindow = initColorPopWindow(v);
                break;
            case R.id.black_circle:
            case R.id.red_circle:
            case R.id.green_circle:
            case R.id.blue_circle:
            case R.id.orange_circle:
            case R.id.purple_circle:
                int color = ((CircleView) v).getColor();
                Log.d("PaintActivity", color + "");
                paintView.changeColor(color);
                if (colorPopupWindow != null) {
                    colorPopupWindow.dismiss();
                }
                break;
            case R.id.eraser_btn:
                paintView.changeColor(Color.WHITE);
                break;
            case R.id.reset_paint_btn:
                paintView.reset();
                break;
        }
    }

    /**
     * 初始化弹出窗口
     * @param v 点击的view
     * @return PoupWindow对象
     */
    private PopupWindow initColorPopWindow(View v) {
        View view = LayoutInflater.from(this).inflate(R.layout.pop_color_selector, null, false);
        CircleView blackCircle = (CircleView) view.findViewById(R.id.black_circle);
        CircleView redCircle = (CircleView) view.findViewById(R.id.red_circle);
        CircleView greenCircle = (CircleView) view.findViewById(R.id.green_circle);
        CircleView blueCircle = (CircleView) view.findViewById(R.id.blue_circle);
        CircleView orangeCircle = (CircleView) view.findViewById(R.id.orange_circle);
        CircleView purpleCircle = (CircleView) view.findViewById(R.id.purple_circle);

        // 将CircleView添加到数组
        circleViewList = Arrays.asList(blackCircle, redCircle, greenCircle, blueCircle, orangeCircle, purpleCircle);
        // 给每个CircleView绑定点击事件
        for (CircleView circleView : circleViewList) {
            circleView.setOnClickListener(this);
        }
        final PopupWindow colorPopupWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        colorPopupWindow.setTouchable(true);
        colorPopupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        colorPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        colorPopupWindow.showAsDropDown(v, 0, -200);

        return colorPopupWindow;
    }

//    private PopupWindow initWeightPopWindow(View v) {
//        View view = LayoutInflater.from(this).inflate(R.layout.pop_weight_selector, null, false);
//        CircleView weight1 = (CircleView) view.findViewById(R.id.weight_1);
//        CircleView weight2 = (CircleView) view.findViewById(R.id.weight_2);
//        CircleView weight3 = (CircleView) view.findViewById(R.id.weight_3);
//        CircleView weight4 = (CircleView) view.findViewById(R.id.weight_4);
//        CircleView weight5 = (CircleView) view.findViewById(R.id.weight_5);
//        CircleView weight6 = (CircleView) view.findViewById(R.id.weight_6);
//
//        // 将CircleView添加到数组
//        circleViewList = Arrays.asList(weight1, weight2, weight3, weight4, weight5, weight6);
//        // 给每个CircleView绑定点击事件
//        for (CircleView circleView : circleViewList) {
//            circleView.setOnClickListener(this);
//        }
//        final PopupWindow weightPopupWindow = new PopupWindow(view,
//                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//
//        colorPopupWindow.setTouchable(true);
//        colorPopupWindow.setTouchInterceptor(new View.OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;
//            }
//        });
//        colorPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
//        colorPopupWindow.showAsDropDown(v, 0, -200);
//
//        return weightPopupWindow;
//    }
}