package com.swift.camera.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.swift.camera.data.PictureDataSource;
import com.swift.camera.data.PicturesFolder;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.swift.camera.Album.PictureDataSource.LOCAL_GUERY_ATTRIBUTE;
import static dagger.internal.Preconditions.checkNotNull;

/**
 * Created by bool on 17-4-12.
 */
@Singleton
public class PictureRepository implements PictureDataSource {
    private Context mContext;
    private PicturesFolder mPicturesFolder;
    //private ArrayList<PictureInfo> mPictureList;
    @Inject
    public PictureRepository(@NonNull Context context) {
        mContext = context;
        mPicturesFolder = new PicturesFolder(); // 使用目录作为key，存放图片信息的List为值
    }

    @Override
    public void loadPicture(@NonNull final LoadPictureCallBack loadCallBack) {
        checkNotNull(loadCallBack);
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                ContentResolver mContentReslver = mContext.getContentResolver();

                Cursor mCursor = mContentReslver.query(LOCAL_IMAGE_URI, LOCAL_GUERY_ATTRIBUTE, null,
                        null, LOCAL_QUERY_ATTR);
                if(mCursor != null) {
                    PictureInfo pictureInfo;
                    while(mCursor.moveToNext()){
                        // 获取图片路径
                        String path = mCursor.getString(0);
                        String name = mCursor.getString(1); // 文件名和后缀
                        int date = mCursor.getInt(2);
                        pictureInfo = new PictureInfo(path, name, date);
                        mPicturesFolder.add(pictureInfo);
                    }
                    mCursor.close();
                }
                if(mPicturesFolder.size() != 0) {
                    loadCallBack.onPictureLoaded(mPicturesFolder);
                }
            }
        });
    }
}
