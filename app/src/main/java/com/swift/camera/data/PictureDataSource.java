package com.swift.camera.data;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import java.util.List;

/**
 * Created by bool on 17-4-18.
 */

public interface PictureDataSource {
    Uri LOCAL_IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    String LOCAL_QUERY_CRITERIA = MediaStore.Images.Media.MIME_TYPE + "=? or "
            + MediaStore.Images.Media.MIME_TYPE + "=?";
    String[] LOCAL_QUERY_VALUE = new String[] { "image/jpeg", "image/png" };
    String LOCAL_QUERY_ATTR = MediaStore.Images.Media.DATE_MODIFIED;

    // 用与Presenter层实现，Module调用刷新界面的接口
    interface LoadPictureCallBack{
        // 图片加载成功
        void onPictureLoaded(PicturesFolder picturesFolder);

        void onLoadFailed();
        // 图片加载失败
    }

    void loadPicture(@NonNull LoadPictureCallBack loadCallBack);

}
