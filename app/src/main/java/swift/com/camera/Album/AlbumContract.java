package swift.com.camera.Album;

import android.graphics.Bitmap;
import android.widget.ImageView;

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

        void toBeautifyActivity(PictureBean pictureBean);

        void pictureBeanLoaded(List<PictureBean> pictureBeanList);

        void setImage(String imageName, int width, int height, ImageView imageView);
    }

    interface Presenter {
        void toProcessingActivity();

        void toBeautifyActivity(PictureBean pictureBean);

        void getImagesList();

        void toBeaytifyActivity(PictureBean pictureBean);

        void setBitMap(String imageName, int width, int height, ImageView imageView);
    }
}
