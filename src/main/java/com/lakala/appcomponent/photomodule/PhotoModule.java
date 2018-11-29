package com.lakala.appcomponent.photomodule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import com.lakala.appcomponent.photomodule.util.FileUtil;
import com.lakala.appcomponent.photomodule.util.ImageUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;

import static android.app.Activity.RESULT_OK;

/**
 * 获取图片
 * Created by dingqq on 2018/4/17.
 */

public class PhotoModule extends WXModule implements IPhotoModule {

    private File mCurPhotoFile;

    private JSCallback mTaskPhotoBack;

    /**
     * 拍照
     *
     * @param callback 回调
     * @return true 成功 false 失败
     */
    @JSMethod
    @Override
    public boolean takePhoto(JSCallback callback) {
        mTaskPhotoBack = callback;

        Context context = mWXSDKInstance.getContext();
        if (context == null) {
            return false;
        }

        final Activity activity = (Activity) context;

        final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCurPhotoFile = new File(FileUtil.getImagePath(activity),
                FileUtil.getPhotoFileName(activity));

        Uri mImageUri;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            mImageUri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider",
                    mCurPhotoFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            mImageUri = Uri.fromFile(mCurPhotoFile);
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        activity.startActivityForResult(intent, 0x01);

        return true;
    }

    /**
     * 选择照片
     *
     * @param callback 回调
     * @return true 成功 false 失败
     */
    @JSMethod
    @Override
    public boolean selectImage(JSCallback callback) {
        mTaskPhotoBack = callback;

        Context context = mWXSDKInstance.getContext();
        if (context == null) {
            mTaskPhotoBack = null;
            return false;
        }

        final Activity activity = (Activity) context;

//        Intent intent = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent, 0x02);

        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 0x01) {
                if (mCurPhotoFile != null && mCurPhotoFile.exists()) {

                    backPath(mCurPhotoFile.getPath());
                } else {
                    Log.e("PhotoModule", "图片不存在! mCurPhotoFile 不存在");
                }
            } else if (requestCode == 0x02) {
                Uri mImageUri = data.getData();

                if (mImageUri == null) {
                    Log.e("PhotoModule", "图片不存在! mImageUri = null");
                    return;
                }

                backPath(FileUtil.getImagePath(mWXSDKInstance.getContext(), mImageUri));
            }
        }

    }

    /**
     * 图片压缩回调
     *
     * @param path 图片路径
     */
    private void backPath(String path) {
        path = ImageUtil.imageCompress(mWXSDKInstance.getContext(), path);

        if (mTaskPhotoBack != null) {
            if (!TextUtils.isEmpty(path)) {
                path = "resLocal://" + path;
            }

            mTaskPhotoBack.invoke(path);
        }
    }

}
