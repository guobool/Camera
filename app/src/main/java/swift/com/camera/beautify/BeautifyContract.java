package swift.com.camera.beautify;

import android.graphics.Bitmap;

import swift.com.camera.BaseView;
import swift.com.camera.processing.ProcessingContract;
import swift.com.camera.ui.BasePresenter;

/**
 * Created by bool on 17-4-19.
 */

public interface BeautifyContract {
    interface View extends BaseView<Presenter>{

        void showImage(Bitmap picture);
    }

    interface Presenter extends BasePresenter{

        void getImage(String s, int width, int height);
    }
}
