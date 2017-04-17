package swift.com.camera.ui.activity;

import android.opengl.GLSurfaceView;

/**
 * Created by junnikokuki on 2017/4/12.
 */

public class CameraContract {
    // UI展示
    interface View {
        void setGLSurfaceViewRenderMode(int renderMode);
    }

    // 逻辑处理
    interface Presenter {
        void onResume();
        void onPause();

        void setGLSurfaceView(GLSurfaceView view);

        boolean canSwitchCamera();
        void switchCamera();

        boolean canSwitchFlashMode();
        void switchFlashMode();

        void chooseFilter();

        void takePhoto();
    }

    // 数据存储
    interface Support {

    }
}
