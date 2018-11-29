package com.lakala.appcomponent.photomodule.util;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件帮助类
 * Created by dqq on 2017/7/5.
 */
public class FileUtil {
    /**
     * 图片存储路径
     */
    private static final String IMAGE_PATH = "image";

    /**
     * 保存地址
     *
     * @return 路径
     */
    public static String getDefaultPath(Context context, String name) {
        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + name);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file.getAbsolutePath();
    }

    /**
     * SD卡地址
     *
     * @return 路径
     */
    public static String getSDCardPath(Context context, String name) {
        File file;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/KHJL/" + context.getPackageName() + "/" + name);
            if (!file.exists()) {
                file.mkdirs();
            }
        } else {
            file = new File(context.getFilesDir().getAbsolutePath() + "/KHJL/" + name);
            if (!file.exists()) {
                file.mkdirs();
            }
        }

        return file.getAbsolutePath();
    }

    /**
     * 获取source路径
     *
     * @param context 上下文
     * @return /sdcard/KHJL/source
     */
    public static String getSourcePath(Context context) {
        return getDefaultPath(context, "source");
    }

    /**
     * 获取source更新路径
     *
     * @param context 上下文
     * @return /sdcard/KHJL/source/update
     */
    public static String getSourceUpdatePath(Context context) {
        return getDefaultPath(context, "update");
    }

    /**
     * 保存地址
     *
     * @return 文件路径
     */
    public static String getImagePath(Context context) {

        return getSDCardPath(context, IMAGE_PATH);
    }

    /**
     * 设置拍照获得的照片名字
     *
     * @return 文件名
     */
    public static String getPhotoFileName(Context context) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyyMMdd_HHmmss", Locale.getDefault());

        return "KHJL_IMG_" + dateFormat.format(date) + ".jpg";
    }

    public static String saveBitmapToFile(Context context, Bitmap bitmap) {
        return saveBitmapToFile(context, bitmap, "");
    }

    /**
     * 保存bitmap到本地
     *
     * @param context 上下文
     * @param bitmap  bitmap
     * @return 图片路径
     */
    public static String saveBitmapToFile(Context context, Bitmap bitmap, String name) {
        if (bitmap == null) {
            return "";
        }

        String savePath = getImagePath(context) + "/";

        if (TextUtils.isEmpty(name)) {
            savePath += getPhotoFileName(context);
        } else {
            savePath += name;
        }

        BufferedOutputStream fos = null;

        try {
            File file = new File(savePath);
            if (file.exists()) {
                file.delete();
            }

            fos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("FileUtil", e.getMessage());
            return "";
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            bitmap.recycle();
            bitmap = null;
        }

        return savePath;
    }

    /**
     * 获取bitmap
     *
     * @param context 上下文
     * @param intent  intent
     * @return bitmap
     */
    public static Bitmap getImageBitmap(Context context, Intent intent) {
        Bitmap bitmap;
        if (intent.getExtras() != null) {
            bitmap = (Bitmap) intent.getExtras().get("data");
        } else {
            Uri uri = intent.getData();
            if (uri == null) {
                return null;
            }
            try {
                bitmap = BitmapFactory.decodeStream(context
                        .getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
        return bitmap;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getImagePath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 文件路转化为String
     *
     * @param inputStream 文件流
     * @return String
     */
    public static String streamToString(InputStream inputStream) {

        if (inputStream == null) {
            return null;
        }

        byte[] by = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int len;
        try {
            while ((len = inputStream.read(by)) != -1) {
                outputStream.write(by, 0, len);
            }

            return outputStream.toString("utf-8");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}
