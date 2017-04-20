package swift.com.camera.Album;

import android.graphics.Bitmap;

import java.util.List;

import swift.com.camera.BaseView;
import swift.com.camera.data.PictureBean;
import swift.com.camera.data.PictureDataSource;

/**
 * Created by bool on 17-4-18.
 */

public interface AlbumContract {
    interface View extends BaseView<Presenter> {

        void toProcessingActivity();

        void toBeautifyActivity();

        void pictureBeanLoaded(List<PictureBean> pictureBeanList);

        void pictureGeted(Bitmap picture);
    }

    interface Presenter {

        void getBitMap(String imageName, int width, int height);

        void toProcessingActivity();

        void toBeautifyActivity();

        void getImagesList();

        void toBeaytifyActivity(PictureBean pictureBean);
    }
}
