package com.android.notepad.ui.edit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.android.notepad.ui.UiUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaintView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mSurfaceHolder;   // SurfaceHolder实例
    private Canvas mCanvas;                 // Canvas对象
    private boolean startDraw;              // 控制子线程是否运行
    private Paint mPaint = new Paint();     // Paint实例
    private Canvas svCanvas;                // 用于保存的Canvas对象
    private Bitmap mBitmap = null;          // 用于保存的Bitmap对象
    private boolean beDraw = false;         // 标记是否绘画过
    private File paintFile;                 // 设置保存的图片文件
    private Thread drawThread;              // 画图线程
    private static int paintColor = Color.BLACK;    // 画笔颜色
    private static float paintStroke = 0;
    private List<Path> pathList = new ArrayList<>();    // 路径列表
    private int pathListLen = -1;           // 路径长度

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initFile();
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

        // 初始化用于保存的画布
        initSvCanvas();
        // startDraw置为true，线程运行
        startDraw = true;
        drawThread = new Thread(this);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        startDraw = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                beDraw = true;  // 表示画布被修改过
                Path path = new Path();
                path.moveTo(x, y);
                pathList.add(path);
                pathListLen++;
                break;
            case MotionEvent.ACTION_MOVE:
                pathList.get(pathListLen).lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    @Override
    public void run() {
        while (startDraw) {
            // 绘制
            draw();
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);

        // 设置可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        // 设置常量
        this.setKeepScreenOn(true);
    }

    /**
     * 初始化目录文件和图片文件
     */
    private void initFile() {
        // 设置图片存储目录
        File paintDir = new File(getContext().getExternalFilesDir(null), "paint");
        if (!paintDir.exists()) {
            paintDir.mkdir();
        }
        // 创建图片文件
        String paintFileName = System.currentTimeMillis() + ".jpg";
        paintFile  = new File(paintDir, paintFileName);
        try {
            paintFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化用于保存的画布
     */
    private void initSvCanvas() {
        if (mBitmap != null) {
            svCanvas = new Canvas(mBitmap);
        } else {
            mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            svCanvas = new Canvas(mBitmap);
            // 先把画布置为白色
            svCanvas.drawColor(Color.WHITE);
        }

    }

    /**
     * 绘制图片的过程
     */
    private void draw() {
        try {
            mCanvas = mSurfaceHolder.lockCanvas();
            // 将画布填充为上次的画布，在上次的画布上继续画
            mCanvas.drawBitmap(mBitmap, 0, 0, null);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(UiUtil.px2dip(getContext(), 5));
            mPaint.setColor(paintColor);
            mCanvas.drawPath(pathList.get(pathListLen), mPaint);
            svCanvas.drawPath(pathList.get(pathListLen), mPaint);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 对画布内容进行提交
            if (mCanvas != null) {
                mSurfaceHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    /**
     * 将通过svCanvas绘制的bitmap保存为图片
     */
    private void saveBitmap() {
        if (!beDraw) {
            Log.d("PaintView", "paintFile delete");
            paintFile.delete();
        } else {
            Log.d("PaintView", "paintFile save");
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(paintFile);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存绘图
     */
    public void savePaintFile() {
        startDraw = false;
        try {
            drawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        saveBitmap();
    }

    /**
     * 舍弃绘图
     */
    public void dropPaintFile() {
        startDraw = false;
        paintFile.delete();
    }

    /**
     * 设置Bitmap
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        svCanvas = new Canvas(bitmap);
    }

    /**
     * 重置画布
     */
    public void reset() {
        for (Path path : pathList) {
            path.reset();
            svCanvas.drawColor(Color.WHITE);
            beDraw = false;
        }
    }

    /**
     * 获取保存的文件路径
     * @return
     */
    public String getPaintFilePath() {
        return paintFile.getAbsolutePath();
    }

    /**
     * 更改颜色
     * @param color 颜色
     */
    public void changeColor(int color) {
        startDraw = false;
        try {
            drawThread.join();
            Path path = new Path();
            pathList.add(path);
            pathListLen++;
            drawThread = new Thread(this);
            paintColor = color;
            startDraw = true;
            drawThread.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void changeWeight(float weight) {

    }

}
