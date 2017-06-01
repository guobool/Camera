package swift.com.camera.Album;

import android.widget.ImageView;
import java.util.List;
import swift.com.camera.BaseView;
import swift.com.camera.data.PictureBean;

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
