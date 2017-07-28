package com.swift.camera.Album;


import com.swift.camera.BaseView;
import com.swift.camera.data.PictureInfo;
import com.swift.camera.data.PicturesFolder;

/**
 * Created by bool on 17-4-18.
 */

public interface AlbumContract {
    interface View extends BaseView<Presenter> {

        void toProcessingActivity();

        void toBeautifyActivity(PictureInfo PictureInfo);

        void showFolderList(PicturesFolder picturesFolder);

        void showNoPictures();
    }

    interface Presenter {
        void toProcessingActivity();

        void toBeaytifyActivity(PictureInfo PictureInfo);
        
        void loadImages();
    }
}
