package swift.com.camera.Album;

import java.util.List;

import swift.com.camera.BaseView;
import swift.com.camera.data.PictureBean;

/**
 * Created by bool on 17-4-18.
 */

public interface AlbumContract {
    interface View extends BaseView<Presenter> {

        void toProcessingActivity();

        void toBeautifyActivity();
    }

    interface Presenter {

        void toProcessingActivity();

        void toBeautifyActivity();

        List<PictureBean> getImagesList();
    }
}
