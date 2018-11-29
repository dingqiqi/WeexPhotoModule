package com.lakala.appcomponent.photomodule.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图片压缩工具类
 * Created by dingqq on 2018/5/2.
 */

public class ImageUtil {

    //单位mb   1M
    private static final int mMaxSize = 1024 * 1024;

    public static String imageCompress(Context context, String path) {

        if (TextUtils.isEmpty(path)) {
            Log.i("aaa", "path is null");
            return "";
        }

        File file = new File(path);

        if (!file.exists()) {
            Log.i("aaa", "file not exists");
            return "";
        }

        double targetWidth = Math.sqrt(mMaxSize);

        BitmapFactory.Options options = new BitmapFactory.Options();
        //不加入内存
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        //获取图片宽高
        BitmapFactory.decodeFile(path, options);

        //计算inSampleSize大小
        options.inSampleSize = calculateInSampleSize(options, (int) targetWidth, (int) targetWidth);
        options.inJustDecodeBounds = false;

        InputStream stream = null;

        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("ImageUtil", e.getMessage());
        }

        if (stream == null) {
            Log.i("aaa", "stream == null");
            return "";
        }

        Bitmap bitmap = BitmapFactory.decodeStream(stream, null, options);

        String newPath = FileUtil.saveBitmapToFile(context, bitmap);

        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newPath;
    }

    public static Bitmap FileToBitmap(String path) {

        if (TextUtils.isEmpty(path)) {
            Log.i("aaa", "path is null");
            return null;
        }

        File file = new File(path);

        if (!file.exists()) {
            Log.i("aaa", "file not exists");
            return null;
        }

        double targetWidth = Math.sqrt(mMaxSize);

        BitmapFactory.Options options = new BitmapFactory.Options();
        //不加入内存
        options.inJustDecodeBounds = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        //获取图片宽高
        BitmapFactory.decodeFile(path, options);

        //计算inSampleSize大小
        options.inSampleSize = calculateInSampleSize(options, (int) targetWidth, (int) targetWidth);
        options.inJustDecodeBounds = false;

        InputStream stream = null;

        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("ImageUtil", e.getMessage());
        }

        if (stream == null) {
            Log.i("aaa", "stream == null");
            return null;
        }

        return BitmapFactory.decodeStream(stream, null, options);
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 源图片的高度和宽度
        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            // 计算出实际宽高和目标宽高的比率
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
            // 一定都会大于等于目标的宽和高。
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

}
