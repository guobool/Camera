package com.swift.camera.Album;

import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import com.swift.camera.data.PicturesFolder;

/**
 * Created by bool on 17-4-18.
 */

public interface PictureDataSource {
    Uri LOCAL_IMAGE_URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    String[] LOCAL_GUERY_ATTRIBUTE = new String[] {
            MediaStore.Images.Media.DATA, // 图片路径
            MediaStore.Images.Media.DISPLAY_NAME, // 图片文件名，包含后缀
            MediaStore.Images.Media.DATE_ADDED // 创建日期
    };
    String LOCAL_QUERY_CRITERIA = MediaStore.Images.Media.MIME_TYPE + "=? or "
            + MediaStore.Images.Media.MIME_TYPE + "=?";
    String[] LOCAL_QUERY_VALUE = new String[] { "image/jpeg", "image/png" };
    String LOCAL_QUERY_ATTR = MediaStore.Images.Media.DATE_MODIFIED; // 更新时间

    // 用与Presenter层实现，Module调用刷新界面的接口
    interface LoadPictureCallBack{
        // 图片加载成功
        void onPictureLoaded(PicturesFolder picturesFolder);

        void onLoadFailed();
        // 图片加载失败
    }

    void loadPicture(@NonNull LoadPictureCallBack loadCallBack);

}
