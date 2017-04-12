package swift.com.camera.processing;

import swift.com.camera.BaseView;

/**
 * Created by bool on 17-4-12.
 */

public interface ProcessingContract {
    interface View extends BaseView<Presenter> {
        void jumpAlbumPage();
        void jumpBeautifyPage();

    }
    interface Presenter{

        void jumpAlbumPage();
        void jumpBeautifyPage();
    }
}
