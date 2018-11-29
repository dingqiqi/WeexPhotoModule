package com.lakala.appcomponent.photomodule;

import com.taobao.weex.bridge.JSCallback;

/**
 * 拍照选择图片
 * Created by dingqq on 2018/4/17.
 */

public interface IPhotoModule {

    //拍照
    boolean takePhoto(JSCallback callback);

    //选择图片
    boolean selectImage(JSCallback callback);

}
