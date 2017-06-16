package swift.com.camera.Album;

import android.widget.ImageView;
import java.util.List;
import swift.com.camera.BaseView;
import swift.com.camera.data.PictureInfo;
import swift.com.camera.data.PicturesFolder;

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
