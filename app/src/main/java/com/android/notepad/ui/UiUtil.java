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
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue * density);
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
                    imageView.setLayoutParams(layoutParams);
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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String filePath, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }
}
