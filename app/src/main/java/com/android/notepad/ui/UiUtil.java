package com.android.notepad.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.FileNotFoundException;

public class UiUtil {
    /**
     * 获取bitmap
     * @param uri
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws FileNotFoundException {
        Bitmap bitmap = null;
        if (context.getContentResolver().openFileDescriptor(uri, "r") != null) {
            bitmap = BitmapFactory.decodeFileDescriptor(
                    context.getContentResolver().openFileDescriptor(uri, "r").getFileDescriptor());
        }
        return bitmap;
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    /**
     * 获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
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
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 调整ImageView的大小，使之根据图片的大小调整
     * @param context
     * @param imageView
     * @param bitmap
     */
    public static void adjustImageView(Context context, ImageView imageView, Bitmap bitmap) {
        imageView.post(new Runnable() {
            @Override
            public void run() {
//                Log.d("UiUtil", "post");
                ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                if (bitmap == null) {
                    layoutParams.height = 0;
                } else {
                    if (bitmap.getWidth() > imageView.getWidth()) {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                    } else {
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                    float newHeight = (float) imageView.getWidth() / (float) bitmap.getWidth() * bitmap.getHeight();
                    layoutParams.height = (int) newHeight;
                    imageView.setLayoutParams(layoutParams);
                }
            }
        });
    }
}
