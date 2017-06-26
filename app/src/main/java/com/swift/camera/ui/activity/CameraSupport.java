package com.swift.camera.ui.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import com.swift.camera.data.PictureDataSource;
import com.swift.camera.data.PictureRepository;
import com.swift.camera.utils.ImageLoad.GlideImageLoader;
import com.swift.camera.utils.ImageLoad.ImageLoader;


/**
 * Created by junnikokuki on 2017/4/12.
 */

public class CameraSupport implements CameraContract.Support {

    private final Context mContext;
    private ImageLoader mImageLoader;

    public CameraSupport(Context context) {
        mContext = context;
        mImageLoader = GlideImageLoader.getInstance(mContext);
    }

    @Override
    public Bitmap getLastPhoto() {
        final String[] imageColumns = { MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA };
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        ContentResolver contentReslver = mContext.getContentResolver();
        Cursor imageCursor = contentReslver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns, null, null, imageOrderBy);
        Bitmap bitmap = null;
        if (imageCursor.moveToFirst()) {
            String fullPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            imageCursor.close();
            // 必须在子线程中调用
            //bitmap = mImageLoader.getSpecifiedSizeImage(fullPath, 120, 120);
        }
        return bitmap;
    }
}
