package com.swift.camera.Album;

import android.content.Context;
import android.content.Intent;

import com.swift.camera.beautify.BeautifyActivity;
import com.swift.camera.data.PictureDataSource;
import com.swift.camera.data.PictureInfo;
import com.swift.camera.data.PictureRepository;
import com.swift.camera.data.PicturesFolder;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by bool on 17-4-14.
 */

class AlbumPresenter implements AlbumContract.Presenter {
    private PictureRepository mPictureRepository;
    private AlbumContract.View mAlbumView;
    private List<PictureInfo> mImagesBean;
    @Inject
    public AlbumPresenter(PictureRepository repository, AlbumContract.View albumView){
        mPictureRepository = repository;
        mAlbumView = albumView;
    }
    /**
     * Method injection is used here to safely reference {@code this} after the object is created.
     * For more information, see Java Concurrency in Practice.
     */
    @Inject
    void setupListeners() {
        mAlbumView.setPresenter(this);
    }

    @Override
    public void toProcessingActivity() {
        mAlbumView.toProcessingActivity();
    }


    @Override
    public void loadImages() {
        mPictureRepository.loadPicture(new PictureDataSource.LoadPictureCallBack() {
            @Override
            public void onPictureLoaded(PicturesFolder picturesFolder) {
                if(picturesFolder != null){
                    mAlbumView.showFolderList(picturesFolder);
                } else {
                    this.onLoadFailed();
                }
            }

            @Override
            public void onLoadFailed() {
                mAlbumView.showNoPictures();
            }
        });
    }

    @Override
    public void toBeaytifyActivity(PictureInfo PictureInfo) {
        Intent intent = new Intent((Context) mAlbumView, BeautifyActivity.class);
        intent.putExtra("PictureInfo", PictureInfo);
        ((Context) mAlbumView).startActivity(intent);
    }
}
