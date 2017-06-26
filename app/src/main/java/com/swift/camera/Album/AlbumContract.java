package com.swift.camera.Album;

import android.widget.ImageView;
import java.util.List;
import com.swift.camera.BaseView;
import com.swift.camera.data.PictureBean;

/**
 * Created by bool on 17-4-18.
 */

public interface AlbumContract {
    interface View extends BaseView<Presenter> {

        void toProcessingActivity();

        void toBeautifyActivity(PictureBean pictureBean);

        void pictureBeanLoaded(List<PictureBean> pictureBeanList);
    }

    interface Presenter {
        void toProcessingActivity();

        void getImagesList();

        void toBeaytifyActivity(PictureBean pictureBean);
    }
}
